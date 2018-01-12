package inagrow.ingreens.com.mynotes.watchers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by root on 12/1/18.
 */

public class EditTextWatcher implements TextWatcher {

    EditText editText;

    public EditTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if( editText.getText().length()>0)
        {
            TextInputLayout textInputLayout=(TextInputLayout) editText.getParent().getParent();
            textInputLayout.setError(null);
        }
    }
}
