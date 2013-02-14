package ee.tut.fundamentals.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Simple Chat Client implementation
 * Connects to the remote server using plain TCP connection and sends over the client's name.
 * The socket is later used for receiving IM's only.
 *
 * Posting messages to the server is done using HTTP protocol.
 * The client makes a POST request to the server, where the username is set as a HTTP header and
 * the message is in the request body.
 */
public class ChatClient {


  private final URL out;
  private final String name;
  private final IncomingMessageListener listener;

  private volatile ee.tut.fundamentals.chat.MessageListener handler;

  public ChatClient (String name, String hostname, int serverPort, int httpPort) throws UnknownHostException, IOException {
    this.name = name;

    //messages will be POST'ed to this URL
    out = new URL("http", hostname, httpPort, "/");

    //connect to the server, send the name and start listening for incoming messages
    //spawned as a new thread, which terminates when the main application is closed
    listener = new IncomingMessageListener(hostname, serverPort);
    Thread t = new Thread(listener);
    t.setDaemon(true);
    t.start();
  }

  /**
   * Invokes the HTTP post request with the message
   */
  public void postMessage(String msg) throws IOException {
    System.out.println("posting message: " + msg);
    HttpURLConnection conn = (HttpURLConnection) out.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("author", name);
    conn.setDoOutput(true);


    OutputStreamWriter out=null;
    try {
      out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
      out.write(msg);
      out.flush();
      System.out.println("Message sent: " + conn.getResponseMessage());
    }
    finally {
      if (out != null) {
        try {
          out.close();
        }
        catch (Exception e) {}
      }
    }

  }


  /**
   * Provides a hook for the GUI to receive notifications about incoming IM's
   */
  public void setMessageListener(ee.tut.fundamentals.chat.MessageListener listener) {
    this.handler = listener;
  }


  private class IncomingMessageListener implements Runnable {

    private final BufferedReader in;

    public IncomingMessageListener (String hostname, int port) throws UnknownHostException, IOException {
      System.out.println("Connecting to " + hostname + ":" + port);
      Socket sock = new Socket(hostname, port);
      in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
      System.out.println("Connected to server");

      //send over the name
      OutputStreamWriter out = new OutputStreamWriter(sock.getOutputStream());
      out.write(name + "\n");
      out.flush();

    }

    /**
     * Main loop that listens for incoming IM's and notifies the listener, if present
     */
    public void run() {

      try {
        while (true) {
          String line = in.readLine();
          if (line == null) {
            return;
          }
          System.out.println("Received line:" + line);

          if (handler != null) {
            handler.onMessage(line);
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }


  }


}
