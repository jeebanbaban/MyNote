package inagrow.ingreens.com.mynotes.models;

/**
 * Created by root on 25/1/18.
 */

public class DeleteNoteModel {
    boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeleteNoteModel{" +
                "status=" + status +
                '}';
    }
}
