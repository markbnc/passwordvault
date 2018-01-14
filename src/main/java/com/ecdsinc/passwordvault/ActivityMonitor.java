package com.ecdsinc.passwordvault;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

public class ActivityMonitor extends Thread {

	private VaultView	vaultView;
	private boolean		shutdown = false;
	
	public ActivityMonitor() {
		
		super("Activity Monitor Thread");
	}

	@Override
	public void run() {

		Environment	env = null;
		Logger		logger = null;
		
		try {
			
			env = Environment.getEnvironment();
			logger = env.getLogger(PvLogger.LOGGER_VAULT);
			if (logger.isLoggable(Level.INFO)) {
				
				logger.info("Activity Monitor Thread Started");
			}
			
			while (!shutdown) {

				//	lockTimeout is in minutes.  Convert to milliseconds
				int lockTimeout = Integer.parseInt(env.getProperty(PasswordVaultProperties.VAULT_LOCK_TIMEOUT, PasswordVaultProperties.VAULT_LOCK_TIMEOUT_DEFAULT));
				lockTimeout *= 60000;
				
				long currentTime = Clock.getTime();
				
				UserVault currentVault = env.getApplication().getCurrentVault();
				
				long lastActivityTime = getLastActivityTime(env);
				if ((currentVault ==  null) || (lastActivityTime == 0)) {
					
					lastActivityTime = currentTime;
				}
				
//			System.out.println("Current Time: " + currentTime + " Last Activity: " + lastActivityTime + " Difference: " + (currentTime - lastActivityTime));
					
				if ((currentTime - lastActivityTime) > lockTimeout) {
					
					PasswordVault	app = env.getApplication();
					
					//	if there is a dialog currently being shown, then close it
					PvDialog<?> currentDialog = app.getCurrentDialog();
					if (currentDialog != null) {
						
						//	Close needs to be called from the Event thread
						Platform.runLater(() -> {
							
							currentDialog.close();
						});
					}
					env.getApplication().lock();
				}
				
				Thread.sleep(1000);
			}

			if (logger.isLoggable(Level.INFO)) {
				
				logger.info("Activity Monitor Thread Shutdown");
			}
		}
		catch (Exception except) {
			
			if (logger != null && logger.isLoggable(Level.INFO)) {
				
				logger.info("Activity Monitor Thread shutdown - Message: " + except);
				except.printStackTrace();
			}
		}
	}
	
	public void shutdown() {
		
		shutdown = true;
	}
	
	private long getLastActivityTime(Environment env) { 
		
		if (vaultView == null) {
			
			PasswordVault app = env.getApplication();
			if (app != null) {
				
				vaultView = app.getVaultView();
			}
		}
		
		if (vaultView != null) {
			
			return vaultView.getLastActivity();
		}
		else {
			
			return Clock.getTime();
		}
	}
}
