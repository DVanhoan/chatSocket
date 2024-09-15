package view;

import javax.swing.*;
import java.awt.*;
public class RegisterForm {
    public JFrame jFrame;
    public JPanel container;
    public JTextField txtUsername;
    public JPasswordField txtPassword;
    public JButton btnExit, btnSignUp;
    public JCheckBox chkShowPassword;

    public JPanel getContentPanel() {
        container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        container.setLayout(new GridLayout(0,1));

        Font font = new Font("Arial", Font.PLAIN, 14);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblHello = new JLabel("Hello, Again");
        JLabel lblEmail = new JLabel("Username");
        txtUsername = new JTextField(20);
        JLabel lblPassword = new JLabel("Password");
        txtPassword = new JPasswordField(20);
        chkShowPassword = new JCheckBox("Show Password");
        btnSignUp = new JButton("Register");
        btnExit = new JButton("Exit");

        lblHello.setFont(font);
        lblEmail.setFont(font);
        lblPassword.setFont(font);
        btnExit.setFont(font);
        btnSignUp.setFont(font);
        txtUsername.setFont(font);
        txtPassword.setFont(font);
        chkShowPassword.setFont(font);

        container.add(lblHello);
        container.add(lblEmail);
        container.add(txtUsername);
        container.add(lblPassword);
        container.add(txtPassword);
        container.add(chkShowPassword);
        container.add(btnSignUp);
        container.add(btnExit);
        return container;
    }

    public void init() {
        jFrame = new JFrame("Register");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(400, 500);
        jFrame.setLocationRelativeTo(null);
        jFrame.setContentPane(getContentPanel());
        jFrame.setVisible(true);
    }
    public void setVisible(boolean b) {
        jFrame.setVisible(b);
    }
    public static void main(String[] args) {
        RegisterForm registerForm = new RegisterForm();
        registerForm.init();
    }


}
