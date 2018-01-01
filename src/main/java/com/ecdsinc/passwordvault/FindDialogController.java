package com.ecdsinc.passwordvault;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * Controller for the Find Dialog.  This dialog prompts the user for the text string to search for.  It
 * also allow the user to specify which fields to search for the string in and also whether the search 
 * should wrap back to the beginning of the list of vault entries when the end of the list is encountered.
 * 
 * Initial Values
 * 
 * The Find dialog accepts the following initial values.  The initial values must be supplied in the order listed
 * 
 *    findText
 *    inLocation
 *    inUserId
 *    inDescription
 *    wrapSearch
 *    caseSensitive
 * 
 * @author MarkB
 *
 */
public class FindDialogController implements PvDialogController<FindOperation> {

	@FXML	TextField			findCtrl;
	@FXML	CheckBox			inLocationCtrl;
	@FXML	CheckBox			inUserIdCtrl;
	@FXML	CheckBox			inDescriptionCtrl;
	@FXML	CheckBox			wrapSearchCtrl;
	@FXML	CheckBox			caseSensitiveCtrl;
	
	private Callback<ButtonType, FindOperation>	responseConverter;
	
	private static final int	INITIAL_VALUE_FIND_TEXT_INDEX = 0;
	private static final int	INITIAL_VALUE_IN_LOCATION_INDEX = 1;
	private static final int	INITIAL_VALUE_IN_USERID_INDEX = 2;
	private static final int	INITIAL_VALUE_IN_DESCRIPTION_INDEX = 3;
	private static final int	INITIAL_VALUE_WRAP_SEARCH_INDEX = 4;
	private static final int	INITIAL_VALUE_CASE_SENSITIVE_INDEX = 5;

    @FXML
    public void initialize() 
    	throws PasswordVaultException {
    	
    	//	The dialog has not been shown yet, and the input focus can not
    	//	be requested until the dialog is visible.  Using RunLater to 
    	//	request the focus after the dialog creation events have been
    	//	processed.
    	Platform.runLater(() -> findCtrl.requestFocus());
    	
    	responseConverter = new Callback<ButtonType, FindOperation>() {

			@Override
			public FindOperation call(ButtonType param) {

				FindOperation result = null;
				if (param == ButtonType.OK) {
					
					result = new FindOperation(findCtrl.getText(), inLocationCtrl.isSelected(), inUserIdCtrl.isSelected(), 
											inDescriptionCtrl.isSelected(), wrapSearchCtrl.isSelected(), caseSensitiveCtrl.isSelected());
				}
				return result;
			}
    	};
    }

    @Override
	public Callback<ButtonType, FindOperation> getResponseConverter() {

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

	   	findCtrl.setText(initialValues[INITIAL_VALUE_FIND_TEXT_INDEX]);
	   	inLocationCtrl.setSelected(Boolean.valueOf(initialValues[INITIAL_VALUE_IN_LOCATION_INDEX]));
	   	inUserIdCtrl.setSelected(Boolean.valueOf(initialValues[INITIAL_VALUE_IN_USERID_INDEX]));
	   	inDescriptionCtrl.setSelected(Boolean.valueOf(initialValues[INITIAL_VALUE_IN_DESCRIPTION_INDEX]));
	   	wrapSearchCtrl.setSelected(Boolean.valueOf(initialValues[INITIAL_VALUE_WRAP_SEARCH_INDEX]));
	   	caseSensitiveCtrl.setSelected(Boolean.valueOf(initialValues[INITIAL_VALUE_CASE_SENSITIVE_INDEX]));
	}
	
	
		
}
