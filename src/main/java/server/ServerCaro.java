package server;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;


class ProcessClient extends Thread{
    final int X = -1;
    final int O = 1;

    ClientSocket clientSocket;
    TreeMap<Integer, ClientSocket> mapClient;
    TreeMap<String,int[][]> caroTable = null;
    public ProcessClient(ClientSocket _clientSocket, TreeMap<Integer, ClientSocket> _mapClient,
                         TreeMap<String,int[][]> _caroTable){
        super();
        this.clientSocket = _clientSocket;
        this.mapClient = _mapClient;
        this.caroTable = _caroTable;
    }


    @Override
    public void run() {
        String line;
        try {
            System.out.println("The user thread is running");
            while (true) {
                line = this.clientSocket.buffReader.readLine();
                System.out.println(" line 35: "+line);
                if(line.equals("null")){
                    return;
                }
                line = line.trim();
                JSONObject json = new JSONObject(line);

                int flag = (int) json.get("flag");
                switch(flag){
                    case MyConstants.set_name_client: {
                        this.clientSocket.getUser().setName((String) json.get("name"));
                        sendListUserToAllUser();

                    }
                    break;
                    case MyConstants.get_list_user: {// get list user
                        sendListUserToAllUser();
                    }
                        break;
                    case MyConstants.start_game: {

                        User user = this.clientSocket.user;
                        int partnerId = (int) json.get("partnerId");

                        ClientSocket partnerClientSocket = this.mapClient.get(partnerId);
                        if(partnerClientSocket == null || partnerId == this.clientSocket.user.getId()) {
                            JSONObject obj = new JSONObject();
                            obj.put("flag", MyConstants.reques_invite_failed);
                            obj.put("message", "doi phuong da trong game hoac da thoat game");
                            System.out.println("fail"); sendToClient(this.clientSocket,obj.toString());
                            break;
                        }
                        else {
                            user.setPartnerId(partnerId);
                            String key = (this.clientSocket.getUser().getId() < this.clientSocket.getUser().getPartnerId())?
                                    this.clientSocket.getUser().getId()+ " " + this.clientSocket.getUser().getPartnerId():
                                    this.clientSocket.getUser().getPartnerId()+ " " +this.clientSocket.getUser().getId();
                            caroTable.put(key, new int[MyConstants.column + 2][MyConstants.row + 2]);
                            if (!partnerClientSocket.user.getInGame()) { // && partnerClientSocket.user.getId() != user.getId()
                                JSONObject obj = new JSONObject();
                                obj.put("flag", MyConstants.invite);
                                obj.put("partnerId", this.clientSocket.user.getId());
                                sendToClient(partnerClientSocket, obj.toString());
                            } else {
                                JSONObject obj = new JSONObject();
                                obj.put("flag", MyConstants.reques_invite_failed);
                                obj.put("message", "doi phuong da trong game");
                                sendToClient(this.clientSocket,obj.toString());
                            }
                        }

                    }
                    break;
                    case MyConstants.accepted: {
                        int partnerId = (int )json.get("partnerId");
                        ClientSocket Skuser = this.clientSocket;
                        ClientSocket SkPartner = this.mapClient.get(partnerId);
                        Skuser.user.setInGame(true);
                        Skuser.user.setPartnerId(partnerId);
                        SkPartner.user.setInGame(true);
                        SkPartner.user.setPartnerId(this.clientSocket.user.getId());

                        JSONObject obj1 = new JSONObject();
                        obj1.put("flag", MyConstants.start_caro);
                        obj1.put("your_side",MyConstants.O);
                        obj1.put("your_name",Skuser.user.getName());
                        obj1.put("message","bat dau game thoi nao!");
                        obj1.put("turn",MyConstants.X);
                        sendToClient(Skuser, obj1.toString());

                        JSONObject obj2 = new JSONObject();
                        obj2.put("flag", MyConstants.start_caro);
                        obj2.put("your_side",X);
                        obj2.put("your_name",SkPartner.user.getName());
                        obj2.put("message","bat dau game thoi nao!");
                        obj2.put("turn",MyConstants.X);
                        sendToClient(SkPartner, obj2.toString());
                        sendListUserToAllUser();

                    }
                    break;
                    case MyConstants.stick_flag: {
                        int i = (int)json.get("i");
                        int j = (int)json.get("j");
                        String key = (this.clientSocket.getUser().getId() < this.clientSocket.getUser().getPartnerId())?
                                this.clientSocket.getUser().getId()+ " " + this.clientSocket.getUser().getPartnerId():
                                this.clientSocket.getUser().getPartnerId()+ " " +this.clientSocket.getUser().getId();

                        int turn;
                        if((int) json.get("your_side") == MyConstants.O) {
                            turn = MyConstants.X;
                            caroTable.get(key)[i][j] = O;
                        }
                        else {
                            turn = MyConstants.O;
                            caroTable.get(key)[i][j] = X;
                        }
                        ClientSocket Skuser = this.clientSocket;
                        ClientSocket SkPartner = this.mapClient.get(this.clientSocket.user.getPartnerId());
                        if(checkWin(i,j,caroTable.get(key))){

                            JSONObject o = new JSONObject();
                            o.put("flag", MyConstants.win);
                            o.put("massage", "Bạn thắng rồi");
                            o.put("i",i);
                            o.put("j",j);
                            o.put("turn", turn);
                            sendToClient(Skuser, o.toString());

                            JSONObject o1 = new JSONObject();
                            o1.put("flag", MyConstants.win);
                            o1.put("massage", "Bạn thua rồi");
                            o1.put("i",i);
                            o1.put("j",j);
                            o1.put("turn", turn);
                            sendToClient(SkPartner, o1.toString());
                            break;
                        }
                        JSONObject obj1 = new JSONObject();
                        obj1.put("flag", MyConstants.stick_flag_res);
                        obj1.put("i",i);
                        obj1.put("j",j);
                        obj1.put("turn", turn);

                        sendToClient(Skuser, obj1.toString());

                        JSONObject obj2 = new JSONObject();
                        obj2.put("flag", MyConstants.stick_flag_res);
                        obj2.put("i",i);
                        obj2.put("j",j);
                        obj2.put("turn", turn);

                        sendToClient(SkPartner, obj2.toString());
                        break;

                    }
                    case MyConstants.exitCaro : {
                        int partnerId = this.clientSocket.getUser().getPartnerId();
                        if(this.clientSocket.getUser().getInGame()){
                            this.clientSocket.getUser().setPartnerId(-1);
                            this.clientSocket.getUser().setInGame(false);
                            this.mapClient.get(partnerId).getUser().setInGame(false);
                            this.mapClient.get(partnerId).getUser().setPartnerId(-1);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("flag",MyConstants.outGame);
                            System.out.println(partnerId);
                            sendToClient(
                                    this.mapClient.get(partnerId),
                                    jsonObject.toString());
                            sendListUserToAllUser();

                        }
                    }
                    break;
                    case MyConstants.exit : {
                        int partnerId = this.clientSocket.getUser().getPartnerId();

                        if(this.clientSocket.getUser().getInGame()){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("flag",MyConstants.outGame);


                            this.mapClient.get(partnerId).getUser().setInGame(false);
                            this.mapClient.get(partnerId).getUser().setPartnerId(-1);
                            sendToClient(
                                    this.mapClient.get(partnerId), jsonObject.toString());
                        }
                        this.mapClient.remove(this.clientSocket.getUser().getId());
                        this.clientSocket = null;
                        sendListUserToAllUser();
                        return;
                    }
                    case MyConstants.requestRestartGame:{
                        int parnerId = this.clientSocket.getUser().getPartnerId();
                        JSONObject o = new JSONObject();
                        o.put("flag", MyConstants.requestRestartGame);
                        sendToClient(this.mapClient.get(parnerId), o.toString());
                    }
                    break;
                    case MyConstants.restartGame:{
                        String key = (this.clientSocket.getUser().getId() < this.clientSocket.getUser().getPartnerId())?
                                this.clientSocket.getUser().getId()+ " " + this.clientSocket.getUser().getPartnerId():
                                this.clientSocket.getUser().getPartnerId()+ " " +this.clientSocket.getUser().getId();
                        this.caroTable.replace(key, new int[MyConstants.column + 2][MyConstants.row + 2]);


                        JSONObject obj1 = new JSONObject();
                        obj1.put("flag", MyConstants.restartGame);
                        obj1.put("your_side",MyConstants.O);
                        obj1.put("your_name",this.clientSocket.user.getName());
                        obj1.put("message","bat dau game thoi nao!");
                        obj1.put("turn",MyConstants.X);
                        sendToClient(this.clientSocket, obj1.toString());

                        JSONObject obj2 = new JSONObject();
                        obj2.put("flag", MyConstants.restartGame);
                        obj2.put("your_side",MyConstants.X);
                        obj2.put("your_name",this.mapClient.get(this.clientSocket.user.getPartnerId()).user.getName());
                        obj2.put("message","bat dau game thoi nao!");
                        obj2.put("turn",MyConstants.X);
                        sendToClient(this.mapClient.get(this.clientSocket.user.getPartnerId()), obj2.toString());

                    }
                    break;


                }

            }
//
        }
        catch (Exception ex) {
        }

//        System.out.println("Closing...");
    }
    public void sendListUserToAllUser(){
        Collection<ClientSocket> arrClientSocket =  this.mapClient.values();
        ArrayList<User> arrUser = new ArrayList<>();
        System.out.println(arrClientSocket.size());
        arrClientSocket.forEach(clientSocket1 -> {
            arrUser.add(clientSocket1.getUser());
        });

        JSONArray lsUser = new JSONArray(arrUser);
        JSONObject resUserObj = new JSONObject();
        resUserObj.put("flag", MyConstants.return_list_user);
        resUserObj.put("list_user", lsUser);
        System.out.println(resUserObj.toString());
        arrClientSocket.forEach(clientSocket1 -> {
            try {
                sendToClient(clientSocket1, resUserObj.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void sendToClient(ClientSocket clientSocket, String jsonString) throws IOException {
        clientSocket.buffWriter.write(jsonString);
        clientSocket.buffWriter.newLine();
        clientSocket.buffWriter.flush();
        System.out.println("send to client");
    }
    public Boolean checkWin(int i, int j, int[][] b){
        int d = 0, k = i, h;
        // kiểm tra hàng
        while (b[k][j] == b[i][j]) {
            d++;
            k++;
        }
        k = i - 1;
        while (b[k][j] == b[i][j]) {
            d++;
            k--;
        }
        if (d > 4) return true;
        d = 0; h = j;
        // kiểm tra cột
        while(b[i][h] == b[i][j]) {
            d++;
            h++;
        }
        h = j - 1;
        while(b[i][h] == b[i][j]) {
            d++;
            h--;
        }
        if (d > 4) return true;
        // kiểm tra đường chéo 1
        h = i; k = j; d = 0;
        while (b[i][j] == b[h][k]) {
            d++;
            h++;
            k++;
        }
        h = i - 1; k = j - 1;
        while (b[i][j] == b[h][k]) {
            d++;
            h--;
            k--;
        }
        if (d > 4) return true;
        // kiểm tra đường chéo 2
        h = i; k = j; d = 0;
        while (b[i][j] == b[h][k]) {
            d++;
            h++;
            k--;
        }
        h = i - 1; k = j + 1;
        while (b[i][j] == b[h][k]) {
            d++;
            h--;
            k++;
        }
        if (d > 4) return true;
        // nếu không đương chéo nào thỏa mãn thì trả về false.
        return false;
    }
}
public class ServerCaro {
    private static int SERVER_PORT = 8080;
    public static void main(String args[]) {

        ServerSocket listener = null;
        String line;
        BufferedReader is;
        BufferedWriter os;
        Socket socketOfClient = null;
        TreeMap<Integer, ClientSocket> mapClient = new TreeMap();
        Boolean b[][] = new Boolean[MyConstants.column + 2][MyConstants.row + 2];
        TreeMap<String,int[][]> caroTable  = new TreeMap<>();
        // Mở một ServerSocket tại cổng serverPort.
        // Chú ý bạn không thể chọn cổng nhỏ hơn 1023 nếu không là người dùng
        // đặc quyền (privileged users (root)).

        try {
            listener = new ServerSocket(SERVER_PORT);
            System.out.println("Server is waiting to accept user...");
            int countUser = 0;
            while(true){

                // Chấp nhận một yêu cầu kết nối từ phía Client.
                // Đồng thời nhận được một đối tượng Socket tại server.

                socketOfClient = listener.accept();
                System.out.println("Accept a client!");

                // Mở luồng vào ra trên Socket tại Server.
                is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
                os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
                User user = new User(countUser++,"{noname}");
                ClientSocket tmpClientSocket = new ClientSocket(socketOfClient,  user, is, os);
                mapClient.put(user.getId(), tmpClientSocket);
                new ProcessClient(tmpClientSocket, mapClient,caroTable).start();

            }

            // Nhận được dữ liệu từ người dùng và gửi lại trả lời

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Sever stopped!");
    }
}
