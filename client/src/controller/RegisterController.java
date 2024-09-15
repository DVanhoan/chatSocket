package controller;

import models.Client;
import models.User;
import utils.RSAHash;
import view.RegisterForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RegisterController {
    private RegisterForm registerFormGUI;

    DataOutputStream dos;
    DataInputStream dis;

    public RegisterController(Client client, DataOutputStream dos, DataInputStream dis) {
        this.registerFormGUI = new RegisterForm();
        this.registerFormGUI.init();
        this.dos = dos;
        this.dis = dis;
        this.registerFormGUI.btnSignUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    String username = registerFormGUI.txtUsername.getText();
                    String password = registerFormGUI.txtPassword.getText();


                    if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter all information!");
                    } else {
                        String request = register(username, RSAHash.encrypt(password));
                        if (request.equals("Sign up success")) {
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JOptionPane.showMessageDialog(null, request);
                                        new LoginController(client, dos, dis);
                                        registerFormGUI.setVisible(false);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                            registerFormGUI.jFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, request);
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        this.registerFormGUI.btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new LoginController(client, dos, dis);
                registerFormGUI.setVisible(false);
            }
        });

        registerFormGUI.chkShowPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (registerFormGUI.chkShowPassword.isSelected()) {
                    registerFormGUI.txtPassword.setEchoChar((char)0);
                }
                else {
                    registerFormGUI.txtPassword.setEchoChar('*');
                }
            }
        });
    }


    public String register(String username, String password) {
        try{
            dos.writeUTF("Sign up");
            dos.writeUTF(username);
            dos.writeUTF(password);
            dos.flush();
            String result = dis.readUTF();
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            return "Network error: Log in fail";
        }
    }
}
