package com.ecdsinc.passwordvault;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PvLogger {

	//	Available Loggers
	public static final String	LOGGER_VAULT			= "com.ecdsinc.passwordvault.vault";
	
	//	Allowed Log Levels
	public static final String		LOG_LEVEL_OFF		= "OFF";
	public static final String		LOG_LEVEL_INFO		= "INFO";
	public static final String		LOG_LEVEL_FINE		= "FINE";
	
	public static final String[] availableLoggers = {
			
			LOGGER_VAULT
	};
	
	public void enableLogging(boolean enable, boolean details) {
		
		Logger	logger = Logger.getLogger(LOGGER_VAULT);
		if (enable) {
			
			logger.setLevel(details ? Level.FINE : Level.INFO);
		}
		else {
			
			logger.setLevel(Level.OFF);
		}
	}
}
