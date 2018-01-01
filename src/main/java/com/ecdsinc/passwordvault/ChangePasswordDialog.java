package com.ecdsinc.passwordvault;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;

public class ChangePasswordDialog extends Dialog<ChangePasswordResult> {

	String		vaultName;
	String		passwordHint;
	
	private static final int	PASSWORD_FIELD_WIDTH 				= 300; 
	
	Label			oldPasswordLabel;
	PasswordField	oldPasswordCtrl;
	Label			newPasswordLabel;
	PasswordField	newPasswordCtrl;
	Label			confirmPasswordLabel;
	PasswordField	confirmPasswordCtrl;
	Label			passwordHintLabel;
	TextField		passwordHintCtrl;

	public ChangePasswordDialog(String vaultName, String passwordHint, Window parent) {
		
		this.vaultName = vaultName;
		this.passwordHint = passwordHint;
		
		setTitle("Change Password for Vault " + vaultName);
		setResizable(false);
		initOwner(parent);
		initModality(Modality.APPLICATION_MODAL);
		
		initControls();
	}
	
	private void initControls() {
		
		oldPasswordLabel = new Label("Password");
		
		oldPasswordCtrl = new PasswordField();
		oldPasswordCtrl.setEditable(true);
		oldPasswordCtrl.setPrefWidth(PASSWORD_FIELD_WIDTH);

		newPasswordLabel = new Label("New Password");
		
		newPasswordCtrl = new PasswordField();
		newPasswordCtrl.setEditable(true);
		newPasswordCtrl.setPrefWidth(PASSWORD_FIELD_WIDTH);

		confirmPasswordLabel = new Label("Confirm Password");
		
		confirmPasswordCtrl = new PasswordField();
		confirmPasswordCtrl.setEditable(true);
		confirmPasswordCtrl.setPrefWidth(PASSWORD_FIELD_WIDTH);
		
		passwordHintLabel = new Label("Password Hint");
		
		passwordHintCtrl = new TextField();
		passwordHintCtrl.setEditable(true);
		passwordHintCtrl.setPrefWidth(PASSWORD_FIELD_WIDTH);
		passwordHintCtrl.setText(passwordHint);

		ButtonType changePasswordButtonType = new ButtonType("Change Password", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(changePasswordButtonType, ButtonType.CANCEL);
		
		Button	changePasswordButton = (Button) getDialogPane().lookupButton(changePasswordButtonType);
		changePasswordButton.addEventFilter(ActionEvent.ACTION, event -> {
			
			String newPassword = newPasswordCtrl.getText();
			String confirmPassword = confirmPasswordCtrl.getText();
			
			if ((newPassword.length() == 0) ||
				!newPassword.equals(confirmPassword)) {
				
				VaultUtil.displayMessageDlg("Invalid Password", "The new password values do not match");
				event.consume();
			}
		});

		GridPane	grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		
		grid.add(oldPasswordLabel, 0, 0);
		grid.add(oldPasswordCtrl, 1, 0);
		grid.add(newPasswordLabel, 0, 1);
		grid.add(newPasswordCtrl, 1, 1);
		grid.add(confirmPasswordLabel, 0, 2);
		grid.add(confirmPasswordCtrl, 1, 2);
		grid.add(passwordHintLabel, 0, 3);
		grid.add(passwordHintCtrl, 1, 3);
		
		getDialogPane().setContent(grid);
		
		// Request focus on the username field by default.
		Platform.runLater(() -> oldPasswordCtrl.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		setResultConverter(clickedButton -> {
		    if (clickedButton == changePasswordButtonType) {
		    	
		    	return new ChangePasswordResult(oldPasswordCtrl.getText(), newPasswordCtrl.getText(), passwordHintCtrl.getText());
		    }
		    return null;
		});
	}
}
