package com.chat;

import com.controllers.DivisionController;
import com.messages.Message;
import com.messages.MessageType;
import java.io.*;
import java.net.Socket;


public class Listener implements Runnable {

    public static String username;
    private static String picture;
    private static ObjectOutputStream oos;
    private Socket socket;
    public String hostname;
    public int port;
    private ObjectInputStream input;
    private DivisionController controller;

    public Listener(String hostname, int port, String username, String picture, DivisionController controller) {
        this.hostname = hostname;
        this.port = port;
        Listener.username = username;
        this.controller = controller;
        Listener.picture = picture;
    }

    public void run() {
        try {
            socket = new Socket(hostname, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            System.out.println("Could not Connect" + e);
        }
        System.out.println("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            connect();
            System.out.println("Socket connected!");
            while (socket.isConnected()) {
                Message message = (Message) input.readObject();

                if (message != null) {
                    switch (message.getType()) {
                        case SELF_CONNECTION:
                            controller.setImageLabel(message.getPicture());
                            controller.setUsernameLabel(message.getName());
                        case USER:
                            controller.addToChat(message);
                            break;
                        case CONNECTED:
                        case DISCONNECTED:
                        case STATUS:
                            controller.setUserList(message);
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void send(String msgText) throws IOException {
        Message msg = new Message();
        msg.setName(username);
        msg.setType(MessageType.USER);
        msg.setStatus("AWAY");
        msg.setMsg(msgText);
        msg.setPicture(picture);
        oos.writeObject(msg);
        oos.flush();
    }

    public static void sendStatusUpdate(String status) throws IOException {
        Message msg = new Message();
        msg.setName(username);
        msg.setType(MessageType.STATUS);
        msg.setStatus(status);
        msg.setPicture(picture);
        oos.writeObject(msg);
        oos.flush();
    }

    public void connect() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.CONNECTED);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
    }
}
