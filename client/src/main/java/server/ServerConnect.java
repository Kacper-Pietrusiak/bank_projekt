package server;

import banker.BankerActions;
import client.ClientActions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class ServerConnect {
    static String email;
    static String password;
    public static void main(String[] args) {

    }


    public static String login(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Podaj email: ");
        email = scan.nextLine();
        System.out.println("Podaj hasło: ");
        password = scan.nextLine();

        return "login " + email + " " + password;
    }
    public void serverConnect() throws IOException {
        String userId;
        String host = "localhost";
        int port = 0;
        try {
            port = Integer.parseInt("6666");
        } catch (NumberFormatException e) {
            System.out.println("Nieprawidłowy argument: port");
            System.exit(-1);
        }
        //Inicjalizacja gniazda klienckiego
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.out.println("Nieznany host.");
            System.exit(-1);
        } catch (ConnectException e) {
            System.out.println("Połączenie odrzucone.");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            System.exit(-1);
        }
        System.out.println("Połączono z " + clientSocket);

        //Deklaracje zmiennych strumieniowych
        String line = null;
        String id = null;
        String name = null;
        String role = null;
        BufferedReader brSockInp = null;
        BufferedReader brLocalInp = null;
        DataOutputStream out = null;
        ClientActions clientActions = new ClientActions();
        BankerActions bankerActions = new BankerActions();

        //Utworzenie strumieni
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            brSockInp = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            brLocalInp = new BufferedReader(
                    new InputStreamReader(System.in));

        } catch (IOException e) {
            System.out.println("Błąd przy tworzeniu strumieni: " + e);
            System.exit(-1);
        }

        try{
            String log = login();
            System.out.println("Wysyłam: " + log);
            out.writeBytes(log + '\n');
            out.flush();

            String res = brSockInp.readLine();
            String[] loginArgs = res.split(" ");
            id = loginArgs[0];
            role = loginArgs[1];
            name =  Arrays.toString(Arrays.copyOfRange(loginArgs, 2, loginArgs.length));

            System.out.println("Hello, " + name );

        }  catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            System.exit(-1);
        }



        //Pętla główna klienta

        while (true) {
            try {
                if(Objects.equals(role, "banker")){
                    line = bankerActions.actions();

                    while(Objects.equals(line, "reset")){
                        line = bankerActions.actions();
                    }
                }
                else{
                    line = clientActions.actions(id);

                    while(Objects.equals(line, "reset")){
                        line = clientActions.actions(id);
                    }
                }


                if (line != null) {
                    System.out.println("Wysyłam: " + line);
                    out.writeBytes(line + '\n');
                    out.flush();
                }
                if (line == null || "exit".equals(line)) {
                    System.out.println("Kończenie pracy...");
                    clientSocket.close();
                    System.exit(0);
                }
                String info = brSockInp.readLine();
                System.out.println("Otrzymano: " + info);
            } catch (IOException e) {
                System.out.println("Błąd wejścia-wyjścia: " + e);
                System.exit(-1);
            }
        }
    }
}