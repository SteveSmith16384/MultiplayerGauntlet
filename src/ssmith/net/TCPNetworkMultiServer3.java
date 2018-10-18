package ssmith.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ssmith.lang.ErrorHandler;

public abstract class TCPNetworkMultiServer3 extends Thread {

    private ServerSocket sckListener;
    public boolean debug = false;
    private static volatile boolean stopNow = false;
    private ErrorHandler error_handler;

    public TCPNetworkMultiServer3(int port, int max_conns, ErrorHandler _error_handler) throws IOException {
	    super("TCPNetworkMultiServer3");
	    
	    error_handler = _error_handler;
	    
        this.setDaemon(false);
        sckListener = new ServerSocket(port, max_conns);
    }

    
    public final void run() {
        try {
            System.out.println("Waiting for connections on port " + this.sckListener.getLocalPort() + "...");
            while (!stopNow) {
                Socket s = sckListener.accept();
                System.out.println("Client connected.");
                createConnectionPre(s);
            }
            System.out.println("NetworkMultiServer2 stopped.");
        } catch (Exception e) {
        	error_handler.handleError(e);
        }
    }

    /**
     * Override this method when a connection is made
     */
    public void createConnectionPre(Socket sck) throws IOException {
    	createConnection(sck);
        System.out.println("Connection made from " + sck.getInetAddress().toString() + ".");//  There are now " + num_conns + " users connected.");
    }


    public abstract void createConnection(Socket sck) throws IOException;


    public static void StopListening() {
        stopNow = true;
    }

}
