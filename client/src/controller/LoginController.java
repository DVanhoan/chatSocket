package controller;

import models.Client;
import utils.RSAHash;
import view.LoginForm;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.swing.JOptionPane;

public class LoginController {
    private Client client;
    private LoginForm loginFormGUI;
    DataOutputStream dos;
    DataInputStream dis;

    public LoginController(Client client, DataOutputStream dos, DataInputStream dis) {
        this.client = client;
        this.dos = dos;
        this.dis = dis;
        this.initalize();
    }
    public void initalize() {
        loginFormGUI = new LoginForm();
        loginFormGUI.init();
        loginFormGUI.btnLogin.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String username = loginFormGUI.txtUsername.getText();
                String password = loginFormGUI.txtPassword.getText();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter all information!");
                }
                else {
                    String result = null;
                    try {
                        result = login(username, password);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    if (result.equals("Log in success")) {
                        JOptionPane.showMessageDialog(null, result);
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ChatController chatform = new ChatController(username, dos, dis);
                                    chatform.setVisible(true);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        loginFormGUI.jFrame.dispose();
                    }
                    else {
                        JOptionPane.showMessageDialog(null, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        loginFormGUI.btnSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loginFormGUI.setVisible(false);
                new RegisterController(client, dos, dis);
            }
        });

        loginFormGUI.chkShowPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (loginFormGUI.chkShowPassword.isSelected()) {
                    loginFormGUI.txtPassword.setEchoChar((char)0);
                }
                else {
                    loginFormGUI.txtPassword.setEchoChar('*');
                }
            }
        });
    }

    public String login(String username, String password) {
        try{
            dos.writeUTF("Log in");
            dos.writeUTF(RSAHash.encrypt(username));
            dos.writeUTF(RSAHash.encrypt(password));
            dos.flush();
            String result = dis.readUTF();
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return "Network error: Log in fail";
        }
    }
}
