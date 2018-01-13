package inagrow.ingreens.com.mynotes.activities;

import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.watchers.EditTextWatcher;

public class NoteDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvBody;
    Button btnEdit;
    DbInterface db;
    SharedPreferences preferences;
    User user;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        db=new DbInterface(this);
        preferences=getSharedPreferences(AllKeys.SP_INSTANCE_NAME,MODE_PRIVATE);
        user=db.getUser(preferences.getInt(AllKeys.SP_USER_ID,0));
        note=db.getNote(user.getId(),preferences.getInt(AllKeys.SP_NOTE_ID,0));
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
                if(validate(etTitle)) {
                    if (db.updateNote(note)) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Note updated !", Toast.LENGTH_SHORT).show();
                        display();
                    } else {
                        Toast.makeText(getApplicationContext(), "Note can't update !", Toast.LENGTH_SHORT).show();
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

}
