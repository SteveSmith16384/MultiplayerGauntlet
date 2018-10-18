package mgs2.shared;

import java.util.Calendar;

import ssmith.lang.Dates;

public class Misc {

	private Misc() {
	}

	
	public static void p(String s) {
		System.out.println(Dates.FormatDate(Calendar.getInstance().getTime(), Dates.UKDATE_FORMAT_WITH_TIME) + "-" + s);
	}


	public static void pe(String s) {
		System.err.println(Dates.FormatDate(Calendar.getInstance().getTime(), Dates.UKDATE_FORMAT_WITH_TIME) + "-" + s);
	}



}
