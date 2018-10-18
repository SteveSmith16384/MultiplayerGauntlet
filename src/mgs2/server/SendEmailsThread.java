package mgs2.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ssmith.lang.Dates;
import ssmith.lang.Functions;

public class SendEmailsThread extends Thread {

	private ArrayList<String> msgs = new ArrayList<String>();
	private ArrayList<String> new_msgs = new ArrayList<String>();

	public SendEmailsThread() {
		super("SendEmailsThread");

		this.setDaemon(true);
	}


	public void run() {
		while (true) {
			try{
				synchronized (new_msgs) {
					new_msgs.wait();
					this.msgs.addAll(new_msgs);
					new_msgs.clear();
				}
				while (msgs.size() > 0) {
					ServerMain.p("Sending email...");
					String msg = msgs.get(0);
					URL url = new URL("http://www.rafakrotiri.info/services/emailservice.cls?cmd=send&from=" + URLEncoder.encode("stephen.carlylesmith@googlemail.com", "UTF-8") + "&to=" + URLEncoder.encode("stephen.carlylesmith@googlemail.com", "UTF-8") + "&subject=" + URLEncoder.encode("TeamTactics Mail", "UTF-8") + "&msg=" + URLEncoder.encode(msg, "UTF-8"));
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setReadTimeout((int)Dates.MINUTE*5);
					if (conn.getResponseCode() == 200) {
						ServerMain.p("Email sent");
						msgs.remove(0);
					} else {
						throw new IOException("Error sending email - got response " + conn.getResponseCode());
					}
					//ServerMain.p("Email sent.");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Functions.delay(Dates.MINUTE * 2);
			}
			Functions.delay(Dates.MINUTE);
		}
	}


	public void addMsg(String msg) {
		synchronized (new_msgs) {
			new_msgs.add(msg);
			new_msgs.notify();
		}
	}
}

