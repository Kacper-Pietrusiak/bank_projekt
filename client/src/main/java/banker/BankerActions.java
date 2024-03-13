package banker;

import java.util.Scanner;

public class BankerActions {

    public String actions() {
        Scanner scan = new Scanner(System.in);

        System.out.println("Wpisz numer operacji którą chcesz wykonać: ");
        System.out.println("1) edytuj dane klienta ");
        System.out.println("2) dodaj klienta ");
        System.out.println("3) wyświetl klienta ");
        System.out.println("9) EXIT ");

        String action = scan.nextLine();

        switch (action) {
            case "1" : {
                System.out.println("Podaj id klienta");
                String clientId = scan.nextLine();
                System.out.println("Podaj nowe imie i nazwisko");
                String name = scan.nextLine();
                System.out.println("Podaj nowy email");
                String email = scan.nextLine();

                return "updateClient " + clientId + " " + name + " " + email;
            }
            case "2": {
                System.out.println("Podaj imie i nazwisko");
                String name = scan.nextLine();
                System.out.println("Podaj hasło do konta");
                String pass = scan.nextLine();
                System.out.println("Podaj email");
                String email = scan.nextLine();
                System.out.println("Podaj pesel");
                String pesel = scan.nextLine();


                return "createClient " + name + " " + pass + " " + email + " " + pesel;
            }
            case "3": {
                System.out.println("podaj id klienta którego chcesz wyswietlic");
                String clientId = scan.nextLine();

                return "showClient " + clientId;
            }
            case "9": {
                return "exit";
            }
            default : return "reset";
        }

    }

}
