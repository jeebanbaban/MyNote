package inagrow.ingreens.com.mynotes.models;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by root on 10/1/18.
 */

public class Note {
    int id;
    String title;
    String body;
    int user_id;
    String created_at;
    String updated_at;

    public Note() {
        this.id = 0;
        this.title = "";
        this.body = "";
        this.user_id = 0;
        this.created_at="";
        this.updated_at="";
    }

    public Note(int id, String title, String body, int user_id) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user_id = user_id;
        this.created_at="";
        this.updated_at="";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user_id=" + user_id +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
