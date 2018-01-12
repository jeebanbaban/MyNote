package inagrow.ingreens.com.mynotes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
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
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    DbInterface db;
    EditText etEmail, etPassword;
    Button btnLogin, btnSignUp;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=new DbInterface(getApplicationContext());
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogIn: {
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();
                User user=db.getUser(email,password);
                if(user.getEmail().equals(email)){
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putBoolean(AllKeys.SP_ISLOGIN,true);
                    editor.putString(AllKeys.SP_EMAIL,user.getEmail());
                    editor.putInt(AllKeys.SP_USER_ID,user.getId());
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Login successfully !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,DashboardActivity.class));
                }
            } break;
            case R.id.btnSignUp: {
                signUp();
            } break;
        }
    }

    private void signUp(){
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(MainActivity.this);
        View parentView=getLayoutInflater().inflate(R.layout.dialog_signup,null);
        bottomSheetDialog.setContentView(parentView);
        BottomSheetBehavior bottomSheetBehavior=BottomSheetBehavior.from((View)parentView.getParent());
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
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && pass.equals(repass)){
                    User user=new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(pass);
                    if(db.insertUser(user)){
                        bottomSheetDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"User registered !", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Failed to register !", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Fill data properly !", Toast.LENGTH_SHORT).show();
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

}
