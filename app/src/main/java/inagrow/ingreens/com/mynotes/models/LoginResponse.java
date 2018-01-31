package inagrow.ingreens.com.mynotes.models;

/**
 * Created by root on 31/1/18.
 */

public class LoginResponse {
    boolean status;
    boolean login;
    String token;
    String message;

    public boolean isStatus() {
        return status;
    }

    public boolean isLogin() {
        return login;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status=" + status +
                ", login=" + login +
                ", token='" + token + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
