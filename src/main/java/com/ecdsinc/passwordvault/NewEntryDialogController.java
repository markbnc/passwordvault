package com.ecdsinc.passwordvault;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * This is the controller for the new vault entry dialog.  The user can specify the
 * location, username, password and description of a new vault entry.
 * 
 * Initial Values
 * 
 * The new vault entry dialog does not require any initial values
 * 
 * @author MarkB
 *
 */
public class NewEntryDialogController implements PvDialogController<VaultEntry> {

	@FXML	TextField		locationCtrl;
	@FXML	TextField		userNameCtrl;
	@FXML	PasswordField	passwordCtrl;
	@FXML	TextField		clearTextPasswordCtrl;
	@FXML	TextField		descriptionCtrl;
	@FXML	CheckBox		hidePasswordCtrl;
	
	private Callback<ButtonType, VaultEntry>  responseConverter;
	
    @FXML
    public void initialize() 
    	throws PasswordVaultException {
    	
    	clearTextPasswordCtrl.setVisible(false);
    	
    	responseConverter = new Callback<ButtonType, VaultEntry>() {

			@Override
			public VaultEntry call(ButtonType param) {

				VaultEntry entry = null;
				if (param == ButtonType.OK) {

					entry = new VaultEntry();
					entry.setLocation(locationCtrl.getText());
					entry.setUserId(userNameCtrl.getText());
					entry.setDescription(descriptionCtrl.getText());
					
					if (hidePasswordCtrl.isSelected()) {
						
						entry.setPassword(passwordCtrl.getText());
					}
					else {
						
						entry.setPassword(clearTextPasswordCtrl.getText());
					}
				}
				return entry;
			}
    	};
    }
    
    @FXML
    protected void handleHidePasswordCtrl(ActionEvent event) {
    	
    	CheckBox hidePassword = (CheckBox) event.getSource();
    	if (hidePassword.isSelected()) {
    		
    		//	Going from not hidden to hidden
    		passwordCtrl.setText(clearTextPasswordCtrl.getText());
    		clearTextPasswordCtrl.setVisible(false);
    		passwordCtrl.setVisible(true);
    	}
    	else {
    		
    		//	Going from hidden to not hidden
    		clearTextPasswordCtrl.setText(passwordCtrl.getText());
    		passwordCtrl.setVisible(false);
    		clearTextPasswordCtrl.setVisible(true);    		
    	}
    }

	@Override
	public Callback<ButtonType, VaultEntry> getResponseConverter() {

		return responseConverter;
	}

	@Override
	public void setInitialValues(String... initialValues) {

		//	The new entry dialog does not need any initial values so the initialValues array passed in is ignored.
	}

}
