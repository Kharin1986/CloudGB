import GUI.MyJFrame;
import GUI.MyJPanel;
import GUI.MyJTextField;
import GUI.TextFieldFocusListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ClientApp {
    private static final String INPUT_LOGIN = "введите логин";
    private static final String INPUT_PASS = "введите пароль";
    private DefaultListModel<String> dlm = new DefaultListModel<String>();

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
        JButton btnRefresh = new JButton("REFRESH");
        //TextFields
        MyJTextField txtLogin = new MyJTextField(INPUT_LOGIN, 12);
        MyJTextField txtPass = new MyJTextField(INPUT_PASS, 12);
        // Chooser
        JFileChooser fileChooserSend = new JFileChooser();
        //Panels
        MyJPanel panelAuth = new MyJPanel("panelAuth", 440, 50);
        panelAuth.setPreferredSize(new Dimension(440, 50));
        panelAuth.setLayout(new FlowLayout(FlowLayout.LEFT));// Компоненты добавляются слева
        //
        MyJPanel panelControl = new MyJPanel("panelControl", 100,180);
        panelControl.setPreferredSize(new Dimension(100, 180));
        panelControl.setLayout(new FlowLayout(FlowLayout.CENTER));// Компоненты добавляются слева
        //
        MyJPanel panelList = new MyJPanel("panelList",340,180);
        panelList.setPreferredSize(new Dimension(340, 180));
        panelList.setLayout(new FlowLayout(FlowLayout.CENTER));// Компоненты добавляются слева
        //  List
        JList<String> serverFileList = null;
        try {
            serverFileList = new JList<String>(getFileList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serverFileList!=null)
        {serverFileList.setPrototypeCellValue("VERY_VERY_VERY_VERY_LONG_FILENAME");}
        // Слушаем кнопки
        btnEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(auth(txtLogin.getText(),txtPass.getText())) panelAuth.setVisible(false);
            }
        });
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooserSend.showDialog(null,"SEND");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        sendFileToServer(fileChooserSend.getSelectedFile());
                    } catch (Exception ex) {
                    //    ex.printStackTrace();
                    }
                }
            }
        });
        btnRefresh.addActionListener(e -> {
            try {
                getFileList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        btnReceive.addActionListener(e -> {
        });

        // Слушаем текстовые поля
        txtLogin.addFocusListener(new TextFieldFocusListener(txtLogin,INPUT_LOGIN));
        txtPass.addFocusListener(new TextFieldFocusListener(txtPass,INPUT_PASS));
        // наполняем панели элементами
        panelAuth.add(lblLogin);
        panelAuth.add(txtLogin);
        panelAuth.add(lblPass);
        panelAuth.add(txtPass);
        panelAuth.add(btnEnter);
        //
        panelList.add(new JScrollPane(serverFileList));
        //
        panelControl.add(btnRefresh);
        panelControl.add(btnReceive);
        panelControl.add(btnSend);
        // Наполняем фрейм панелями
        MyJFrame frame = new MyJFrame("Client", 450, 260, new BorderLayout(2, 2));
        frame.setResizable(false);
        frame.getContentPane().add(panelAuth, BorderLayout.NORTH);
        frame.getContentPane().add(panelControl,BorderLayout.WEST);
        frame.getContentPane().add(panelList,BorderLayout.EAST);
        frame.setVisible(true); // СТАВИТСЯ В САМОМ КОНЦЕ!!!
    }

    private static Vector<String> getFileList() throws Exception{
        CountDownLatch networkStarter = new CountDownLatch(1);
        new Thread(() -> Network.getInstance().start(networkStarter)).start();
        networkStarter.await();

        ProtoCommandSender.sendCommand(Network.getInstance().getCurrentChannel(),future ->{
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
            if (future.isSuccess()) {
                System.out.println("Команда успешно передана");
            }
        });
        // ЗАГЛУШКА!!!! TODO с сервера через ProtoOutHandler
        Vector<String> list = new Vector<>();

        List<String> fileList1 = Files.list(Paths.get(".","ClientName1"))
                .filter(p -> !Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        for (int i = 0; i < fileList1.size(); i++) {
            list.add(fileList1.get(i));
        }
        return list;
    }

    private static boolean auth(String login, String pass) {
        System.out.println("В систему вошел "+login);
        return true;
    }

    private static void sendFileToServer(File file) throws Exception{
        CountDownLatch networkStarter = new CountDownLatch(1);
        new Thread(() -> Network.getInstance().start(networkStarter)).start();
            networkStarter.await();

        ProtoFileSender.sendFile(Paths.get(file.getPath()), Network.getInstance().getCurrentChannel(), future -> { // прописываем листенер, типа колбэк
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
            if (future.isSuccess()) {
                System.out.println("Файл успешно передан");
            }
        });
    }
}


