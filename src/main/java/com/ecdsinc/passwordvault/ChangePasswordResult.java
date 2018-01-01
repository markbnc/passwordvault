package com.ecdsinc.passwordvault;

public class ChangePasswordResult {

	private String	oldPassword;
	private String	newPassword;
	private String	passwordHint;
	
	public ChangePasswordResult(String oldPassword, String newPassword, String passwordHint) {
		
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.passwordHint = passwordHint;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getPasswordHint() {
		return passwordHint;
	}
}
