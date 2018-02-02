package inagrow.ingreens.com.mynotes.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.adapters.NoteAdapter;
import inagrow.ingreens.com.mynotes.apis.ApiDao;
import inagrow.ingreens.com.mynotes.apis.ApiInterface;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.LogoutModel;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.NoteList;
import inagrow.ingreens.com.mynotes.models.NoteListModel;
import inagrow.ingreens.com.mynotes.models.NoteModel;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.utils.AllUrls;
import inagrow.ingreens.com.mynotes.watchers.EditTextWatcher;
import retrofit2.Call;
import retrofit2.Callback;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DashboardActivity";
    DbInterface db;
    SharedPreferences preferences;
    RecyclerView rvNotes;
    FloatingActionButton fabAdd;
    NoteAdapter adapter;
    User user;
    List<NoteList> notes;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        db=new DbInterface(getApplicationContext());
        apiInterface= ApiDao.getApiDao();
        setUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "refresh notelist", Toast.LENGTH_SHORT).show();
        getNotes();
    }

    private void setUI() {
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
        rvNotes=findViewById(R.id.rvNotes);
        fabAdd=findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);
        user=db.getUser(preferences.getInt(AllKeys.SP_USER_ID,0));
        notes=new ArrayList<>();
        getNotes();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAdd: {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(DashboardActivity.this);
                View parentView=getLayoutInflater().inflate(R.layout.dialog_add_note,null);
                bottomSheetDialog.setContentView(parentView);
                bottomSheetDialog.show();

                Button btnAdd=parentView.findViewById(R.id.btnAdd);
                Button btnCancel=parentView.findViewById(R.id.btnCancel);
                final TextView tvDialogTitle=parentView.findViewById(R.id.tvDialogTitle);
                final EditText etTitle=parentView.findViewById(R.id.etTitle);
                final EditText etBody=parentView.findViewById(R.id.etBody);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title=etTitle.getText().toString();
                        String body=etBody.getText().toString();
                        String token= preferences.getString("token","");
                        if (validate(etTitle,etBody)){
                            createNote(token,title,body);
                            bottomSheetDialog.dismiss();
                        }
                    }
                });

            } break;
        }
    }


    private boolean validate(EditText etTitle,EditText etBody){

        etTitle.addTextChangedListener(new EditTextWatcher(etTitle));

        if(TextUtils.isEmpty(etTitle.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etTitle.getParent().getParent();
            textInputLayout.setError("Title can't be empty.");
            etTitle.requestFocus();
            Toast.makeText(getApplicationContext(),"Title can't be empty !", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(etBody.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etBody.getParent().getParent();
            textInputLayout.setError("Body can't be empty.");
            etTitle.requestFocus();
            Toast.makeText(getApplicationContext(),"Body can't be empty !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    private void createNote(final String token, final String title, final String body){

        Call<NoteModel> call=apiInterface.createNote(token,title,body);
        call.enqueue(new Callback<NoteModel>() {
            @Override
            public void onResponse(Call<NoteModel> call, retrofit2.Response<NoteModel> response) {
                NoteModel noteModel = response.body();
                System.out.println("response@@@@@@@@@2==="+noteModel);
                if (!(noteModel == null)) {
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    System.out.println("create note status==" + noteModel.isStatus());
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    if (noteModel.isStatus()) {
                        Toast.makeText(DashboardActivity.this, "note created..", Toast.LENGTH_LONG).show();
                        String token=preferences.getString("token","");
                        getNotes();

                    }
                }
            }

            @Override
            public void onFailure(Call<NoteModel> call, Throwable t) {
                System.out.println("#######################");
                System.out.println("error==="+t.getMessage());
                System.out.println("#######################");


            }
        });
    }


    private void getNotes(){
        Call<NoteListModel> call=apiInterface.getNotes(preferences.getString("token",""));
        call.enqueue(new Callback<NoteListModel>() {
            @Override
            public void onResponse(Call<NoteListModel> call, retrofit2.Response<NoteListModel> response) {
                NoteListModel noteListModel=response.body();
                System.out.println("response==="+noteListModel);
                if (!(noteListModel==null)){
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    System.out.println("getnotes status===="+noteListModel.isStatus());
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@");

                    notes=noteListModel.getNotes();
                    adapter=new NoteAdapter(DashboardActivity.this,notes);
                    rvNotes.setAdapter(adapter);
                    rvNotes.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));

                }
            }

            @Override
            public void onFailure(Call<NoteListModel> call, Throwable t) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menuitem,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.menuLogout){
            String token=preferences.getString("token","");
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&");
            System.out.println("token===="+token);
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&");
            logout(token);
            //startActivity(new Intent(DashboardActivity.this,MainActivity.class));

            return true;
        }
        if (id==R.id.menuChangePwd){


            return true;
        }


        return super.onOptionsItemSelected(item);
    }

/*    public boolean validateCPW(EditText etNewPassword, EditText etNewCnfPassword){


        String pwd=etNewPassword.getText().toString();
        String cnfPwd=etNewCnfPassword.getText().toString();

        etNewPassword.addTextChangedListener(new EditTextWatcher(etNewPassword));
        etNewCnfPassword.addTextChangedListener(new EditTextWatcher(etNewCnfPassword));

        if(TextUtils.isEmpty(etNewPassword.getText().toString())){

            TextInputLayout textInputLayout= (TextInputLayout) etNewPassword.getParent().getParent();
            textInputLayout.setError("new password can't be empty");
            textInputLayout.requestFocus();

            Toast.makeText(this, "please enter new password..", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (TextUtils.isEmpty(etNewCnfPassword.getText().toString())){
            TextInputLayout textInputLayout= (TextInputLayout) etNewCnfPassword.getParent().getParent();
            textInputLayout.setError("password doesn't match");
            textInputLayout.requestFocus();
            Toast.makeText(this, "please enter confirm paswsword.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!pwd.equals(cnfPwd)){

            TextInputLayout textInputLayout= (TextInputLayout) etNewCnfPassword.getParent().getParent();
            textInputLayout.setError("passwords are not same");
            textInputLayout.requestFocus();
            return false;
        }
        *//*
        if(pwd.equals(cnfPwd)){
            return  true;
        }
        else {
            return false;
        }
        *//*
        return true;
    }*/


    private void logout(final String token){
        final ProgressDialog progressDialog=new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Logout");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        Call<LogoutModel> call=apiInterface.logout(token);
        call.enqueue(new Callback<LogoutModel>() {
            @Override
            public void onResponse(Call<LogoutModel> call, retrofit2.Response<LogoutModel> response) {

                LogoutModel logoutModel=response.body();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("logout response==="+logoutModel);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");

                if (logoutModel.isStatus()){

                    preferences.edit().remove(AllKeys.SP_ISLOGIN).commit();
                    preferences.edit().remove(AllKeys.SP_EMAIL).commit();
                    Toast.makeText(DashboardActivity.this, "you have sucessfully logout", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DashboardActivity.this,MainActivity.class));

                }
            }

            @Override
            public void onFailure(Call<LogoutModel> call, Throwable t) {
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
                System.out.println("error==="+t.getMessage());
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");

            }
        });
     }
}
