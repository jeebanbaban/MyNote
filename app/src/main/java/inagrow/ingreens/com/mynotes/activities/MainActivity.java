package inagrow.ingreens.com.mynotes.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
  /*      User user=new User();
        user.setName("Jeeban");
        user.setEmail("jb@jb.com");
        user.setPassword("1234");
*/
        /*if(db.insertUser(user)){
            Log.e(TAG, "onCreate: User created.");
        }
        else{
            Log.e(TAG, "onCreate: User creation failed.");
        }

        User u1=db.getUser("jb@jb.com","1234");
        if(u1.getEmail().equals("jb@jb.com")){
            Log.e(TAG, "onCreate: login success.");
            Log.e(TAG, "onCreate: "+u1 );
            List<Note> notes=db.getNotes(u1.getId());

            for (int i=0; i<notes.size(); i++){
                Note note=notes.get(i);
            }

            for (Note note:notes){
                Log.e(TAG, "onCreate: "+note );
            }


            Note note=new Note();

            note.setTitle("First Note");
            note.setBody("My Body");
            note.setUser_id(u1.getId());
            db.insertNote(note);

            note.setTitle("First Note 2");
            note.setBody("My Body 2");
            note.setUser_id(u1.getId());
            db.insertNote(note);


        }*/

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

            } break;
        }
    }
}
