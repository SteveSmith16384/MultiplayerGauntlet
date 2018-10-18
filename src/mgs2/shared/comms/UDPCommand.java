package mgs2.shared.comms;

import mgs2.shared.Statics;

public enum UDPCommand {

	UNKNOWN(-1),
	S2C_OBJECT_UPDATE(1),
	C2S_OBJECT_UPDATE(2),
	C2S_UDP_CONN(3),
	S2C_UDP_CONN_OK(4),
	S2C_TIME_REMAINING(5),
	C2S_CHECK_ALIVE(6),
	S2C_I_AM_ALIVE(7);

	private byte theID;

	UDPCommand( int pID )
	{
		theID = (byte)pID;
	}

	public byte getID()
	{
		return this.theID;
	}

	public static UDPCommand get( final int pID )
	{
		UDPCommand lTypes[] = UDPCommand.values();
		for( UDPCommand lType : lTypes )
		{
			if( lType.getID() == pID )
			{
				return lType;
			}
		}
		if (Statics.STRICT) {
			throw new IllegalArgumentException("Unknown type: " + pID);
		} else {
			System.err.println("Unknown type: " + pID);
			return UNKNOWN;
		}
	}
	
}