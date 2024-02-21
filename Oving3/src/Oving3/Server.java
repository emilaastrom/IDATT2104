package Oving3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
  static class ClientHandler implements Runnable{
    Socket clientSocket;
    public ClientHandler(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
      try{
      System.out.println("Oving3.Client: "  + clientSocket + "\nAssigned thread: " +  Thread.currentThread());
      BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

      // Receive input from the client
      while(true){
        String clientInput = input.readLine();

        // Exit condition
        if (clientInput.equalsIgnoreCase("exit")) {
          System.out.println("\nOving3.Client requested disconnect.");
          Thread.currentThread().interrupt();
        }

        // Process the math input and send the result back to the client
        if (sillyMath(clientInput).equals("Not silly")) {
          double result = math(clientInput);
          output.println("SERVER: " + result);
        } else {
          output.println("SERVER: " + sillyMath(clientInput));
        }
      }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }




  public static void main(String[] args) throws IOException {
    System.out.println("Oving3.Server starting...");
    ServerSocket serverSocket = null;
    Socket clientSocket = null;

    try {
      // Create a server socket listening on a specific port
      serverSocket = new ServerSocket(5678);
      System.out.println("Up and running, waiting for clients...\n");

      while (true) {
        clientSocket = serverSocket.accept();
        System.out.println("Oving3.Client connected: " + clientSocket + "\n");
        System.out.println(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "\n");

        Thread clientThread = new Thread(new ClientHandler(clientSocket));
        clientThread.start();
      }


    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      System.out.println("Oving3.Server shutting down...");
      assert clientSocket != null;
      clientSocket.close();
      serverSocket.close();
    }
}

  public static String sillyMath(String input){
    String[] arr = input.split("(?<=[+\\-*/])|(?=[+\\-*/])");
    System.out.println(Arrays.toString(arr));
    if (arr.length != 3) {
      System.err.println("Invalid input format");
      return "Invalid input format";
    } else if (arr[1].equals("/") && arr[2].equals("0")) {
      System.err.println("Division by zero");
      return "Division by zero";
    } else {
      return "Not silly";
    }
  }

  public static double math(String input) {

    String[] arr = input.split("(?<=[+\\-*/])|(?=[+\\-*/])");

    System.out.println("Array in math func: " + Arrays.toString(arr));

    if (arr.length != 3) {
      System.err.println("Invalid input format");
      return -1;
    }

    double a = Double.parseDouble(arr[0]);
    double b = Double.parseDouble(arr[2]);
    String op = arr[1];

    switch (op) {
      case "+":
        return a + b;
      case "-":
        return a - b;
      case "*":
        return a * b;
      case "/":
        if (b != 0)
          return a / b;
      default:
        System.err.println("Unhandled error in math()");
        System.out.println("INPUT: " + arr.toString());
        return -1; // or throw an exception
    }
  }

}