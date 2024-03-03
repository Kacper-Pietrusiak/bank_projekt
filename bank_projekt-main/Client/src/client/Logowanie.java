package client;

import java.io.PrintWriter;
import java.util.Scanner;

public class Logowanie
{
    String login = "wiktor";
    String password = "kacper";

    public boolean logging_in()
    {
        //PrintWriter Baza = new PrintWriter("BazaDanychBank.txt");
        //String login =
        Scanner odczyt = new Scanner(System.in);
        System.out.println("Wpisz login");
        String log= odczyt.next();
        System.out.println("Wpisz haslo");
        String pass= odczyt.next();
        if(log.equals(login) && pass.equals(password))
        {
            return true;
        }
        return false;
    }
}
