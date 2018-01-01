package com.ecdsinc.passwordvault;

import java.net.URL;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class VaultUtil {

	private static final String STYLESHEET_PATH = "/com/ecdsinc/passwordvault/PasswordVault.css";
	private static final String ICON_PATH = "/images/vault64_2.png";
	
//	public static void displayError(String message, Throwable except) {
//		
//		StringBuilder	fullMessage = new StringBuilder();
//		if (except != null) {
//			fullMessage.append(except.toString());
//			
//			Throwable	cause = except.getCause();
//			while (cause != null) {
//				
//				fullMessage.append("\n   Caused By: ");
//				fullMessage.append(cause);
//				cause = cause.getCause();
//			}
//		}
//		
//		Alert alert = new Alert(AlertType.ERROR);
//		alert.setTitle("Password Vault");
//		alert.setHeaderText(message);
//		
//		Text text = new Text(fullMessage.toString());
//		alert.getDialogPane().setContent(text);
//		alert.showAndWait();
// 	}
	
	public static boolean displayConfirmationDlg(String title, String message) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		
		setStyleSheetandIcon(alert, STYLESHEET_PATH, ICON_PATH);
		
		Optional<ButtonType> result = alert.showAndWait();
		return (result.get() == ButtonType.OK) ? true : false;
	}
	
	public static void displayMessageDlg(String title, String message) {
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		
		setStyleSheetandIcon(alert, STYLESHEET_PATH, ICON_PATH);

		alert.showAndWait();
	}
	
	public static void displayErrorDialog(String message) {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(message);
		alert.setHeaderText(null);
		alert.setTitle("Error");

		setStyleSheetandIcon(alert, STYLESHEET_PATH, ICON_PATH);

		alert.showAndWait();
	}
	
	public static void displayErrorDialog(String message, Throwable error) {
		
		StringBuilder newMessage = new StringBuilder(message);
		newMessage.append(" - Error: ");
		newMessage.append(error);
		
		Throwable cause = error.getCause();
		while (cause != null) {
			
			newMessage.append("\n   Caused by:");
			newMessage.append(cause);
			cause = cause.getCause();
		}
		
		displayErrorDialog(newMessage.toString());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T doCast (Object obj) {
		
		return (T) obj;
	}
	
	private static void setStyleSheetandIcon(Alert alert, String stylesheetPath, String iconPath) {
		
		DialogPane dialogPane = alert.getDialogPane();

		URL cssURL = VaultUtil.class.getResource(stylesheetPath);
		dialogPane.getStylesheets().add(cssURL.toExternalForm());

		URL iconURL = VaultUtil.class.getResource(iconPath);
		Image icon = new Image(iconURL.toString());
		Stage stage = (Stage) dialogPane.getScene().getWindow();
		stage.getIcons().add(icon);
	}
}
