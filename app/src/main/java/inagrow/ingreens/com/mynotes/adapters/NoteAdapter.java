package inagrow.ingreens.com.mynotes.adapters;


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

import java.util.List;

import inagrow.ingreens.com.mynotes.R;
import inagrow.ingreens.com.mynotes.activities.NoteDetailsActivity;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.utils.AllKeys;

/**
 * Created by root on 11/1/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<Note> notes;
    DbInterface db;
    SharedPreferences preferences;
    Context context;

    public NoteAdapter(Context context, List<Note> notes){
        this.context=context;
        this.inflater=LayoutInflater.from(context);
        this.notes=notes;
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
                    Note note=notes.get(getPosition());
                    if(db.deleteNote(note)){
                        Toast.makeText(inflater.getContext(),"Note \""+note.getTitle()+"\" deleted !",Toast.LENGTH_SHORT).show();
                        notes.remove(getPosition());
                        notifyItemRemoved(getPosition());
                    }
                } break;
                case R.id.cvItem: {
//                    Toast.makeText(inflater.getContext(),"Item clicked "+getPosition(),Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt(AllKeys.SP_NOTE_ID,notes.get(getPosition()).getId());
                    editor.commit();
                    context.startActivity(new Intent(context, NoteDetailsActivity.class));
                } break;
            }
        }
    }
}
