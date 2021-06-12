package com.company;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Timer;

public class Main {

    public static void main(String[] args) throws SQLException, ParseException {
        GUI gui = new GUI();
        gui.setVisible(true);

        new Thread(new Runnable() {
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new GUI.timer(gui), 0, 30000);
            }
        }).start();

    }
}

