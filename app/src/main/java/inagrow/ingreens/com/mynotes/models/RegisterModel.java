package inagrow.ingreens.com.mynotes.models;

import java.util.List;

/**
 * Created by root on 17/1/18.
 */

public class RegisterModel {

    boolean status;
    String message;
    List<RegisterData> registerDataList;

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

    public List<RegisterData> getRegisterDataList() {
        return registerDataList;
    }

    public void setRegisterDataList(List<RegisterData> registerDataList) {
        this.registerDataList = registerDataList;
    }
}
