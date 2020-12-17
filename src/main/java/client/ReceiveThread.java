package client;

import org.json.JSONArray;
import org.json.JSONObject;
import server.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static server.MyConstants.requestRestartGame;

class ReceiveThread extends  Thread{

    BufferedWriter os = null;
    BufferedReader is = null;
    Socket socket = null;
    ServerConnection connection = null;
    public ReceiveThread( BufferedWriter os, BufferedReader is, Socket socket, ServerConnection connection){
        System.out.println("init connection");
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
                System.out.println("waiting receive server");
                responseLine = is.readLine();
                if(responseLine == null) return;
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
                            connection.clientgui.setVisible(false);
                            connection.showClientCaro((int) obj.get("turn"), (int) obj.get("your_side"),(String) obj.get("your_name"));
                        }
                        break;
                        case MyConstants.stick_flag_res: {
                            connection.clientCaro.tickToPanel((int) obj.get("turn"),(int) obj.get("i"), (int) obj.get("j"));
                        }
                        break;
                        case MyConstants.win:{
                            connection.clientCaro.tickToPanel((int) obj.get("turn"),(int) obj.get("i"), (int) obj.get("j"));
                            connection.clientCaro.setWinLose((String) obj.get("massage"));
                        }
                        break;
                        case MyConstants.restartGame:{
                            connection.restartClientCaro((int) obj.get("turn"), (int) obj.get("your_side"),(String) obj.get("your_name"));
                        }
                        break;
                        case MyConstants.requestRestartGame:{
                            int i = connection.clientCaro.sendMessageToDialog("Thông báo","Đối thủ muốn đánh lại !");

                            if(i == 0) {
                                JSONObject json = new JSONObject();
                                json.put("flag", MyConstants.restartGame);
                                connection.sendMessage(json.toString());
                                System.out.println(i);
                            }
                        }
                        break;
                        case MyConstants.outGame:{
                             connection.clientCaro.sendMessageToDialog("Thông báo"," Đối thủ đã thoát game");
                             connection.clientCaro.dispose();
                             connection.clientCaro = null;
                             connection.clientgui.setVisible(true);
                        }
                        break;
                    }
                    if (responseLine.equals("500 bye")) {
                        os.close();
                        is.close();
                        socket.close();
                        return;
                    }
                }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}