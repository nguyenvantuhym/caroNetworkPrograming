package client;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class ClientGui implements ActionListener {
    private ServerConnection connection;
    private  JFrame f;
    private JTable table;
    private JTextField inp;
    private JButton b;

    public ClientGui(ServerConnection connection) throws IOException {
        this.connection = connection;
        JSONObject req =new JSONObject();
        req.put("message","get_list_user");
        req.put("flag", 2);
        connection.sendMessage(req.toString());
        f = new JFrame();

        String[][] data = new String[0][];

        inp = new JTextField("Welcome to Javatpoint.");
        inp.setBounds(50,40, 200,30);

        JButton b = new JButton("Play with");
        b.setActionCommand("start_game");
        b.setBounds(280,40,95,30);
        f.add(b);
        f.add(inp);
        f.setSize(400,400);
        f.setLayout(null);
        f.setVisible(true);
    }
    public void setList(String names){
        JSONObject obj = new JSONObject(names);
        JSONArray a = obj.getJSONArray("list_user");
        String[][] data = new String[a.length()][];

        for(int i = 0; i < a.length(); i++){
            JSONObject b = (JSONObject) a.get(0);
            data[i] = new String[]{Integer.toString((int)b.get("id")), (String) b.get("name"), Integer.toString((int)b.get("partnerId"))};
        }
        String column[]= {"ID","NAME","partnerId"};
        table = new JTable(data,column);
        table.setBounds(0,100, 400,300);
        f.add(table);
        f.invalidate();
        f.validate();
        f.repaint();

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == "start_game") {

        }
    }
}
