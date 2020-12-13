package client;

import org.json.JSONObject;

import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientCaro extends JFrame implements ActionListener {

    Color background_cl = Color.white;
    Color x_cl = Color.red;
    Color y_cl = Color.blue;

    boolean tick[][] = new boolean[MyConstants.column + 2][MyConstants.row + 2];

    Container cn;
    JPanel pn, pn2;
    JLabel lb;
    JButton newGame_bt, undo_bt, exit_bt;

    public ServerConnection getConnection() {
        return connection;
    }

    public void setConnection(ServerConnection connection) {
        this.connection = connection;
    }

    private ServerConnection connection = null;
    private JButton b[][] = new JButton[MyConstants.column + 2][MyConstants.row + 2];

    public Boolean getYourTurn() {
        return yourTurn;
    }

    public void setYourTurn(Boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    private Boolean yourTurn = false;

    public Boolean getYourSide() {
        return yourSide;
    }

    public void setYourSide(Boolean yourSide) {
        this.yourSide = yourSide;
    }

    private Boolean yourSide = null;
    public ClientCaro(ServerConnection _connection, String s) {
        super(s);
        connection = _connection;
        cn = this.getContentPane();
        pn = new JPanel();
        pn.setLayout(new GridLayout(MyConstants.column,MyConstants.row));
        for (int i = 0; i <= MyConstants.column + 1; i++)
            for (int j = 0; j <= MyConstants.row + 1; j++) {
                b[i][j] = new JButton(" ");
                b[i][j].setActionCommand(i + " " + j);
                b[i][j].setBackground(background_cl);
                b[i][j].addActionListener(this);
                tick[i][j] = true;
            }
        for (int i = 1; i <= MyConstants.column; i++)
            for (int j = 1; j <= MyConstants.row; j++)
                pn.add(b[i][j]);
        lb = new JLabel("X Đánh Trước");
        newGame_bt = new JButton("Đánh lại");
        newGame_bt.setActionCommand("tryAgain");
        undo_bt = new JButton("Undo");
        exit_bt = new JButton("Exit");
        newGame_bt.addActionListener(this);
        undo_bt.addActionListener(this);
        exit_bt.addActionListener(this);
        exit_bt.setForeground(x_cl);
        cn.add(pn);
        pn2 = new JPanel();
        pn2.setLayout(new FlowLayout());
        pn2.add(lb);
        pn2.add(newGame_bt);
        pn2.add(undo_bt);
        pn2.add(exit_bt);
        cn.add(pn2,"North");
        this.setVisible(true);
        this.setSize(1400, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        undo_bt.setEnabled(false);
    }
    public void sendMessage(String title){
        int a = JOptionPane.showConfirmDialog(this,title);
        if(a == JOptionPane.YES_OPTION){}
    }


    public void tickToPanel(Boolean side, int i, int j){
        String strNextSide = side == MyConstants.O?"X":"O";
        String strSide = side == MyConstants.O?"O":"X";
        b[i][j].setText(strSide);
        tick[i][j] = false;


        if(side != yourSide){
            yourTurn = true;
        } else {
            yourTurn = false;
        }
        lb.setText("Lượt Của "+strNextSide);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "tryAgain") {
            this.connection.clientCaro = new ClientCaro(this.connection,"CODELEARN - GAME DEMO");
            this.dispose();
        }
        else
        if (e.getActionCommand() == "Undo") {
           // undo();
        }
        else
        if (e.getActionCommand() == "Exit") {
            System.exit(0);;
        }
        else {
            String s = e.getActionCommand();
            if(yourTurn){
                System.out.println(s);
                int k = s.indexOf(32);
                int i = Integer.parseInt(s.substring(0, k));
                int j = Integer.parseInt(s.substring(k + 1, s.length()));
                if (tick[i][j]) { //y,x

                    JSONObject json = new JSONObject();
                    json.put("flag", MyConstants.stick_flag);
                    if(yourSide ==  MyConstants.O)
                        json.put("your_side", MyConstants.O);
                    else json.put("your_side", MyConstants.X);
                    json.put("i", i);
                    json.put("j", j);
                    try {
                        connection.getBuffWriter().write(json.toString());
                        connection.getBuffWriter().newLine();
                        connection.getBuffWriter().flush();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } else {
                int a = JOptionPane.showConfirmDialog(this,"Chưa đến lượt của bạn!!!");
                if(a == JOptionPane.YES_OPTION){

                }
            }

        }
    }
}