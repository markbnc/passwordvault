package com.ecdsinc.passwordvault;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//    <VaultEntry>
//        <Id>1</Id>
//        <Description>Non-Wireless Router Config Page</Description>
//        <Location>192.168.0.1</Location>
//        <UserId>b24e5132c2d5fe04</UserId>
//        <Password>KBs5ANsW6e0=</Password>
//    </VaultEntry>

public class VaultEntry implements Observable {

	private static final List<String> 	encryptedFields = new ArrayList<>();

	private IntegerProperty		id = new SimpleIntegerProperty();
	private StringProperty		description = new SimpleStringProperty();
	private StringProperty		location = new SimpleStringProperty();
	private StringProperty		userId = new SimpleStringProperty();
	private StringProperty		password = new SimpleStringProperty();
	
	private ArrayList<InvalidationListener> listeners = new ArrayList<>();
	
	static {
		
		encryptedFields.add(UserVault.ELEMENT_USER_ID);
		encryptedFields.add(UserVault.ELEMENT_PASSWORD);
	}
	
	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}
	
	public IntegerProperty idProperty() {
		
		return id;
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public StringProperty descriptionProperty() {
		
		return description;
	}

	public String getLocation() {
		return location.get();
	}

	public void setLocation(String location) {
		this.location.set(location);
	}

	public StringProperty locationProperty() {
		
		return location;
	}

	public String getUserId() {
		return userId.get();
	}

	public void setUserId(String userId) {
		
		this.userId.set(userId);
	}

	public StringProperty userIdProperty() {
		
		return userId;
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}
	
	public StringProperty passwordProperty() {
		
		return password;
	}

	@Override
	public void addListener(InvalidationListener listener) {

		listeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		
		listeners.remove(listener);
	}
}
