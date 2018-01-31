package inagrow.ingreens.com.mynotes.models;

/**
 * Created by root on 31/1/18.
 */

public class LogoutResponse {
    boolean status;
    String message;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
