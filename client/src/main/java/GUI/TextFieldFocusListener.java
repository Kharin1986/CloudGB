package GUI;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextFieldFocusListener implements FocusListener {

    private MyJTextField field;
    private String dummy;
    public TextFieldFocusListener(MyJTextField field,String dummy) {
        this.field = field;
        this.dummy = dummy;
    }

    @Override
    public void focusGained(FocusEvent e){
        if(field.getText().trim().equals(dummy)){
            field.setText("");
            field.setForeground(Color.BLACK);
        }
    }
    @Override
    public void focusLost(FocusEvent e){
        if(field.getText().trim().equals("")){
            field.setText(dummy);
            field.setForeground(Color.GRAY);
        }
    }
}
