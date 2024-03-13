/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

/**
 *
 * @author dzelazny
 */
import database.QuickStart;

import java.net.*;
import java.io.*;

public class EchoServerThread implements Runnable
{
  protected Socket socket;
  public EchoServerThread(Socket clientSocket)
  {
    this.socket = clientSocket;
  }
  public void run()
  {
    //Deklaracje zmiennych
    BufferedReader brinp = null;
    DataOutputStream out = null;
    String threadName = Thread.currentThread().getName();

    //inicjalizacja strumieni
    try{
      brinp = new BufferedReader(
              new InputStreamReader(
                      socket.getInputStream()
              )
      );
      out = new DataOutputStream(socket.getOutputStream());
    }
    catch(IOException e){
      System.out.println(threadName + "| Błąd przy tworzeniu strumieni " + e);
      return;
    }
    String line = null;

    //pętla główna
    while(true){
      try{
        line = brinp.readLine();
        System.out.println(threadName + "| Odczytano linię: " + line);
        String[] variables = line.split(" ");
        String actionType = variables[0];

        System.out.println(actionType);
        //badanie warunku zakończenia pracy
        if("login".equals(actionType)){
          QuickStart server = new QuickStart();
          String loggedUser = server.login(variables[1], variables[2]);
          server.closeConnection();
          out.writeBytes(loggedUser+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + loggedUser);
        }
        if("transfer".equals(actionType)){
          QuickStart server = new QuickStart();
          String info = server.transfer(variables[1], variables[2], new Integer(variables[3]));
          server.closeConnection();
          out.writeBytes(info+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + info);
        }

        if("showAccountBalance".equals(actionType)){
          QuickStart server = new QuickStart();
          String info = server.showAccountBalance(variables[1]);
          server.closeConnection();
          out.writeBytes(info+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + info);
        }

        if("paycheck".equals(actionType)){
          QuickStart server = new QuickStart();
          String info = server.paycheck(variables[1], new Integer(variables[2]));
          server.closeConnection();
          out.writeBytes(info+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + info);
        }

        if("payment".equals(actionType)){
          QuickStart server = new QuickStart();
          String info = server.payment(variables[1], new Integer(variables[2]));
          server.closeConnection();
          out.writeBytes(info+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + info);
        }

        if("updateClient".equals(actionType)){
          QuickStart server = new QuickStart();
          String info = server.updateClient(variables[1], variables[2], variables[3]);
          server.closeConnection();
          out.writeBytes(info+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + info);
        }
        if("createClient".equals(actionType)){
          String name = variables[1] + " " + variables[2];
          QuickStart server = new QuickStart();
          String info = server.createUser(name, variables[3], variables[4], variables[5]);
          server.closeConnection();
          out.writeBytes(info+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + info);
        }
        if("showClient".equals(actionType)){
          QuickStart server = new QuickStart();
          String info = server.findUserById(variables[1]);
          server.closeConnection();
          out.writeBytes(info+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + info);
        }

        else if ((line == null) || "quit".equals(line)){
          System.out.println(threadName + "| Zakończenie pracy z klientem: " + socket);
          socket.close();
          return;
        }
      }
      catch(IOException e){
        System.out.println(threadName + "| Błąd wejścia-wyjścia." + e);
        return;
      }
    }
  }
}