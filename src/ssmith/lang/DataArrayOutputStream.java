package ssmith.lang;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class DataArrayOutputStream extends DataOutputStream {
	
	private ByteArrayOutputStream bos;

	public DataArrayOutputStream() {
		super(null);
		
		bos = new ByteArrayOutputStream();
		this.out = bos;
	}
	
	
	public byte[] getByteArray() {
		return bos.toByteArray();
	}

}
