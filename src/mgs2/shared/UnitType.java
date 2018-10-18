package mgs2.shared;


public enum UnitType {

	UNSET(0),
	CLERIC(1),
	THEIF(2),
	WIZARD(3),
	ELF(4),
	WARRIOR(5);
	
	private byte theID;

	UnitType( int pID )
	{
		theID = (byte)pID;
	}

	public byte getID()
	{
		return this.theID;
	}

	public static UnitType get( final byte pID )
	{
		UnitType lTypes[] = UnitType.values();
		for( UnitType lType : lTypes )
		{
			if( lType.getID() == pID )
			{
				return lType;
			}
		}
		throw new IllegalArgumentException("Unknown type: " + pID);
	}
	
}
