package models;

import configs.Configs;
import services.UserService;
import utils.RSAHash;
import utils.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server{
    private ServerSocket serverSocket = null;
    private Object lock;
    public UserService userService;
    public static ArrayList<Handler> clients = new ArrayList<>();

    public Server(int port) throws Exception {
        userService = new UserService();

        try {
            lock = new Object();
            this.loadData();
            serverSocket = new ServerSocket(port);
            System.out.println("Server started at: " + Util.getIPv4());

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress().getHostAddress() + " connected");

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());


                String request = dis.readUTF();

                if (request.equals("Sign up")) {
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    String password_decrypted = RSAHash.decrypt(password);

                    if(!isExistUser(username)) {
                        boolean check = userService.register(username, password);
                        if (check) {
                            Handler newHandler = new Handler(socket, username, password_decrypted,false, lock);
                            clients.add(newHandler);
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    updateOnlineUsers();
                                }
                            };
                            thread.start();
                            dos.writeUTF("Sign up success");
                            dos.flush();
                        }
                        else {
                            dos.writeUTF("Sign up failed");
                            dos.flush();
                        }
                    }else {
                        dos.writeUTF("Username is already exist");
                        dos.flush();
                    }
                }

                if (request.equals("Log in")) {
                    String username_hash = dis.readUTF();
                    String password_hash = dis.readUTF();

                    String username = RSAHash.decrypt(username_hash);
                    String password = RSAHash.decrypt(password_hash);

                    if (isExistUser(username)){
                        for (Handler handler : clients) {
                            if (handler.getUsername().equals(username)) {
                                if (handler.getPassword().equals(password)) {
                                    handler.setSocket(socket);
                                    handler.setIsLoggedIn(true);

                                    dos.writeUTF("Log in success");
                                    dos.flush();

                                    Thread thread = new Thread(handler);
                                    thread.start();

                                    updateOnlineUsers();
                                }
                                else {
                                    dos.writeUTF("Password is not correct");
                                    dos.flush();
                                }
                                break;
                            }
                        }
                    }
                    else {
                        dos.writeUTF("This username is not exist");
                        dos.flush();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isExistUser(String username) {
        for (Handler handler : clients) {
            if (handler.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }


    public static void updateOnlineUsers() {
        StringBuilder message = new StringBuilder(" ");
        for (Handler client : clients) {
            if (client.getIsLoggedIn()) {
                message.append(",");
                message.append(client.getUsername());
            }
        }
        for (Handler client : clients) {
            if (client.getIsLoggedIn()) {
                try {
                    client.getDos().writeUTF("Online users");
                    client.getDos().writeUTF(message.toString());
                    client.getDos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadData(){
        try{
            ArrayList<User> users = userService.findAll();
            for (User user : users) {
                String username = user.getUsername();
                String password = RSAHash.decrypt(user.getPassword());
                try {
                    clients.add(new Handler(username, password,false, lock));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        try {
            Server server = new Server(Configs.SERVER_PORT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
