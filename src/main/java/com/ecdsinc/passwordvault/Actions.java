package com.ecdsinc.passwordvault;

import java.io.File;
import java.security.Key;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import com.ecdsinc.security.EcdsKeyStore;
import com.ecdsinc.security.EcdsSecurityException;
import com.ecdsinc.util.Util;
import com.ecdsinc.util.UtilException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.stage.WindowEvent;

public class Actions {

	private static final String			FXML_NEW_ENTRY_DIALOG			= "NewEntryDialog.fxml";
	private static final String			FXML_PASSWORD_DIALOG			= "PasswordDialog.fxml";
	private static final String			FXML_CHANGE_PASSWORD_DIALOG		= "ChangePasswordDialog.fxml";
	private static final String			FXML_VAULT_PROPERTIES_DIALOG	= "VaultPropertiesDialog.fxml";
	private static final String			FXML_CREATE_VAULT_DIALOG		= "CreateVaultDialog.fxml";
	private static final String			FXML_FIND_DIALOG				= "FindDialog.fxml";
	private static final String			FXML_OPTIONS_DIALOG				= "OptionsDialog.fxml";
	private static final String			FXML_ABOUT_DIALOG				= "AboutDialog.fxml";
		
	public static void doOpenVault(ActionEvent event) {
		
		//	TODO: 	Open the vault in a separate thread to allow the wait cursor to be shown.  If the vault is opened in the event
		//			handling thread, the scene does not get rendered until after the vault is opened and after the wait cursor is
		//			changed back to the original cursor.
	   	try {
	    	
    		Environment env = Environment.getEnvironment();
    		Logger		logger = env.getLogger(PvLogger.LOGGER_VAULT);
    		    		
    		ChoiceBox<String> 	vaultListCtrl = getVaultListCtrl(event);
    		PasswordField		passwordField = getPasswordCtrl(event);
    		String				password = passwordField.getText();
    		
     		//	Get the selected vault and open it
    		String vaultName = vaultListCtrl.getSelectionModel().getSelectedItem();
    		logger.log(Level.INFO, "Opening Vault " + vaultName);
    		File vaultDir = env.getVaultDir();
    		
    		UserVault	vault = UserVault.loadUserVault(new File(vaultDir, vaultName + UserVault.VAULT_FILE_EXTENSTION), vaultName, password);
    		passwordField.setText("");
    		
    		env.setProperty(PasswordVaultProperties.LAST_OPEN_VAULT, vaultName + UserVault.VAULT_FILE_EXTENSTION);
    		
    		env.getApplication().setVaultContents(vault);
    		env.getApplication().setVisiblePane(PasswordVault.VAULT_CONTENTS_PANE_INDEX);
    		env.getApplication().updateControls();
    	}
    	catch (Exception except) {
    		
    		VaultUtil.displayErrorDialog("Error opening vault", except);
    	}
	}
	
	public static void doCloseVault(ActionEvent event) {

		try {
			
			saveVault(event, true);
			
			Environment env = Environment.getEnvironment();
			env.getApplication().setVaultContents(null);
			env.getApplication().setVisiblePane(PasswordVault.OPEN_VAULT_PANE_INDEX);
    		env.getApplication().updateControls();
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error closing vault", except);
		}
		
	}
	
	public static void doSaveVault(ActionEvent event) {
		
		saveVault(event, false);
	}
	
	public static void doCreateVault(ActionEvent event) {
		

	   	try {
	    	
    		Environment env = Environment.getEnvironment();
    		Logger		logger = env.getLogger(PvLogger.LOGGER_VAULT);
 
    		PvDialog<CreateVaultResult> dlg = new PvDialog<>(FXML_CREATE_VAULT_DIALOG, "Create New Vault");
    		Optional<CreateVaultResult> result = dlg.showAndWait();
    		if (result.isPresent()) {
    			
    			String vaultName = result.get().getVaultName();
    			UserVault	newVault = new UserVault.Builder(vaultName, result.get().getVaultPassword())
    										.description(result.get().getDescription())
    										.passwordHint(result.get().getPasswordHint()).build();
    									
	    		logger.log(Level.INFO, "Creating New Vault " + vaultName);
	    		File vaultDir = env.getVaultDir();
	    		newVault.store(new File(vaultDir, vaultName + UserVault.VAULT_FILE_EXTENSTION));
	    		
	    		env.setProperty(PasswordVaultProperties.LAST_OPEN_VAULT, vaultName + UserVault.VAULT_FILE_EXTENSTION);
	    		
	    		env.getApplication().setVaultContents(newVault);
	    		env.getApplication().setVisiblePane(PasswordVault.VAULT_CONTENTS_PANE_INDEX);
	    		env.getApplication().updateControls();
    		}
    	}
    	catch (Exception except) {
    		
    		VaultUtil.displayErrorDialog("Error creating new vault", except);
    	}
	}
	
	public static void doChangePassword(ActionEvent event) {
		
		Object							source = event.getSource();
		Environment						env = null;
		UserVault						vault = null;
		Optional<ChangePasswordResult> 	result = null;
		
		if (source instanceof MenuItem) {
			
			try {
				env = Environment.getEnvironment();
				vault = env.getApplication().getCurrentVault();
//				ChangePasswordDialog	dialog = new ChangePasswordDialog(vault.getVaultName(), vault.getPasswordHint(), parentWindow);
//				result = dialog.showAndWait();
				PvDialog<ChangePasswordResult> dlg = new PvDialog<>(FXML_CHANGE_PASSWORD_DIALOG, "Change Password", vault.getPasswordHint());
				result = dlg.showAndWait();
			}
			catch (PasswordVaultException except) {
				
				VaultUtil.displayErrorDialog("Error displaying Change Password Dialog", except);
			}
			
			try {
				
				if (result.isPresent()) {
				
					EcdsKeyStore keyStore = Environment.getEnvironment().getKeyStore();
					String	oldPassword = result.get().getOldPassword();
					String	newPassword = result.get().getNewPassword();
					String	passwordHint = result.get().getPasswordHint();
					
					Key vaultKey = null;
					try {
						
						vaultKey = keyStore.fetchKey(vault.getVaultName(), oldPassword);
					}
					catch (EcdsSecurityException except) {
						
						VaultUtil.displayErrorDialog("Error changing vault password", except);
						return;
					}
					
					try {
						vault.setPasswordHint(passwordHint);
						File vaultDir = env.getVaultDir();
						vault.store(new File (vaultDir, vault.getVaultName() + UserVault.VAULT_FILE_EXTENSTION));
					}
					catch (PasswordVaultException except) {
						
						VaultUtil.displayErrorDialog("Error updating vault password hint - Password NOT changed", except);
						return;
					}
					
					keyStore.storeKey(vaultKey, vault.getVaultName(), newPassword);
					keyStore.store();
					Logger	logger = env.getLogger(PvLogger.LOGGER_VAULT);
					logger.info("Password for vault " + vault.getVaultName() + " was changed");
					VaultUtil.displayMessageDlg("Change Vault Password", "Password for vault " + vault.getVaultName() + " was changed");
				}
				else {
					
					VaultUtil.displayMessageDlg("Change Vault Password", "Change Password request cancelled");
					return;
				}
			}
			catch (Exception except) {
				
				VaultUtil.displayErrorDialog("Error changing password - Password Not Changed", except);
			}
		}
	}
	
	public static void doEditVaultProperties(ActionEvent event) {
		
		try {
			
			UserVault	currentVault = getVault();
			
			PvDialog<VaultPropertiesResult> 	dlg = new PvDialog<>(FXML_VAULT_PROPERTIES_DIALOG, "VaultProperties",
					currentVault.getVaultName(), currentVault.getDescription(), currentVault.getPasswordHint(), (currentVault.getEncryptUserId()) ? "true" : "false");
			Optional<VaultPropertiesResult> result = dlg.showAndWait();
			if (result.isPresent()) {
				
				VaultPropertiesResult vaultPropertiesResult = result.get();
				currentVault.setDescription(vaultPropertiesResult.getDescription());
				currentVault.setPasswordHint(vaultPropertiesResult.getPasswordHint());
			}
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error edititing vault properties", except);
		}
	}
	
	public static void doExit(Event event) {
		
		saveVault(event, true);
		
		try {
		
			Environment.releaseEnvironment();
			
			//	If the Exit event is due to the close button (red x at top right of window) being
			//	pressed then the JavaFX platform will shut down the stage associated with the 
			//	window and close the program.
			//
			//	If the Exit event is due to the Exit menu item being selected, the JavaFX framework
			//	does not shut down the stage automatically.  Call the JavaFX platform to shutdown the
			//	stage and exit the application
			if (event.getEventType() != WindowEvent.WINDOW_CLOSE_REQUEST) {
				
				Platform.exit();
			}
		}
		catch (Exception except) {
			
			VaultUtil.displayErrorDialog("Error shutting down Password Vault", except);
		}
	}
	
	public static void doFind(ActionEvent event) {
		
		try {

			String	findText;
			String	inLocationStr;
			String	inUserIdStr;
			String 	inDescriptionStr;
			String	wrapSearchStr;
			String	caseSensitiveStr;
			
			Environment env = Environment.getEnvironment();
			FindOperation	findOperation = env.getApplication().getFindOperation();
			if (findOperation != null) {
				
				findText = findOperation.getFindText();
				inLocationStr = String.valueOf(findOperation.isInLocation());
				inUserIdStr = String.valueOf(findOperation.isInUserId());
				inDescriptionStr = String.valueOf(findOperation.isInDescription());
				wrapSearchStr = String.valueOf(findOperation.isWrapSearch());
				caseSensitiveStr = String.valueOf(findOperation.isCaseSensitive());
			}
			else {
				
				findText = "";
				inLocationStr = env.getProperty(PasswordVaultProperties.FIND_IN_LOCATION, PasswordVaultProperties.FIND_IN_LOCATION_DEFAULT);
				inUserIdStr = env.getProperty(PasswordVaultProperties.FIND_IN_USERID, PasswordVaultProperties.FIND_IN_USERID_DEFAULT);
				inDescriptionStr = env.getProperty(PasswordVaultProperties.FIND_IN_DESCRIPTION, PasswordVaultProperties.FIND_IN_DESCRIPTION_DEFAULT);
				wrapSearchStr = env.getProperty(PasswordVaultProperties.FIND_WRAP_SEARCH, PasswordVaultProperties.FIND_WRAP_SEARCH_DEFAULT);
				caseSensitiveStr = env.getProperty(PasswordVaultProperties.FIND_CASE_SENSITIVE, PasswordVaultProperties.FIND_CASE_SENSITIVE_DEFAULT);
			}
			
			PvDialog<FindOperation> dialog = new PvDialog<>(FXML_FIND_DIALOG, "Find", findText, 
															inLocationStr, inUserIdStr, inDescriptionStr,
															wrapSearchStr, caseSensitiveStr);
			Optional<FindOperation>	result = dialog.showAndWait();
			if (result.isPresent()) {
				
				findOperation = result.get();
				env.getApplication().setFindOperation(findOperation);
				
				env.setProperty(PasswordVaultProperties.FIND_IN_LOCATION, String.valueOf(findOperation.isInLocation()));
				env.setProperty(PasswordVaultProperties.FIND_IN_USERID, String.valueOf(findOperation.isInUserId()));
				env.setProperty(PasswordVaultProperties.FIND_IN_DESCRIPTION, String.valueOf(findOperation.isInDescription()));
				env.setProperty(PasswordVaultProperties.FIND_WRAP_SEARCH, String.valueOf(findOperation.isWrapSearch()));
				env.setProperty(PasswordVaultProperties.FIND_CASE_SENSITIVE, String.valueOf(findOperation.isCaseSensitive()));
				
				findNextEntry();
			}
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error error searching for text", except);
		}
	}
	
	public static void doFindNext(ActionEvent event) {

		try {
			
			findNextEntry();
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error error searching for text", except);
		}
	}

	public static void doEditOptions(ActionEvent event) {

		try {
			
			Environment	env = Environment.getEnvironment();
			boolean		idleLock = Boolean.valueOf(env.getProperty(PasswordVaultProperties.VAULT_IDLE_LOCK, PasswordVaultProperties.VAULT_IDLE_LOCK_DEFAULT));
			int			lockTimeout = Integer.valueOf(env.getProperty(PasswordVaultProperties.VAULT_LOCK_TIMEOUT, 
																	  PasswordVaultProperties.VAULT_LOCK_TIMEOUT_DEFAULT));
			String		vaultPath = env.getProperty(PasswordVaultProperties.VAULT_PATH, PasswordVaultProperties.VAULT_PATH_DEFAULT);
			
			PvDialog<OptionsResult> 	dialog = new PvDialog<>(FXML_OPTIONS_DIALOG, "PasswordVaultOptions", 
																String.valueOf(idleLock),
																String.valueOf(lockTimeout),
																vaultPath);
			Optional<OptionsResult>	resultOpt = dialog.showAndWait();
			if (resultOpt.isPresent()) {
				
				OptionsResult	results = resultOpt.get();
				
				boolean newIdleLock = results.isIdleLock();
				int		newLockTimeout = results.getLockTimeout();
				
				if ((idleLock != newIdleLock) || (lockTimeout != newLockTimeout)) {
					
					env.setProperty(PasswordVaultProperties.VAULT_IDLE_LOCK, String.valueOf(results.isIdleLock()));
					env.setProperty(PasswordVaultProperties.VAULT_LOCK_TIMEOUT, String.valueOf(results.getLockTimeout()));
					env.getApplication().updateLockSettings();
				}
				
				String	newVaultPath = results.getVaultDirectory();
				if (!vaultPath.equals(newVaultPath)) {
				
					env.setProperty(PasswordVaultProperties.VAULT_PATH, results.getVaultDirectory());
					env.openKeyStore();
					env.getApplication().updateOpenVaultPane();
				}
			}
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error editing the Password Vault Options");
		}
	}
	
	public static void doCreateEntry(ActionEvent event) {
		
		PvDialog<VaultEntry> dialog = new PvDialog<VaultEntry>(FXML_NEW_ENTRY_DIALOG, "Create new entry");
		
		Optional<VaultEntry> result = dialog.showAndWait();
		if (result.isPresent()) {
		
			try {

				UserVault	vault = getVault();
				VaultEntry	newEntry = result.get();
				
				//	The user id and password returned from the dialog are not encrypted.  They need to be
				//	encrypted before adding the vault entry to the vault
				if (vault.getEncryptUserId()) {
				
					boolean isBase64 = vault.getUserIdEncoding() == UserVault.ENCODING_BASE64;
					newEntry.setUserId(encryptVaultValue(newEntry.getUserId(), isBase64));
				}
				newEntry.setPassword(encryptVaultValue(newEntry.getPassword()));
				vault.addEntry(newEntry);
				resortEntryList();
				updateControls();
			}
			catch (PasswordVaultException except) {
				
				VaultUtil.displayErrorDialog("Error adding a new vault entry", except);
			}
		}
	}
	
	public static void doDeleteEntry(ActionEvent event) {

		try {
			
			UserVault	vault = getVault();
			VaultEntry	entry = getSelectedVaultEntry();
			
			boolean doDelete = VaultUtil.displayConfirmationDlg("Delete Entry", "Delete vault entry " + entry.getLocation() + "?");
			if (doDelete) {
				boolean removed = vault.removeEntry(entry);
				if (!removed) {
					
					VaultUtil.displayErrorDialog("Unable to remove selected vault entry");
				}
			}
			resortEntryList();
			updateControls();
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error deleting vaultEntry", except);
		}
	}
	
	public static void doModifyEntry(ActionEvent event) {
		
		JOptionPane.showMessageDialog(null, "Entry Modify Item clicked");
	}
	
	public static void doShowPassword(ActionEvent event) {

		VaultEntry	entry = null;
		
		try {
			
			entry = getSelectedVaultEntry();
			String	password = decryptVaultValue(entry.getPassword());
			
			PvDialog<String> 	dlg = new PvDialog<>(FXML_PASSWORD_DIALOG, "Show / Modify Password", password);
			Optional<String> result = dlg.showAndWait();
			if (result.isPresent()) {
				
				String newPassword = result.get();
				if (!newPassword.equals(password)) {
					
					try {
						
						entry.setPassword(encryptVaultValue(newPassword));
					}
					catch (PasswordVaultException except) {
							
						VaultUtil.displayErrorDialog("Error getting and encrypting new password", except);
					}
				}
			}
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error opening Show Password dialog", except);
		}
	}
	
	public static void doAbout(ActionEvent event) {
		
		
		PvDialog<Void> aboutDialog = new PvDialog<>(FXML_ABOUT_DIALOG, "About Password Vault");
		aboutDialog.showAndWait();
	}
	
	public static void doUnlockVault(ActionEvent event, String password) {
		
		try {
			
			PasswordVault	app = Environment.getEnvironment().getApplication();
			UserVault		vault = app.getCurrentVault();
			VaultView		vaultView = app.getVaultView();
			
			if (vault.unlock(password)) {
				
				vaultView.setLastActivity();
				app.setVisiblePane(PasswordVault.VAULT_CONTENTS_PANE_INDEX);
				app.updateControls();
			}
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error unlocking vault", except);
		}
	}
	
	private static Parent getOpenVaultPane(Button openVaultButton) {
		
		Parent	parent = openVaultButton.getParent();
		if (OpenVaultController.BUTTON_PANE_ID.equals(parent.getId())) {
			
			//	The Open Vault button is contained in an HBox.  Need to get the parent of the HBox to get the Open Vault Pane
			parent = parent.getParent();
		}	
		
		return parent;
	}
	
	private static ChoiceBox<String> getVaultListCtrl(ActionEvent event) {
		
		//	Find the Open Vault top level pane
   		Button	clickedButton = (Button) event.getSource();
   		Parent	parent = getOpenVaultPane(clickedButton);
   		
		ChoiceBox<String> vaultListCtrl = null;
		
		for (Node curCtrl : parent.getChildrenUnmodifiable()) {
			
			if (OpenVaultController.VAULT_LIST_CTRL_ID.equals(curCtrl.getId())) {
				
				vaultListCtrl = asChoiceBox(curCtrl);
				break;
			}
		}
		return vaultListCtrl;
	}
	
	private static PasswordField getPasswordCtrl(ActionEvent event) {
		
		//	Find the Open Vault top level pane
   		Button	clickedButton = (Button) event.getSource();
   		Parent	parent = getOpenVaultPane(clickedButton);
		PasswordField passwordCtrl = null;
		
		for (Node curCtrl : parent.getChildrenUnmodifiable()) {
			
			if (OpenVaultController.PASSWORD_CTRL_ID.equals(curCtrl.getId())) {
				
				passwordCtrl = (PasswordField) curCtrl;
				break;
			}
		}
		return passwordCtrl;
	}
	
	private static void saveVault(Event event, boolean prompt) {

		try {
		
			Environment	env = Environment.getEnvironment();
			UserVault curVault = env.getApplication().getCurrentVault();
			if((curVault != null) && (curVault.isModified())) {
				
				if (prompt) {
					
					if (!VaultUtil.displayConfirmationDlg("Save Vault", "Vault " + curVault.getVaultName() + " has been modified.\nDo you want to save the changes?")) {
						
						return;
					}
				}
					
				File vaultDir = env.getVaultDir();
				curVault.store(new File (vaultDir, curVault.getVaultName() + UserVault.VAULT_FILE_EXTENSTION));
				env.getApplication().updateControls();
			}
		}
		catch (Exception except) {
			
			VaultUtil.displayErrorDialog("Error Saving Vault", except);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static ChoiceBox<String> asChoiceBox(Node ctrl) {
		
		return (ChoiceBox<String>) ctrl;
	}
	
	private static UserVault getVault()
		throws PasswordVaultException {
		
		Environment	env = Environment.getEnvironment();
		UserVault	vault = env.getApplication().getCurrentVault();
		if (vault == null) {

			throw new PasswordVaultException("There is not vault open");
		}
		
		return vault;
	}
	
	private static void resortEntryList() 
		throws PasswordVaultException {
		
		Environment env = Environment.getEnvironment();
		VaultView	view = env.getApplication().getVaultView();
		if (view != null) {
			
			view.sort();
		}
	}
	//	Gets the entry that was selected in the current vault.
	//	Returns null if there is no selection or there is not an open vault
	private static VaultEntry getSelectedVaultEntry() 
		throws PasswordVaultException {
		
		Environment	env = Environment.getEnvironment();
		VaultView	view = env.getApplication().getVaultView();
		VaultEntry	entry = null;
		
		if (view != null) {
			
			entry = view.getSelectionModel().getSelectedItem();
		}
		
		return entry;
	}
	
	private static String encryptVaultValue(String value) 
		throws PasswordVaultException {
		
		return encryptVaultValue(value, true);
	}
	
	private static String encryptVaultValue(String value, boolean isBase64) 
		throws PasswordVaultException {
		
		Environment	env = Environment.getEnvironment();
		UserVault	vault = env.getApplication().getCurrentVault();
		if (vault != null) {

			byte[] encryptedBytes = vault.encrypt(value);
			
			if (isBase64) {
			
				return Base64.getEncoder().encodeToString(encryptedBytes);
			}
			else {
				
				try {
				
					return Util.bytesToHexString(encryptedBytes);
				}
				catch (UtilException except) {
					
					throw new PasswordVaultException("Error encrypting vault value", except);
				}
			}
		}
		else {
			
			throw new PasswordVaultException("Error encrypting value - there is no open vault");
		}
	}

	private static String decryptVaultValue(String encryptedValue)
		throws PasswordVaultException {
		
		return decryptVaultValue(encryptedValue, true);
	}
	
	private static String decryptVaultValue(String encryptedValue, boolean isBase64) 
		throws PasswordVaultException {
		
		Environment	env = Environment.getEnvironment();
		UserVault	vault = env.getApplication().getCurrentVault();
		if (vault != null) {

			byte	valueBytes[] = null;
			if (isBase64) {
			
				valueBytes = Base64.getDecoder().decode(encryptedValue);
			}
			else {
				
				try {
				
					valueBytes = Util.HexStringToBytes(encryptedValue);
				}
				catch (UtilException except) {
					
					throw new PasswordVaultException("Error decrypting vault value", except);
				}
			}
			return vault.decrypt(valueBytes);
		}
		else {
			
			throw new PasswordVaultException("Error decrypting value - there is no open vault");
		}
	}
	
	private static void findNextEntry()
		throws PasswordVaultException {
		
		PasswordVault	app = Environment.getEnvironment().getApplication();
		VaultView		view = app.getVaultView();

		VaultEntry	entry = app.getFindOperation().findNext();
		if (entry != null) {
			view.getSelectionModel().clearSelection();
			view.getSelectionModel().select(entry);
			view.scrollTo(entry);
		}
		else {
			
			view.getSelectionModel().clearSelection();
		}
	}
	
	private static void updateControls() 
		throws PasswordVaultException {
		
		Environment.getEnvironment().getApplication().updateControls();
	}
}
