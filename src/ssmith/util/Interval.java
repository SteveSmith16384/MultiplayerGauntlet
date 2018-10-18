package ssmith.util;

public class Interval {

	private long next_check_time, duration_ms;

	public Interval(long duration_ms) {
		this(duration_ms, false);
	}
	
	
	public Interval(long _duration_ms, boolean fire_now) {
		super();
		this.duration_ms = _duration_ms;
		if (fire_now) {
			this.next_check_time = System.currentTimeMillis(); // Fire straight away
		} else {
			this.next_check_time = System.currentTimeMillis() + duration_ms;
		}
	}
	
	
	public void restartTimer() {
		this.next_check_time = System.currentTimeMillis() + duration_ms;
	}

	
	public void setInterval(long amt, boolean restart) {
		duration_ms = amt;
		
		if (restart) {
			this.restartTimer();
		}
	}

	
	public boolean hitInterval() {
		if (System.currentTimeMillis() >= this.next_check_time) { // System.currentTimeMillis() - this.next_check_time
			this.restartTimer();
			return true;
		}
		return false;
	}
	
	
	public void fireInterval() {
		this.next_check_time = System.currentTimeMillis(); // Fire straight away
	}

}

