package com.ecdsinc.passwordvault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Callback;

/**
 * This is the controller for the Password Vault About dialog.  This dialog dispalys the version
 * of this app as well as other info for the app
 * 
 * Initial Values
 * 
 * The About dialog does not take any initial values
 * 
 * @author MarkB
 *
 */
public class AboutDialogController implements PvDialogController<Void> {

	@FXML	Label				versionCtrl;
	@FXML	Label				urlCtrl;
	@FXML	TextArea			licenseCtrl;
	
	private Callback<ButtonType, Void>  responseConverter;
	private	Attributes					manifestAttributes;
	
	private static final String			NOTICES_PATH					= "NOTICES.txt";
	
	private static final String			MANIFEST_PATH					= "META-INF/MANIFEST.MF";
	private static final String			MANIFEST_ATTRIB_PROGRAM_NAME	= "Implementation-Title";
	private static final String			MANIFEST_ATTRIB_VERSION			= "Implementation-Version";
	private static final String			MANIFEST_ATTRIB_PROJECT_URL		= "Implementation-URL";
	
	private static final String			PROGRAM_NAME_VALUE				= "passwordvault";
	
    @FXML
    public void initialize() 
    	throws PasswordVaultException {
    	
    	getManifestAttributes();
    	
    	if (manifestAttributes != null) {
    	
    		versionCtrl.setText(manifestAttributes.getValue(MANIFEST_ATTRIB_VERSION));
    		urlCtrl.setText(manifestAttributes.getValue(MANIFEST_ATTRIB_PROJECT_URL));
    	}
    	licenseCtrl.setText(getNoticesText());
    	
    	responseConverter = new Callback<ButtonType, Void>() {

			@Override
			public Void call(ButtonType param) {
				
				return null;
			}
		};
    }
    
	@Override
	public Callback<ButtonType, Void> getResponseConverter() {

		return responseConverter;
	}

	@Override
	public void setInitialValues(String... initialValues) {

		//	This dialog accepts no initial values
	}
	
	private void getManifestAttributes() {
		
		try {
			
			Enumeration<URL> manifests = Actions.class.getClassLoader().getResources(MANIFEST_PATH);
			while (manifests.hasMoreElements()) {
				
				try (InputStream inStream = manifests.nextElement().openStream()) {
				
					Manifest	curManifest = new Manifest(inStream);
					try {
						inStream.close();
					}
					catch(IOException closeExcept) {}
					
					Attributes	attributes = curManifest.getMainAttributes();
					String		programName = attributes.getValue(MANIFEST_ATTRIB_PROGRAM_NAME);
					if (PROGRAM_NAME_VALUE.equals(programName)) {
					
						manifestAttributes = curManifest.getMainAttributes();
						break;
					}
				}	
			}
		}
		catch (IOException except) {
			
			try {
				Logger	logger = Environment.getEnvironment().getLogger(PvLogger.LOGGER_VAULT);
				if (logger.isLoggable(Level.INFO) ) {
					
					logger.info("Error getting Version and other application information from the manifest - " + except);
				}
			}
			catch (PasswordVaultException except2) {
				//	Ignore error getting environment
			}
		}
	}
	
	private String getNoticesText() {
		
		try {
			
			StringBuffer	noticesText = null;

			Enumeration<URL> notices = Actions.class.getClassLoader().getResources(NOTICES_PATH);
			while (notices.hasMoreElements()) {
				
				try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(notices.nextElement().openStream()))) {

					String			firstLine = fileReader.readLine();
					if (PROGRAM_NAME_VALUE.equalsIgnoreCase(firstLine)) {

						noticesText = new StringBuffer();
						int 	charsRead = 0;
						char[]	buffer = new char[8*1024];
						while (charsRead >= 0) {
							
							charsRead = fileReader.read(buffer);
							if (charsRead > 0) {
								
								noticesText.append(buffer, 0, charsRead);
							}
						}
						break;
					}
					try {
						fileReader.close();
					}
					catch(IOException closeExcept) {}
				}	
			}
			
			return (noticesText == null) ? null : noticesText.toString();
		}
		catch (IOException except) {
			
			try {
				Logger	logger = Environment.getEnvironment().getLogger(PvLogger.LOGGER_VAULT);
				if (logger.isLoggable(Level.INFO) ) {
					
					logger.info("Error getting Version and other application information from the manifest - " + except);
				}
			}
			catch (PasswordVaultException except2) {
				//	Ignore error getting environment
			}
			return null;
		}		
	}
}
