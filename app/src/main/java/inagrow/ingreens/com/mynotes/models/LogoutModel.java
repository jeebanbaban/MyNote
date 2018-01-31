package inagrow.ingreens.com.mynotes.models;

/**
 * Created by root on 30/1/18.
 */

public class LogoutModel {
    boolean status;
    String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
