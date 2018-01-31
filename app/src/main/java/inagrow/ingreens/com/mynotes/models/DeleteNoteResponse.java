package inagrow.ingreens.com.mynotes.models;

/**
 * Created by root on 31/1/18.
 */

public class DeleteNoteResponse {
    boolean status;

    public boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "DeleteNoteResponse{" +
                "status=" + status +
                '}';
    }
}
