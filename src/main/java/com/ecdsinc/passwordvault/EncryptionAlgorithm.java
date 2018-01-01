package com.ecdsinc.passwordvault;

public enum EncryptionAlgorithm {

	UNKNOWN(0),
	DES(1),				//	Version 2 Vault files use DES encryption
	AES128(2),
	AES256(3);
	
	private final int value;
	
	EncryptionAlgorithm(int value) {
		
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static EncryptionAlgorithm fromValue(int value) {
		
		switch (value) {
		
			case 1:		return DES;
			case 2:		return AES128;
			case 3:		return AES256;
			default:	return UNKNOWN;
		}
	}

	@Override
	public String toString() {

		switch (value) {
		
			case 1:		return "DES";
			case 2:		return "AES128";
			case 3:		return "AES256";
			default:	return "UNKNOWN";
		}
	}
	
	
}
