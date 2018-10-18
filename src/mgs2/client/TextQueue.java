package mgs2.client;

import java.util.ArrayList;

public class TextQueue  {

	private static final int DURATION = 2000;

	private String text = "";
	private long show_until;
	private ArrayList<String> queue = new ArrayList<String>();

	public TextQueue() {
		super();
	}
	
	public String getText() {
		return text;
	}


	public void add(String _text) {
		if (queue.size() == 0 || _text.equalsIgnoreCase(queue.get(0)) == false) {
			queue.add(_text);
		}
	}


	public void process() {
		if (text.length() > 0) {
			if (System.currentTimeMillis() > show_until) { // show_until -System.currentTimeMillis()
				this.text = "";
			}
		} else {
			if (queue.size() > 0) {
				this.text = queue.remove(0);
				show_until = System.currentTimeMillis() + DURATION;
			}
		}
	}

}
