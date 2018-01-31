package inagrow.ingreens.com.mynotes.activities;

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

import java.util.List;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.apis.ApiDao;
import inagrow.ingreens.com.mynotes.apis.ApiInterface;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.LoginResponse;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.RegisterResponse;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.utils.AllUrls;
import inagrow.ingreens.com.mynotes.watchers.EditTextWatcher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    EditText etEmail, etPassword;
    Button btnLogin, btnSignUp;
    SharedPreferences preferences;
    ApiInterface apis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apis= ApiDao.getApiDao();
        setUI();
    }

    private void setUI() {
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        btnLogin=findViewById(R.id.btnLogIn);
        btnSignUp=findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        if (preferences.getBoolean(AllKeys.SP_ISLOGIN,false)){
            startActivity(new Intent(MainActivity.this,DashboardActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogIn: {
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();
                /*User user=db.getUser(email,password);
                if(user.getEmail().equals(email)){
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean(AllKeys.SP_ISLOGIN,true);
                    editor.putString(AllKeys.SP_EMAIL,user.getEmail());
                    editor.putInt(AllKeys.SP_USER_ID,user.getId());
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Login successfully !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                }*/
                login(email,password);
            } break;
            case R.id.btnSignUp: {
                signUp();
            } break;
        }
    }

    private void login(final String email, String password){
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Login");
        dialog.setMessage("Please wait...");
        dialog.show();
        Call<LoginResponse> call=apis.login(email,password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse=response.body();
                Log.d(TAG, "onResponse: "+loginResponse.toString());
                if(loginResponse.isLogin()){
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean(AllKeys.SP_ISLOGIN,true);
                    editor.putString(AllKeys.SP_EMAIL,email);
                    editor.putString(AllKeys.SP_TOKEN,loginResponse.getToken());
                    editor.commit();
                    Toast.makeText(getApplicationContext(), loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(), loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage() );
                dialog.dismiss();
            }
        });
    }

    private boolean validate(EditText etName, EditText etEmail, EditText etPassword, EditText etRePassword ){

        etName.addTextChangedListener(new EditTextWatcher(etName));
        etEmail.addTextChangedListener(new EditTextWatcher(etEmail));
        etPassword.addTextChangedListener(new EditTextWatcher(etPassword));
        etRePassword.addTextChangedListener(new EditTextWatcher(etRePassword));

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

        if(!etPassword.getText().toString().equals(etRePassword.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etRePassword.getParent().getParent();
            textInputLayout.setError("Password is not same.");
            etRePassword.requestFocus();
            Toast.makeText(getApplicationContext(),"Password is not same !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signUp(){
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(MainActivity.this);
        View parentView=getLayoutInflater().inflate(R.layout.dialog_signup,null);
        bottomSheetDialog.setContentView(parentView);

        bottomSheetDialog.show();
        Button btnSignUp=parentView.findViewById(R.id.btnSignUp);
        Button btnCancel=parentView.findViewById(R.id.btnCancel);
        final EditText etName=parentView.findViewById(R.id.etName);
        final EditText etEmail=parentView.findViewById(R.id.etEmail);
        final EditText etPass1=parentView.findViewById(R.id.etPassword);
        final EditText etPass2=parentView.findViewById(R.id.etRePassword);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=etName.getText().toString();
                String email=etEmail.getText().toString();
                String pass=etPass1.getText().toString();
                String repass=etPass2.getText().toString();
                if(validate(etName,etEmail,etPass1,etPass2)){
                    bottomSheetDialog.dismiss();
                    register(name,email,pass);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void register(String name, String email, String password) {
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Register");
        dialog.setMessage("Please wait...");
        dialog.show();
        Call<RegisterResponse> call=apis.register(name,email,password);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                RegisterResponse registerResponse=response.body();
                if(registerResponse.isStatus()){
                    Toast.makeText(getApplicationContext(),"User registered !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Failed to register !", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed to register !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }


}
