package controller;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import static controller.ChatController.*;

public class Receiver implements Runnable {

    private DataInputStream dis;
    ChatController chatController;
    static String username;

    public Receiver(String username, DataInputStream dis, ChatController chatController) {
        this.dis = dis;
        this.username = username;
        this.chatController = chatController;
    }

    public static void saveChatHistoryForUser(String user, String message) {
        File file = new File(username + "_" + user + "_chat_history.xml");
        File file2 = new File(user + "_" + username + "_chat_history.xml");
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            Element rootElement;
    
            if (file.exists()) {
                doc = dBuilder.parse(file);
                rootElement = doc.getDocumentElement();
            } else {
                doc = dBuilder.newDocument();
                rootElement = doc.createElement("chatHistory");
                doc.appendChild(rootElement);
            }
    
            Element messageElement = doc.createElement("message");
            messageElement.setAttribute("sender", username);
            messageElement.setTextContent(message);
            rootElement.appendChild(messageElement);
    
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
            
            // Ghi vào file thứ hai
            if (!file2.equals(file)) {
                StreamResult result2 = new StreamResult(file2);
                transformer.transform(source, result2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    
    public static void loadChatHistoryForUser(String user) {
        File file = new File(username + "_" + user + "_chat_history.xml");
        if (!file.exists()) {
            file = new File(user + "_" + username + "_chat_history.xml");
        }
        if (file.exists()) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();
    
                NodeList nList = doc.getElementsByTagName("message");
    
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String sender = eElement.getAttribute("sender");
                        String message = eElement.getTextContent();
                        System.out.println(sender + ": " + message);
                        boolean isCurrentUser = sender.equals(username);
                        newMessage(sender, message, isCurrentUser);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    

    
    
    
    @Override
    public void run() {
        try {

            while (true) {
                String method = dis.readUTF();

                if (method.equals("Text")) {
                    // Nhận một tin nhắn văn bản
                    String sender = dis.readUTF();
                    String message = dis.readUTF();

                    // In tin nhắn lên màn hình chat với người gửi
                    newMessage(sender, message, false);
                }

                else if (method.equals("Emoji")) {
                    // Nhận một tin nhắn Emoji
                    String sender = dis.readUTF();
                    String emoji = dis.readUTF();

                    // In tin nhắn lên màn hình chat với người gửi
                    newEmoji(sender, emoji, false);
                }

                else if (method.equals("File")) {
                    // Nhận một file
                    String sender = dis.readUTF();
                    String filename = dis.readUTF();
                    int size = Integer.parseInt(dis.readUTF());
                    int bufferSize = 2048;
                    byte[] buffer = new byte[bufferSize];
                    ByteArrayOutputStream file = new ByteArrayOutputStream();

                    while (size > 0) {
                        dis.read(buffer, 0, Math.min(bufferSize, size));
                        file.write(buffer, 0, Math.min(bufferSize, size));
                        size -= bufferSize;
                    }

                    // In ra màn hình file đó
                    newFile(sender, filename, file.toByteArray(), false);

                }

                else if (method.equals("Online users")) {
                    // Nhận yêu cầu cập nhật danh sách người dùng trực tuyến
                    String[] users = dis.readUTF().split(",");
                    chatform.jComboBox.removeAllItems();

                    String chatting = chatform.labelReceiver.getText();

                    boolean isChattingOnline = false;

                    for (String user : users) {
                        if (user.equals(name) == false) {
                            // Cập nhật danh sách các người dùng trực tuyến vào ComboBox onlineUsers (trừ
                            // bản thân)
                            chatform.jComboBox.addItem(user);
                            if (chatController.chats.get(user) == null) {
                                JTextPane temp = new JTextPane();
                                temp.setFont(new Font("Arial", Font.PLAIN, 18));
                                temp.setEditable(false);
                                chats.put(user, temp);
                            }
                        }
                        if (chatting.equals(user)) {
                            isChattingOnline = true;
                        }
                    }

                    if (isChattingOnline == false) {
                        // Nếu người đang chat không online thì chuyển hướng về màn hình mặc định và
                        // thông báo cho người dùng
                        chatform.jComboBox.setSelectedItem(" ");
                        JOptionPane.showMessageDialog(null,
                                chatting + " is offline!\nYou will be redirect to default chat window");
                    } else {
                        chatform.jComboBox.setSelectedItem(chatting);
                    }
                    chatform.jComboBox.validate();
                }

                else if (method.equals("Safe to leave")) {
                    break;
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}