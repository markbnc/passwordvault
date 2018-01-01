package com.ecdsinc.passwordvault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.ecdsinc.security.EcdsAESKeyStore;
import com.ecdsinc.security.EcdsKeyStore;
import com.ecdsinc.security.EcdsSecurityException;
import com.ecdsinc.security.KeyStoreNotFoundException;


public class Environment {

	private EcdsKeyStore				keystore;
	private Properties					properties = new Properties();
	private Map<String, Logger>			loggers = new HashMap<>();
	private File						passwordVaultDir;
	private PasswordVault				application;
	
	static private String				PASSWORD_VAULT_DIR_WIN = "PasswordVault";
	static private String				PASSWORD_VAULT_DIR_LINUX = ".password_vault";
	static private String				PROPERTIES_FILENAME = "pvault.properties";
	
	
	static private class Holder {
		volatile Environment	environment;
		volatile int			refCount;
	}

	static private final Holder holder = new Holder();

	/**
	 * This method should be used to obtain an instance of the environment.  The
	 * reference counting is not needed for this application, but is left to 
	 * maintain consistency with the use of the Environment class in other 
	 * applications
	 */
	public static Environment initEnvironment() 
		throws PasswordVaultException {
		
		synchronized (holder) {
			if (holder.environment == null) {
				Environment env = new Environment();
				env.initialize();
				holder.environment = env;
				holder.refCount = 1;
			}
			else {
				holder.refCount++;
			}
			return holder.environment;
		}
	}

	/**
	 * This method should be invoked when the application shuts down.
	 */
	public static void releaseEnvironment() 
		throws PasswordVaultException {
		
		synchronized (holder) {
			--holder.refCount;
			if (holder.refCount == 0) {
				Environment env = holder.environment;
				holder.environment = null;
				env.destroy();
			}
		}
	}

	public static Environment getEnvironment() 
		throws PasswordVaultException {
		
		synchronized (holder) {
			if (holder.environment == null) {
				
				throw new PasswordVaultException("Environment not initialized");
			}
			
			return holder.environment;
		}
	}
	
	public EcdsKeyStore getKeyStore() {
		
		return keystore;
	}
	
	public String getProperty(String propertyName, String defaultValue) {
		
		return properties.getProperty(propertyName, defaultValue);
	}
	
	public void setProperty(String propertyName, String propertyValue) {
		
		properties.setProperty(propertyName, propertyValue);
	}
	
	public Logger getLogger(String loggerName)
		throws PasswordVaultException {
		
		Logger logger = loggers.get(loggerName);
		if (logger == null) {
			
			throw new PasswordVaultException("Invalid Logger " + loggerName);
		}
		return logger;
	}
	
	public String getApplicationDataDirName() {
		
		return passwordVaultDir.getAbsolutePath();
	}
	
	public File getApplicationDataDir() {
		
		return passwordVaultDir;
	}
	
	public File getVaultDir() {
		
		String	vaultDirStr = this.properties.getProperty(PasswordVaultProperties.VAULT_PATH, PasswordVaultProperties.VAULT_PATH_DEFAULT);
		File	vaultDir = new File(vaultDirStr);
		if (!vaultDir.isAbsolute()) {
			
			vaultDir = new File(getApplicationDataDir(), vaultDirStr);
		}
		return vaultDir;
	}
	
	public File getLogDir() {
		
		return new File(getApplicationDataDir(), PasswordVaultProperties.LOG_PATH);
	}
	
	public PasswordVault getApplication() {
		return application;
	}

	public void setApplication(PasswordVault application) {
		
		this.application = application;
	}
	
	/**
	 * This private constructor will prevent any misuse of the initialization system.
	 */
	private Environment() {
	}

	private void initialize() 
		throws PasswordVaultException {
		
		initPasswordVaultDir();
		loadProperties();
		initLoggers();
		openKeyStore();
	}
	
	private void destroy()
		throws PasswordVaultException {
		
		saveProperties();
	}
	
	public void openKeyStore() {

		String	keyStorePath = null;
		String  keyStoreName = "pwvks";
		String	keyStorePassword = "markb";
		 
		try{
			
			if (keystore != null) {
				
				keystore.close();
			}
			
			keyStorePath = getVaultDir().getAbsolutePath() + File.separator + keyStoreName;
			
			//	AES key store can be used with any PasswordVault key store 
			//	regardless of the version.  New vaults will be created using AES Keys 
			keystore = new EcdsAESKeyStore();
	    	keystore.open(keyStorePath , keyStorePassword);
		}
		catch (KeyStoreNotFoundException except) {
			
			try {
				
				keystore.createKeyStore(keyStorePath, 
										keyStorePassword);
				new File(getVaultDir().getAbsolutePath()).mkdirs();
				keystore.store();
			}
			catch (EcdsSecurityException createExcept) {

				JOptionPane.showMessageDialog(null,
					"Error creating the key store.  Program will exit\n" + except,
					"Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
		}
		catch (EcdsSecurityException except) {

			JOptionPane.showMessageDialog(null,
			    "Error opening the key store.  Program will exit\n" + except,
				"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	private void loadProperties() {
		
		try {
			
			File	propertiesFile = new File(getApplicationDataDir(), PROPERTIES_FILENAME);
			FileInputStream	inStream = new FileInputStream(propertiesFile);
			this.properties.load(inStream);
			
		}
		catch(IOException except) {
			//	The defaults file may not exist.
		}
	}
	
	private void saveProperties() {
		
		try {
			
			application.updateProperties(properties);
			
			File	propertiesFile = new File(getApplicationDataDir(), PROPERTIES_FILENAME);
			FileOutputStream	outStream = new FileOutputStream(propertiesFile);
			properties.store(outStream, "Password Vault Defaults");
		}
		catch(IOException except) {
	
			JOptionPane.showMessageDialog(
				null, 
				"Error saving the PasswordVault defaults",
				"Error",
				JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void initLoggers() 
		throws PasswordVaultException {
		
		try {
			
			String	logFilename = getProperty(PasswordVaultProperties.LOG_FILE_NAME, PasswordVaultProperties.LOG_FILE_NAME_DEFAULT);
			File	logFilePath = new File(logFilename);
			if (!logFilePath.isAbsolute()) {
				
				logFilePath = new File(getLogDir(), logFilename);
			}
			if (!logFilePath.exists()) {
				
				logFilePath.mkdirs();
			}
			
			int		logFileCount = Integer.parseInt(getProperty(PasswordVaultProperties.LOG_FILE_COUNT, PasswordVaultProperties.LOG_FILE_COUNT_DEFAULT));
			
			FileHandler	handler = new FileHandler(logFilePath.getAbsolutePath(), 0, logFileCount);
			handler.setLevel(Level.FINEST);
			handler.setFormatter(new SingleLineFormatter());
			
			String	logLevelStr = getProperty(PasswordVaultProperties.LOG_LEVEL_ALL, PasswordVaultProperties.LOG_LEVEL_ALL_DEFAULT);
			Level	logLevel = null;
			switch (logLevelStr) {
				
				case PvLogger.LOG_LEVEL_OFF:
					logLevel = Level.OFF;
					break;
						
				case PvLogger.LOG_LEVEL_INFO:
					logLevel = Level.INFO;
					break;
					
				case PvLogger.LOG_LEVEL_FINE:
					logLevel = Level.FINE;
					break;
					
				default:
					
					File	propertiesFile = new File(getApplicationDataDir(), PROPERTIES_FILENAME);
					VaultUtil.displayErrorDialog("Invalid log level " + logLevelStr + " specified for " + PasswordVaultProperties.LOG_LEVEL_ALL + " in " + propertiesFile + "\nContinuing with logging disabled", null);
			}
			
			for (String loggerName : PvLogger.availableLoggers) {
				
				Logger	logger = Logger.getLogger(loggerName);
				logger.addHandler(handler);
				logger.setUseParentHandlers(false);
				logger.setLevel(logLevel);
				
				loggers.put(loggerName, logger);
				
			}
		}
		catch (IOException except ) {
			
			throw new PasswordVaultException("Error initializing loggers", except);
		}
	}
	
	private void initPasswordVaultDir() {
		
		String os = (System.getProperty("os.name")).toUpperCase();
		if (os.contains("WIN"))
		{
		    //it is simply the location of the "AppData" folder
		    passwordVaultDir = new File(System.getenv("AppData"), PASSWORD_VAULT_DIR_WIN);
		}
		else
		{
			passwordVaultDir = new File(System.getProperty("user.home"), PASSWORD_VAULT_DIR_LINUX);
		}

		if (!passwordVaultDir.exists()) {
			
			passwordVaultDir.mkdir();
		}		
	}
}
