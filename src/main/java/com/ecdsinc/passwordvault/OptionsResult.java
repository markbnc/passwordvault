package com.ecdsinc.passwordvault;

public class OptionsResult {

	private boolean 	idleLock;
	private int			lockTimeout;			// in minutes
	private String		vaultDirectory;
	
	public OptionsResult(boolean idleLock, int lockTimeout, String vaultDirectory) {
		
		this.idleLock = idleLock;
		this.lockTimeout = lockTimeout;
		this.vaultDirectory = vaultDirectory;
	}

	public boolean isIdleLock() {
		return idleLock;
	}

	public int getLockTimeout() {
		return lockTimeout;
	}

	public String getVaultDirectory() {
		return vaultDirectory;
	}
}
