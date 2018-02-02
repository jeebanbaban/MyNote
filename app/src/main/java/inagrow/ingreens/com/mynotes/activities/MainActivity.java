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
import inagrow.ingreens.com.mynotes.apis.ApiDao;
import inagrow.ingreens.com.mynotes.apis.ApiInterface;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.LoginModel;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.RegisterModel;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.utils.AllUrls;
import inagrow.ingreens.com.mynotes.watchers.EditTextWatcher;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    DbInterface db;
    EditText etName,etEmail, etPassword;
    Button btnLogin, btnSignUp;
    SharedPreferences preferences;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DbInterface(getApplicationContext());
        apiInterface= ApiDao.getApiDao();
        setUI();
    }


    private boolean validate(EditText etName, EditText etEmail, EditText etPassword ){

        etName.addTextChangedListener(new EditTextWatcher(etName));
        etEmail.addTextChangedListener(new EditTextWatcher(etEmail));
        etPassword.addTextChangedListener(new EditTextWatcher(etPassword));
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

        return true;
    }


    private void setUI() {
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
        etName=findViewById(R.id.etName);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btnLogin=findViewById(R.id.btnLogIn);
        btnSignUp=findViewById(R.id.btnSignUp);

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

            } break;
        }
    }

    private void login(final String email, final String password){

        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loging in...");
        progressDialog.setMessage("Please wait for response...");
//        progressDialog.setCancelable(false);
        progressDialog.show();
        Call<LoginModel> call=apiInterface.login(email,password);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                LoginModel loginModel=response.body();
                System.out.println("response==="+loginModel.toString());
                if (!(loginModel==null)){
                    if (loginModel.isLogin()) {
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putBoolean(AllKeys.SP_ISLOGIN,true);
                        editor.putString(AllKeys.SP_EMAIL,email);
                        editor.putString("token",loginModel.getToken());
                        editor.commit();
                        Log.e(TAG, "onResponse: "+loginModel.getToken() );
                        Toast.makeText(MainActivity.this, "login successfull..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(MainActivity.this, "login failed..", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

            }
        });


    }


    private void signUp(final String name,final String email,final String password){

        final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Registering In..");
        progressDialog.setMessage("authenticating....");
        progressDialog.show();
        Call<RegisterModel> call=apiInterface.register(name,email,password);
        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(Call<RegisterModel> call, retrofit2.Response<RegisterModel> response) {
                RegisterModel registerModel=response.body();
                System.out.println("response==="+registerModel.toString());
                if (!(registerModel==null)){

                    if (registerModel.isStatus()){
                        Toast.makeText(MainActivity.this, "registration succesfull..", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(MainActivity.this, "registration failed...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    }
                }
            }
            @Override
            public void onFailure(Call<RegisterModel> call, Throwable t) {
                System.out.println("error===="+t.getMessage());
            }
        });

    }

}
