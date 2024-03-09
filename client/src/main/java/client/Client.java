package client;

import server.ServerConnect;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author dzelazny
 */

public class Client  {
     static String email;
     static String password;







    public static void main(String args[]) throws IOException {

//        login();

        ServerConnect server = new ServerConnect();
        server.serverConnect();


    }



}
