package client;

import java.util.Scanner;

public class ClientActions {

    public String actions(String id) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Wpisz numer operacji którą chcesz wykonać: ");
        System.out.println("1) Przelew na inne konto ");
        System.out.println("2) Sprawdź stan konta ");
        System.out.println("3) Wypłać środki ");
        System.out.println("4) Wpłać środki ");
        System.out.println("9) EXIT ");


        String action = scan.nextLine();

        switch (action) {
            case "1" : {
                System.out.println("Na jakie konto chcesz przelac środki?");
                String account = scan.nextLine();
                System.out.println("ile chcesz przesłać?");
                String amount = scan.nextLine();

                if( Integer.parseInt(amount) <= 0){
                    System.out.println("Niepoprawna kwota");
                    return "reset";
                }

                return "transfer " + id + " " + account + " " + amount;
            }
            case "2": {
                return "showAccountBalance " + id;
            }
            case "3": {
                System.out.println("Ile chcesz wypłacić?");
                String amountString = scan.nextLine();

                try {
                    int amount = Integer.parseInt(amountString);
                    if(amount <= 0){
                        System.out.println("Niepoprawna kwota");
                        return "reset";
                    }
                    return "paycheck " + id + " " + amount;
                } catch (NumberFormatException e) {
                    System.out.println("Niepoprawne dane");
                    return "reset";
                }
            }
            case "4": {
                System.out.println("ile chcesz wplacic?");
                String amountString = scan.nextLine();

                try {
                    int amount = Integer.parseInt(amountString);
                    if(amount <= 0){
                        System.out.println("Niepoprawna kwota");
                        return "reset";
                    }
                    return "payment " + id + " " + amount;
                } catch (NumberFormatException e) {
                    System.out.println("Niepoprawne dane");
                    return "reset";
                }
            }
            case "9": {
                return "exit";
            }
            default : return "reset";
        }

    }


}
