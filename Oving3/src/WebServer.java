import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

  public static void main(String[] args) {
    final int Port = 80;
    try (ServerSocket serverSocket = new ServerSocket(Port)) {
      System.out.println("WebServer started. Listening on port " + Port);
      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket + "\n");
        System.out.println("IP:port - " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + "\n");

        Thread webServerThread = new Thread(new WebServerThread(clientSocket));
        webServerThread.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static class WebServerThread implements Runnable {
    Socket clientSocket;
    public WebServerThread(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
      try {
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

        StringBuilder requestHeader = new StringBuilder();
        String lines;
        while ((lines = input.readLine()) != null && !lines.isEmpty()){
          requestHeader.append("<LI>").append(lines).append("</LI>");
        }

        // Send the response to the client
        output.println("HTTP/1.1 200 OK");
        output.println("Connection: Keep-Alive");
        output.println("Keep-Alive: timeout=5, max=1000");
        output.println("Content-Type: text/html; charset=utf-8\r\n");
        output.println("");
        output.println("<html><body>");
        output.println("<h1>Enkel web-tjener!</h1>");
        output.println("<p> Dette er en del av nettverksprogrammering øving 3 </p>");
        output.println("<h2>Header-linjer mottatt:</h2>");
        output.println("<ul>");
        output.println(requestHeader);
        output.println("</ul>");
        output.println("</body></html>");

        /*
        // Uncomment to keep the thread and connection open
        while (true) {
        }*/

      } catch (Exception e){
        e.printStackTrace();
      } finally {
        try {
          clientSocket.close();
          System.out.println("Client disconnected: " + clientSocket + "\n");
        } catch (IOException e) {
          e.printStackTrace();
          }
      }
    }
  }
}
