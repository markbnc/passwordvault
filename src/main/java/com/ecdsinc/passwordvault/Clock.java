package com.ecdsinc.passwordvault;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Clock extends Thread {

	private static long		time;
	
	private boolean			shutdown = false;
		
	public static long getTime() {
		
		return time;
	}

	@Override
	public void run() {

		Logger	logger = null;
		
		try {
			
			logger = Environment.getEnvironment().getLogger(PvLogger.LOGGER_VAULT);
			if (logger.isLoggable(Level.INFO)) {
				
				logger.info("Clock Thread started");
			}
			while (!shutdown) {
			
				time = System.currentTimeMillis();
				Thread.sleep(1000);
			}
		}
		catch (Exception except) {
			
			if (logger != null) {
				
				if (logger.isLoggable(Level.INFO)) {
					
					logger.info("Clock Thread shut down - Message: " + except);
				}
			}
		}
	}
	
	public void shutdown() {
		
		shutdown = true;
	}
	
	public Clock() {
		
		super("Clock Thread");
	}
}
