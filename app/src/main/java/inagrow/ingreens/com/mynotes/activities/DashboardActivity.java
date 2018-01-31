package inagrow.ingreens.com.mynotes.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.adapters.NoteAdapter;
import inagrow.ingreens.com.mynotes.apis.ApiDao;
import inagrow.ingreens.com.mynotes.apis.ApiInterface;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.CreateNoteResponse;
import inagrow.ingreens.com.mynotes.models.DeleteUserResponse;
import inagrow.ingreens.com.mynotes.models.LogoutResponse;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.NoteResponse;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.watchers.EditTextWatcher;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DashboardActivity";

    SharedPreferences preferences;
    RecyclerView rvNotes;
    FloatingActionButton fabAdd;
    NoteAdapter adapter;
    String token;
    List<Note> notes;

    ApiInterface apis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        apis= ApiDao.getApiDao();
        setUI();
    }

    private void setUI() {
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
        rvNotes=findViewById(R.id.rvNotes);
        fabAdd=findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);
        notes=new ArrayList<>();
        token=preferences.getString(AllKeys.SP_TOKEN,"");
        loadList();
    }

    private void loadList(){
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Note");
        dialog.setMessage("Getting notes...");
        dialog.show();
        Call<NoteResponse> call=apis.getNotes(token);
        call.enqueue(new retrofit2.Callback<NoteResponse>() {
            @Override
            public void onResponse(Call<NoteResponse> call, Response<NoteResponse> response) {
                NoteResponse noteResponse=response.body();
                if (null==noteResponse){
                    preferences.edit().remove(AllKeys.SP_ISLOGIN).commit();
                    finish();
                }
                if(noteResponse.isStatus()){
                    dialog.dismiss();
                    notes=noteResponse.getNotes();
                    adapter=new NoteAdapter(getApplicationContext(),notes,DashboardActivity.this);
                    rvNotes.setAdapter(adapter);
                    rvNotes.setLayoutManager(new LinearLayoutManager(DashboardActivity.this));
                }
            }

            @Override
            public void onFailure(Call<NoteResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabAdd: {
                addNote();
            } break;
        }
    }

    private void addNote(){
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
                final ProgressDialog dialog=new ProgressDialog(DashboardActivity.this);
                dialog.setTitle("Create Note");
                dialog.setMessage("Please wait...");
                dialog.show();
                Call<CreateNoteResponse> call=apis.createNote(token,etTitle.getText().toString(),etBody.getText().toString());
                call.enqueue(new retrofit2.Callback<CreateNoteResponse>() {
                    @Override
                    public void onResponse(Call<CreateNoteResponse> call, Response<CreateNoteResponse> response) {
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Note added !", Toast.LENGTH_SHORT).show();
                        loadList();
                    }

                    @Override
                    public void onFailure(Call<CreateNoteResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Note can't create !", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private boolean validate(EditText etTitle){

        etTitle.addTextChangedListener(new EditTextWatcher(etTitle));

        if(TextUtils.isEmpty(etTitle.getText().toString())){
            TextInputLayout textInputLayout=(TextInputLayout) etTitle.getParent().getParent();
            textInputLayout.setError("Title can't be empty.");
            etTitle.requestFocus();
            Toast.makeText(getApplicationContext(),"Title can't be empty !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:{
                final ProgressDialog dialog=new ProgressDialog(DashboardActivity.this);
                dialog.setTitle("Logout");
                dialog.setMessage("Please wait...");
                dialog.show();
                Call<LogoutResponse> call=apis.logout(token);
                call.enqueue(new Callback<LogoutResponse>() {
                    @Override
                    public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                        LogoutResponse logoutResponse=response.body();
                        if (logoutResponse.isStatus()){
                            Toast.makeText(getApplicationContext(),"Logout successfully !",Toast.LENGTH_SHORT).show();
                            preferences.edit().remove(AllKeys.SP_ISLOGIN).commit();
                            finish();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<LogoutResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Logout failed !",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            } break;
            case R.id.menuDelete:{
                final ProgressDialog dialog=new ProgressDialog(DashboardActivity.this);
                dialog.setTitle("User");
                dialog.setMessage("User deleting...");
                dialog.show();
                Call<DeleteUserResponse> call=apis.deleteUser(token);
                call.enqueue(new Callback<DeleteUserResponse>() {
                    @Override
                    public void onResponse(Call<DeleteUserResponse> call, Response<DeleteUserResponse> response) {
                        DeleteUserResponse deleteUserResponse=response.body();
                        if(deleteUserResponse.isStatus()){
                            Toast.makeText(getApplicationContext(),deleteUserResponse.getMessage(),Toast.LENGTH_SHORT).show();
                            preferences.edit().remove(AllKeys.SP_ISLOGIN).commit();
                            finish();
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<DeleteUserResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Logout failed !",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            } break;
        }
        return super.onOptionsItemSelected(item);
    }
}
