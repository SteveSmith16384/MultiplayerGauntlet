package mgs2.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import mgs2.shared.Statics;
import mgs2.shared.comms.UDPCommand;

public final class UDPConnection extends Thread {

	private DatagramSocket socket;
	private InetAddress address;
	private int port;
	private volatile boolean stop_now = false;
	private ClientMain main;

	public static int next_packet_id = 0;

	public UDPConnection(ClientMain _main, String server, int _port) throws IOException {
		super("UDPConnection_Thread");

		this.setDaemon(true);

		main = _main;

		address = InetAddress.getByName(server);
		socket = new DatagramSocket();
		port = _port;

		socket.setSoTimeout(10000);
	}


	public void run() {
		try {
			while (stop_now == false) {
				try {
					DatagramPacket packet;
					byte[] buf = new byte[256];
					packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);

					DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.getData()));
					UDPCommand cmd = UDPCommand.get(dis.readByte());
					//ClientMain.p("Rcvd new packet: " + cmd);
					switch (cmd) {
					case S2C_OBJECT_UPDATE:
						main.decodeObjectUpdate(dis);
						break;
					case S2C_UDP_CONN_OK:
						byte check = dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						main.server_responded_to_udpconn = true;
						break;
					case S2C_TIME_REMAINING:
						main.window.str_time = dis.readUTF();
						main.window.str_respawn_time = dis.readUTF();
						check = dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						break;
					case S2C_I_AM_ALIVE:
						check = dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						main.last_server_alive_response_time = System.currentTimeMillis();
						break;
					default:
						throw new IllegalArgumentException("Unknown command: " + cmd);
					}
				} catch (SocketTimeoutException ex) {
					//ex.printStackTrace();
					// Loop around
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			socket.close();
		}
	}


	public void sendPacket(byte sendData[]) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
		socket.send(sendPacket);
	}


	public void stopNow() {
		this.interrupt();
		this.stop_now = true;
	}

}
