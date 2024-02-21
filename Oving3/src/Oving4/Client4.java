package Oving4;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;


public class Client4 {
  public static void main(String[] args) {
    final int port = 5679;
    Scanner scanner = new Scanner(System.in);
    DatagramSocket clientSocket = null;

    try {
      clientSocket = new DatagramSocket();

      String serverMachine = "127.0.0.1";
      InetAddress ip = InetAddress.getByName(serverMachine);
      System.out.println(serverMachine + ": " + ip.toString());


      while (true) {
        // Input numbers and operation from the user
        System.out.print("Input equation, or 'exit': ");
        String line = scanner.nextLine();

        byte[] sendData = line.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
        clientSocket.send(sendPacket);

        if (line.equals("exit")) {
          break;
        }

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Server response: " + response);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (scanner != null) {
        scanner.close();
      }
      if (clientSocket != null) {
        clientSocket.close();
      }
    }
  }
}