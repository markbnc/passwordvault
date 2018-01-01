/*
 * Created on Sep 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ecdsinc.passwordvault;

/**
 * @author MarkB
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PasswordVaultProperties {

	//	Specifies the location of the directory that contains the vault files
	public static final String		VAULT_PATH = "vaultPath";
	public static final String		VAULT_PATH_DEFAULT = "data";
	
	//	Tracks the name of last vault that was opened
	public static final String		LAST_OPEN_VAULT = "lastVault";
	public static final String		LAST_OPEN_VAULT_DEFAULT = "";
	
	//	Idle Lock controls whether the App will lock after lock timeout minutes of inactivity
	public static final String		VAULT_IDLE_LOCK = "idleLock";
	public static final String		VAULT_IDLE_LOCK_DEFAULT = "true";
	
	//	Vault Lock Timeout is in minutes
	public static final String		VAULT_LOCK_TIMEOUT = "vaultLockTimeout";
	public static final String		VAULT_LOCK_TIMEOUT_DEFAULT="5";
	
	// The name of the file that log records are written to.
	public static final String		LOG_FILE_NAME = "logFilename";
	public static final String		LOG_FILE_NAME_DEFAULT = "pvault.log";
	public static final String		LOG_PATH = "log";
	
	//	The log level to set for all password vault loggers
	public static final String		LOG_LEVEL_ALL = "logLevelAll";
	public static final String		LOG_LEVEL_ALL_DEFAULT = PvLogger.LOG_LEVEL_OFF;
	
	//	The number of log files to rotate through before overwriting them
	public static final String		LOG_FILE_COUNT = "logFileCount";
	public static final String		LOG_FILE_COUNT_DEFAULT = "10";
	
	// The size of the main application window
	public static final String		MAIN_WINDOW_WIDTH = "mainWindowWidth";
	public static final String		MAIN_WINDOW_HEIGHT = "mainWindowHeight";
	public static final String		MAIN_WINDOW_WIDTH_DEFAULT = "800";
	public static final String		MAIN_WINDOW_HEIGHT_DEFAULT = "600";
	
	//	VaultView Column Widths and Sort order
	public static final String		VAULT_VIEW_LOCATION_WIDTH = "locationWidth";
	public static final String		VAULT_VIEW_USERID_WITDH = "useridWidth";
	public static final String		VAULT_VIEW_DESCRIPTION_WIDTH = "descriptionWidth";
	public static final String 		VAULT_VIEW_LOCATION_WIDTH_DEFAULT = "200";
	public static final String		VAULT_VIEW_USERID_WIDTH_DEFAULT = "100";
	public static final String		VAULT_VIEW_DESCRIPTION_WIDTH_DEFAULT = "400";
	
	public static final String		VAULT_VIEW_SORT_COLUMN = "sortColumn";
	public static final String		VAULT_VIEW_SORT_DIRECTION = "sortDirection";
	public static final String		VAULT_VIEW_SORT_COLUMN_DEFAULT = String.valueOf(VaultView.COLUMN_LOCATION_INDEX);
	public static final String		VAULT_VIEW_SORT_DIRECTION_DEFAULT = String.valueOf(VaultView.SORT_DIR_ASCENDING);
	
	public static final String		FIND_IN_LOCATION = "findInLocation";
	public static final String		FIND_IN_USERID = "findInUserId";
	public static final String		FIND_IN_DESCRIPTION = "findInDescription";
	public static final String		FIND_WRAP_SEARCH = "findWrapSearch";
	public static final String		FIND_CASE_SENSITIVE = "findCaseSensitive";
	public static final String		FIND_IN_LOCATION_DEFAULT = String.valueOf(true);
	public static final String		FIND_IN_USERID_DEFAULT = String.valueOf(true);
	public static final String		FIND_IN_DESCRIPTION_DEFAULT = String.valueOf(true);
	public static final String		FIND_WRAP_SEARCH_DEFAULT = String.valueOf(true);
	public static final String		FIND_CASE_SENSITIVE_DEFAULT = String.valueOf(false);

}
