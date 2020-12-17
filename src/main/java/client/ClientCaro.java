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
    public Boolean gameOver = false;

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

    public int getTurn() {
        return turn;
    }

    public void setTurn(int yourTurn) {
        this.turn = yourTurn;
    }

    private int turn;

    public int getYourSide() {
        return yourSide;
    }

    public void setYourSide(int yourSide) {
        this.yourSide = yourSide;
    }

    private int yourSide;
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
        newGame_bt = new JButton("Chơi lại");
        newGame_bt.setActionCommand("tryAgain");
        undo_bt = new JButton("Undo");
        exit_bt = new JButton("Exit");
        newGame_bt.setEnabled(false);
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
        JFrame f = this;
        this.addWindowListener(new java.awt.event.WindowAdapter() {
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

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
    }
    public int sendMessageToDialog(String title, String content){
        return JOptionPane.showConfirmDialog(this,  content, title,JOptionPane.OK_OPTION,JOptionPane.CANCEL_OPTION);
    }
    public void setWinLose(String message){
        gameOver = true;
        sendMessageToDialog("Thông báo",message);
        newGame_bt.setEnabled(true);
    }

    public void tickToPanel(int turn, int i, int j){
        setTurn(turn);
        String strNextTurn = turn == MyConstants.O?"O":"X";
        String strTurn = turn == MyConstants.O?"X":"O";
        b[i][j].setText(strTurn);
        tick[i][j] = false;

        String tmp;
        if(turn == yourSide){
           tmp = " bạn ";
        } else {
            tmp = " đối thủ ";
        }

        lb.setText("Lượt Của" + tmp + strNextTurn);
    }
    public void restartGame(Boolean yourTurn, Boolean yourSide, String name){
        this.connection.clientCaro = new ClientCaro(this.connection,name);
        this.dispose();
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "tryAgain") {
            JSONObject json = new JSONObject();
            json.put("flag",MyConstants.requestRestartGame);
            try {
                connection.sendMessage(json.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            sendMessageToDialog("Thông báo","Đã gửi yêu cầu restart game");
        }
        else
        if (e.getActionCommand() == "Undo") {
           // undo();
        }
        else
        if (e.getActionCommand() == "Exit") {
            JSONObject json = new JSONObject();
            json.put("flag", MyConstants.exit);
            try {
                connection.sendMessage(json.toString());

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


        }
        else {
            String s = e.getActionCommand();
            if(turn == getYourSide() && !gameOver){
                System.out.println(s);
                int k = s.indexOf(32);
                int i = Integer.parseInt(s.substring(0, k));
                int j = Integer.parseInt(s.substring(k + 1, s.length()));
                if (tick[i][j]) { //y,x

                    JSONObject json = new JSONObject();
                    json.put("flag", MyConstants.stick_flag);
                    json.put("your_side", getYourSide());
//                    if(yourSide ==  MyConstants.O)
//                        json.put("your_side", MyConstants.O);
//                    else json.put("your_side", MyConstants.X);
                    json.put("i", i);
                    json.put("j", j);
                    try {
                        connection.sendMessage(json.toString());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } else {

                if(!gameOver){
                     int a = JOptionPane.showConfirmDialog(this,"Chưa đến lượt của bạn!!!");
                    if(a == JOptionPane.YES_OPTION){

                    }
                }
                else {
                    int a = JOptionPane.showConfirmDialog(this,"Trò chơi đã kết thúc bạn có muốn chơi lại!!!");
                    if(a == JOptionPane.YES_OPTION){
                        JSONObject json = new JSONObject();
                        json.put("flag",MyConstants.requestRestartGame);
                        try {
                            connection.sendMessage(json.toString());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        sendMessageToDialog("Thông báo","Đã gửi yêu cầu restart game");
                    }
                }

            }

        }
    }
}