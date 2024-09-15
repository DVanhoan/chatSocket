package controller;

import javax.swing.JOptionPane;
import configs.Configs;
import models.Client;
import view.AddressIP;
public class ClientController {
    private Client client;
    private AddressIP addressIP;
    public ClientController() {
        this.initialize();
    }


    public void initialize() {
        this.addressIP = new AddressIP();
        addressIP.init();
        this.addressIP.getAccepButton().addActionListener(e -> {
            String ip = addressIP.getTextField().getText();
            try {
                client = new Client(ip, Configs.SERVER_PORT);
                if (client.connect()) {
                    JOptionPane.showMessageDialog(null, "You entered: " + addressIP.getTextField().getText() + " connect success!");
                    new LoginController(client, client.getDos(), client.getDis());
                    addressIP.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "You entered: " + addressIP.getTextField().getText() + " connect failed!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "You entered: " + addressIP.getTextField().getText() + " connect failed!");
            }
        });
    }

    public static void main(String[] args) {
        new ClientController();
    }
}
