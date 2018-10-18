package ssmith.lang;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Dates {

	public static String UKDATE_FORMAT = "dd/MM/yyyy";
	public static String UKDATE_FORMAT_WITH_TIME = "dd/MM/yyyy HH:mm";
	public static String UKDATE_FORMAT2_WITH_TIME = "dd MMM yyyy HH:mm";
	public static String TIME = "HH:mm";
	public static String SQLDATE_FORMAT = "yyyy-MM-dd";
	public static String SQLDATE_FORMAT_WITH_TIME = "yyyy-MM-dd HH:mm";
	
	public static final long MINUTE = 1000 * 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	public static final long YEAR = DAY * 365;

	
	public static String FormatDate(java.util.Date dt, String format) {
		if (dt != null) {
			SimpleDateFormat date_format = new SimpleDateFormat(format);
			return date_format.format(dt);
		} else {
			return "";
		}
	}
	

	public static String FormatCalendar(Calendar dt, String format) {
		return FormatDate(dt.getTime(), format);
	}

	
	public static java.util.Date ParseDate(String dt, String format) throws ParseException {
		SimpleDateFormat date_format = new SimpleDateFormat(format);
		return date_format.parse(dt);
	}

	
	public static Calendar ParseDateToCalendar(String dt, String format) throws ParseException {
		SimpleDateFormat date_format = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.setTime(date_format.parse(dt));
		return c;
	}

	
	public static java.util.Date ukd2d(String s) throws ParseException {
		SimpleDateFormat sim;
		if (s.length() > 10) {
			sim = new SimpleDateFormat(UKDATE_FORMAT_WITH_TIME);
		} else {
			sim = new SimpleDateFormat(UKDATE_FORMAT);
		}
		s = s.replace("-", "/"); // FSR doesn't like "-"
		return sim.parse(s);
	}

	
	public static int GetDaysInMonth(int m) {
		switch (m) {
		case 1: return 31;
		case 2: return 28;
		case 3: return 31;
		case 4: return 30;
		case 5: return 31;
		case 6: return 30;
		case 7: return 31;
		case 8: return 30;
		case 9: return 30;
		case 10: return 31;
		case 11: return 30;
		case 12: return 31;
		}
		return 0;
	}
	

	public static int GetDaysBetweenDates(Calendar scal, Calendar ecal) {
		long ms_in_day = (1000*60*60*24);
		long ONE_HOUR = 60 * 60 * 1000L;

		long ms = (ecal.getTime().getTime() - scal.getTime().getTime() + ONE_HOUR);
		//System.out.println("ms=" + ms);
		return (int) (ms/ms_in_day);
	}

}
