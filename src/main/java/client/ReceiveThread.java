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
                    int flag = (int) obj.get("flag");
                    switch (flag) {
                        case MyConstants.return_list_user:
                            connection.sendListUserToClientGui(responseLine);
                            break;
                        case MyConstants.invite:
                            connection.sendinviteRequestToClient((int) obj.get("partnerId"));
                            break;
                        case MyConstants.start_caro: {
                            connection.clientgui.setDisable();
                            connection.showClientCaro((Boolean) obj.get("your_turn"), (Boolean) obj.get("your_turn"),(String) obj.get("your_name"));
                        }
                        break;
                        case MyConstants.stick_flag_res: {
                            connection.clientCaro.tickToPanel((Boolean) obj.get("side"),(int) obj.get("i"), (int) obj.get("j"));
                        }
                        break;
                        case MyConstants.win:{
                            connection.clientCaro.tickToPanel((Boolean) obj.get("side"),(int) obj.get("i"), (int) obj.get("j"));
                            connection.clientCaro.sendMessage((String) obj.get("massage"));
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