package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerConnect {
    static String email;
    static String password;
    public static void main(String[] args) {

    }

    public String actions(String id) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Wpisz numer operacji którą chcesz wykonać: ");
        System.out.println("1) Przlew na inne konto ");
        System.out.println("2) Sprawdź stan konta ");
        System.out.println("3) Wypłać środki ");
        System.out.println("4) Wpłać środki ");

        String action = scan.nextLine();

        switch (action) {
            case "1" : {
                System.out.println("Na jakie konto chcesz przelac środki?");
                String account = scan.nextLine();
                System.out.println("ile chcesz przesłać?");
                String amount = scan.nextLine();

                if(new Integer(amount) < 0){
                    return "Błąd złe dane";
                }

                return "transfer " + id + " " + account + " " + amount;
            }
            case "2": {
                return "showAccountBalance " + id;
            }
            case "3": {
                System.out.println("ile chcesz wyplacic?");
                String amount = scan.nextLine();
                return "paycheck " + id + " " + amount;
            }
            case "4": {
                System.out.println("ile chcesz wplacic?");
                String amount = scan.nextLine();
                return "payment " + id + " " + amount;
            }
            default : return "quit";
        }

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
            port = new Integer("6666").intValue();
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
        BufferedReader brSockInp = null;
        BufferedReader brLocalInp = null;
        DataOutputStream out = null;

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

            id = brSockInp.readLine();
            System.out.println("Otrzymano: " + id );

        }  catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            System.exit(-1);
        }



        //Pętla główna klienta

        while (true) {
            try {
                line = actions(id);


                if (line != null) {
                    System.out.println("Wysyłam: " + line);
                    out.writeBytes(line + '\n');
                    out.flush();
                }
                if (line == null || "quit".equals(line)) {
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
