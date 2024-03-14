package client;

import server.BankGUI;
import server.ServerConnect;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author dzelazny
 */

public class Client  {
    public static void main(String args[]) throws IOException {
        new BankGUI();
    }
}
