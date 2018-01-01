package com.ecdsinc.passwordvault;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {

	   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	    @Override
	    public String format(LogRecord record) {
	        StringBuilder sb = new StringBuilder();

	        sb.append(new Date(record.getMillis()))
	            .append(" ")
	            .append(record.getLevel().getLocalizedName())
	            .append(": ")
	            .append(formatMessage(record))
	            .append(LINE_SEPARATOR);

	        return sb.toString();
	    }
}
