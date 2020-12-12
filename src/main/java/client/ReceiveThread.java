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

    final int set_name_client = 1;
    final int get_list_user = 2;
    final int start_game = 3;
    final int invite = 4;
    final int reques_invite_failed = 50;
    final int start_caro = 7;

    final int accepted = 6;
    final int return_list_user = 5;

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
                    int flag = (int) obj.get("flag");
                    switch (flag) {
                        case return_list_user:
                            connection.sendListUserToClientGui(responseLine);
                            break;
                        case invite:
                            connection.sendinviteRequestToClient((int) obj.get("partnerId"));
                            break;
                        case start_caro:{
                            connection.clientgui.setDisable();
                            new ClientCaro("banj dang danh ");
                        }
                        break;
                    }
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