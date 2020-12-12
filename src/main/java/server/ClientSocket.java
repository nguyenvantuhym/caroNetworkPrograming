package server;

import server.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public class ClientSocket {
    Socket socket;
    User user;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BufferedReader getBuffReader() {
        return buffReader;
    }

    public void setBuffReader(BufferedReader buffReader) {
        this.buffReader = buffReader;
    }

    public BufferedWriter getBuffWriter() {
        return buffWriter;
    }

    public void setBuffWriter(BufferedWriter buffWriter) {
        this.buffWriter = buffWriter;
    }

    BufferedReader buffReader;
    BufferedWriter buffWriter;
    public ClientSocket(Socket socket, User _user, BufferedReader _buffReader, BufferedWriter _buffWriter){
        this.socket = socket;
        this.user = _user;
        this.buffReader = _buffReader;
        this.buffWriter = _buffWriter;
    }
}