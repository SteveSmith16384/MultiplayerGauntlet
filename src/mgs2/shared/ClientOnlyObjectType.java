package mgs2.shared;

public enum ClientOnlyObjectType {

	SHOT_EXPLOSION(0),
	COMPUTER_EXPLOSION(1);
	
	private byte theID;

	ClientOnlyObjectType( int pID )
	{
		theID = (byte)pID;
	}

	public byte getID()
	{
		return this.theID;
	}

	public static ClientOnlyObjectType get( final byte pID )
	{
		ClientOnlyObjectType lTypes[] = ClientOnlyObjectType.values();
		for( ClientOnlyObjectType lType : lTypes )
		{
			if( lType.getID() == pID )
			{
				return lType;
			}
		}
		throw new IllegalArgumentException("Unknown type: " + pID);
	}
	
}
