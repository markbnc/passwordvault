package com.ecdsinc.passwordvault;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * This is the controller for the Password Vault Options dialog.  The user can specify the
 * whether the application should lock after a specified period of inactivity.  The user can
 * also specify the directory the application will look for and store vaults.
 * 
 * Initial Values
 * 
 * The Options dialog takes the following initialization values.  The values must be specified
 * in the order listed
 * 
 *    idleLock			- true or false
 *    lockTimeout		- an integer specifying the idle time in minutes after which the application 
 *    				  	  will lock.  The value must be between 1 and 60 inclusive
 *    vaultDirectory	- a string specifying the full path to the directory that contains the vaults and keystore
 * 
 * @author MarkB
 *
 */
public class OptionsDialogController implements PvDialogController<OptionsResult> {

	@FXML	VBox				generalBox;
	@FXML	CheckBox			idleLockCtrl;
	@FXML	Spinner<Integer>	lockTimeoutCtrl;
	
	@FXML	VBox				pathsBox;
	@FXML	TextField			vaultPathCtrl;
	@FXML	Button				browseButton;
	
	@FXML	DialogPane		dialogPane;
	
	private Callback<ButtonType, OptionsResult>  responseConverter;
	
	private static final int	INITIAL_VALUE_IDLE_LOCK_INDEX = 0;
	private static final int	INITIAL_VALUE_LOCK_TIMEOUT_INDEX = 1;
	private static final int	INITIAL_VALUE_VAULT_PATH_INDEX = 2;

    @FXML
    public void initialize() 
    	throws PasswordVaultException {
    	
    	responseConverter = new Callback<ButtonType, OptionsResult>() {

			@Override
			public OptionsResult call(ButtonType param) {

				OptionsResult result = null;
				if (param == ButtonType.OK) {

					result = new OptionsResult(idleLockCtrl.isSelected(), lockTimeoutCtrl.getValue(), vaultPathCtrl.getText());
				}
				return result;
			}
    	};
    }
    
    @FXML
    protected void handleIdleLock(ActionEvent event) {
    	
    	lockTimeoutCtrl.setDisable(!idleLockCtrl.isSelected());
    }

    @FXML
    protected void handleBrowse(ActionEvent event) {
    
    	VaultUtil.displayMessageDlg("Browse", "The Browse button was clicked");
    }
    
	@Override
	public Callback<ButtonType, OptionsResult> getResponseConverter() {

		return responseConverter;
	}

	@Override
	public void setInitialValues(String... initialValues) {

		boolean 	idleLock = Boolean.valueOf(initialValues[INITIAL_VALUE_IDLE_LOCK_INDEX]);
		idleLockCtrl.setSelected(idleLock);
		lockTimeoutCtrl.getValueFactory().setValue(Integer.valueOf(initialValues[INITIAL_VALUE_LOCK_TIMEOUT_INDEX]));
		lockTimeoutCtrl.setDisable(!idleLock);
		vaultPathCtrl.setText(initialValues[INITIAL_VALUE_VAULT_PATH_INDEX]);
	}

}
