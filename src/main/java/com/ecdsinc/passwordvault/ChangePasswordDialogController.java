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
 * Controller for the Show Password Dialog.  This dialog will display the current password, either
 * masked or in plain text.  The user may also change the current password in this dialog.
 * 
 * Initial Values
 * 
 * The Show Password Dialog requires the following initial values.  They must be specified in the order shown
 * 
 *    passwordHint
 * 
 * @author MarkB
 *
 */
public class ChangePasswordDialogController implements PvDialogController<ChangePasswordResult> {

	@FXML	PasswordField		currentPasswordCtrl;
	@FXML	PasswordField		newPasswordCtrl;
	@FXML	PasswordField		confirmNewPasswordCtrl;
	@FXML	TextField			passwordHintCtrl;
	@FXML	DialogPane			dialogPane;
	
	private Callback<ButtonType, ChangePasswordResult>	responseConverter;
	
	
	private static final int	INITIAL_VALUE_PASSWORD_HINT_INDEX = 0;
	
    @FXML
    public void initialize() 
    	throws PasswordVaultException {
    	
    	Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    	okButton.addEventFilter(ActionEvent.ACTION, event -> {
    		
    		validateNewPassword(event);
    	});

    	responseConverter = new Callback<ButtonType, ChangePasswordResult>() {

			@Override
			public ChangePasswordResult call(ButtonType param) {
				
				ChangePasswordResult	result = null;
				if (param == ButtonType.OK) {
					
					result = new ChangePasswordResult(
							currentPasswordCtrl.getText(), 
							newPasswordCtrl.getText(),
							passwordHintCtrl.getText());
				}
				return result;
			}
    	};
    }

    @FXML
    protected void validateNewPassword(ActionEvent event) {
    	
    	String	newPassword = newPasswordCtrl.getText();
    	String	confirmPassword = confirmNewPasswordCtrl.getText();
    	
    	if ((newPassword == null) || !newPassword.equals(confirmPassword)) {
    		
    		if (newPassword == null) {
    		
    			VaultUtil.displayErrorDialog("Invalid Password - New Password can not be null");
    		}
    		else {
    			
    			VaultUtil.displayErrorDialog("Invalid Password - New Passwords do not match");
    		}
    		event.consume();
    	}
    }
     
	@Override
	public Callback<ButtonType, ChangePasswordResult> getResponseConverter() {

		return responseConverter;
	}

	/**
	 * Sets the initial values for the Show Password Dialog
	 * 
	 * Initial Values
     * 
     * The Show Password Dialog requires the following initial values.  They must be specified in the order shown
     * 
     *    passwordHint
     *
	 */
	@Override
	public void setInitialValues(String... initialValues) {

	   	passwordHintCtrl.setText(initialValues[INITIAL_VALUE_PASSWORD_HINT_INDEX]);
	}
	
	
		
}
