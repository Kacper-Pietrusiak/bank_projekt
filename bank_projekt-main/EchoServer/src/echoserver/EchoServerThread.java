package echoserver;

/**
 *
 * @author dzelazny
 */
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
    BufferedReader in = null;
    DataOutputStream out = null;
    String threadName = Thread.currentThread().getName();
    
    //inicjalizacja strumieni
    try{
      in = new BufferedReader(
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
        line = in.readLine();
        System.out.println(threadName + "| Odczytano linię: " + line);
        
        //badanie warunku zakończenia pracy
        if((line == null) || "quit".equals(line)){
          System.out.println(threadName + "| Zakończenie pracy z klientem: " + socket);
          socket.close();
          return;
        }
        else{ //odesłanie danych do klienta
          out.writeBytes(line + "\n\r");
          System.out.println(threadName + "| Wysłano linię: " + line);
        }
      }
      catch(IOException e){
        System.out.println(threadName + "| Błąd wejścia-wyjścia." + e);
        return;
      }
    }
  }
}
