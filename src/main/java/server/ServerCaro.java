package server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ProcessClient extends Thread{
    ClientSocket clientSocket;
    ArrayList<ClientSocket> lsClient;
    public ProcessClient(ClientSocket _clientSocket, ArrayList<ClientSocket> _lsClient){
        super();
        this.clientSocket = _clientSocket;
        this.lsClient = _lsClient;
    }

    @Override
    public void run() {
        String line;
        try {
            while (true) {
                line = this.clientSocket.buffReader.readLine();
                System.out.println(line);
                line = line.trim();
                JSONObject json = new JSONObject(line);

                int flag = (int) json.get("flag");
                switch(flag){
                    case 1: // set name client
                        this.clientSocket.getUser().setName((String) json.get("name"));
                        break;
                    case 2: // get list user

                        ArrayList<User> arrayUser = new ArrayList();
                        this.lsClient.forEach(client -> {
                            arrayUser.add(client.getUser());
                        });
                        JSONArray lsUser = new JSONArray(arrayUser);
                        JSONObject lsUserObj = new JSONObject();
                        lsUserObj.put("to", "client-gui");
                        lsUserObj.put("list_user", lsUser);
                        System.out.println(lsUserObj.toString());
                        this.clientSocket.getBuffWriter().write(lsUserObj.toString());
                        this.clientSocket.getBuffWriter().newLine();
                        this.clientSocket.getBuffWriter().flush();
                        break;

                }

            }

//                String cmd = dataInputStream.readUTF().trim();
//                System.out.println("Message from client: " + cmd);
//                String result = "Error command";
//                if (cmd.equals("QUIT")) {
//                    end = true;
//                    result = "500 bye";
//                    System.out.println("Send message to client: " + result);
//                    dataOutputStream.writeUTF(result);
//                } else if (cmd.equals("HELO Server")) {
//                    result = "200 Hello Client";
//                    System.out.println("Send message to client: " + result);
//                    dataOutputStream.writeUTF(result);
//                    hello = true;
//                } else if (cmd.equals("file") && hello) {
//                    result = "210 file server OK";
//                    System.out.println("Send message to client: " + result);
//                    dataOutputStream.writeUTF(result);
//                    download = true;
//
//                } else if (download) {
//                    String fileName = cmd;
//                    System.out.println("Client needs file: " + fileName);
//                    File file = new File(folderName + "/" + fileName);
//                    if (!file.exists()) {
//                        result = "404 not found";
//                        System.out.println("Send message to client: " + result);
//                        dataOutputStream.writeUTF(result);
//
//                        download = false;
//                    } else {
//                        result = "FILE OK";
//                        System.out.println("Send message to client: " + result);
//                        dataOutputStream.writeUTF(result);
//
//                        fileName = dataInputStream.readUTF().trim();
//
//                        FileInputStream fileInputStream = new FileInputStream(file);
//                        long fileSize = file.length();
//                        dataOutputStream.writeLong(fileSize);
//                        byte[] sendBytes = new byte[bufferSize];
//                        int n;
//                        System.out.println("-> Sending file: " + fileName + ", size: " + fileSize + " Bytes...");
//                        while ((n = fileInputStream.read(sendBytes)) != -1) {
//                            dataOutputStream.write(sendBytes, 0, n);
//                        }
//                        System.out.println("-> Done!");
//                        fileInputStream.close();
//
//                        download = false;
//                    }
//                } else {
//                    System.out.println("Send message to client: " + result);
//                    dataOutputStream.writeUTF(result);
//                }
//                if (end) {
//
//                    dataInputStream.close();
//                    dataOutputStream.close();
//                    new_fd.close();
//                    break;
//                }
        }  catch (Exception ex) {
        }

//        System.out.println("Closing...");
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
        ArrayList<ClientSocket> listSocket = new ArrayList<ClientSocket>();
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
                listSocket.add(tmpClientSocket);
                new ProcessClient(tmpClientSocket, listSocket).start();

            }


            // Nhận được dữ liệu từ người dùng và gửi lại trả lời.


        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Sever stopped!");
    }
}
