package inagrow.ingreens.com.mynotes.apis;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import inagrow.ingreens.com.mynotes.models.Note;
import inagrow.ingreens.com.mynotes.models.User;
import inagrow.ingreens.com.mynotes.utils.AllKeys;

/**
 * Created by root on 10/1/18.
 */

public class DbInterface {
    MyDB dbms;

    public DbInterface(Context context) {
        MyDB d1 = new MyDB(context);
        this.dbms=d1;
    }

    public boolean insertUser(User user){
        return dbms.executeNonQuery("INSERT INTO "+
                AllKeys.DB_TBL_USER+"('"+
                AllKeys.DB_TBL_USER_NAME+"','"+
                AllKeys.DB_TBL_USER_EMAIL+"',"+
                AllKeys.DB_TBL_USER_PASSWORD+") VALUES('"+
                user.getName() +"','"+user.getEmail()+"','"+user.getPassword()+"');");
    }

    public User getUser(int id){
        User user=new User();
        Cursor c=dbms.executeQuery("SELECT * FROM "+
                AllKeys.DB_TBL_USER+" WHERE "+AllKeys.DB_TBL_USER_ID+"="+
                id);
        c.moveToFirst();
        if(!c.isAfterLast()){
            user.setId(c.getInt(0));
            user.setName(c.getString(1));
            user.setEmail(c.getString(2));
        }
        return user;
    }

    public User getUser(String email, String password){
        User user=new User();
        Cursor c=dbms.executeQuery("SELECT * FROM "+
                AllKeys.DB_TBL_USER+" WHERE "+AllKeys.DB_TBL_USER_EMAIL+"='"+
                email+"' AND "+AllKeys.DB_TBL_USER_PASSWORD+"='"+password+"'");
        c.moveToFirst();
        if(!c.isAfterLast()){
            user.setId(c.getInt(0));
            user.setName(c.getString(1));
            user.setEmail(c.getString(2));
        }
        return user;
    }

    public boolean insertNote(Note note){
        return dbms.executeNonQuery("INSERT INTO "+
                AllKeys.DB_TBL_NOTE+"('"+
                AllKeys.DB_TBL_NOTE_TITLE+"','"+
                AllKeys.DB_TBL_NOTE_BODY+"',"+
                AllKeys.DB_TBL_NOTE_USERID+") VALUES('"+
                note.getTitle() +"','"+note.getBody()+"',"+note.getUser_id()+");");
    }

    public boolean deleteNote(Note note){
        return dbms.executeNonQuery("DELETE FROM "+
                AllKeys.DB_TBL_NOTE+" WHERE "+
                AllKeys.DB_TBL_NOTE_ID+"="+
                note.getId()+" AND "+AllKeys.DB_TBL_NOTE_USERID+"='"+note.getUser_id()+"'");
    }

    public List<Note> getNotes(int user_id){
        List<Note> notes=new ArrayList<Note>();

        Cursor c=dbms.executeQuery("SELECT * FROM "+
                AllKeys.DB_TBL_NOTE+" WHERE "+AllKeys.DB_TBL_NOTE_USERID+"="+user_id+"");
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            Note note=new Note(c.getInt(0),c.getString(1),c.getString(2),c.getInt(3));
            notes.add(note);
        }
        return notes;
    }
}
