package inagrow.ingreens.com.mynotes.models;

import java.util.List;

/**
 * Created by root on 17/1/18.
 */

public class NoteListModel {

    boolean status;
    List<NoteList> notes;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<NoteList> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteList> noteLists) {
        this.notes = notes;
    }
}
