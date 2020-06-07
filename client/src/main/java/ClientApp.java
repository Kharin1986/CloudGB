import GUI.MyJFrame;
import GUI.MyJPanel;
import GUI.MyJTextField;
import GUI.TextFieldFocusListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
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

    public static void main(String[] args)   throws Exception {
        CountDownLatch networkStarter = new CountDownLatch(1); // самоблокировка, чтобы не создать еще один Network
        new Thread(() -> Network.getInstance().start(networkStarter)).start();
        networkStarter.await();

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
        //  List TODO наполнение листа
        DefaultListModel<String> dlm = new DefaultListModel<>();
        dlm.addElement("222.txt");
        dlm.addElement("222.txt");
        dlm.addElement("222.txt");
        dlm.addElement("222.txt");
        dlm.addElement("222.txt");
        JList serverFileList = new JList(dlm);
        serverFileList.setPrototypeCellValue("VERY_VERY_VERY_VERY_LONG_FILENAME");


        JScrollPane scrollPane = new JScrollPane(serverFileList);
        // Слушаем кнопки
        // AUTH
        btnEnter.addActionListener(e -> {
            if(auth(txtLogin.getText(),txtPass.getText())) panelAuth.setVisible(false);
        });
        //SEND
        btnSend.addActionListener(e -> {
            int ret = fileChooserSend.showDialog(null,"SEND");
            if (ret == JFileChooser.APPROVE_OPTION) {
                try {
                    sendFileToServer(fileChooserSend.getSelectedFile());
                } catch (Exception ex) {
                //    ex.printStackTrace();
                }
            }
        });
        //REFRESH
        btnRefresh.addActionListener(e -> {
          //  отображаем справа список файлов
            //TODO наполнение листа
            dlm.clear();
            dlm.addElement("333.txt");
        });
        //RECEIVE
        btnReceive.addActionListener(e -> {
            // скачиваем выбранный справа файл
            String filename = serverFileList.getSelectedValue().toString();
            downloadFileRequest(filename,Network.getInstance().getCurrentChannel());
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

       // panelList.add(new JScrollPane(serverFileList));
        panelList.add(scrollPane);
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
        frame.setVisible(true);
    }

//    private static Vector<String> getFileList() throws Exception{
//        CountDownLatch networkStarter = new CountDownLatch(1);
//        new Thread(() -> Network.getInstance().start(networkStarter)).start();
//        networkStarter.await();
//
//        ProtoCommandSender.sendCommand(Network.getInstance().getCurrentChannel(),future ->{
//            if (!future.isSuccess()) {
//                future.cause().printStackTrace();
//            }
//            if (future.isSuccess()) {
//                System.out.println("Команда успешно передана");
//            }
//        });
//        // ЗАГЛУШКА!!!!
//
//        Vector<String> list = new Vector<>();
//        List<String> fileList1 = Files.list(Paths.get(".","ClientName1"))
//                .filter(p -> !Files.isDirectory(p))
//                .map(p -> p.getFileName().toString())
//                .collect(Collectors.toList());
//        System.out.println(fileList1.size());
//        for (int i = 0; i < fileList1.size(); i++) {
//            System.out.println(fileList1.get(i));
//            list.add(fileList1.get(i));
//
//        }
//        return list;
//    }


    private static boolean auth(String login, String pass) {
        System.out.println("В систему вошел "+login);
        return true;
    }

    private static void sendFileToServer(File file) throws Exception{
        FileSender.sendFile(Paths.get(file.getPath()), Network.getInstance().getCurrentChannel(), future -> { // прописываем листенер, типа колбэк
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
            }
            if (future.isSuccess()) {
                System.out.println("Файл успешно передан");
            }
        });
    } // РАБОТАЕТ

    private static void downloadFileRequest(String filename, Channel outChannel) {
       // формируем байтбуфер, отправляем его в сеть, а парсим в ServerCommandReceiver
        // делаем массив байт из строки
        byte[] filenameBytes = ("/request " + filename).getBytes(StandardCharsets.UTF_8);
        // определяем размер буфера как 1(сигнальный байт)+4(выделено на int, здесь будет длина имени как число)+ длина имени файла
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + 4 + filenameBytes.length);
        // говорим, что отправляем именно команду (дописывая в буфер кодовый байт CMD_SIGNAL_BYTE
        buf.writeByte(CommandList.CMD_SIGNAL_BYTE);
        // дописываем в буфер длину имени файла (int
        buf.writeInt(filenameBytes.length);
        // дописываем байты имени в UTF_8
        buf.writeBytes(filenameBytes);
        // выдаем в канал
        outChannel.writeAndFlush(buf);
    } // РАБОТАЕТ

    private static void listFileRequest(String filename, Channel outChannel) {
        // формируем байтбуфер, отправляем его в сеть, а парсим в ServerCommandReceiver
        // делаем массив байт из строки
        byte[] filenameBytes = ("/getFileList ").getBytes(StandardCharsets.UTF_8);
        // определяем размер буфера как 1(сигнальный байт)+4(выделено на int, здесь будет длина имени как число)+ длина имени файла
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + 4 + filenameBytes.length);
        // говорим, что отправляем именно команду (дописывая в буфер кодовый байт CMD_SIGNAL_BYTE
        buf.writeByte(CommandList.CMD_SIGNAL_BYTE);
        // дописываем в буфер длину имени файла (int
        buf.writeInt(filenameBytes.length);
        // дописываем байты имени в UTF_8
        buf.writeBytes(filenameBytes);
        // выдаем в канал
        outChannel.writeAndFlush(buf);

// ПРИМЕР СТРИМА
        //        Vector<String> list = new Vector<>();
//        List<String> fileList1 = Files.list(Paths.get(".","ClientName1"))
//                .filter(p -> !Files.isDirectory(p))
//                .map(p -> p.getFileName().toString())
//                .collect(Collectors.toList());
//        System.out.println(fileList1.size());
//        for (int i = 0; i < fileList1.size(); i++) {
//            System.out.println(fileList1.get(i));
//            list.add(fileList1.get(i));
//
//        }
//        return list;
    } // ЭКСПЕРИМЕНТ

}


