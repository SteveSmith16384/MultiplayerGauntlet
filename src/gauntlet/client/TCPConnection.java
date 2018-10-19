package gauntlet.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public final class TCPConnection {

	private Socket sck;
	public DataInputStream dis;
	public DataOutputStream dos;

	public TCPConnection(Socket _sck) throws IOException {
		super();
		
		sck = _sck;
		
		sck.setTcpNoDelay(true);

		dis = new DataInputStream(sck.getInputStream());
		dos = new DataOutputStream(sck.getOutputStream());
	}
	
	
	public boolean isConnected() {
		return sck.isConnected();
	}
	

}
