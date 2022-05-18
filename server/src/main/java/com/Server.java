package com;

import com.messages.Message;
import com.messages.MessageType;
import com.messages.User;
import javafx.scene.Parent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;


public class Server {
    private final int PORT;
    private static ArrayList<User> users = new ArrayList<>();
    private static HashSet<ObjectOutputStream> writers = new HashSet<>();

    public Server(int port) {
        PORT = port;
    }

    public void run() {
        try {
            ServerSocket listener = new ServerSocket(PORT);
            System.out.println("The server is running.");
            while (true) {
                new Handler(listener.accept()).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Handler extends Thread {
        private Socket socket;
        private User user;
        private String name;
        private ObjectInputStream input;
        private OutputStream os;
        private ObjectOutputStream output;
        private InputStream is;

        public Handler(Socket socket) throws IOException {
            this.socket = socket;
        }

        public void run() {
            System.out.println("Attempting to connect a user...");
            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());

                Message loginResponse = (Message) input.readObject();
                if(!addUser(loginResponse)){
                    return;
                }
                writers.add(output);
                addToList();
                System.out.println("New user connected!");

                while (socket.isConnected()) {
                    Message userMsg = (Message) input.readObject();
                    if (userMsg != null) {
                        switch (userMsg.getType()) {
                            case USER:
                                write(userMsg);
                                break;
                            case CONNECTED:
                                addToList();
                                break;
                            case STATUS:
                                changeStatus(userMsg);
                                break;
                        }
                    }
                }

            } catch (SocketException socketException) {
                System.out.println("Socket Exception for user ");
                socketException.printStackTrace();
            } catch (Exception e) {
                System.out.println("Exception in run() method for user" + " | ");
                e.printStackTrace();
            }
             finally {
                closeConnections();
            }
        }

        private Message addToList() throws IOException {
            Message msg = new Message();
            msg.setType(MessageType.CONNECTED);
            write(msg);
            return msg;
        }

        private Message removeFromList() throws IOException {
            Message msg = new Message();
            msg.setType(MessageType.DISCONNECTED);
            msg.setUsers(users);
            write(msg);
            return msg;
        }

        private synchronized boolean addUser(Message firstmsg) throws IOException {
            for(int i=0;i<users.size();i++){
                if(Objects.equals(firstmsg.getName(), users.get(i).getName())){
                    return false;
                }
            }
            this.name = firstmsg.getName();
            user = new User();
            user.setName(name);
            user.setStatus("Online");
            user.setPicture(firstmsg.getPicture());
            users.add(user);

            Message msg = new Message();
            msg.setType(MessageType.SELF_CONNECTION);
            msg.setName(firstmsg.getName());
            msg.setMsg("Dołączono do Czatu!");
            msg.setPicture(firstmsg.getPicture());
            output.writeObject(msg);
            output.flush();
            return true;
        }

        private void write(Message msg) throws IOException {
            for (ObjectOutputStream writer : writers) {
                msg.setUsers(users);
                msg.setOnlineCount(users.size());
                writer.writeObject(msg);
                writer.reset();
            }
        }

        private Message changeStatus(Message msgUser) throws IOException {
            Message msg = new Message();
            msg.setName(user.getName());
            msg.setType(MessageType.STATUS);
            msg.setMsg("");
            User userObj = null;
            for(int i=0;i<users.size();i++) {
                if (users.get(i).getName() == name) {
                    userObj = users.get(i);
                }
            }
            userObj.setStatus(msgUser.getStatus());
            write(msg);
            return msg;
        }
        private synchronized void closeConnections()  {
            System.out.println("Disconnecting user: "+name);
            if (user != null){
                users.remove(user);
            }
            if (output != null){
                writers.remove(output);
            }
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                removeFromList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
