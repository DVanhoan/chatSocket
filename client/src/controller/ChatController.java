package controller;

import view.Chatform;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

import static controller.Receiver.loadChatHistoryForUser;

public class ChatController {
    Thread receiver;
    static Chatform chatform;
    DataOutputStream dos;
    DataInputStream dis;
    public static String name;
    public static HashMap<String, JTextPane> chats = new HashMap<String, JTextPane>();

    public ChatController(String username, DataOutputStream dos, DataInputStream dis) {
        this.dos = dos;
        this.dis = dis;
        this.name = username;
        init();
        receiver = new Thread(new Receiver (name ,dis, this));
        receiver.start();
    }

    public void init(){
        chatform = new Chatform(name);
        chatform.setVisible(true);


        chatform.loveIcon.addMouseListener(new IconListener("src\\icon\\love.png"));
        chatform.sadIcon.addMouseListener(new IconListener("src\\icon\\sad.png"));
        chatform.hahaIcon.addMouseListener(new IconListener("src\\icon\\haha.png"));
        chatform.angryIcon.addMouseListener(new IconListener("src\\icon\\angry.png"));
        chatform.likeIcon.addMouseListener(new IconListener("src\\icon\\like.png"));
        chatform.madIcon.addMouseListener(new IconListener("src\\icon\\mad.png"));
        chatform.smileIcon.addMouseListener(new IconListener("src\\icon\\smile.png"));



        chatform.btnFile.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int rVal = fileChooser.showOpenDialog(chatform.frame.getParent());
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    byte[] selectedFile = new byte[(int) fileChooser.getSelectedFile().length()];
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileChooser.getSelectedFile()));
                    // Đọc file vào biến selectedFile
                    bis.read(selectedFile, 0, selectedFile.length);

                    dos.writeUTF("File");
                    dos.writeUTF(chatform.labelReceiver.getText());
                    dos.writeUTF(fileChooser.getSelectedFile().getName());
                    dos.writeUTF(String.valueOf(selectedFile.length));

                    int size = selectedFile.length;
                    int bufferSize = 2048;
                    int offset = 0;

                    while (size > 0) {
                        dos.write(selectedFile, offset, Math.min(size, bufferSize));
                        offset += Math.min(size, bufferSize);
                        size -= bufferSize;
                    }
                    dos.flush();
                    bis.close();

                    // In ra màn hình file
                    newFile(name, fileChooser.getSelectedFile().getName(), selectedFile, true);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });



        chatform.btnsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("Text");
                    dos.writeUTF(chatform.labelReceiver.getText());
                    dos.writeUTF(chatform.txt_message.getText());
                    dos.flush();

                    Receiver.saveChatHistoryForUser(chatform.labelReceiver.getText(), chatform.txt_message.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                    newMessage("ERROR", "Network error!", true);
                }

                // In ra tin nhắn lên màn hình chat
                newMessage(name, chatform.txt_message.getText(), true);
                chatform.txt_message.setText("");
                chatform.btnsend.setEnabled(false);
            }
        });


        chatform.jComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    chatform.labelReceiver.setText((String) chatform.jComboBox.getSelectedItem());
                    if (chatform.txt_messages != chats.get(chatform.labelReceiver.getText())) {
                        chatform.txt_message.setText("");
                        chatform.txt_messages = chats.get(chatform.labelReceiver.getText());
                        chatform.jScrollPane2.setViewportView(chatform.txt_messages);
                        chatform.jScrollPane2.validate();
                        loadChatHistoryForUser(chatform.labelReceiver.getText());

                    }

                    if (chatform.labelReceiver.getText().isBlank()) {
                        chatform.btnsend.setEnabled(false);
                        chatform.btnFile.setEnabled(false);
                        chatform.txt_message.setEnabled(false);
                    } else {
                        chatform.btnsend.setEnabled(true);
                        chatform.btnFile.setEnabled(true);
                        chatform.txt_message.setEnabled(true);
                    }
                }
            }
        });

        chatform.txt_message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (chatform.txt_message.getText().isBlank() || chatform.labelReceiver.getText().isBlank()) {
                    chatform.btnsend.setEnabled(false);
                } else {
                    chatform.btnsend.setEnabled(true);
                }
            }
        });


        chatform.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    dos.writeUTF("Log out");
                    dos.flush();
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private static void autoScroll() {
        chatform.jScrollPane2.getVerticalScrollBar().setValue(chatform.jScrollPane2.getVerticalScrollBar().getMaximum());
    }

    public static void setUsername(String username) {
        name = username;
    }




    public static void newMessage(String username, String message, Boolean yourMessage) {

        StyledDocument doc;
        if (username.equals(name)) {
            doc = chats.get(chatform.labelReceiver.getText()).getStyledDocument();
        } else {
            doc = chats.get(username).getStyledDocument();
        }

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.green);
        } else {
            StyleConstants.setForeground(userStyle, Color.RED);
        }

        // In ra tên người gửi
        try {
            doc.insertString(doc.getLength(), username + ": ", userStyle);
        } catch (BadLocationException e) {
        }

        Style messageStyle = doc.getStyle("Message style");
        if (messageStyle == null) {
            messageStyle = doc.addStyle("Message style", null);
            StyleConstants.setForeground(messageStyle, Color.BLACK);
            StyleConstants.setBold(messageStyle, false);
        }

        // In ra nội dung tin nhắn
        try {
            doc.insertString(doc.getLength(), message + "\n", messageStyle);
        } catch (BadLocationException e) {
        }

        autoScroll();
    }

    public static void newEmoji(String username, String emoji, Boolean yourMessage) {

        StyledDocument doc;
        if (username.equals(name)) {
            doc = chats.get(chatform.labelReceiver.getText()).getStyledDocument();
        } else {
            doc = chats.get(username).getStyledDocument();
        }

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.green);
        } else {
            StyleConstants.setForeground(userStyle, Color.red);
        }

        // In ra màn hình tên người gửi
        try {
            doc.insertString(doc.getLength(), username + ": ", userStyle);
        } catch (BadLocationException e) {
        }

        Style iconStyle = doc.getStyle("Icon style");
        if (iconStyle == null) {
            iconStyle = doc.addStyle("Icon style", null);
        }

        StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));

        // In ra màn hình Emoji
        try {
            doc.insertString(doc.getLength(), "invisible text", iconStyle);
        } catch (BadLocationException e) {
        }

        // Xuống dòng
        try {
            doc.insertString(doc.getLength(), "\n", userStyle);
        } catch (BadLocationException e) {
        }

        autoScroll();
    }



    static void newFile(String username, String filename, byte[] file, Boolean yourMessage) {

        StyledDocument doc;
        String window = null;
        if (username.equals(name)) {
            window = chatform.labelReceiver.getText();
        } else {
            window = username;
        }
        doc = chats.get(window).getStyledDocument();

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.green);
        } else {
            StyleConstants.setForeground(userStyle, Color.red);
        }

        try {
            doc.insertString(doc.getLength(), username + ": ", userStyle);
        } catch (BadLocationException e) {
        }

        Style linkStyle = doc.getStyle("Link style");
        if (linkStyle == null) {
            linkStyle = doc.addStyle("Link style", null);
            StyleConstants.setForeground(linkStyle, Color.BLUE);
            StyleConstants.setUnderline(linkStyle, true);
            StyleConstants.setBold(linkStyle, true);
            linkStyle.addAttribute("link", new HyberlinkListener(filename, file, chatform.frame));
        }

        if (chats.get(window).getMouseListeners() != null) {
            // Tạo MouseListener cho các đường dẫn tải về file
            chats.get(window).addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    Element ele = doc.getCharacterElement(chatform.txt_messages.viewToModel(e.getPoint()));
                    AttributeSet as = ele.getAttributes();
                    HyberlinkListener listener = (HyberlinkListener) as.getAttribute("link");
                    if (listener != null) {
                        listener.execute();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

            });
        }

        // In ra đường dẫn tải file
        try {
            doc.insertString(doc.getLength(), "<" + filename + ">", linkStyle);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        // Xuống dòng
        try {
            doc.insertString(doc.getLength(), "\n", userStyle);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        autoScroll();
    }



    class IconListener extends MouseAdapter {
        String emoji;

        public IconListener(String emoji) {
            this.emoji = emoji;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (chatform.txt_message.isEnabled() == true) {

                try {
                    dos.writeUTF("Emoji");
                    dos.writeUTF(chatform.labelReceiver.getText());
                    dos.writeUTF(this.emoji);
                    dos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    newMessage("ERROR", "Network error!", true);
                }

                // In Emoji lên màn hình chat với người nhận
                newEmoji(name, this.emoji, true);
            }
        }
    }






    public static void main(String[] args) {
        new ChatController("hoan", null, null);
    }


    public void setVisible(boolean b) {
        if (b) {
            this.chatform.frame.setVisible(true);
        } else {
            this.chatform.frame.setVisible(false);
        }
    }
}