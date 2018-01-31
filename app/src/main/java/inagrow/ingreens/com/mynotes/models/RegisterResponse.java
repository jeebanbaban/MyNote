package inagrow.ingreens.com.mynotes.models;

/**
 * Created by root on 31/1/18.
 */

public class RegisterResponse {
    boolean status;
    String message;
    User user;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", user=" + user +
                '}';
    }
}
