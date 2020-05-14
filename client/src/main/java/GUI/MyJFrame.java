package GUI;

import javax.swing.*;
import java.awt.*;

public class MyJFrame extends JFrame {
    // Основной конструктор
    public MyJFrame(String title, int width, int height)  {
        super(title);
        super.setSize(width,height);
        super.setLocationRelativeTo(null);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLayout(new FlowLayout());
        super.setVisible(true);
    }
    // конструктор для добавления компонента
    public MyJFrame(String title, int width, int height, Component comp) {
        this(title,width,height);
        super.getContentPane().add(comp);
    }
    // Конструктор для лейаута

    public MyJFrame(String title, int width, int height, LayoutManager layout) {
        super(title);
        super.setSize(width,height);
        super.setLocationRelativeTo(null);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLayout(layout);
    }
}
