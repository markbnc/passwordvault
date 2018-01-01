package com.ecdsinc.passwordvault;

import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ecdsinc.util.Util;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.util.Callback;

public class VaultView extends TableView<VaultEntry>{

	private UserVault				vault;
	private SortProperties			lastSort;
	private long					lastActivity = 0;
	
	public static final int		COLUMN_LOCATION_INDEX = 0;
	public static final int		COLUMN_USERID_INDEX = 1;
	public static final int		COLUMN_DESCRIPTION_INDEX = 2;
	
	public static final String	COLUMN_LOCATION_NAME = "Location";
	public static final String	COLUMN_USERID_NAME = "User Id";
	public static final String	COLUMN_DESCRIPTION_NAME = "Description";
	
	public static final int		SORT_COL_NONE = -1;
	public static final int		SORT_DIR_ASCENDING = 0;
	public static final int		SORT_DIR_DESCENDING = 1;
	
	
	public VaultView () {
		
		super();
		
		Environment	env = null;
		Logger		logger = null;
		
		int			locationWidth = Integer.parseInt(PasswordVaultProperties.VAULT_VIEW_LOCATION_WIDTH_DEFAULT);
		int			useridWidth = Integer.parseInt(PasswordVaultProperties.VAULT_VIEW_USERID_WIDTH_DEFAULT);
		int			descriptionWidth = Integer.parseInt(PasswordVaultProperties.VAULT_VIEW_DESCRIPTION_WIDTH_DEFAULT);
		
		try {
		
			env = Environment.getEnvironment();
			logger = env.getLogger(PvLogger.LOGGER_VAULT);
			
			locationWidth = Integer.parseInt(env.getProperty(PasswordVaultProperties.VAULT_VIEW_LOCATION_WIDTH, 
					PasswordVaultProperties.VAULT_VIEW_LOCATION_WIDTH_DEFAULT));
			useridWidth = Integer.parseInt(env.getProperty(PasswordVaultProperties.VAULT_VIEW_USERID_WITDH, 
					PasswordVaultProperties.VAULT_VIEW_USERID_WIDTH_DEFAULT));
			descriptionWidth = Integer.parseInt(env.getProperty(PasswordVaultProperties.VAULT_VIEW_DESCRIPTION_WIDTH, 
					PasswordVaultProperties.VAULT_VIEW_DESCRIPTION_WIDTH_DEFAULT));
			
			if (logger.isLoggable(Level.INFO)) {
				
				logger.log(Level.INFO, "Initializing Vault View Column Widths - LocationWidth: " + locationWidth + " UseridWidth: " + useridWidth + 
						   " DescriptionWidth: " + descriptionWidth);
			}
		}
		catch (Exception except) {
			
			if ((logger != null) && (logger.isLoggable(Level.INFO))) {
					
				logger.log(Level.INFO, "Error getting Vault View properties", except);
			}
		}
			
		TableColumn<VaultEntry, String> locationCol = new TableColumn<>(COLUMN_LOCATION_NAME);
		locationCol.setPrefWidth(locationWidth);
		locationCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VaultEntry,String>, ObservableValue<String>>() {
			
			@Override
			public ObservableValue<String> call(CellDataFeatures<VaultEntry, String> param) {
				
				return new ReadOnlyStringWrapper(param.getValue().getLocation());
			}
		});
		locationCol.setCellFactory(TextFieldTableCell.forTableColumn());
		locationCol.setOnEditCommit(new EventHandler<CellEditEvent<VaultEntry, String>>() {

			@Override
			public void handle(CellEditEvent<VaultEntry, String> event) {
				
				VaultEntry	entry = event.getTableView().getItems().get(event.getTablePosition().getRow());
				entry.setLocation(event.getNewValue());
			}
		});
		getColumns().add(locationCol);

		TableColumn<VaultEntry, String> useridCol = new TableColumn<>(COLUMN_USERID_NAME);
		useridCol.setPrefWidth(useridWidth);
		useridCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VaultEntry,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<VaultEntry, String> param) {

				Environment	env = null;
				Logger		logger = null;
				try {
				
					env = Environment.getEnvironment();
					logger = env.getLogger(PvLogger.LOGGER_VAULT);
					
					String value = param.getValue().getUserId();
					if (vault.getEncryptUserId()) {
						
						if (vault.getUserIdEncoding() == UserVault.ENCODING_UTILHEX) {
						
							return new ReadOnlyStringWrapper(vault.decrypt(Util.HexStringToBytes(value)));
						}
						else {
							
							return new ReadOnlyStringWrapper(vault.decrypt(Base64.getDecoder().decode(value)));
						}
					}
					else {
						
						return new ReadOnlyStringWrapper(value);
					}
				}
				catch (Exception except) {
					
					if ((logger != null) &&
						logger.isLoggable(Level.WARNING)) {
						
						logger.log(Level.WARNING, "Error rendering cell value for vault entry with id " + param.getValue().getId());
					}
					return new ReadOnlyStringWrapper("ERROR");
				}
			}
		});
		useridCol.setCellFactory(TextFieldTableCell.forTableColumn());
		useridCol.setOnEditCommit(new EventHandler<CellEditEvent<VaultEntry, String>>() {

			@Override
			public void handle(CellEditEvent<VaultEntry, String> event) {
				
				Environment	env = null;
				Logger		logger = null;
				try {
				
					env = Environment.getEnvironment();
					logger = env.getLogger(PvLogger.LOGGER_VAULT);
					
					VaultEntry	entry = event.getTableView().getItems().get(event.getTablePosition().getRow());
					String		value = event.getNewValue();
					if (vault.getEncryptUserId()) {
						
						if (vault.getUserIdEncoding() == UserVault.ENCODING_UTILHEX) {
						
							value = Util.bytesToHexString(vault.encrypt(value));
						}
						else {
							
							value = Base64.getEncoder().encodeToString(vault.encrypt(value));
						}
					}
					entry.setUserId(value);
				}
				catch (Exception except) {
					
					if ((logger != null) &&
						 logger.isLoggable(Level.WARNING)) {
							
						logger.log(Level.WARNING, "Error updated user id: " + except);
					}
				}
			}
		});
		getColumns().add(useridCol);
		
		TableColumn<VaultEntry, String> descriptionCol = new TableColumn<>(COLUMN_DESCRIPTION_NAME);
		descriptionCol.setPrefWidth(descriptionWidth);
		descriptionCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VaultEntry,String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<VaultEntry, String> param) {
				
				return new ReadOnlyStringWrapper(param.getValue().getDescription());
			}
		});
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descriptionCol.setOnEditCommit(new EventHandler<CellEditEvent<VaultEntry, String>>() {

			@Override
			public void handle(CellEditEvent<VaultEntry, String> event) {
				
				VaultEntry	entry = event.getTableView().getItems().get(event.getTablePosition().getRow());
				entry.setDescription(event.getNewValue());
			}
		});
		getColumns().add(descriptionCol);
		
		getSelectionModel().selectedItemProperty().addListener(new ChangeListener<VaultEntry>() {

			@Override
			public void changed(ObservableValue<? extends VaultEntry> observable, VaultEntry oldValue,
					VaultEntry newValue) {
				
				Environment env = null;
				try {
				
					env = Environment.getEnvironment();
					env.getApplication().updateControls();
				}
				catch (Exception except) {
					
					try {
						
						Logger logger = env.getLogger(PvLogger.LOGGER_VAULT);
						if (logger.isLoggable(Level.WARNING)) {
							
							logger.log(Level.WARNING, "Error handling list change selection", except);
						}
					}
					catch (Exception except2) {
						
						// Can't do much else
					}
				}
			}
		});	
		
		setOnSort(event -> {
			
			//	if the sort order changes the current find operation must be cleared
			try {
				Environment.getEnvironment().getApplication().setFindOperation(null);
			}
			catch (PasswordVaultException except) {
				
				VaultUtil.displayErrorDialog("Error clearing find operation - " + except);
			}
		});
		
		setOnMouseMoved(event -> {
			
			lastActivity = Clock.getTime();
		});
		
		visibleProperty().addListener((visibleProperty, oldValue, newValue) -> {
			
			//	reset the last activity value when this view is made the visible pane in the application stack
			if (newValue) {
				
				lastActivity = Clock.getTime();
			}
		});
	}
	
	public void initSorting() 
		throws PasswordVaultException {
		
		Environment	env = Environment.getEnvironment();
		Logger		logger = env.getLogger(PvLogger.LOGGER_VAULT);

		if (lastSort == null) {
			
			lastSort = new SortProperties();
			lastSort.sortColumnIndex = Integer.parseInt(env.getProperty(PasswordVaultProperties.VAULT_VIEW_SORT_COLUMN, 
					PasswordVaultProperties.VAULT_VIEW_SORT_COLUMN_DEFAULT));
			lastSort.sortDirection = Integer.parseInt(env.getProperty(PasswordVaultProperties.VAULT_VIEW_SORT_DIRECTION, 
					PasswordVaultProperties.VAULT_VIEW_SORT_DIRECTION_DEFAULT));
		}

		SortType sortType = SortType.ASCENDING;
		switch (lastSort.sortDirection) {
		
			case SORT_DIR_ASCENDING:

				//	sortType was initialized to Ascending so nothing to do.
				break;
				
			case SORT_DIR_DESCENDING:
				
				sortType = SortType.DESCENDING;
				break;
			
			default:
				
				throw new PasswordVaultException("Invalid Sort Direction " + lastSort.sortDirection);
		}
	
		if (logger.isLoggable(Level.INFO)) {
			
			logger.log(Level.INFO, "Setting Vault View Sort Order - SortColumn: " + lastSort.sortColumnIndex + " SortOrder: " + lastSort.sortDirection);
		}
		
		//	Set the sort order
		switch (lastSort.sortColumnIndex) {
		
			case COLUMN_LOCATION_INDEX:
				
				TableColumn<VaultEntry, ?> locationCol = getColumns().get(COLUMN_LOCATION_INDEX);
				locationCol.setSortType(sortType);
				getSortOrder().add(locationCol);
				break;
				
			case COLUMN_USERID_INDEX:
				
				TableColumn<VaultEntry, ?> useridCol = getColumns().get(COLUMN_USERID_INDEX);
				useridCol.setSortType(sortType);
				getSortOrder().add(useridCol);
				break;
				
			case COLUMN_DESCRIPTION_INDEX:
				
				TableColumn<VaultEntry, ?> descriptionCol = getColumns().get(COLUMN_DESCRIPTION_INDEX);
				descriptionCol.setSortType(sortType);
				getSortOrder().add(descriptionCol);
				break;
				
			case SORT_COL_NONE:
				
				//	There is not sort order on the view
				break;
				
			default:
				
				if ((logger != null) && (logger.isLoggable(Level.WARNING))) {
					
					logger.log(Level.WARNING, "Invalid Sort column index " + lastSort.sortColumnIndex + " sort order not applied");
				}
		}
	}
	
	public void setVault(UserVault vault) 
		throws PasswordVaultException {
		
		//	If there is a vault currently being displayed, get the sort properties for that vault so
		//	that those properties can be used for the next vault
		if (this.vault != null) {
			
			lastSort = getSortProperties();
		}
		
		this.vault = vault;
		Environment.getEnvironment().getApplication().setVaultTitle((vault == null) ? null : vault.getVaultName());
		if (this.vault != null) {
		
			setItems(vault.getEntries());
			initSorting();
			sort();
		}
		else {
			
			setItems(FXCollections.emptyObservableList());
		}	
	}
	
	public UserVault getVault() {
		
		return this.vault;
	}
	
	public boolean isValid() {
		
		return (vault != null);
	}
	
	public boolean isModified() {
		
		return (vault != null) ? vault.isModified() : false;
	}
	
	public void updateProperties(Properties properties) {
		
		updateColumnProperties(properties);
		updateSortProperties(properties);
	}
	
	private void updateColumnProperties(Properties properties) {
		
		ObservableList<TableColumn<VaultEntry, ?>> columns = getColumns();
		int locationWidth = Double.valueOf(columns.get(COLUMN_LOCATION_INDEX).getWidth()).intValue();
		int useridWidth = Double.valueOf(columns.get(COLUMN_USERID_INDEX).getWidth()).intValue();
		int descriptionWidth = Double.valueOf(columns.get(COLUMN_DESCRIPTION_INDEX).getWidth()).intValue();
		
		properties.setProperty(PasswordVaultProperties.VAULT_VIEW_LOCATION_WIDTH, String.valueOf(locationWidth));
		properties.setProperty(PasswordVaultProperties.VAULT_VIEW_USERID_WITDH, String.valueOf(useridWidth));
		properties.setProperty(PasswordVaultProperties.VAULT_VIEW_DESCRIPTION_WIDTH, String.valueOf(descriptionWidth));
	}
	
	private void updateSortProperties(Properties properties) {
		
		//	If there is currently a vault open update the last sort properties
		//	to the sort order and direction of the open vault
		if (vault != null) {
			
			lastSort = getSortProperties();
		}
		
		if (lastSort != null) {
		
			properties.setProperty(PasswordVaultProperties.VAULT_VIEW_SORT_COLUMN, String.valueOf(lastSort.sortColumnIndex));
			properties.setProperty(PasswordVaultProperties.VAULT_VIEW_SORT_DIRECTION, String.valueOf(lastSort.sortDirection));
		}
	}
	
	private SortProperties getSortProperties() {
		
		SortProperties	sortProperties = new SortProperties();
		
		//	Currently only support persisting a single column for the sort order
		sortProperties.sortColumnIndex = SORT_COL_NONE;
		sortProperties.sortDirection = SORT_DIR_ASCENDING;
		SortType	sortType = null;
		
		ObservableList<TableColumn<VaultEntry, ?>> sortOrder = getSortOrder();
		if (sortOrder.size() > 0) {
			
			TableColumn<VaultEntry, ?> sortColumn = sortOrder.get(0);
			sortType = sortColumn.getSortType();
			if (sortType == SortType.DESCENDING) {
				
				sortProperties.sortDirection = SORT_DIR_DESCENDING;
			}
			
			String columnLabel = sortColumn.getText();
			
			switch (columnLabel) {
			
				case COLUMN_LOCATION_NAME:
					
					sortProperties.sortColumnIndex = COLUMN_LOCATION_INDEX;
					break;
					
				case COLUMN_USERID_NAME:
					
					sortProperties.sortColumnIndex = COLUMN_USERID_INDEX;
					break;
					
				case COLUMN_DESCRIPTION_NAME:
					
					sortProperties.sortColumnIndex = COLUMN_DESCRIPTION_INDEX;
					break;
			}
		}

		return sortProperties;
	}
	
	public long getLastActivity() {
		
		return lastActivity;
	}
	
	public void setLastActivity() {
		
		lastActivity = Clock.getTime();
	}
}

class SortProperties {
	
	int	sortColumnIndex;
	int	sortDirection;
}
