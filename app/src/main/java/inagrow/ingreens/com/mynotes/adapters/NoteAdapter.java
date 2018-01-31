package inagrow.ingreens.com.mynotes.adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import inagrow.ingreens.com.mynotes.activities.NoteDetailsActivity;
import inagrow.ingreens.com.mynotes.apis.DbInterface;
import inagrow.ingreens.com.mynotes.models.DeleteNoteModel;
import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.NoteList;
import inagrow.ingreens.com.mynotes.models.UpdateNoteModel;
import inagrow.ingreens.com.mynotes.utils.AllKeys;
import inagrow.ingreens.com.mynotes.utils.AllUrls;

/**
 * Created by root on 11/1/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private static final String TAG = "NoteAdapter";


    LayoutInflater inflater;
    List<NoteList> notes;
    DbInterface db;
    SharedPreferences preferences;
    Context context;

    public NoteAdapter(Context context, List<NoteList> notes){
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
        NoteList note=notes.get(position);
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
                    Toast.makeText(context, "note deleted...", Toast.LENGTH_SHORT).show();
                    String token=preferences.getString("token","");
                    String note_id= String.valueOf(notes.get(getPosition()).getId());

                    deleteNote(token,note_id,getPosition());

                    //NoteList note=notes.get(getPosition());
//                    if(db.deleteNote(note)){
//                        Toast.makeText(inflater.getContext(),"Note \""+note.getTitle()+"\" deleted !",Toast.LENGTH_SHORT).show();
//                        notes.remove(getPosition());
//                        notifyItemRemoved(getPosition());
//                    }
                } break;
                case R.id.cvItem: {

                    //String title=tvTitle.getText().toString();
                    //String body=tvBody.getText().toString();
                    //String note_id= String.valueOf(notes.get(getPosition()).getId());
                    //String token=preferences.getString("token","");
                    //notes.get(getPosition()).getId();
                    //updateNote(token, note_id,title,body);
                    //context.startActivity(new Intent(context,NoteDetailsActivity.class));
                    NoteList note=notes.get(getPosition());
                    String json=new Gson().toJson(note);
//                    Log.e("Object to String ", "onClick: "+gson );
                    preferences.edit().putString("note",json).commit();
                    Intent intent =new Intent(context,NoteDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("title",notes.get(getPosition()).getTitle());
                    context.getApplicationContext().startActivity(intent);



//                    Toast.makeText(inflater.getContext(),"Item clicked "+getPosition(),Toast.LENGTH_SHORT).show();
                    /*SharedPreferences.Editor editor=preferences.edit();
                    editor.putInt(AllKeys.SP_NOTE_ID,notes.get(getPosition()).getId());
                    editor.commit();
                    context.startActivity(new Intent(context, NoteDetailsActivity.class));*/
                } break;
            }
        }
    }

    private void deleteNote(final String token, final String note_id, final int position){
        RequestQueue queue= Volley.newRequestQueue(context.getApplicationContext());
       /* ProgressDialog progressDialog=new ProgressDialog(context.getApplicationContext());
        progressDialog.setTitle("delete note");
        progressDialog.setMessage("deleting...");
        progressDialog.show();*/

        StringRequest request=new StringRequest(Request.Method.POST, AllUrls.SERVER+"api/delete.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DeleteNoteModel deleteNoteModel=new Gson().fromJson(response,DeleteNoteModel.class);
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
                        System.out.println("response(DeleteNote)=========="+response);
                        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@");
                        if (!(deleteNoteModel==null)){

                            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                            System.out.println("delete note model status=====" +deleteNoteModel.isStatus());
                            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");

                            notes.remove(position);
                            notifyItemRemoved(position);

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("token",token);
                params.put("id",note_id);
                return params;
            }
        };
        queue.getCache().clear();
        queue.add(request);

    }

}
