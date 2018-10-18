package mgs2.server;

import java.io.IOException;
import java.net.Socket;

import ssmith.net.TCPNetworkMultiServer3;
import ssmith.net.TCPNetworkMultiServerConn3;

public final class TCPClientConnection extends TCPNetworkMultiServerConn3 {

	public TCPClientConnection(TCPNetworkMultiServer3 svr, Socket sck) throws IOException {
		super(svr, sck);
	}

}
