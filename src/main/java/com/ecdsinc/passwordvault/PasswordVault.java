package com.ecdsinc.passwordvault;



import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PasswordVault extends Application {

	private	Stage					primaryStage;
	private MenuBar					menuBar;
	private ToolBar					toolBar;
	private StackPane				stackPane;
	private Node					openVaultPane;
	private OpenVaultController		openVaultController;
	private VaultView				vaultContentsPane;
	private Node					lockedVaultPane;
	private LockScreenController	lockedVaultController;
	private FindOperation			findOperation;
	private PvDialog<?>				currentDialog;
	
	private Clock					clockThread;
	private ActivityMonitor			activityMonitorThread;
	
	public static final int			OPEN_VAULT_PANE_INDEX 			= 0;
	public static final int			VAULT_CONTENTS_PANE_INDEX 		= 1;
	public static final int			LOCKED_VAULT_PANE_INDEX			= 2;
	
	private static final String		PASSWORD_VAULT_CSS				= "pv.css";
	private static final String		TITLE_PREFIX				= "Password Vault";
	
	public static void main(String[] args) {
		
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		//	Initialize the environment
		Environment env = Environment.initEnvironment();
		env.setApplication(this);
		
		this.primaryStage = primaryStage;
		
		clockThread = new Clock();
		clockThread.start();
		
		activityMonitorThread = new ActivityMonitor();
		activityMonitorThread.start();
		
		menuBar = new MenuBar();
		toolBar = new ToolBar();
		VBox topPane = new VBox();
		topPane.getChildren().add(menuBar);
		topPane.getChildren().add(toolBar);
		
		stackPane = new StackPane();
		
		URL	location = getClass().getResource("OpenVault.fxml");
	    FXMLLoader loader = new FXMLLoader(location);
	    openVaultPane = loader.load();
	    openVaultController = loader.getController();
	    openVaultPane.visibleProperty().addListener((visibleProperty, oldValue, newValue) -> {
	    	
	    	openVaultController.handleVisibleChanged(newValue);
	    });
	    
	    //	The open vault pane is shown first so set it to have the default button to start with
	    openVaultController.handleVisibleChanged(true);
	    
		vaultContentsPane = new VaultView();
		vaultContentsPane.setEditable(true);
		
		location = getClass().getResource("LockScreen.fxml");
		loader = new FXMLLoader(location);
		lockedVaultPane = loader.load();
		lockedVaultController = loader.getController();
		lockedVaultPane.visibleProperty().addListener((visibleProperty, oldValue, newValue) -> {
			
			lockedVaultController.handleVisibleChanged(newValue);
		});
		
		stackPane.getChildren().addAll(openVaultPane, vaultContentsPane, lockedVaultPane);
		setVisiblePane(OPEN_VAULT_PANE_INDEX);
		updateControls();
		
		BorderPane	root = new BorderPane();
		root.setTop(topPane);
		root.setCenter(stackPane);
		
		double width = Double.parseDouble(env.getProperty(PasswordVaultProperties.MAIN_WINDOW_WIDTH, PasswordVaultProperties.MAIN_WINDOW_WIDTH_DEFAULT));
		double height = Double.parseDouble(env.getProperty(PasswordVaultProperties.MAIN_WINDOW_HEIGHT, PasswordVaultProperties.MAIN_WINDOW_HEIGHT_DEFAULT));
		
		Logger logger = env.getLogger(PvLogger.LOGGER_VAULT);
		if (logger.isLoggable(Level.INFO)) {
			
			logger.log(Level.INFO, "Initializing main window size - Width: " + width + " Height: " + height);
		}
		
		Scene mainScene = new Scene(root, width, height);
		mainScene.getStylesheets().add(getClass().getResource(PASSWORD_VAULT_CSS).toExternalForm());

		setVaultTitle(null);
		primaryStage.setScene(mainScene);
		Image applicationIcon = new Image(getClass().getResourceAsStream(ToolBar.APPLICATION_ICON));
		primaryStage.getIcons().add(applicationIcon);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {

				Actions.doExit(event);
			}
		});
		primaryStage.show();
		
	}

	@Override
	public void stop() throws Exception {
		
		clockThread.shutdown();
		activityMonitorThread.shutdown();
		super.stop();
	}

	public void setVaultContents(UserVault vault) 
		throws PasswordVaultException {
		
		vaultContentsPane.setVault(vault);
	}
	
	public void setVisiblePane(int paneIndex) 
		throws PasswordVaultException {
		
		switch(paneIndex) {
		
			case OPEN_VAULT_PANE_INDEX:
				
				openVaultPane.setVisible(true);
				vaultContentsPane.setVisible(false);
				lockedVaultPane.setVisible(false);
				break;
				
			case VAULT_CONTENTS_PANE_INDEX:
				
				openVaultPane.setVisible(false);
				vaultContentsPane.setVisible(true);
				lockedVaultPane.setVisible(false);
				break;
				
			case LOCKED_VAULT_PANE_INDEX:
				openVaultPane.setVisible(false);
				vaultContentsPane.setVisible(false);
				lockedVaultPane.setVisible(true);
				break;
				
				
			default :
				
				throw new PasswordVaultException("Invalid stack pane index " + paneIndex);
		}
	}
	
	public int getVisiblePane() {
		
		int	visiblePane = -1;
		
		if (openVaultPane.isVisible()) {
			
			visiblePane = OPEN_VAULT_PANE_INDEX;
		}
		else if (vaultContentsPane.isVisible()) {
			
			visiblePane = VAULT_CONTENTS_PANE_INDEX;
		}
		else if (lockedVaultPane.isVisible()) {
			
			visiblePane = LOCKED_VAULT_PANE_INDEX;
		}	
		return visiblePane;
	}
	
	public void updateControls() {
		
		boolean vaultLocked = (getVisiblePane() == LOCKED_VAULT_PANE_INDEX) ? true : false;
		
		toolBar.updateControls(vaultContentsPane, vaultLocked);
		menuBar.updateControls(vaultContentsPane, vaultLocked);
	}
	
	public void updateOpenVaultPane() {
		
		try {
		
			openVaultController.initialize();
		}
		catch (PasswordVaultException except) {
			
			//	Log, but do not display the error
			try {
				
				Logger logger = Environment.getEnvironment().getLogger(PvLogger.LOGGER_VAULT);
				if (logger.isLoggable(Level.INFO)) {
					
					logger.log(Level.INFO, "Error initializing Open Vault Pane - " + except);
				}
			}
			catch (PasswordVaultException except2) {}
		}
	}
	
	public void updateLockSettings() {
		
	}
	
	public void updateProperties(Properties properties) {
		
		Scene scene = getScene();
			
		double 	width = scene.getWidth();
		double 	height = scene.getHeight();

		try {
		
			Logger logger = Environment.getEnvironment().getLogger(PvLogger.LOGGER_VAULT);
			if (logger.isLoggable(Level.INFO)) {
				
				logger.log(Level.INFO, "Saving main window width - Widht: " + width + " Height: " + height);
			}
		}
		catch (PasswordVaultException except) {
			
			//	Don't fail if there is an error writing the log info.
		}
		
		properties.setProperty(PasswordVaultProperties.MAIN_WINDOW_WIDTH, String.valueOf(width));
		properties.setProperty(PasswordVaultProperties.MAIN_WINDOW_HEIGHT, String.valueOf(height));
		
		vaultContentsPane.updateProperties(properties);
	}
	
	public UserVault getCurrentVault() {
		
		if (vaultContentsPane != null) {
			
			return vaultContentsPane.getVault();
		}
		return null;
	}
	
	public Scene getScene() {
		
		return stackPane.getScene();
	}
	
	public void setVaultTitle(String vaultName) {
		
		if (vaultName == null) {
			
			primaryStage.setTitle(TITLE_PREFIX);
		}
		else {
		
			primaryStage.setTitle(TITLE_PREFIX + " - " + vaultName);
		}
	}
	
	public VaultView getVaultView() {
		
		return vaultContentsPane;
	}

	public FindOperation getFindOperation() {
		return findOperation;
	}

	public void setFindOperation(FindOperation findOperation) {
		this.findOperation = findOperation;
	}
	
	public void lock() {

		try {
		
			if (getVisiblePane() == VAULT_CONTENTS_PANE_INDEX) {
			
				setVisiblePane(LOCKED_VAULT_PANE_INDEX);
				updateControls();
			}
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error unlocking vault", except);
		}
	}
	
	public Stage getStage() {
		
		return primaryStage;
	}

	public PvDialog<?> getCurrentDialog() {
		return currentDialog;
	}

	public void setCurrentDialog(PvDialog<?> currentDialog) {
		this.currentDialog = currentDialog;
	}
	
	
}
