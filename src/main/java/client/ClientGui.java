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
    String data[][] ={{}};
    DefaultTableModel model = null;

    private JTable table = new JTable();
    private JTextField inp;
    private JButton b;

    public ClientGui(ServerConnection connection) throws IOException {
        this.connection = connection;
        f = new JFrame();
        table.setBounds(0,100, 400,300);
        f.add(table);
        setShow(true);
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(f,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    JSONObject json = new JSONObject();
                    json.put("flag", MyConstants.exit);
                    //json.put("flag", );
                    try {
                        connection.sendMessage(json.toString());
//                        connection.getBuffWriter().close();
                        connection.getBuffReader().close();
//                        connection.getSocketOfServer().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });

    }

    public void setDisable(){

        WindowEvent wev = new WindowEvent(f, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

        // this will hide and dispose the frame, so that the application quits by
        // itself if there is nothing else around.
        f.setVisible(false);
        System.out.println("logasdasd");
        f.dispose();
    }

    public void setShow(Boolean visible) throws IOException {
        JSONObject req =new JSONObject();
        req.put("message","get_list_user");
        req.put("flag", 2);
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
        int a = JOptionPane.showConfirmDialog(f,"banj co loi moi cho caro tu nguoi dung co id :" +partnerId);
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
    public void setList(String names){

        JSONObject obj = new JSONObject(names);
        JSONArray a = obj.getJSONArray("list_user");
        String data [][] = new String[a.length()][];
        for(int i = 0; i < a.length(); i++){
            JSONObject b = (JSONObject) a.get(i);
            data[i] =  new String[]{Integer.toString((int)b.get("id")), (String) b.get("name"), Integer.toString((int)b.get("partnerId")), (String) b.get("name") };

        }
        model = new DefaultTableModel(data,column);
        table.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == "start_game") {
            int id = Integer.parseInt(inp.getText());
            JSONObject json = new JSONObject();
            json.put("flag", MyConstants.start_game);
            json.put("partnerId", id);

            try {
                BufferedWriter bw = connection.getBuffWriter();
                bw.write(json.toString());
                bw.newLine();
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
