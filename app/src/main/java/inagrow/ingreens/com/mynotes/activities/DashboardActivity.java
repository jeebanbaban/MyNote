package inagrow.ingreens.com.mynotes.activities;

import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.adapters.NoteAdapter;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DashboardActivity";
    DbInterface db;
    SharedPreferences preferences;
    RecyclerView rvNotes;
    FloatingActionButton fabAdd;
    NoteAdapter adapter;
    User user;

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
        loadList();
    }

    private void loadList(){
        adapter=new NoteAdapter(getApplicationContext(),db.getNotes(user.getId()));
        rvNotes.setAdapter(adapter);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
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
        BottomSheetBehavior bottomSheetBehavior=BottomSheetBehavior.from((View)parentView.getParent());
//                bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,400,getResources().getDisplayMetrics()));
        bottomSheetDialog.show();
        Button btnAdd=parentView.findViewById(R.id.btnAdd);
        Button btnCancel=parentView.findViewById(R.id.btnCancel);
        final EditText etTitle=parentView.findViewById(R.id.etTitle);
        final EditText etBody=parentView.findViewById(R.id.etBody);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note=new Note();
                note.setTitle(etTitle.getText().toString());
                note.setBody(etBody.getText().toString());
                note.setUser_id(user.getId());
                if(db.insertNote(note)){
                    bottomSheetDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Note added !", Toast.LENGTH_SHORT).show();
                    loadList();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Note can't create !", Toast.LENGTH_SHORT).show();
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
