package com.ecdsinc.passwordvault;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class MenuBar extends javafx.scene.control.MenuBar {
	
	private Menu		fileMenu;
	private MenuItem	fileClose;
	private MenuItem	fileSave;
	private MenuItem	fileNew;
	private MenuItem	fileChangePassword;
	private MenuItem	fileProperties;
	private MenuItem	fileExit;
	
	private Menu		editMenu;
	private MenuItem	editFind;
	private MenuItem	editFindNext;
	private MenuItem	editOptions;
	
	private Menu		entryMenu;
	private MenuItem	entryNew;
	private MenuItem	entryDelete;
	private MenuItem	entryModify;
	private MenuItem	entryShowPassword;
	
	private Menu		helpMenu;
	private MenuItem	helpAbout;

	public MenuBar() {
		
		super();
		
		//	File menu
		//	=============================================
		fileMenu = new Menu("_File");
		
		fileClose = new MenuItem("_Close Vault");
		fileClose.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doCloseVault(event);
			}
		});
		fileMenu.getItems().add(fileClose);

		fileSave = new MenuItem("_Save Vault");
		fileSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doSaveVault(event);
			}
		});
		fileSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
		fileMenu.getItems().add(fileSave);
		
		fileNew = new MenuItem("Create _New Vault ...");
		fileNew.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doCreateVault(event);
			}
		});
		fileMenu.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
		fileMenu.getItems().add(fileNew);

		fileChangePassword = new MenuItem("Change Vault _Password ...");
		fileChangePassword.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doChangePassword(event);
			}
		});
		fileMenu.getItems().add(fileChangePassword);
		
		fileProperties = new MenuItem("_Vault Properties ...");
		fileProperties.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doEditVaultProperties(event);
			}
		});
		fileMenu.getItems().add(fileProperties);
		
		fileExit = new MenuItem("_Exit");
		fileExit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doExit(event);
			}
		});
		fileExit.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
		fileMenu.getItems().add(fileExit);
		
		getMenus().add(fileMenu);
		
		//	Edit menu
		//	=============================================
		editMenu = new Menu("_Edit");
		
		editFind = new MenuItem("_Find ...");
		editFind.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doFind(event);
			}
		});
		editFind.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN));
		editMenu.getItems().add(editFind);
		
		editFindNext = new MenuItem("Find _Next");
		editFindNext.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doFindNext(event);
			}
		});
		editFindNext.setAccelerator(new KeyCodeCombination(KeyCode.F3));
		editMenu.getItems().add(editFindNext);
		
		editOptions = new MenuItem("_Options ...");
		editOptions.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doEditOptions(event);
			}
		});
		editMenu.getItems().add(editOptions);
		
		getMenus().add(editMenu);
		
		//	Entry menu
		//	=============================================
		entryMenu = new Menu("E_ntry");
		
		entryNew = new MenuItem("_New ...");
		entryNew.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doCreateEntry(event);
			}
		});
		entryMenu.getItems().add(entryNew);
		
		entryDelete = new MenuItem("_Delete");
		entryDelete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doDeleteEntry(event);
			}
		});
		entryMenu.getItems().add(entryDelete);
		
		entryModify = new MenuItem("_Modify ...");
		entryModify.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doModifyEntry(event);
			}
		});
		entryMenu.getItems().add(entryModify);
		
		getMenus().add(entryMenu);
		
		entryShowPassword = new MenuItem("Show _Password ...");
		entryShowPassword.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doShowPassword(event);
			}
		});
		entryMenu.getItems().add(entryShowPassword);

		//	Help menu
		//	=============================================
		helpMenu = new Menu("_Help");
		
		helpAbout = new MenuItem("_About ...");
		helpAbout.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				
				Actions.doAbout(event);
			}
		});
		helpMenu.getItems().add(helpAbout);
		
		getMenus().add(helpMenu);
	}
	
	public void updateControls(VaultView vaultView, boolean vaultLocked) {
		
		boolean valid = vaultView.isValid();
		boolean modified = vaultView.isModified();
		int selectedItem = vaultView.getSelectionModel().getSelectedIndex();
		boolean findStarted = false;
		
		try {
			
			findStarted = Environment.getEnvironment().getApplication().getFindOperation() != null;
		}
		catch (PasswordVaultException except) {
			
			// use default findStarted value if error occurs
		}

		if (vaultLocked) {
			
			//	If the vault is locked, the vault can be closed, the app can be exited and the 
			//	about dialog can be viewed any other operations are not allowed
			fileClose.setDisable(false);
			fileSave.setDisable(true);
			fileNew.setDisable(true);
			fileChangePassword.setDisable(true);
			fileProperties.setDisable(true);
			fileExit.setDisable(false);
			
			editFind.setDisable(true);
			editFindNext.setDisable(true);
			editOptions.setDisable(true);
			
			entryNew.setDisable(true);
			entryDelete.setDisable(true);
			entryModify.setDisable(true);
			entryShowPassword.setDisable(true);
			
			helpAbout.setDisable(false);
		}
		else {
			
			fileClose.setDisable(!valid);
			fileSave.setDisable(!modified);;
			fileNew.setDisable(valid);
			fileChangePassword.setDisable(!valid);
			fileProperties.setDisable(!valid);
			fileExit.setDisable(false);;
			
			editFind.setDisable(!valid);
			editFindNext.setDisable(!(valid && findStarted));
			editOptions.setDisable(false);
			
			entryNew.setDisable(!valid);
			entryDelete.setDisable(!(valid && (selectedItem != -1)));
			entryModify.setDisable(!(valid && (selectedItem != -1)));
			entryShowPassword.setDisable(!(valid && (selectedItem != -1)));
			
			helpAbout.setDisable(false);
		}
	}
}
