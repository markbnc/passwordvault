package com.ecdsinc.passwordvault;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ToolBar extends javafx.scene.control.ToolBar {

	Button		closeButton;
	Button		saveButton;
	Button		newButton;
	Button		addButton;
	Button		deleteButton;
	Button		editButton;
	Button		showPasswordButton;
	
//	private static final String	OPEN_IMAGE  		= "/images/open64.jpg";
	public static final String APPLICATION_ICON			= "/images/vault64_2.png";
	public static final String CLOSE_IMAGE  			= "/images/close64.png";
	public static final String SAVE_IMAGE   			= "/images/save64.png";
	public static final String NEW_IMAGE    			= "/images/vault64.png";
	public static final String ADD_IMAGE				= "/images/add64.png";
	public static final String DELETE_IMAGE				= "/images/delete64.png";
	public static final String MODIFY_IMAGE				= "/images/modify64.png";
	public static final String SHOW_PASSWORD_IMAGE		= "/images/showPassword64.png";
	
	public ToolBar() {
		
		super();
	
		closeButton = new Button();
		Image closeImage = new Image(getClass().getResourceAsStream(CLOSE_IMAGE));
		closeButton.setGraphic(new ImageView(closeImage));
		closeButton.setTooltip(new Tooltip("Close Vault"));
		closeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Actions.doCloseVault(event);
			}
		});
		getItems().add(closeButton);

		saveButton = new Button();
		Image saveImage = new Image(getClass().getResourceAsStream(SAVE_IMAGE));
		saveButton.setGraphic(new ImageView(saveImage));
		saveButton.setTooltip(new Tooltip("Save Changes"));
		saveButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Actions.doSaveVault(event);
			}
		});
		getItems().add(saveButton);

		newButton = new Button();
		Image newImage = new Image(getClass().getResourceAsStream(NEW_IMAGE));
		newButton.setGraphic(new ImageView(newImage));
		newButton.setTooltip(new Tooltip("Create New Vault"));
		newButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Actions.doCreateVault(event);
			}
		});
		getItems().add(newButton);

		addButton = new Button();
		Image addImage = new Image(getClass().getResourceAsStream(ADD_IMAGE));
		addButton.setGraphic(new ImageView(addImage));
		addButton.setTooltip(new Tooltip("Add new entry"));
		addButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Actions.doCreateEntry(event);
			}
		});
		getItems().add(addButton);

		deleteButton = new Button ();
		Image deleteImage = new Image(getClass().getResourceAsStream(DELETE_IMAGE));
		deleteButton.setGraphic(new ImageView(deleteImage));
		deleteButton.setTooltip(new Tooltip("Delete the selected entry"));
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Actions.doDeleteEntry(event);
			}
		});
		getItems().add(deleteButton);

		editButton = new Button();
		Image editImage = new Image(getClass().getResourceAsStream(MODIFY_IMAGE));
		editButton.setGraphic(new ImageView(editImage));
		editButton.setTooltip(new Tooltip("Edit the selected entry"));
		editButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Actions.doModifyEntry(event);
			}
		});
		getItems().add(editButton);

		showPasswordButton = new Button ();	
		Image showPasswordImage = new Image(getClass().getResourceAsStream(SHOW_PASSWORD_IMAGE));
		showPasswordButton.setGraphic(new ImageView(showPasswordImage));
		showPasswordButton.setTooltip(new Tooltip("Show the password for the selected entry"));
		showPasswordButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				Actions.doShowPassword(event);
			}
		});
		getItems().add(showPasswordButton);
	}
	
	public void updateControls(VaultView vaultView, boolean vaultLocked) {
		
		boolean valid = vaultView.isValid();
		boolean modified = vaultView.isModified();
		int selectedItem = vaultView.getSelectionModel().getSelectedIndex();
		
		if (vaultLocked) {
			
			//	A vault can be closed if it is locked, but no other operations are permitted
			closeButton.setDisable(false);
			saveButton.setDisable(true);
			newButton.setDisable(true);
			addButton.setDisable(true);
			deleteButton.setDisable(true);
			editButton.setDisable(true);
			showPasswordButton.setDisable(true);
		}
		else {
			
			closeButton.setDisable(!valid);
			saveButton.setDisable(!modified);
			newButton.setDisable(valid);
			addButton.setDisable(!valid);
			deleteButton.setDisable(!(valid && (selectedItem != -1)));
			editButton.setDisable(!(valid && (selectedItem != -1)));
			showPasswordButton.setDisable(!(valid && (selectedItem != -1)));
		}
	}
}
