package inagrow.ingreens.com.mynotes.activities;

import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.adapters.NoteAdapter;
import inagrow.ingreens.com.mynotes.apis.ApiDao;
import inagrow.ingreens.com.mynotes.apis.ApiInterface;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.NoteList;
import inagrow.ingreens.com.mynotes.models.NoteModel;
import inagrow.ingreens.com.mynotes.models.UpdateNoteModel;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.utils.AllUrls;
import inagrow.ingreens.com.mynotes.watchers.EditTextWatcher;
import retrofit2.Call;
import retrofit2.Callback;

public class NoteDetailsActivity extends AppCompatActivity {
    private static final String TAG = "NoteDetailsActivity";
    TextView tvTitle, tvBody;
    Button btnEdit;
    DbInterface db;
    SharedPreferences preferences;
    User user;
    NoteList note;
    List<NoteList> notes;
    ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        db=new DbInterface(this);
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
//        user=db.getUser(preferences.getInt(AllKeys.SP_USER_ID,0));
//        note=db.getNote(user.getId(),preferences.getInt(AllKeys.SP_NOTE_ID,0));
        note=new Gson().fromJson(preferences.getString("note",""),NoteList.class);
        apiInterface= ApiDao.getApiDao();
        setUI();
    }

    private void setUI() {
        tvTitle=findViewById(R.id.tvTitle);
        tvBody=findViewById(R.id.tvBody);
        btnEdit=findViewById(R.id.btnEdit);
        display();
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNote();
            }
        });
    }

    private void display(){
//        Log.e(TAG, "display: "+preferences.getString("note","") );
        tvTitle.setText(note.getTitle());
        tvBody.setText(note.getBody());

    }

    private void editNote(){
        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(NoteDetailsActivity.this);
        View parentView=getLayoutInflater().inflate(R.layout.dialog_add_note,null);
        bottomSheetDialog.setContentView(parentView);
        bottomSheetDialog.show();
        Button btnAdd=parentView.findViewById(R.id.btnAdd);
        Button btnCancel=parentView.findViewById(R.id.btnCancel);
        final TextView tvDialogTitle=parentView.findViewById(R.id.tvDialogTitle);
        final EditText etTitle=parentView.findViewById(R.id.etTitle);
        final EditText etBody=parentView.findViewById(R.id.etBody);

        tvDialogTitle.setText("Edit Note");
        etTitle.setText(note.getTitle());
        etBody.setText(note.getBody());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.setTitle(etTitle.getText().toString());
                note.setBody(etBody.getText().toString());
                String token=preferences.getString("token","");
                String id= String.valueOf(note.getId());
                String title=etTitle.getText().toString();
                String body=etBody.getText().toString();
                if(validate(etTitle,etBody)) {

                    updateNote(token,id,title,body);

                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    System.out.println("update note er title,body and id====="+title+" "+body+" "+id);
                    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    bottomSheetDialog.dismiss();
                    display();
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

    private boolean validate(EditText etTitle,EditText etBody){

        etTitle.addTextChangedListener(new EditTextWatcher(etTitle));
        etBody.addTextChangedListener(new EditTextWatcher(etBody));

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
            etBody.requestFocus();
            Toast.makeText(getApplicationContext(),"Body can't be empty !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void updateNote(final String token,final String id,final String title,final String body){


        Call<UpdateNoteModel> call=apiInterface.updateNote(token,id,title,body);
        call.enqueue(new Callback<UpdateNoteModel>() {
            @Override
            public void onResponse(Call<UpdateNoteModel> call, retrofit2.Response<UpdateNoteModel> response) {
                UpdateNoteModel updateNoteModel=response.body();
                if (updateNoteModel.isStatus()){
                    Toast.makeText(NoteDetailsActivity.this, "note updated", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<UpdateNoteModel> call, Throwable t) {

            }
        });
    }



}
