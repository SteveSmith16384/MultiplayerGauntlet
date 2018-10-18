package mgs2.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Iterator;

import mgs2.shared.Statics;
import mgs2.shared.comms.UDPCommand;
import ssmith.lang.DataArrayOutputStream;
import ssmith.lang.ErrorHandler;

public final class UDPConnection extends Thread {

	private DatagramSocket socket;
	private volatile boolean stop_now = false;
	private final ServerMain main;
	private ErrorHandler error_handler;

	public static int next_packet_id = 0;

	public UDPConnection(ServerMain _main, int port, ErrorHandler _handler) throws IOException {
		super("UDPConnection_Thread");

		main = _main;
		error_handler = _handler;

		socket = new DatagramSocket(port);
		socket.setSoTimeout(10000);

		this.setDaemon(true);

		ServerMain.p("Waiting for UDP on port " + port);
	}


	public void run() {
		try {
			while (stop_now == false) {
				try {
					DatagramPacket packet;
					byte[] buf = new byte[256];
					packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);

					//ServerMain.p("Received packet");
					DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.getData()));
					UDPCommand cmd = UDPCommand.get(dis.readByte());
					switch(cmd) {
					case C2S_UDP_CONN:
						int playerid = dis.readInt();
						byte check = dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						PlayerData player = main.game.players_by_id.get(playerid);
						if (player.address == null) { // Haven't got the data yet
							player.address = packet.getAddress();
							player.port = packet.getPort();
						}

						// Send response
						DataArrayOutputStream bos = new DataArrayOutputStream();
						bos.write(UDPCommand.S2C_UDP_CONN_OK.getID());
						bos.writeByte(Statics.CHECK_BYTE);
						byte b[] = bos.getByteArray();
						DatagramPacket sendPacket = new DatagramPacket(b, b.length, packet.getAddress(), packet.getPort());
						this.socket.send(sendPacket);
						bos.close();
						break;
					case C2S_OBJECT_UPDATE:
						main.updateObject(dis, true);
						break;
					case C2S_CHECK_ALIVE:
						check = dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}

						bos = new DataArrayOutputStream();
						bos.write(UDPCommand.S2C_I_AM_ALIVE.getID());
						bos.writeByte(Statics.CHECK_BYTE);
						b = bos.getByteArray();
						sendPacket = new DatagramPacket(b, b.length, packet.getAddress(), packet.getPort());
						this.socket.send(sendPacket);
						bos.close();
						break;
					}
				} catch (SocketTimeoutException ex) {
					// Loop around
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		} catch (Exception ex) {
			this.error_handler.handleError(ex);
		} finally {
			socket.close();
		}
	}


	public void sendPacketToAll(byte sendData[]) throws IOException {
		synchronized (main.players_by_sck) {
		Iterator<PlayerData> it = main.players_by_sck.values().iterator();
		while (it.hasNext()) {
			PlayerData pd = it.next();
			if (pd != null && pd.address != null) { // Might be null if player's data hasn't been created
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, pd.address, pd.port);
				this.socket.send(sendPacket);
				//ServerMain.p("Sent UDP packet to " + pd.name);
			}
		}
		}
	}


	public void stopNow() {
		this.interrupt();
		this.stop_now = true;
	}

}
