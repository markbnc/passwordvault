package com.ecdsinc.passwordvault;

import java.util.Base64;

import com.ecdsinc.util.Util;

import javafx.collections.ObservableList;

public class FindOperation {
	
	private String					findText;
	private boolean					inLocation;
	private boolean 				inUserId;
	private boolean 				inDescription;
	private boolean 				wrapSearch;
	private boolean					caseSensitive;

	public FindOperation(String findText, boolean inLocation, boolean inUserId, boolean inDescription, boolean wrapSearch, boolean caseSensitive) {
		
		this.findText = findText;
		this.inLocation = inLocation;
		this.inUserId = inUserId;
		this.inDescription = inDescription;
		this.wrapSearch = wrapSearch;
		this.caseSensitive = caseSensitive;
	}

	public String getFindText() {
		return findText;
	}

	public boolean isInLocation() {
		return inLocation;
	}

	public boolean isInUserId() {
		return inUserId;
	}

	public boolean isInDescription() {
		return inDescription;
	}

	public boolean isWrapSearch() {
		return wrapSearch;
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	/**
	 * Finds the next entry in the list of entries in the vault that contains the findText in one of the selected
	 * columns.  The search starts from the item after the currently selected item and continues until the next row that
	 * matches is found or the end of the list is reached.  If there is no currently selected item the search starts from
	 * the beginning of the list.
	 * 
	 * If the wrapSearch property is set to true, the search will continue from the beginning of the list.
	 * 
	 * If the caseSensitive property is set to true the search will do a case sensitive match.  Otherwise the match
	 * is case insensitive.
	 * 
	 * @return The next entry that contains the findText or null if there are no entries that contain the find text.
	 */
	public VaultEntry findNext() {
		
		try {

			VaultView					view = Environment.getEnvironment().getApplication().getVaultView();
			ObservableList<VaultEntry> 	entryList = view.getItems();
			
			//	getSelectedIndex returns -1 if there is no selection.  The search will start at
			//	selectedIndex+1 so if there is no selection the search will start at 0 which is the
			//	beginning of the list.  Given that there is no need to test for -1
			int							selectedIndex = view.getSelectionModel().getSelectedIndex();
		
			VaultEntry foundEntry = doFind(entryList, selectedIndex+1, entryList.size());
			if ((foundEntry == null) && wrapSearch && (selectedIndex >= 0)) {
				
				foundEntry = doFind(entryList, 0, selectedIndex+1);
			}
			return foundEntry;
		}
		catch (PasswordVaultException except) {
			
			VaultUtil.displayErrorDialog("Error searching for " + findText + " - " + except);
			return null;
		}
	}
	
	/**
	 * Searches the current sortedEntries list for the findText starting with the start index
	 * up to but not including the end index
	 * 
	 * @param start		the index in the sorted list of entries to start the search at
	 * @param end		the index in the sorted list of entries to end the search at
	 * @return			A VaultEntry if the findText was found in the entry or null if no entry in the range
	 * 					matched the findText
	 */
	private VaultEntry doFind(ObservableList<VaultEntry> entryList, int start, int end) {
		
		try {

			VaultEntry foundEntry = null;
		
			for (int curRow = start; curRow < end; curRow++) {
				
				VaultEntry curEntry = entryList.get(curRow);
				if (inLocation) {
					
					if (match(curEntry.getLocation())) {
						
						foundEntry = curEntry;
					}
				}
				
				if ((foundEntry == null) && inUserId) {
					
					String		userId;
					String		encryptedUserId = curEntry.getUserId();
					UserVault	vault = Environment.getEnvironment().getApplication().getCurrentVault();
					boolean		isBase64 = vault.getUserIdEncoding() == UserVault.ENCODING_BASE64;
					if (isBase64) {
						                                               
						userId = vault.decrypt(Base64.getDecoder().decode(encryptedUserId));
					}
					else {
						userId = vault.decrypt(Util.HexStringToBytes(encryptedUserId));
					}
					
					if (match(userId)) {
						
						foundEntry = curEntry;
					}
				}
				
				if ((foundEntry == null) && inDescription) {
					
					if (match(curEntry.getDescription())) {
						
						foundEntry = curEntry;
					}
				}
				
				if (foundEntry != null) {
					
					break;
				}
			}
			
			return foundEntry;
		}
		catch (Exception except) {
			
			VaultUtil.displayErrorDialog("Error finding text " + findText + " - " + except);
			return null;
		}
	}
	
	private boolean match(String value) {
		
		boolean match = false;
		
		if (value != null) {
			
			if (caseSensitive) {
				
				match = value.contains(findText);
			}
			else {
				
				match = value.toLowerCase().contains(findText.toLowerCase());
			}
		}
		
		return match;
	}
}
