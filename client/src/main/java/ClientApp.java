import GUI.MyJFrame;
import GUI.MyJPanel;
import GUI.MyJTextField;
import GUI.TextFieldFocusListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientApp {
    public static final String INPUT_LOGIN = "введите логин";
    public static final String INPUT_PASS = "введите пароль";
    public static void main(String[] args)   {
        createGUI();
    }


    private static void createGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        // Labels
        JLabel lblLogin = new JLabel("Login");
        JLabel lblPass = new JLabel("Pass");
        // Buttons
        JButton btnEnter = new JButton("Enter");
        //TextFields
        MyJTextField txtLogin = new MyJTextField(INPUT_LOGIN, 12);
        MyJTextField txtPass = new MyJTextField(INPUT_PASS, 12);
        // Слушаем текстовые поля
        txtLogin.addFocusListener(new TextFieldFocusListener(txtLogin,INPUT_LOGIN));
        txtPass.addFocusListener(new TextFieldFocusListener(txtPass,INPUT_PASS));

        //Panels
        MyJPanel panel1 = new MyJPanel("panel1", 450, 100);
        panel1.setPreferredSize(new Dimension(490, 50)); // Панель не будет меньше 200/50!
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));// Компоненты добавляются слева
        // Слушаем кнопки
        btnEnter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel1.setVisible(false);
            }
        });
        // наполняем панели элементами
        panel1.add(lblLogin);
        panel1.add(txtLogin);
        panel1.add(lblPass);
        panel1.add(txtPass);
        panel1.add(btnEnter);
        // Наполняем фрейм панелями
        MyJFrame frame = new MyJFrame("Client", 500, 200, new BorderLayout(2, 2));
        frame.setResizable(false);
        frame.getContentPane().add(panel1, BorderLayout.NORTH);
        frame.setVisible(true); // СТАВИТСЯ В САМОМ КОНЦЕ!!!
    }
}


