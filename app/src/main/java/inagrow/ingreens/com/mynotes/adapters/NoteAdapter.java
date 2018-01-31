package inagrow.ingreens.com.mynotes.adapters;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.activities.DashboardActivity;
import inagrow.ingreens.com.mynotes.activities.NoteDetailsActivity;
import inagrow.ingreens.com.mynotes.apis.ApiDao;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.DeleteNoteResponse;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

/**
 * Created by root on 11/1/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String TAG = "NoteAdapter";

    LayoutInflater inflater;
    List<Note> notes;
    DbInterface db;
    SharedPreferences preferences;
    Context context;
    Activity activity;

    public NoteAdapter(Context context, List<Note> notes, Activity activity){
        this.context=context;
        this.inflater=LayoutInflater.from(context);
        this.notes=notes;
        this.activity=activity;
        db=new DbInterface(context);
        preferences=context.getSharedPreferences(AllKeys.SP_INSTANCE_NAME,Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_note,parent,false);
        ViewHolder holder=new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note=notes.get(position);
        holder.tvTitle.setText(note.getTitle());
        String body=note.getBody();
        if(body.length()>27){
            body=body.substring(0,27)+"...";
        }
        body=body.replace("\n"," ");
        holder.tvBody.setText(body);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle, tvBody;
        Button btnDelete;
        CardView cvItem;

        public ViewHolder(View itemView) {
            super(itemView);
            cvItem=itemView.findViewById(R.id.cvItem);
            tvTitle=itemView.findViewById(R.id.tvTitle);
            tvBody=itemView.findViewById(R.id.tvBody);
            btnDelete=itemView.findViewById(R.id.btnDelete);

            cvItem.setOnClickListener(this);
            btnDelete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnDelete: {
                    final ProgressDialog dialog=new ProgressDialog(activity);
                    dialog.setTitle("Note");
                    dialog.setMessage("Note deleting...");
                    dialog.show();
                    final Note note=notes.get(getPosition());
                    Call<DeleteNoteResponse> call= ApiDao.getApiDao().deleteNote(preferences.getString(AllKeys.SP_TOKEN,""),note.getId());
                    call.enqueue(new Callback<DeleteNoteResponse>() {
                        @Override
                        public void onResponse(Call<DeleteNoteResponse> call, Response<DeleteNoteResponse> response) {
                            DeleteNoteResponse deleteNoteResponse=response.body();
                            if (deleteNoteResponse.isStatus()){
                                Toast.makeText(inflater.getContext(),"Note deleted !",Toast.LENGTH_SHORT).show();
                                notes.remove(getPosition());
                                notifyItemRemoved(getPosition());
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<DeleteNoteResponse> call, Throwable t) {
                            Toast.makeText(inflater.getContext(),"Note deletion failed !",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                } break;
                case R.id.cvItem: {
//                    Toast.makeText(inflater.getContext(),"Item clicked "+getPosition(),Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor=preferences.edit();
                    String noteJson=new Gson().toJson(notes.get(getPosition()));
                    editor.putString(AllKeys.SP_NOTE,noteJson);
                    editor.commit();
                    Intent intent=new Intent(context, NoteDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } break;
            }
        }
    }
}
