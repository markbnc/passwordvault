package com.ecdsinc.passwordvault;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
 
public class OpenVaultController {
	
	@FXML ChoiceBox<String>			vaultListCtrl;
	@FXML TextField					passwordHintField;
	@FXML Button					openVaultButton;
	
	public static final String	VAULT_LIST_CTRL_ID = "vaultListCtrl";
	public static final String	PASSWORD_CTRL_ID = "passwordField";
	public static final String	PASSWORD_HINT_CTRL_ID = "passwordHintField";
	public static final String	BUTTON_PANE_ID = "buttonPane";
	
    @FXML
    public void initialize() 
    	throws PasswordVaultException {
    	
    	try {
    		
    		Environment	env = Environment.getEnvironment();
    		File vaultDir = env.getVaultDir();
	    	String[] vaultNames = vaultDir.list(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					
					int extIndex = name.lastIndexOf('.');
					if (extIndex > 0) {
						String	ext = name.substring(extIndex);
						return UserVault.VAULT_FILE_EXTENSTION.equals(ext) ? true : false;
					}
					return false;
				}
			});
   
	    	//	Initialize the Vault List
	    	ArrayList<String> 		vaultList = new ArrayList<>();
	    	int						defaultSelection = 0;
	    	String					lastOpenedVault = env.getProperty(PasswordVaultProperties.LAST_OPEN_VAULT, PasswordVaultProperties.LAST_OPEN_VAULT_DEFAULT);
	    	
	    	for (int index = 0; index < vaultNames.length; index++) {
	    		
	    		String	curVaultName = vaultNames[index];
	    		if (curVaultName.equals(lastOpenedVault)) {
	    			
	    			defaultSelection = index;
	    		}
				int extIndex = curVaultName.lastIndexOf('.');
				String displayName = curVaultName.substring(0, extIndex);
	    		vaultList.add(displayName);
	    	}
	    	
	    	vaultListCtrl.setItems(FXCollections.observableArrayList(vaultList));
	    	vaultListCtrl.getSelectionModel().select(defaultSelection);
	    	vaultListCtrl.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
	    		
	    		updatePasswordHint();
	    	});
	    	
	    	updatePasswordHint();
    	}
    	catch (Exception except) {
    		
    		throw new PasswordVaultException("Error initializing Open Vault pane", except);
    	}
    }

    @FXML 
    protected void handleSubmitButtonAction(ActionEvent event) {

    	Actions.doOpenVault(event);
     }
    
    public void updatePasswordHint() {
    	
   		String vaultName = vaultListCtrl.getSelectionModel().getSelectedItem();
   		if (vaultName != null) {

   			String passwordHint = UserVault.getPasswordHint(vaultName);
   			passwordHintField.setText(passwordHint);
   		}
    }
    
   public void handleVisibleChanged(boolean newVisibleValue) {
    	
    	openVaultButton.setDefaultButton(newVisibleValue);
    }

}
