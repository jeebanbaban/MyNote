package inagrow.ingreens.com.mynotes.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.LoginModel;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.RegisterModel;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.utils.AllUrls;
import inagrow.ingreens.com.mynotes.watchers.EditTextWatcher;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    DbInterface db;
    EditText etName,etEmail, etPassword;
    Button btnLogin, btnSignUp,btnRegister;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DbInterface(getApplicationContext());
        setUI();
    }


    private boolean validate(EditText etName, EditText etEmail, EditText etPassword ){

        etName.addTextChangedListener(new EditTextWatcher(etName));
        etEmail.addTextChangedListener(new EditTextWatcher(etEmail));
        etPassword.addTextChangedListener(new EditTextWatcher(etPassword));
        //etRePassword.addTextChangedListener(new EditTextWatcher(etRePassword));

        if(TextUtils.isEmpty(etName.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etName.getParent().getParent();
            textInputLayout.setError("Name can't be empty.");
            etName.requestFocus();
            Toast.makeText(getApplicationContext(),"Name can't be empty !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(etEmail.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etEmail.getParent().getParent();
            textInputLayout.setError("Email can't be empty.");
            etEmail.requestFocus();
            Toast.makeText(getApplicationContext(),"Email can't be empty !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(etPassword.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etPassword.getParent().getParent();
            textInputLayout.setError("Password can't be empty.");
            etPassword.requestFocus();
            Toast.makeText(getApplicationContext(),"Password can't be empty !", Toast.LENGTH_SHORT).show();
            return false;
        }
/*
        if(!etPassword.getText().toString().equals(etRePassword.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etRePassword.getParent().getParent();
            textInputLayout.setError("Password is not same.");
            etRePassword.requestFocus();
            Toast.makeText(getApplicationContext(),"Password is not same !", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
    }


    private void setUI() {
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
        etName=findViewById(R.id.etName);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btnLogin=findViewById(R.id.btnLogIn);
        btnSignUp=findViewById(R.id.btnSignUp);
        //btnRegister=findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);

        if(preferences.getBoolean(AllKeys.SP_ISLOGIN,false)){
            startActivity(new Intent(MainActivity.this,DashboardActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogIn: {
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();
                login(email,password);
                /*
                User user=db.getUser(email,password);
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                System.out.println("emai and password===" +user.getEmail()+"  "+user.getPassword());
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                if(user.getEmail().equals(email)){

                    System.out.println("@@@@@@@@@@@@@ login validation checked @@@@@@@@@@@@");

                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean(AllKeys.SP_ISLOGIN,true);
                    editor.putString(AllKeys.SP_EMAIL,user.getEmail());
                    editor.putInt(AllKeys.SP_USER_ID,user.getId());
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Login successfully !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                }
                else{
                    Toast.makeText(this, "invalid emailid and password......", Toast.LENGTH_SHORT).show();
                }*/
            } break;
            case R.id.btnSignUp: {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                View parentView = getLayoutInflater().inflate(R.layout.dialog_signup, null);
                bottomSheetDialog.setContentView(parentView);
                bottomSheetDialog.show();

                Button btnSignUp = parentView.findViewById(R.id.btnSignUp);
                Button btnCancel = parentView.findViewById(R.id.btnCancel);

                final EditText etName = parentView.findViewById(R.id.etName);
                final EditText etEmail = parentView.findViewById(R.id.etEmail);
                final EditText etPassword = parentView.findViewById(R.id.etPassword);
                btnSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String name = etName.getText().toString();
                        String email = etEmail.getText().toString();
                        String password = etPassword.getText().toString();

                        if (validate(etName,etEmail,etPassword)){
                           signUp(name,email,password);

                            bottomSheetDialog.dismiss();
                        }

                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });



                //signUp(name,email,password);

                /*if (validate(etName,etEmail,etPassword)){
                    signUp(name,email,password);
                }
*/




                //signUp();
            } break;
        }
    }

    private void login(final String email, final String password){
        RequestQueue queue= Volley.newRequestQueue(getApplicationContext());

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loging in...");
        progressDialog.setMessage("Please wait for response...");
//        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest request=new StringRequest(Request.Method.POST,
                AllUrls.SERVER+"auth/login.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onResponse: "+response );
                        LoginModel loginModel=new Gson().fromJson(response,LoginModel.class);
                        Log.e(TAG, "onResponse: Token is -> "+loginModel.getToken()+" " +loginModel.getMessage() +" "+loginModel.isLogin()+" "+loginModel.isStatus() );
                        if (!(loginModel==null)){

                           SharedPreferences.Editor editor=preferences.edit();
                            editor.putBoolean(AllKeys.SP_ISLOGIN,true);
                            editor.putString("token",loginModel.getToken());

                            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTT");
                            System.out.println("token========="+loginModel.getToken());
                            System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTT");
                            //editor.putString(AllKeys.SP_EMAIL,user.getEmail());
                            //editor.putInt(AllKeys.SP_USER_ID,user.getId());
                            editor.commit();
                            startActivity(new Intent(MainActivity.this,DashboardActivity.class));


                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: "+error.getMessage() );
                        progressDialog.dismiss();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        queue.getCache().clear();
        queue.add(request);

    }




   /* private void signUp() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View parentView = getLayoutInflater().inflate(R.layout.dialog_signup, null);
        bottomSheetDialog.setContentView(parentView);

        bottomSheetDialog.show();
        Button btnSignUp = parentView.findViewById(R.id.btnSignUp);
        Button btnCancel = parentView.findViewById(R.id.btnCancel);
        final EditText etName = parentView.findViewById(R.id.etName);
        final EditText etEmail = parentView.findViewById(R.id.etEmail);
        final EditText etPass1 = parentView.findViewById(R.id.etPassword);
        //final EditText etPass2=parentView.findViewById(R.id.etRePassword);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String pass = etPass1.getText().toString();
                String repass = etPass2.getText().toString();
                if (validate(etName, etEmail, etPass1, etPass2)) {
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(pass);
                    if (db.insertUser(user)) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "User registered !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to register !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }*/


    private void signUp(final String name,final String email,final String password){

        RequestQueue queue=Volley.newRequestQueue(getApplicationContext());

        final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Registering In..");
        progressDialog.setMessage("authenticating....");
        progressDialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AllUrls.SERVER+"auth/register.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%");
                        System.out.println("on response..." +response);
                        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%");
                        RegisterModel registerModel=new Gson().fromJson(response,RegisterModel.class);

                        if (!(registerModel==null)){

                            System.out.println("################################");
                            System.out.println("" +registerModel.getMessage() +" " +registerModel.isStatus());
                            SharedPreferences.Editor editor=preferences.edit();
                            editor.putBoolean(AllKeys.SP_ISLOGIN,true);
                            editor.putString("message",registerModel.getMessage());
                            editor.commit();
                            progressDialog.dismiss();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("@@@@@@@@@@@@@@@@@@");
                System.out.println("error====" +error.getMessage());
                System.out.println("@@@@@@@@@@@@@@@@@@");

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();
                params.put("name",name);
                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };
        queue.getCache().clear();
        queue.add(stringRequest);

    }

}
