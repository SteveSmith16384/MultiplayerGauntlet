package mgs2.server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import mgs2.server.gameobjects.PlayersAvatar;
import mgs2.shared.Statics;
import mgs2.shared.UnitType;
import ssmith.util.Interval;

public final class PlayerData {
	
	private static String HELP_TEXT[] = {"Welcome to " + Statics.TITLE, 
		"Choose a side and a unit type by pressing a number", 
		"Move your avatar with W, A, S and D", 
		"Aim your weapon with the mouse"};
	
	public ServerMain main;
	public TCPClientConnection conn;
	public long pingme_time, ping;
	public boolean awaiting_ping_response = false;
	
	public boolean been_sent_data = false;
	public String name;
	public PlayersAvatar avatar;
	public UnitType unit_type = UnitType.UNSET;
	public int score = 0, victories = 0;
	public String prev_msg = "";
	
	public boolean send_help = true;
	public Interval send_help_interval = new Interval(3000);
	private ArrayList<String> help_text = new ArrayList<String>();
	public boolean reached_exit = false;

	// For UDP
	public InetAddress address;
	public int port;

	public PlayerData(ServerMain _main, TCPClientConnection _conn, String _name) {
		super();
		
		main = _main;
		conn = _conn;
		name = _name;
		
		for (int i=0 ; i<HELP_TEXT.length ; i++) {
			help_text.add(HELP_TEXT[i]);
		}
	}
	
	
	public void process() throws IOException {
		if (send_help && help_text.size() > 0) {
			if (send_help_interval.hitInterval()) {
				main.sendMsg(help_text.remove(0), conn.getDataOutputStream());
			}
		}
	}

}
