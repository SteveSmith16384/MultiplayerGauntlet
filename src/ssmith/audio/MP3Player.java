package ssmith.audio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class MP3Player extends Thread {

	private String mp3_filename;
	private volatile boolean stop_now = false;

	
	public MP3Player(String fname) {
		super("MP3Player");
		this.setDaemon(true);
		this.mp3_filename = fname;
		start();
	}

	
	public void run() {
		AudioInputStream din = null;
		try {
			File file = new File(this.mp3_filename);
			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			if(line != null) {
				line.open(decodedFormat);
				byte[] data = new byte[4096];
				// Start
				line.start();

				int nBytesRead;
				while ((nBytesRead = din.read(data, 0, data.length)) != -1 && stop_now == false) {
					line.write(data, 0, nBytesRead);
				}
				// Stop
				line.drain();
				line.stop();
				line.close();
				din.close();
				in.close();
			}

		} catch(Exception ex) {
			System.err.println("Cannot play '" + this.mp3_filename + "' :" + ex.getMessage());
			//ex.printStackTrace();
		} finally {
			if(din != null) {
				try { 
					din.close(); 
				} catch(IOException e) { 
					// Do nothing
				}
			}
		}
	}

	
	public void stopNow() {
		this.stop_now = true;
	}

}

