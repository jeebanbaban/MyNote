package inagrow.ingreens.com.mynotes.models;

/**
 * Created by root on 31/1/18.
 */

public class UpdateNoteResponse {
    boolean status;

    public boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "UpdateNoteResponse{" +
                "status=" + status +
                '}';
    }
}
