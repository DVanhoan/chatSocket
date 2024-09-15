package view;

import javax.swing.*;
import java.awt.*;

public class Chatform {
    public JLabel labelReceiver;
    public JFrame frame;
    public JPanel mainPanel, southPanel, northPanel, emojisPanel, sendPanel;
    public JTextField txt_message;
    public JTextPane txt_messages;
    public JButton btnsend, btnFile, btnlogout;
    public JLabel loveIcon, sadIcon, hahaIcon, angryIcon, likeIcon, madIcon, smileIcon;
    public JScrollPane jScrollPane1, jScrollPane2;
    public JComboBox<String> jComboBox;



    public Chatform(String name) {
        init(name);
    }

    public void init(String name){
        frame = new JFrame();
        frame.setTitle("chat" + " - " + name);
        frame.setSize(500, 500);
        frame.add(getContainer());
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public JPanel getContainer(){
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        Font font = new Font("Arial", Font.BOLD, 18);


        northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(1, 2, 5, 5));
        labelReceiver = new JLabel("Receiver");
        labelReceiver.setFont(font);
        northPanel.add(labelReceiver);
        jComboBox = new JComboBox<>();
        Dimension preferredSize = new Dimension(200, 30);
        jComboBox.setPreferredSize(preferredSize);
        jComboBox.setFont(font);
        northPanel.add(jComboBox);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        txt_messages = new JTextPane();
        txt_messages.setEditable(false);
        jScrollPane2 = new JScrollPane(txt_messages);
        jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(jScrollPane2, BorderLayout.CENTER);

        southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(2, 1, 5, 5));



        emojisPanel = new JPanel();
        emojisPanel.setBorder(BorderFactory.createTitledBorder("Emojis"));
        emojisPanel.setLayout(new GridLayout(1, 6, 5, 5));
        jScrollPane1 = new JScrollPane(emojisPanel);

        loveIcon = new JLabel();
        sadIcon = new JLabel();
        hahaIcon = new JLabel();
        angryIcon = new JLabel();
        likeIcon = new JLabel();
        madIcon = new JLabel();
        smileIcon = new JLabel();

        addIcon(loveIcon, "src\\icon\\love.png", 30, 30);
        addIcon(sadIcon, "src\\icon\\sad.png", 30, 30);
        addIcon(hahaIcon, "src\\icon\\haha.png", 30, 30);
        addIcon(angryIcon, "src\\icon\\angry.png", 30, 30);
        addIcon(likeIcon, "src\\icon\\like.png", 30, 30);
        addIcon(madIcon, "src\\icon\\mad.png", 30, 30);
        addIcon(smileIcon, "src\\icon\\smile.png", 30, 30);



        emojisPanel.add(loveIcon);
        emojisPanel.add(sadIcon);
        emojisPanel.add(hahaIcon);
        emojisPanel.add(angryIcon);
        emojisPanel.add(likeIcon);
        emojisPanel.add(madIcon);
        emojisPanel.add(smileIcon);
        southPanel.add(jScrollPane1);

        txt_message = new JTextField(21);
        btnsend = new JButton("Send");
        btnFile = new JButton("File");
        btnlogout = new JButton("Logout");
        sendPanel = new JPanel();
        sendPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        sendPanel.add(txt_message);
        sendPanel.add(btnFile);
        sendPanel.add(btnsend);
        southPanel.add(sendPanel);

        txt_messages.setFont(font);
        txt_message.setFont(font);
        btnsend.setFont(font);
        btnFile.setFont(font);
        btnlogout.setFont(font);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        return mainPanel;
    }


    private void addIcon(JLabel label, String iconPath, int width, int height) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage();
        Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);

        label.setIcon(icon);
    }


    public static void main(String[] args) {
        new Chatform("hoan");
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
}