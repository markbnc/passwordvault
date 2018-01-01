package com.ecdsinc.passwordvault;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Controller for the Vault Properties Dialog.  This dialog display the current vault property values.  The user
 * may modifiy the description and password hint values.  The vault name and whether user ids in the vault can
 * not be changed.
 * 
 * Initial Values
 * 
 * The Show Password Dialog requires the following initial values.  They must be specified in the order shown
 * 
 *    vaultName
 *    description
 *    passwordHint
 *    encryptedUserId - the value of this initial value string must be either "true" or "false"
 * 
 * @author MarkB
 *
 */
public class VaultPropertiesDialogController implements PvDialogController<VaultPropertiesResult> {

	@FXML	Label				vaultNameCtrl;
	@FXML	TextField			descriptionCtrl;
	@FXML	TextField			passwordHintCtrl;
	@FXML	Label				encryptedUserIdCtrl;

	private Callback<ButtonType, VaultPropertiesResult>	responseConverter;
	
	private static final int	INITIAL_VALUE_VAULT_NAME_INDEX = 0;
	private static final int	INITIAL_VALUE_DESCRIPTION_INDEX = 1;
	private static final int	INITIAL_VALUE_PASSWORD_HINT_INDEX = 2;
	private static final int	INITIAL_VALUE_ENCRYPTED_USER_ID_INDEX = 3;
	
    @FXML
    public void initialize() 
    	throws PasswordVaultException {

       	responseConverter = new Callback<ButtonType, VaultPropertiesResult>() {

    			@Override
    			public VaultPropertiesResult call(ButtonType param) {

    				VaultPropertiesResult	result = null;
    				if (param == ButtonType.OK) {
    					
    					result = new VaultPropertiesResult(descriptionCtrl.getText(), passwordHintCtrl.getText());
    				}
    				return result;
    			}
        	};
    }

     
	@Override
	public Callback<ButtonType, VaultPropertiesResult> getResponseConverter() {

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

		vaultNameCtrl.setText(initialValues[INITIAL_VALUE_VAULT_NAME_INDEX]);
		descriptionCtrl.setText(initialValues[INITIAL_VALUE_DESCRIPTION_INDEX]);
		passwordHintCtrl.setText(initialValues[INITIAL_VALUE_PASSWORD_HINT_INDEX]);
		encryptedUserIdCtrl.setText(initialValues[INITIAL_VALUE_ENCRYPTED_USER_ID_INDEX]);
	}	
}
