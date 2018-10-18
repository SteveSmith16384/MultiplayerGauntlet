package mgs2.shared.comms;


public enum TCPCommand {

	C2S_VERSION(1), 
	S2C_OK(2), 
	S2C_ERROR(3), 
	C2S_REQ_GAME_OPTIONS(21),
	S2C_GAME_VALUES(22),
	C2S_UNIT_TYPE(25),
	S2C_UNIT_TYPE_CONFIRMED(27),
	S2C_PLAYER_ID(5),
	C2S_REQ_ALL_DATA(6),
	S2C_MAP_SIZE_DATA(7),
	S2C_NEW_LEVEL(8),
	S2C_NEW_OBJECT(9),
	C2S_REQUEST_BULLET(11),
	S2C_PING_ME(12),
	C2S_PING_RESPONSE(14),
	S2C_ALL_DATA_SENT(15),
	C2S_EXIT(16),
	C2S_COLLISION(17),
	S2C_MESSAGE(18),
	S2C_REMOVE_OBJ(19),
	C2S_OBJECT_UPDATE(20),
	C2S_PLAYER_NAME(24),
	S2C_OBJECT_UPDATE(28),
	S2C_STAT_UPDATE(29),
	S2C_SEND_CLIENT_ONLY_OBJ(32),
	S2C_NEW_FLOATING_TEXT(33),
	C2S_SEND_CHAT(34),
	S2C_CHAT_UPDATE(35),
	C2S_BULLET_DATA(37),
	S2C_BULLET_DATA(38),
	C2S_SEND_ERROR(39),
	S2C_PLAY_SOUND(40);
	
	private byte theID;

	TCPCommand( int pID )
	{
		theID = (byte)pID;
	}

	public byte getID()
	{
		return this.theID;
	}

	public static TCPCommand get( final int pID )
	{
		TCPCommand lTypes[] = TCPCommand.values();
		for( TCPCommand lType : lTypes )
		{
			if( lType.getID() == pID )
			{
				return lType;
			}
		}
		throw new IllegalArgumentException("Unknown type: " + pID);
	}
	
}
