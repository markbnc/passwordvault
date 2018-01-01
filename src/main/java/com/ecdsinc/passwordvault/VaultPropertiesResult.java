package com.ecdsinc.passwordvault;

public class VaultPropertiesResult {

	private String description;
	private String passwordHint;
	
	public VaultPropertiesResult(String description, String passwordHint) {
		
		this.description = description;
		this.passwordHint = passwordHint;
	}

	public String getDescription() {
		return description;
	}

	public String getPasswordHint() {
		return passwordHint;
	}
}
