package com.ecdsinc.passwordvault;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PvDialog<R> extends Dialog<R> {

	private static final String		ICON_PATH = "/images/vault64_2.png";
	
	public PvDialog(String fxmlFile, String title, String... initialValues) {
		
		
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            DialogPane dlgPane = loader.load();
            PvDialogController<R> controller = loader.getController();
            controller.setInitialValues(initialValues);
            setDialogPane(dlgPane);

            setResultConverter(controller.getResponseConverter());
            setTitle(title);
            setIcon(ICON_PATH);
            initOwner(Environment.getEnvironment().getApplication().getStage());
            
           setOnShowing((event) -> {
            	
            	try {
            		
            		Environment.getEnvironment().getApplication().setCurrentDialog(this);
            	}
            	catch (Exception except) {
            		
            		//	Don't want to pop a dialog in a listener.
            		//	If the environment can not be obtained there is something very wrong.
            	}
            });
           
           setOnHiding((event) -> {
        	   
        	   try {
        		
        		   Environment.getEnvironment().getApplication().setCurrentDialog(null);
        	   }
        	   catch (Exception except) {
        		
        		   //	Don't want to pop a dialog in a listener.
        		   //	If the environment can not be obtained there is something very wrong.
        	   }
           });

        } catch (Exception except) {
            VaultUtil.displayErrorDialog("Error initializing dialog " + title, except);
        }
	}
	
	private void setIcon(String path) {
		
		URL		iconURL = getClass().getResource(ICON_PATH);
		Image	icon = new Image(iconURL.toString());
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(icon);	
	}
	
	
}
