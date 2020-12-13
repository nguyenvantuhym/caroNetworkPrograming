package client;

import org.json.JSONObject;
import server.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnection {
    int SERVER_PORT = 8080;
    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Socket getSocketOfServer() {
        return socketOfServer;
    }

    public void setSocketOfServer(Socket socketOfServer) {
        this.socketOfServer = socketOfServer;
    }

    public BufferedWriter getBuffWriter() {
        return buffWriter;
    }

    public void setBuffWriter(BufferedWriter buffWriter) {
        this.buffWriter = buffWriter;
    }

    public BufferedReader getBuffReader() {
        return buffReader;
    }

    public void setBuffReader(BufferedReader buffReader) {
        this.buffReader = buffReader;
    }

    private String serverHost = "localhost";
    private Socket socketOfServer = null;
    private BufferedWriter buffWriter = null;
    private BufferedReader buffReader = null;
    ClientGui clientgui = null;
    ClientCaro clientCaro = null;

    public ServerConnection() throws IOException {
        this.socketOfServer = new Socket(serverHost, SERVER_PORT);
        this.buffWriter = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
        // Luồng đầu vào tại Client (Nhận dữ liệu từ server).
        this.buffReader = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
        clientgui = new ClientGui(this);
        receiveServer();
        enterName();
        clientgui.setShow(true);

    }

    public void showClientCaro(Boolean yourTurn, Boolean yourSide, String name){
        clientCaro = new ClientCaro(this,"Game caro - "+ name);
        clientCaro.setYourTurn(yourTurn);
        clientCaro.setYourSide(yourSide);

        if(clientCaro.getYourTurn()){
            String side = clientCaro.getYourSide()? "X": "O";
            int a = JOptionPane.showConfirmDialog(clientCaro,"bạn ("+side+") được đánh trước !!!");
            if(a == JOptionPane.YES_OPTION){

            }
            clientCaro.lb.setText("Đến Lượt bạn ("+side+")");
        } else {
            String _side = clientCaro.getYourSide()? "O": "X";
            int a = JOptionPane.showConfirmDialog(clientCaro,"Đối thủ ("+_side+") là người đánh trước !!!");
            if(a == JOptionPane.YES_OPTION){

            }
            clientCaro.lb.setText("Đến Lượt đối thủ ("+_side+")");
        }
    }

    public void enterName() throws IOException {
        JFrame f;
        f = new JFrame();
        String name = JOptionPane.showInputDialog(f,"Enter Name");

        if(name == null || (name != null && ("".equals(name))))
        {
            f.dispose();
            return;
        }
        JSONObject req =new JSONObject();
        req.put("message","registerName");
        req.put("name", name);
        req.put("flag", 1);
        sendMessage(req.toString());
    }

    public void receiveServer(){
        clientgui.setDisable();
        new ReceiveThread(buffWriter,buffReader, socketOfServer, this).start();
    }
    public void sendinviteRequestToClient(int partnerId) throws IOException {
        clientgui.sendMessage(partnerId);
    }

    public void sendMessage(String message) throws IOException {

        this.buffWriter.write(message);
        this.buffWriter.newLine();
        this.buffWriter.flush();
    }

    public void sendListUserToClientGui(String data){
        clientgui.setList(data);
    }

    public static void main(String[] args) throws IOException {
        new ServerConnection();
    }
}
