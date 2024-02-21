package Oving4;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class Server4 {
  public static void main(String[] args) throws IOException {
    DatagramSocket ds;
    byte[] byteArray = new byte[256];
    boolean running = true;

    ds = new DatagramSocket(5679);
    System.out.println("DatagramSocket created, local:" + ds.getLocalAddress() + " - " + ds.getLocalPort());
    DatagramPacket dp;

    while (running) {
      dp = new DatagramPacket(byteArray, byteArray.length);
      System.out.println("DatagramPacket created");

      ds.receive(dp);
      System.out.println("Client: " + data(byteArray));

      if (data(byteArray).toString().equals("exit")){
        running = false;
      } else {
        String response = sillyMath(data(byteArray).toString());
        if (response.equals("Not silly")) {
          response = String.valueOf(math(data(byteArray).toString()));
        }
        byteArray = response.getBytes();
        dp = new DatagramPacket(byteArray, byteArray.length, dp.getAddress(), dp.getPort());
        ds.send(dp);
      }

      byteArray = new byte[256];
    }
  }

  public static StringBuilder data(byte[] a) {
    if (a == null) return null;
    StringBuilder ret = new StringBuilder();
    int i = 0;
    while (a[i] != 0) {
      ret.append((char) a[i]);
      i++;
    }
    return ret;
  }

  public static String sillyMath(String input) {
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
        if (b != 0) return a / b;
      default:
        System.err.println("Unhandled error in math()");
        System.out.println("INPUT: " + arr.toString());
        return -1; // or throw an exception
    }
  }

}