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
    public String name;
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
        name = enterName();
        if(name == null) return;
        clientgui = new ClientGui(this, name);
        //clientgui.setShow(true);

    }

    public void showClientCaro(int turn, int yourSide, String name){
        clientCaro = new ClientCaro(this,"Game caro - "+ name);
        clientCaro.setTurn(turn);
        clientCaro.setYourSide(yourSide);

        if(clientCaro.getTurn() == clientCaro.getYourSide()){
            String side = clientCaro.getYourSide() == MyConstants.X? "X": "O";

            int a = JOptionPane.showConfirmDialog(clientCaro,"bạn ("+side+") được đánh trước !!!");
            if(a == JOptionPane.YES_OPTION){

            }
            clientCaro.lb.setText("Đến Lượt bạn ("+side+")");
        } else {
            String _side = clientCaro.getYourSide() == MyConstants.X? "O": "X";
            int a = JOptionPane.showConfirmDialog(clientCaro,"Đối thủ ("+_side+") là người đánh trước !!!");
            if(a == JOptionPane.YES_OPTION){

            }
            clientCaro.lb.setText("Đến Lượt đối thủ ("+_side+")");
        }
    }

    public void restartClientCaro(int turn, int yourSide, String name){
        this.clientCaro.dispose();
        this.clientCaro = new ClientCaro(this,"Caro - " + name);
        clientCaro.setTurn(turn);
        clientCaro.setYourSide(yourSide);
        if(clientCaro.getTurn() ==  clientCaro.getYourSide()){
            String side = clientCaro.getYourSide() == MyConstants.X? "X": "O";
            int a = JOptionPane.showConfirmDialog(clientCaro,"bạn ("+side+") được đánh trước !!!");
            if(a == JOptionPane.YES_OPTION){

            }
            clientCaro.lb.setText("Đến Lượt bạn ("+side+")");
        } else {
            String _side = clientCaro.getYourSide() ==  MyConstants.X? "O": "X";
            int a = JOptionPane.showConfirmDialog(clientCaro,"Đối thủ ("+_side+") là người đánh trước !!!");
            if(a == JOptionPane.YES_OPTION){

            }
            clientCaro.lb.setText("Đến Lượt đối thủ ("+_side+")");
        }
    }

    public String enterName() throws IOException {
        JFrame f;
        f = new JFrame();
        String name = JOptionPane.showInputDialog(f,"Enter Name");

        if(name == null || (name != null && ("".equals(name))))
        {
            f.dispose();
            return null;
        }
        JSONObject req =new JSONObject();
        req.put("message","registerName");
        req.put("name", name);
        req.put("flag", MyConstants.set_name_client);
        sendMessage(req.toString());
        return name;
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
        ServerConnection _connection = new ServerConnection();
        ReceiveThread receiveThread = new ReceiveThread(
                _connection.getBuffWriter(),
                _connection.getBuffReader(),
                _connection.getSocketOfServer(),
                _connection
        );
        receiveThread.start();

    }
}
