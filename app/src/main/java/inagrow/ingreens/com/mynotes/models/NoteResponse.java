package inagrow.ingreens.com.mynotes.models;

import java.util.List;

/**
 * Created by root on 31/1/18.
 */

public class NoteResponse {
    boolean status;
    List<Note> notes;

    public boolean isStatus() {
        return status;
    }

    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "NoteResponse{" +
                "status=" + status +
                ", notes=" + notes +
                '}';
    }
}
