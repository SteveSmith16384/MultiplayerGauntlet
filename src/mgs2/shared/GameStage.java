package mgs2.shared;


public enum GameStage {

	WAIT_FOR_PLAYERS(0, "Waiting for Players..."), 
	START_IMINENT(1, "Start Imminent!"), // Extra time for more players to join 
	STARTED(2, "Game Started!"), 
	POST_GAME(3, "Game Over!");
	
	private byte theID;
	private String name;

	GameStage( int pID, String _name )
	{
		theID = (byte)pID;
		name  =_name;
	}

	public byte getID()
	{
		return this.theID;
	}

	
	public static GameStage get( final byte pID )
	{
		GameStage lTypes[] = GameStage.values();
		for( GameStage lType : lTypes )
		{
			if( lType.getID() == pID )
			{
				return lType;
			}
		}
		throw new IllegalArgumentException("Unknown type: " + pID);
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public static String GetNextDesc(GameStage stage) {
		switch (stage) {
		case WAIT_FOR_PLAYERS: return "game restarts";
		case START_IMINENT: return "game starts"; 
		case STARTED: return "game ends"; 
		case POST_GAME: return "next game starts";
		default: return "the rapture";
		}
	}
	
}
