import GUI.MyJFrame;
import GUI.MyJPanel;
import GUI.MyJTextField;
import GUI.TextFieldFocusListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

public class ClientApp {
    private static final String INPUT_LOGIN = "введите логин";
    private static final String INPUT_PASS = "введите пароль";
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
        JButton btnSend = new JButton("SEND");
        JButton btnReceive = new JButton("RECEIVE");
        //TextFields
        MyJTextField txtLogin = new MyJTextField(INPUT_LOGIN, 12);
        MyJTextField txtPass = new MyJTextField(INPUT_PASS, 12);
        // Chooser
        JFileChooser fileChooserSend = new JFileChooser();
        //Panels
        MyJPanel panel1 = new MyJPanel("panel1", 490, 50);
        panel1.setPreferredSize(new Dimension(490, 50)); // Панель не будет меньше 200/50!
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));// Компоненты добавляются слева
        MyJPanel panel2 = new MyJPanel("panel2", 450,50);
        panel2.setPreferredSize(new Dimension(490, 50)); // Панель не будет меньше 200/50!
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));// Компоненты добавляются слева
        // Слушаем кнопки
        btnEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(auth(txtLogin.getText(),txtPass.getText())) panel1.setVisible(false);
            }
        });
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooserSend.showDialog(null,"SEND");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    sendFileToServer(fileChooserSend.getSelectedFile());
                }
            }
        });
        // Слушаем текстовые поля
        txtLogin.addFocusListener(new TextFieldFocusListener(txtLogin,INPUT_LOGIN));
        txtPass.addFocusListener(new TextFieldFocusListener(txtPass,INPUT_PASS));
        // наполняем панели элементами
        panel1.add(lblLogin);
        panel1.add(txtLogin);
        panel1.add(lblPass);
        panel1.add(txtPass);
        panel1.add(btnEnter);
        //
        panel2.add(btnSend);
        // Наполняем фрейм панелями
        MyJFrame frame = new MyJFrame("Client", 500, 130, new BorderLayout(2, 2));
        frame.setResizable(false);
        frame.getContentPane().add(panel1, BorderLayout.NORTH);
        frame.getContentPane().add(panel2,BorderLayout.SOUTH);
        frame.setVisible(true); // СТАВИТСЯ В САМОМ КОНЦЕ!!!
    }

    private static boolean auth(String login, String pass) {
        //System.out.println(login+" "+pass);
        return true;
    }

    private static void sendFileToServer(File file) {
        try (Socket socket = new Socket("localhost", 8189)) { // создаем сокет
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(15); // записываем 15 - это значит, что дальше пойдет файл
            byte[] filenameBytes = file.getName().getBytes(); // получаем байтовый массив из имени файла
            out.writeInt(filenameBytes.length); // дописываем длину байтового массива имени
            out.write(filenameBytes); // дописываем само имя
            // итого: отправляем команду (15), длину имени файла, имя файла. хотим получить имя файла в сообщениях сервера
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


