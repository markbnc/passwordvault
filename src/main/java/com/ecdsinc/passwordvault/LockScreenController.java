package com.ecdsinc.passwordvault;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
 
public class LockScreenController {
	
	@FXML Label				messageCtrl;
	@FXML PasswordField		passwordCtrl;
	@FXML Button			submitButton;
	
   @FXML
    public void initialize() 
    	throws PasswordVaultException {
    }

    @FXML 
    protected void handleSubmit(ActionEvent event) {

    	Actions.doUnlockVault(event, passwordCtrl.getText());
    	passwordCtrl.setText("");
    }
    
    public void handleVisibleChanged(boolean newVisibleValue) {
    	
    	submitButton.setDefaultButton(newVisibleValue);
    }
}
