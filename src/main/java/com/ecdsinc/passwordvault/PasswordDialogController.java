package com.ecdsinc.passwordvault;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Controller for the Show Password Dialog.  This dialog will display the current password, either
 * masked or in plain text.  The user may also change the current password in this dialog.
 * 
 * Initial Values
 * 
 * The Show Password Dialog requires the following initial values.  They must be specified in the order shown
 * 
 *    currentPassword
 * 
 * @author MarkB
 *
 */
public class PasswordDialogController implements PvDialogController<String> {

	@FXML	PasswordField		passwordCtrl;
	@FXML	TextField			clearTextPasswordCtrl;
	@FXML	CheckBox			hidePasswordCtrl;
	
	private Callback<ButtonType, String>	responseConverter;
	
	private static final int	INITIAL_VALUE_CURRENT_PASSWORD_INDEX = 0;
	
    @FXML
    public void initialize() 
    	throws PasswordVaultException {
    	
     	clearTextPasswordCtrl.setVisible(false);
    	
    	responseConverter = new Callback<ButtonType, String>() {

			@Override
			public String call(ButtonType param) {

				String password = null;
				if (param == ButtonType.OK) {
					
					if (hidePasswordCtrl.isSelected()) {
					
						password = passwordCtrl.getText();
					}
					else {
					
						password = clearTextPasswordCtrl.getText();
					}
				}
				return password;
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
	public Callback<ButtonType, String> getResponseConverter() {

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

	   	passwordCtrl.setText(initialValues[INITIAL_VALUE_CURRENT_PASSWORD_INDEX]);
	}
	
	
		
}
