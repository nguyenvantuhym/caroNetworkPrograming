package client;

import org.json.JSONArray;
import org.json.JSONObject;
import server.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class ReceiveThread extends  Thread{

    BufferedWriter os = null;
    BufferedReader is = null;
    Socket socket = null;
    ServerConnection connection = null;
    public ReceiveThread( BufferedWriter os, BufferedReader is, Socket socket, ServerConnection connection){
        this.connection = connection;
        this.socket = socket;
        this.os = os;
        this.is = is;
    }


    @Override
    public void run() {
        try {
            // Tạo luồng đầu ra tại client (Gửi dữ liệu tới server)
            String responseLine;
            while (true) {

                if (!((responseLine = is.readLine()) != null)) break;
                    System.out.println(responseLine);
                    JSONObject obj = new JSONObject(responseLine);
                    String to = (String) obj.get("to");
                        if(to.equals("client-gui")){
                            System.out.println(responseLine);
                            connection.sendListUserToClientGui(responseLine);
                        }
                        break;
                }
                if (responseLine.equals("500 bye")) {
                    os.close();
                    is.close();
                    socket.close();
                    return;
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}