package server.chat;

import java.io.*;
import java.net.*;

public class ChatServerThread extends Thread {
  private ChatServer server = null;
  private Socket socket = null;
  private int ID = -1;
  private DataInputStream streamIn = null;
  private DataOutputStream streamOut = null;
  private volatile Thread thread = null;

  public ChatServerThread(ChatServer _server, Socket _socket) {
    super();
    server = _server;
    socket = _socket;
    ID = socket.getPort();
  }

  void send(String msg) {
    try {
      streamOut.writeUTF(msg);
      streamOut.flush();
    } catch (IOException ioe) {
      System.out.println(ID + " ERROR sending: " + ioe.getMessage());
      server.remove(ID);
      stopThread();
    }
  }

  int getID() {
    return ID;
  }

  public void run() {
    System.out.println("Server Thread " + ID + " running.");
    Thread thisThread = Thread.currentThread();
    while (thread == thisThread) {
      try {
        server.handle(ID, streamIn.readUTF());
      } catch (IOException ioe) {
        System.out.println(ID + " ERROR reading: " + ioe.getMessage());
        server.remove(ID);
        stopThread();
      }
    }
  }

  void open() throws IOException {
    streamIn = new DataInputStream(new
        BufferedInputStream(socket.getInputStream()));
    streamOut = new DataOutputStream(new
        BufferedOutputStream(socket.getOutputStream()));
  }

  void close() throws IOException {
    if (socket != null) socket.close();
    if (streamIn != null) streamIn.close();
    if (streamOut != null) streamOut.close();
  }

  public void start() {
    thread = new Thread(this);
    thread.start();
  }

  void stopThread() {
    thread = null;
  }
}
