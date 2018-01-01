package com.ecdsinc.passwordvault;

public class CreateVaultResult {

	private String vaultName;
	private String vaultPassword;
	private String description;
	private String passwordHint;
	
	public CreateVaultResult(String vaultName, String vaultPassword, String description, String passwordHint) {
		
		this.vaultName = vaultName;
		this.vaultPassword = vaultPassword;
		this.description = description;
		this.passwordHint = passwordHint;
	}
	
	public String getVaultName() {
		return vaultName;
	}
	
	public String getVaultPassword() {
		return vaultPassword;
	}
	
	public String getDescription() {
		return description;
	}

	public String getPasswordHint() {
		return passwordHint;
	}
}
