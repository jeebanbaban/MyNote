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

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DashboardActivity";
    DbInterface db;
    SharedPreferences preferences;
    RecyclerView rvNotes;
    FloatingActionButton fabAdd;
    NoteAdapter adapter;
    User user;
    List<NoteList> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        db=new DbInterface(getApplicationContext());
        setUI();
    }

    private void setUI() {
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
        rvNotes=findViewById(R.id.rvNotes);
        fabAdd=findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);
        user=db.getUser(preferences.getInt(AllKeys.SP_USER_ID,0));
        notes=new ArrayList<>();
        //loadList();
        getNotes();
    }

   /* private void loadList(){
        adapter=new NoteAdapter(getApplicationContext(),notes);
        rvNotes.setAdapter(adapter);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAdd: {
                //addNote();
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

                        }
                    }
                });

            } break;
        }
    }

   /* private void addNote(){
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
                Note note=new Note();
                note.setTitle(etTitle.getText().toString());
                note.setBody(etBody.getText().toString());
                note.setUser_id(user.getId());
                if(validate(etTitle)) {
                    if (db.insertNote(note)) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Note added !", Toast.LENGTH_SHORT).show();
                        loadList();
                    } else {
                        Toast.makeText(getApplicationContext(), "Note can't create !", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog progressDialog=new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Creating note");
        progressDialog.setMessage("loading.....");
        progressDialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                AllUrls.SERVER+"api/create.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        NoteModel noteModel= new Gson().fromJson(response,NoteModel.class);

                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
                        System.out.println("response=====" +response);
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@");

                        if (!(noteModel==null)){

                            System.out.println("#############################");
                            System.out.println("notemodel a dhuke geche.......");
                            System.out.println("token====="+preferences.getString("token",""));
                            System.out.println("#############################");
                            progressDialog.dismiss();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                progressDialog.dismiss();
                Toast.makeText(DashboardActivity.this, "Unable to create....", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params= new HashMap<>();
                params.put("token",token);
                params.put("title",title);
                params.put("body",body);
                return params;
            }
        };
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }


    private void getNotes(){

        RequestQueue queue=Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog progressDialog=new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Note Adding");
        progressDialog.setMessage("loading...");
        progressDialog.show();

        StringRequest stringRequest= new StringRequest(Request.Method.GET,
                AllUrls.SERVER+"api/notes.json?token="+preferences.getString("token",""),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        NoteListModel noteListModel=new Gson().fromJson(response,NoteListModel.class);

                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
                        System.out.println("response============" +response);
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@");

                        if (!(noteListModel==null)){
                            notes=noteListModel.getNotes();

                            Log.e(TAG, "onResponse: "+notes.size() );

                            adapter=new NoteAdapter(getBaseContext(),notes);
                            rvNotes.setAdapter(adapter);
                            rvNotes.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));

                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
                            System.out.println("notelistmodel status========"+noteListModel.isStatus());
                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@");
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                return super.getParams();
            }
        };
        queue.getCache().clear();
        queue.add(stringRequest);
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id =item.getItemId();
        if (id==R.id.menuLogout){
            finish();
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            SharedPreferences.Editor editor=preferences.edit();
            editor.remove(AllKeys.SP_ISLOGIN);
            editor.remove(AllKeys.SP_EMAIL);
            editor.remove(AllKeys.SP_USER_ID);
            editor.commit();
            Toast.makeText(this, "logout successfull...", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id==R.id.menuChangePwd){

            changePassword();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public boolean validateCPW(EditText etNewPassword, EditText etNewCnfPassword){


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
        /*
        if(pwd.equals(cnfPwd)){
            return  true;
        }
        else {
            return false;
        }
        */
        return true;
    }


    private void logout(final String token){
        RequestQueue queue=Volley.newRequestQueue(getApplicationContext());

        final ProgressDialog progressDialog=new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Logout");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        StringRequest request=new StringRequest(Request.Method.POST, AllUrls.SERVER+"auth/logout.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        LogoutModel logoutModel=new Gson().fromJson(response,LogoutModel.class);
                        System.out.println("@@@@@@@@@@@@@@@@@@");
                        System.out.println("response====" +response);
                        System.out.println("@@@@@@@@@@@@@@@@@@");
                        if (logoutModel.isStatus()){
                            /*System.out.println("#######################");
                            System.out.println("status==="+logoutModel.isStatus());
                            System.out.println("message==="+logoutModel.getMessage());
                            System.out.println("#######################");*/

                            preferences.edit().remove(AllKeys.SP_ISLOGIN).commit();
                                Toast.makeText(DashboardActivity.this, "logout successfull", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DashboardActivity.this,MainActivity.class));


                        }
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("$%#^%$^%$%^$^#$%@#%$$%^&^%^&");
                System.out.println("error===="+error);
                System.out.println("$%#^%$^%$%^$^#$%@#%$$%^&^%^&");

                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("token",token);
                return params;
            }
        };
        queue.getCache().clear();
        queue.add(request);

    }

    /*private void changePassword(){
        final Dialog dialog=new Dialog(DashboardActivity.this);
        View parentView=getLayoutInflater().inflate(R.layout.dialogchangepwd,null);
        dialog.setContentView(parentView);
        dialog.show();

       Button btnsave=parentView.findViewById(R.id.btnsave);
        Button btnCancel=parentView.findViewById(R.id.btnCancel);
        final EditText etOldPassword=parentView.findViewById(R.id.oldpwd);
        final EditText etNewPassword=parentView.findViewById(R.id.newpwd);
        final EditText etNewCnfPassword=parentView.findViewById(R.id.confirmpwd);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
*//*
                if (!etOldPassword.getText().toString().equals(user.getPassword())){

                    etOldPassword.addTextChangedListener(new EditTextWatcher(etOldPassword));
                    TextInputLayout textInputLayout= (TextInputLayout) etOldPassword.getParent().getParent();
                    textInputLayout.setError("old password is wrong");
                    textInputLayout.requestFocus();

                    System.out.println("@@@@@@@@@@@@@@@@");
                    System.out.println("@@@password not matched@@@@");
                    System.out.println("@@@@@@@@@@@@@@@@");
                }*//*

                if(validateCPW(etNewPassword,etNewCnfPassword)) {
                    if (db.changePwd(user, etOldPassword.getText().toString(), etNewPassword.getText().toString())) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Password changed !", Toast.LENGTH_SHORT).show();
                    } else {
                        TextInputLayout textInputLayout= (TextInputLayout) etOldPassword.getParent().getParent();
                        textInputLayout.setError("old password is wrong");
                        textInputLayout.requestFocus();
                        Toast.makeText(getApplicationContext(), "Password changing failed !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }*/
}
