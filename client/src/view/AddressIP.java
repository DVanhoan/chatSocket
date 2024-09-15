package view;

import utils.Util;
import javax.swing.*;

import java.awt.*;

public class AddressIP {
    public JFrame frame;
    public JPanel container;
    public JButton accepButton;
    public JTextField textField;

    public AddressIP() {
        frame = new JFrame("Address IP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public JPanel getContent() {
        container = new JPanel();
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        container.setLayout(new GridLayout(0,1));

        Font font = new Font("Arial", Font.PLAIN, 14);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblAddress = new JLabel("Address IP");
        textField = new JTextField(Util.getIPv4());


        accepButton = new JButton("Accept");
        lblAddress.setFont(font);
        textField.setFont(font);

        container.add(lblAddress);
        container.add(textField);
        container.add(accepButton);

        lblAddress.setFont(font);
        textField.setFont(font);
        accepButton.setFont(font);

        container.add(lblAddress);
        container.add(textField);
        container.add(accepButton);
        return container;
    }

    public void init() {
        frame.add(getContent());
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public JButton getAccepButton() {
        return accepButton;
    }

    public void setAccepButton(JButton accepButton) {
        this.accepButton = accepButton;
    }

    public JTextField getTextField() {
        return textField;
    }

    public void setTextField(JTextField textField) {
        this.textField = textField;
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }

    public static void main(String[] args) {
        AddressIP addressIP = new AddressIP();
        addressIP.init();
    }


}
