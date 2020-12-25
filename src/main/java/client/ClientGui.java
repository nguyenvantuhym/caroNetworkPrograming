package client;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ClientGui implements ActionListener {
    private ServerConnection connection;
    private  JFrame f;

    String column[]= {"ID", "NAME", "partnerId", "ingame"};
    String data[][] ={{"ID", "NAME", "partnerId", "ingame"}};
    DefaultTableModel model = null;

    private JTable table = new JTable();
    private JTextField inp;
    private JButton b;

    public ClientGui(ServerConnection connection, String titleFrame) throws IOException {
        this.connection = connection;
        f = new JFrame(titleFrame);
        table.setBounds(0,100, 400,300);
        f.add(table);
        setShow(true);
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(f,
                        "Bạn có thực sự muốn thoát khỏi trò chơi không", "Thoát game",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    JSONObject json = new JSONObject();
                    json.put("flag", MyConstants.exit);
                    //json.put("flag", );
                    try {
                        connection.sendMessage(json.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
//                    try {
////
////                        connection.getBuffWriter().close();
////                        connection.getBuffReader().close();
////                        connection.getSocketOfServer().close();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                }
            }
        });

    }

    public void setVisible(boolean visible){
        f.setVisible(visible);
    }

    public void setShow(Boolean visible) throws IOException {
        JSONObject req =new JSONObject();
        req.put("message","get_list_user");
        req.put("flag", MyConstants.get_list_user);
        connection.sendMessage(req.toString());
        String[][] data = new String[0][];

        inp = new JTextField("Enter id partner");
        inp.setBounds(50,40, 200,30);

        JButton b = new JButton("Play with");
        b.addActionListener(this);
        b.setActionCommand("start_game");
        b.setBounds(280,40,95,30);
        f.add(b);
        f.add(inp);
        f.setSize(400,400);
        f.setLayout(null);
        f.setVisible(visible);
    }

    public void sendMessage(int partnerId) throws IOException {
        int a = JOptionPane.showConfirmDialog(f,"Bạn nhận được lời mời đánh cờ Caro từ người dùng có ID :" +partnerId);
        if(a == JOptionPane.YES_OPTION){
           JSONObject object = new JSONObject();
           object.put("flag", MyConstants.accepted);
           object.put("partnerId",partnerId);
            BufferedWriter bw = connection.getBuffWriter();
            bw.write(object.toString());
            bw.newLine();
            bw.flush();

        }
    }
    public void sendMessageRequestFail(){
        int a = JOptionPane.showConfirmDialog(f,"Đối thủ không online hoặc từ chối yêu cầu của bạn");

    }
    public void setList(String names){

        JSONObject obj = new JSONObject(names);
        JSONArray a = obj.getJSONArray("list_user");
        String data [][] = new String[a.length() + 1][];
        data[0] = new String[]{"ID", "NAME", "partnerId", "ingame"};
        for(int i = 1; i < a.length() + 1; i++){
            JSONObject b = (JSONObject) a.get(i - 1);
            data[i] =  new String[]{Integer.toString((int)b.get("id")), (String) b.get("name"), Integer.toString((int)b.get("partnerId")), (String) b.get("inGame").toString() };

        }
        model = new DefaultTableModel(data,column);
        table.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == "start_game") {
            int id = -1;
            System.out.println("vantu");
            try {
                id = Integer.parseInt(inp.getText());
            } catch (NumberFormatException ignored) {
                JOptionPane.showConfirmDialog(f,"Bạn cần phải nhập chính xác ID của đối thủ muốn mời chơi");
                return;
            }

            JSONObject json = new JSONObject();
            json.put("flag", MyConstants.start_game);
            json.put("partnerId", id);
            System.out.println(json.toString());
            try {
                connection.sendMessage(json.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
