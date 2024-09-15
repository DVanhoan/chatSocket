package models;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import configs.Configs;

public class Client extends Thread {
    private String host;
    private int port;
    private Socket socket = new Socket();
    private DataInputStream dis;
    private DataOutputStream dos;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean connect() {
        try {
            if(socket.isConnected()) {
                socket.close();
            }
            this.socket = new Socket(this.host, this.port);
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", Configs.SERVER_PORT);
        client.connect();
    }
}
