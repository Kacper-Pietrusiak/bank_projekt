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
          String userId = server.login(variables[1], variables[2]);
          server.closeConnection();
          out.writeBytes(userId+ "\n");
          System.out.println(threadName + "| Wysłano linię: " + userId);
        }
        if("transfer".equals(actionType)){
          QuickStart server = new QuickStart();
          String info = server.transfer(variables[1], variables[2], new Integer(variables[3]));
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
