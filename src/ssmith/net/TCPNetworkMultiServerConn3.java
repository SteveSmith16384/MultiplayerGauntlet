/**
 *
 * @author  steve smith
 * @version
 */

package ssmith.net;

import java.net.*;
import java.io.*;

/**
 * Extend this class for the clients.
 */
public abstract class TCPNetworkMultiServerConn3 {//extends Thread {

	public Socket sck;
	private DataOutputStream dos;
	private DataInputStream dis;
	protected TCPNetworkMultiServer3 server;
	public long started_time;

	
	public TCPNetworkMultiServerConn3(TCPNetworkMultiServer3 svr, Socket sck) throws IOException {
		super();//"Client connection");
		
		//this.setDaemon(false); //No, as we don't want to stop it abruptly
		this.sck = sck;
		this.server = svr;
		
		dos = new DataOutputStream(sck.getOutputStream());
		dis = new DataInputStream(sck.getInputStream());
		this.started_time = System.currentTimeMillis();
		
		sck.setTcpNoDelay(true);
	}

	
	public void close() {
		try {
			dis.close();
			dos.close();
			sck.close();
		} catch (IOException e) {
			// Nothing
		}
		//server.removeConnection(this);
	}

	
/*	public BufferedReader getBufferedReader() {
		return bis;
	}
	

	public PrintWriter getPrintWriter() {
		return pw;
	}
*/
	
	public DataOutputStream getDataOutputStream() {
		return dos;
	}

	
	public DataInputStream getDataInputStream() {
		return dis;
	}


	public InetAddress getINetAddress() {
		return sck.getInetAddress();
	}

}
