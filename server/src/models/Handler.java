package models;

import java.io.*;
import java.net.Socket;

class Handler implements Runnable {
    private Object lock;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String username;
    private String password;
    private boolean isLoggedIn;

    public Handler(Socket socket, String username, String password, boolean isLoggedIn, Object lock)
            throws IOException {
        this.socket = socket;
        this.username = username;
        this.password = password;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.isLoggedIn = isLoggedIn;
        this.lock = lock;

    }

    public Handler(String username, String password, boolean isLoggedIn, Object lock) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.lock = lock;
    }

    public void setIsLoggedIn(boolean IsLoggedIn) {
        this.isLoggedIn = IsLoggedIn;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đóng socket kết nối với client Được gọi khi người dùng offline
     */

    public boolean getIsLoggedIn() {
        return this.isLoggedIn;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public DataOutputStream getDos() {
        return this.dos;
    }


    @Override
    public void run() {

        while (true) {
            try {
                String message = dis.readUTF();

                if (message.equals("Log out")) {
                    dos.writeUTF("Safe to leave");
                    dos.flush();
                    System.out.println(this.username + " log out");
                    socket.close();
                    this.isLoggedIn = false;
                    Server.updateOnlineUsers();
                    break;
                }


                else if (message.equals("Text")) {
                    String receiver = dis.readUTF();
                    String content = dis.readUTF();

                    for (Handler client : Server.clients) {
                        if (client.getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Text");
                                client.getDos().writeUTF(this.username);
                                client.getDos().writeUTF(content);
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                }

                else if (message.equals("Emoji")) {
                    String receiver = dis.readUTF();
                    String emoji = dis.readUTF();

                    for (Handler client : Server.clients) {
                        if (client.getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Emoji");
                                client.getDos().writeUTF(this.username);
                                client.getDos().writeUTF(emoji);
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                }


                else if (message.equals("File")) {
                    // Đọc các header của tin nhắn gửi file
                    String receiver = dis.readUTF();
                    String filename = dis.readUTF();
                    int size = Integer.parseInt(dis.readUTF());
                    int bufferSize = 2048;
                    byte[] buffer = new byte[bufferSize];

                    for (Handler client : Server.clients) {
                        if (client.getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("File");
                                client.getDos().writeUTF(this.username);
                                client.getDos().writeUTF(filename);
                                client.getDos().writeUTF(String.valueOf(size));
                                while (size > 0) {
                                    // Gửi lần lượt từng buffer cho người nhận cho đến khi hết file
                                    dis.read(buffer, 0, Math.min(size, bufferSize));
                                    client.getDos().write(buffer, 0, Math.min(size, bufferSize));
                                    size -= bufferSize;
                                }
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}