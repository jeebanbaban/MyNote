package inagrow.ingreens.com.mynotes.apis;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import inagrow.ingreens.com.mynotes.utils.AllKeys;

/**
 * Created by root on 10/1/18.
 */

public class MyDbms extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public MyDbms(Context context) {
        super(context, AllKeys.DB_NAME, null, AllKeys.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL("CREATE TABLE IF NOT EXISTS "+AllKeys.DB_TBL_USER+"(" +
                AllKeys.DB_TBL_USER_ID+" integer primary key autoincrement, " +
                AllKeys.DB_TBL_USER_NAME+" text, " +
                AllKeys.DB_TBL_USER_EMAIL+" text, " +
                AllKeys.DB_TBL_USER_PASSWORD+" text);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+AllKeys.DB_TBL_NOTE+"(" +
                AllKeys.DB_TBL_NOTE_ID+" integer primary key autoincrement, " +
                AllKeys.DB_TBL_NOTE_TITLE+" text, " +
                AllKeys.DB_TBL_NOTE_BODY+" text, " +
                AllKeys.DB_TBL_NOTE_USERID+" text," +
                "CONSTRAINT fk_users FOREIGN KEY ("+
                AllKeys.DB_TBL_NOTE_USERID+
                ") REFERENCES "+
                AllKeys.DB_TBL_USER+"("+AllKeys.DB_TBL_USER_ID+") ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db=db;
        if(oldVersion<newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+AllKeys.DB_TBL_USER);
            db.execSQL("DROP TABLE IF EXISTS "+AllKeys.DB_TBL_NOTE);
            onCreate(db);
        }
    }

    public boolean executeNonQuery(String sql){
        boolean flag=false;
        try {
            this.db = getWritableDatabase();
            db.execSQL(sql);
            flag=true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    public Cursor executeQuery(String sql){
        Cursor c=null;
        try {
            this.db = getReadableDatabase();
            c=db.rawQuery(sql,null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }
}
