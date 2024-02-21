package Oving3;

import java.io.*;
import java.net.Socket;


public class Client {

  public static void main(String[] args){
    String host = "127.0.0.1";
    int serverPort = 5678;

    try (Socket socket = new Socket(host, serverPort);){
      BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
      BufferedReader ois = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter oos = new PrintWriter(socket.getOutputStream(), false);
      while (true) {
        // Read user input
        System.out.print("CLIENT: Enter math expression (or 'exit' to quit): ");
        String userInputString = userInput.readLine();

        // Send input to the server
        oos.println(userInputString);
        oos.flush();

        // Check if there was an error while sending
        if (oos.checkError()) {
          System.err.println("Error occurred while sending data to server");
        }

        // Exit condition
        if (userInputString.equalsIgnoreCase("exit")){
          System.out.println("Requesting disconnect...");
          ois.close();
          oos.close();
          break;
        }

        // Receive and display response from the server
        System.out.println(ois.readLine() + "\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Oving3.Client shutting down...");
  }
}