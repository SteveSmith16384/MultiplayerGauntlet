package ssmith.lang;


public final class Functions {

	public static void delay(int milliseconds) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			}
			catch (InterruptedException e) {
			}
		}
	}

	
	public static void delay(long milliseconds) {
		if (milliseconds > 0) {
			try {
				Thread.sleep(milliseconds);
			}
			catch (InterruptedException e) {
			}
		}
	}
	
	
	public static String Throwable2String( Throwable pThrowable )
	{
		StringBuilder lStackTrace = new StringBuilder();
		while( pThrowable != null )
		{
			lStackTrace.append( pThrowable + "\n" );
			for( int i = 0; i < pThrowable.getStackTrace().length; i++ )
			{
				lStackTrace.append( " " ).append( pThrowable.getStackTrace()[i].getClassName() );
				lStackTrace.append( ":" ).append( pThrowable.getStackTrace()[i].getLineNumber() ).append( " - " );
				lStackTrace.append( pThrowable.getStackTrace()[i].getMethodName() );
				lStackTrace.append( "\n" );
			}
			pThrowable = pThrowable.getCause();
			if( pThrowable != null )
			{
				lStackTrace.append( "Caused by:\n" );
			}
		}
		return lStackTrace.toString();
	}

}


