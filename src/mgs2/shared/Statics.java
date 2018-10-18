package mgs2.shared;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

public class Statics {

	public static final boolean DEBUG = false;
	public static final boolean VERBOSE = true;
	public static final boolean STRICT = true;
	public static final boolean DEBUG_GFX = false;
	public static final boolean USE_LOS = false;
	
	public static final float CODE_VERSION = 0.01f;
	public static final int COMMS_VERSION = 1;
	
	// Missions
	public static final int GM_DUNGEON1 = 1;

	public static final String TITLE = "Multiplayer Gauntlet (alpha)";
	public static final Dimension WINDOW_SIZE = new Dimension(800, 800);
	public static final int SHOW_SQUARES = 20;
	public static final int VIEW_ANGLE = 70;
	
	public static final int SQ_SIZE = WINDOW_SIZE.width/SHOW_SQUARES; 
	public static final int PLAYER_SIZE_W = (int)(SQ_SIZE * 0.55f);
	public static final int PLAYER_SIZE_H = (int)(SQ_SIZE * 0.8f);
	public static final int BULLET_SIZE = (int)(SQ_SIZE * 0.3f);
	
	public static final int BULLET_CACHE_SIZE = 10;
	public static final long LOOP_DELAY = 25;
	public static final byte CHECK_BYTE = 55;
	public static final int CHECK_SERVER_ALIVE_INTERVAL = 5000;
	public static final long RESPAWN_TIME = 10000;
	public static final long SERVER_DIED_DURATION = 1000*60*2;
	public static final long RESTART_DURATION = 12 * 1000;
	public static final long WAIT_FOR_PLAYERS_DURATION = 1 * 1000;
	public static final String CLIENT_ERROR_LOG = "client_errors.txt";
	public static final String SERVER_ERROR_LOG = "server_errors.txt";
	public static final String SERVER_PROPS = "server_settings.txt";
	public static final int DEF_PORT = 27018;
	public static final int DEF_ARRAY_SIZE = 4500;
	public static final int DEF_ARRAY_INC = 100;

	// Stats
	public static final float PLAYER_SPEED = 0.005f * SQ_SIZE;
	public static final float BULLET_SPEED = 0.045f * SQ_SIZE;
	public static final float DOOR_SPEED = 0.002f * SQ_SIZE;
	
	public static final long DOOR_OPEN_DURATION = 4000;
	public static final int COMPUTER_HEALTH = 100;
	public static final int MEDIKIT_HEALTH_INC = 50;
	
	
	public static final int KEY_UP = KeyEvent.VK_W;
	public static final int KEY_DOWN = KeyEvent.VK_S;
	public static final int KEY_LEFT = KeyEvent.VK_A;
	public static final int KEY_RIGHT = KeyEvent.VK_D;


}
