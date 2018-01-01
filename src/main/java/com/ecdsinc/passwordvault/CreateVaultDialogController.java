package com.ecdsinc.passwordvault;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Controller for the Create Vault Dialog.  This dialog will prompt the user to enter the name of 
 * the new vault.  The dialog will also allow the user to specify a new description and a 
 * password hint
 * 
 * Initial Values
 * 
 * The Create Vault Dialog does not use any initial values
 * 
 * @author MarkB
 *
 */
public class CreateVaultDialogController implements PvDialogController<CreateVaultResult> {

	@FXML	TextField			vaultNameCtrl;
	@FXML	PasswordField		vaultPasswordCtrl;
	@FXML	PasswordField		confirmVaultPasswordCtrl;
	@FXML	TextField			descriptionCtrl;
	@FXML	TextField			passwordHintCtrl;
	@FXML	DialogPane			dialogPane;

	private Callback<ButtonType, CreateVaultResult>	responseConverter;
	
    @FXML
    public void initialize() 
    	throws PasswordVaultException {

    	Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    	okButton.addEventFilter(ActionEvent.ACTION, event -> {
    		
    		validateVaultProperties(event);
    	});

       	responseConverter = new Callback<ButtonType, CreateVaultResult>() {

    			@Override
    			public CreateVaultResult call(ButtonType param) {

    				CreateVaultResult	result = null;
    				if (param == ButtonType.OK) {
    					
    					result = new CreateVaultResult(vaultNameCtrl.getText(), vaultPasswordCtrl.getText(),
    												   descriptionCtrl.getText(), passwordHintCtrl.getText());
    				}
    				return result;
    			}
        	};
    }

     
	@Override
	public Callback<ButtonType, CreateVaultResult> getResponseConverter() {

		return responseConverter;
	}

	/**
	 * Sets the initial values for the Show Password Dialog
	 * 
	 * Initial Values
     * 
     * The Show Password Dialog requires the following initial values.  They must be specified in the order shown
     * 
     *    currentPassword
     *
	 */
	@Override
	public void setInitialValues(String... initialValues) {

		// This dialog does not use any initial values
	}	
	
	private void validateVaultProperties(ActionEvent event) {
		
		String	message = null;
		String	vaultName = vaultNameCtrl.getText();
		String	vaultPassword = vaultPasswordCtrl.getText();
		String	confirmVaultPassword = confirmVaultPasswordCtrl.getText();
		
		if ((vaultName == null) || (vaultName.length() == 0)) {
			
			message = "Invalid Vault Name - A Vault Name must be specified";
		}
		else if ((vaultPassword == null) || (vaultPassword.length() == 0)) {
			
			message = "Invalid Vault Password - A Vault Password must be specified";
		}
		else if (!vaultPassword.equals(confirmVaultPassword)) {
			
			message = "Passwords do not match";
		}
		
		if (message != null) {
		
			event.consume();
			VaultUtil.displayErrorDialog(message);
		}
	}
}
