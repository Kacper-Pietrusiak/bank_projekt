package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class BankGUI extends JFrame {
    private JPanel MainPanel;
    private JButton zaloguj;
    private JTextField login;
    private JTextField haslo;

    public BankGUI(){
        setContentPane(MainPanel);
        setTitle("Aplikacja Bankowa Wiktor Derkacz i Kacper Pietrusiak");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600,600);
        setLocationRelativeTo(null);
        setVisible(true);
        zaloguj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String log = login.getText();
                String pas = haslo.getText();
                JOptionPane.showMessageDialog(BankGUI.this,"login: " + log + " " + "haslo: " + pas);
                ServerConnect server = new ServerConnect();
                try {
                    server.setEmail(log);
                    server.setPassword(pas);
                    setVisible(false);
                    server.serverConnect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public static String SetEmail(String log)
    {
        return log;
    }
}
