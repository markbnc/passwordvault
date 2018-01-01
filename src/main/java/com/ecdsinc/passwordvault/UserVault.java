package com.ecdsinc.passwordvault;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ecdsinc.security.EcdsAESEncryptorDecryptor;
import com.ecdsinc.security.EcdsDESEncryptorDecryptor;
import com.ecdsinc.security.EcdsEncryptorDecryptor;
import com.ecdsinc.security.EcdsKeyStore;
import com.ecdsinc.security.EcdsKeyStore.KeySize;
import com.ecdsinc.security.EcdsSecurityException;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;

//<UserVault>
//  <Version>2</Version>
//  <UserName>MarkBiamonte</UserName>
//  <Description>Mark's Accounts</Description>
//  <PasswordHint>Personal Acct Password (3)</PasswordHint>
//  <EncryptUserId>true</EncryptUserId>
//	<EncryptionAlgorithm>2</EncryptionAlgorithm>					- Vault file version 3 or higher
//  <VaultEntries>
//    <VaultEntry>
//        <Id>1</Id>
//        <Description>Non-Wireless Router Config Page</Description>
//        <Location>192.168.0.1</Location>
//        <UserId>b24e5132c2d5fe04</UserId>
//        <Password>KBs5ANsW6e0=</Password>
//    </VaultEntry>
//    <VaultEntry>
//	  </VaultEntry>
//  </VaultEntries>
//</UserVault>

public class UserVault {
	
	static final String		ELEMENT_USER_VAULT				= "UserVault";
	static final String		ELEMENT_VAULT_NAME				= "UserName";
	static final String		ELEMENT_VERSION					= "Version";
	static final String		ELEMENT_VAULT_DESCRIPTION		= "Description";
	static final String		ELEMENT_PASSWORD_HINT			= "PasswordHint";
	static final String		ELEMENT_ENCRYPT_USER_ID			= "EncryptUserId";
	static final String		ELEMENT_ENCRYPTION_ALGORITHM	= "EncryptionAlgorithm";
	static final String		ELEMENT_VAULT_ENTRIES			= "VaultEntries";
	static final String		ELEMENT_VAULT_ENTRY				= "VaultEntry";
	static final String		ELEMENT_ID						= "Id";
	static final String		ELEMENT_ENTRY_DESCRIPTION		= "Description";
	static final String		ELEMENT_LOCATION				= "Location";
	static final String		ELEMENT_USER_ID					= "UserId";
	static final String		ELEMENT_PASSWORD				= "Password";
	
	static final String		VAULT_FILE_EXTENSTION			= ".pwv";
	
	//	Vault Version 2 - Encryption: DES
	//					  UserId Encoding: UtilHex
	//					  Password Encoding: Base 64
	//
	//	Vault Version 3 - Encryption: Specified in EncryptionAlgoritm element
	//					  UserId Encoding: Base 64
	//					  Password Encoding: Base 64
	static final int		CURRENT_VAULT_VERSION			= 3;
	
	static final EncryptionAlgorithm	DEFAULT_ENCRYPTION_ALGORITHM = EncryptionAlgorithm.AES128;
	
	static final int	ENCODING_UNKNOWN				= 0;
	static final int	ENCODING_UTILHEX				= 1;	//	Version 2 Vault files used UtilHex encoding for the User ID
	static final int	ENCODING_BASE64					= 2;
	
	private int							version;
	private String						vaultName;		//UserName field in vault xml maps to this
	private String						description;
	private String						passwordHint;
	private boolean						encryptUserId;
	private EncryptionAlgorithm			encryptionAlgorithm;
	private EcdsEncryptorDecryptor		encryptorDecryptor;
	private Key							encryptionKey;
	private Environment					env;
	private boolean						modified;
	private ObservableList<VaultEntry> 	vaultEntries;
	private int							userIdEncoding = ENCODING_BASE64;
	private int							numSaltBytes = 16;
	
	private UserVault() {
		
		// need to explicitly declare default constructor because this class defines another constructor
	}
	
	private UserVault(Builder builder) 
		throws PasswordVaultException {
		
		version = CURRENT_VAULT_VERSION;
		vaultName = builder.vaultName;
		description = builder.description;
		passwordHint = builder.passwordHint;
		encryptUserId = true;
		encryptionAlgorithm = (builder.encryptionAlgorithm == null) ? DEFAULT_ENCRYPTION_ALGORITHM : builder.encryptionAlgorithm;
		env = Environment.getEnvironment();
		modified = true;
		
		createEncryptionKey(vaultName, builder.vaultPassword);
		createVaultEntriesList();
		getEncryptorDecryptor();

	}
	
	public void load(File vaultFile, String userId, String password)
		throws PasswordVaultException {
		
		env = Environment.getEnvironment();
		getEncryptionKey(userId, password);
		
		if (vaultEntries == null) {
			
			createVaultEntriesList();
		}
		else {
			
			vaultEntries.clear();
		}
		Logger	logger = env.getLogger(PvLogger.LOGGER_VAULT);
		
		try (FileInputStream	inStream = new FileInputStream(vaultFile)) {
		
			DocumentBuilder	docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document		vaultDoc = docBuilder.parse(inStream);
			if (logger.isLoggable(Level.INFO)) {
				
				logger.log(Level.INFO, "Opened Vault File " + vaultFile.getAbsolutePath());
			}

			Node		userVault = vaultDoc.getDocumentElement();
			NodeList	childNodes = userVault.getChildNodes();
			
			for (int index = 0; index < childNodes.getLength(); index++) {
				
				Node	curNode = childNodes.item(index);
				String	content = curNode.getTextContent();
				String	name = curNode.getNodeName();
				if (name != null) {
					switch (name) {
					
						case ELEMENT_VERSION:
							
							version = Integer.parseInt(content);
							break;
							
						case ELEMENT_VAULT_NAME:
							
							vaultName = content;
							break;
					
						case ELEMENT_VAULT_DESCRIPTION:
							
							description = content;
							break;
							
						case ELEMENT_PASSWORD_HINT:
							
							passwordHint = content;
							break;
							
						case ELEMENT_ENCRYPT_USER_ID:
							
							encryptUserId = Boolean.parseBoolean(content);
							break;
							
						case ELEMENT_ENCRYPTION_ALGORITHM:
							encryptionAlgorithm = EncryptionAlgorithm.fromValue(Integer.parseInt(content));
							break;
							
						case ELEMENT_VAULT_ENTRIES:
							
							processVaultEntries(curNode, logger);
							break;
							
						default:
							
							if (curNode.getNodeType() == Node.ELEMENT_NODE) {
								
								content = "Skipping unknown element";
							}
					}
					
					if (logger.isLoggable(Level.INFO) && 
						(curNode.getNodeType() == Node.ELEMENT_NODE)
						&& (curNode.getNodeName() != ELEMENT_VAULT_ENTRIES)) {
						
						logger.log(Level.INFO, "Read element " + name + " Value: " + content);
					}
				}
			}
			
			modified = false;
			
			//	Version 2 of the vault file definition did not specify an encryption algorithm or number of salt bytes and had a bug where the userId was encoded using
			//	The UtilHex algorithm instead of Base 64.  Set the encryption algorithm, numSaltBytes and user id encoding for a Version 2 vault file
			if (version == 2) {
				
				encryptionAlgorithm = EncryptionAlgorithm.DES;
				userIdEncoding = ENCODING_UTILHEX;
				numSaltBytes = 0;
			}
			
			getEncryptorDecryptor();
		}
		catch (Exception except) {
			
			String	message = "Error loading vault file " + vaultName;
			if (logger.isLoggable(Level.SEVERE)) {
				
				logger.log(Level.SEVERE, message, except);
			}
			throw new PasswordVaultException(message, except);
		}
	}
	
	public void store(File vaultFile) 
		throws PasswordVaultException {
		
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	
			// root UserVault Element
			Document doc = docBuilder.newDocument();
			Element userVault = doc.createElement(ELEMENT_USER_VAULT);
			doc.appendChild(userVault);
	
			//	Add the Vault attributes
			Element versionElement = doc.createElement(ELEMENT_VERSION);
			versionElement.appendChild(doc.createTextNode(String.valueOf(version)));
			userVault.appendChild(versionElement);
			
			Element vaultNameElement = doc.createElement(ELEMENT_VAULT_NAME);
			vaultNameElement.appendChild(doc.createTextNode(vaultName));
			userVault.appendChild(vaultNameElement);
			
			Element descriptionElement = doc.createElement(ELEMENT_VAULT_DESCRIPTION);
			descriptionElement.appendChild(doc.createTextNode(description));
			userVault.appendChild(descriptionElement);
			
			Element passwordHintElement = doc.createElement(ELEMENT_PASSWORD_HINT);
			passwordHintElement.appendChild(doc.createTextNode(passwordHint));
			userVault.appendChild(passwordHintElement);
			
			Element encryptUserIdElement = doc.createElement(ELEMENT_ENCRYPT_USER_ID);
			encryptUserIdElement.appendChild(doc.createTextNode(String.valueOf(encryptUserId)));
			userVault.appendChild(encryptUserIdElement);
			
			if (version >= 3) {
				
				Element encryptionAlgorithmElement = doc.createElement(ELEMENT_ENCRYPTION_ALGORITHM);
				encryptionAlgorithmElement.appendChild(doc.createTextNode(String.valueOf(encryptionAlgorithm.getValue())));
				userVault.appendChild(encryptionAlgorithmElement);
			}
			
			//	Add the VaultEntries Element
			Element vaultEntriesElement = doc.createElement(ELEMENT_VAULT_ENTRIES);
			for (VaultEntry curEntry : vaultEntries) {
				
				Element		curVaultEntryElement = createVaultEntryElement(curEntry, doc);
				vaultEntriesElement.appendChild(curVaultEntryElement);
			}
			userVault.appendChild(vaultEntriesElement);
	
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(vaultFile);
			transformer.transform(source, result);
			modified = false;
		}
		catch (Exception except) {
			
			throw new PasswordVaultException("Error storing vault " + vaultName, except);
		}
	}
	
	public byte[] encrypt(String value)
		throws PasswordVaultException {
		
		try {

			return encryptorDecryptor.encrypt(value, encryptionKey);
		}
		catch (EcdsSecurityException except){
			
			throw new PasswordVaultException("Error encrypting value", except);
		}
	}
	
	public String decrypt(byte[] encryptedValue)
		throws PasswordVaultException {
		
		try {
			
			return encryptorDecryptor.decrypt(encryptedValue, encryptionKey);
		}
		catch (EcdsSecurityException except){
			
			throw new PasswordVaultException("Error encrypting value", except);
		}
	}

	public VaultEntry getEntryById(int entryId) {
		
		for (VaultEntry curEntry : vaultEntries) {
			
			if (curEntry.getId() == entryId) {
				
				return curEntry;
			}
		}
		
		return null;
	}

	public ObservableList<VaultEntry> getEntries() {
		
		return vaultEntries;
	}
	
	public void addEntry(VaultEntry entry) {
		
		vaultEntries.add(entry);
	}
	
	public boolean removeEntry(VaultEntry entry) {
		
		return vaultEntries.remove(entry);
	}
	
	public int getVersion() {
		return version;
	}

	public String getVaultName() {
		return vaultName;
	}

	public String getDescription() {
		return description;
	}

	public String getPasswordHint() {
		return passwordHint;
	}

	public static String getPasswordHint(String vaultName) {
		
		String		passwordHint = "";

		try {
			
			File vaultFile = new File(Environment.getEnvironment().getVaultDir(), vaultName + VAULT_FILE_EXTENSTION);
			
			DocumentBuilder	docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document		vaultDoc = docBuilder.parse(vaultFile);
			
			NodeList	passwordHintNodes = vaultDoc.getElementsByTagName(ELEMENT_PASSWORD_HINT);
			int			numPasswordHints = passwordHintNodes.getLength();
			if (numPasswordHints > 0) {

				//	There should be at most one PasswordHint element
				if (numPasswordHints > 1) {
					
					VaultUtil.displayMessageDlg("Password Hint", "Vault has multiple password hint entries - Using first hint found in vault");
				}
				
				//	There should be a single child TEXT node that contains the actual password hint.  If the hint
				//	does not exist return the default empty string
				Node	passwordHintNode = passwordHintNodes.item(0);
				NodeList	children = passwordHintNode.getChildNodes();
				if (children.getLength() == 1) {
					
					Node	childNode = children.item(0);
					passwordHint = childNode.getNodeValue();
				}
			}
		}
		catch(Exception except) {
			
			VaultUtil.displayErrorDialog("Error getting password hint from vault", except);
		}

		return passwordHint;
	}
	
	public boolean getEncryptUserId() {
		return encryptUserId;
	}

	public void setDescription(String description) {
		
		if ((this.description == null) && (description != null) ||
		   (this.description != null) && (!this.description.equals(description))) {
			
			this.description = description;
			modified = true;
			env.getApplication().updateControls();
		}
	}

	public void setPasswordHint(String passwordHint) {
		
		if ((this.passwordHint == null) && (passwordHint != null) ||
		   (this.passwordHint != null) && (!this.passwordHint.equals(passwordHint))) {
			
			this.passwordHint = passwordHint;
			modified = true;
			env.getApplication().updateControls();
		}
	}

	public boolean isModified() {
		return modified;
	}
	
	public EncryptionAlgorithm getEncryptionAlgorithm() {
		
		return encryptionAlgorithm;
	}
	
	public int getUserIdEncoding() {
		
		return userIdEncoding;
	}
	
	public boolean unlock(String password) {
		
		try {

			getEncryptionKey(vaultName, password);
			return true;
		}
		catch (PasswordVaultException except) {
			
			return false;
		}
	}

	private void processVaultEntries(Node vaultEntriesNode, Logger logger) 
		throws PasswordVaultException {
		
		try {
			
			if (logger.isLoggable(Level.INFO)) {
				
				if (!logger.isLoggable(Level.FINE)) {
				
					logger.log(Level.INFO, "Processing Vault Entries - Logging of individual entries is disabled.  To enable logging of the individual entries set the log level for the " + PvLogger.LOGGER_VAULT + " logger to FINE");
				}
				else {
					
					logger.log(Level.INFO, "Processing Vault Entries");
				}
			}
			NodeList childNodes = vaultEntriesNode.getChildNodes();
			for (int index = 0; index < childNodes.getLength(); index++) {
				
				Node curNode = childNodes.item(index);
				if (curNode.getNodeName().equals(ELEMENT_VAULT_ENTRY)) {
					
					processVaultEntry(curNode, logger);
				}
			}
		}
		catch (Exception except) {
			
			String	message = "Error processing vault entries list";
			if (logger.isLoggable(Level.SEVERE)) {
				
				logger.log(Level.SEVERE, message, except);
			}
			throw new PasswordVaultException(message, except);
		}
	}
	
	private void processVaultEntry(Node vaultEntryNode, Logger logger)
		throws PasswordVaultException {
		
		try {
			
			VaultEntry	vaultEntry = new VaultEntry();
			
			NodeList childNodes = vaultEntryNode.getChildNodes();
			for (int index = 0; index < childNodes.getLength(); index++) {
				
				Node curNode = childNodes.item(index);
				String	content = curNode.getTextContent();
				switch (curNode.getNodeName()) {
				
					case ELEMENT_ID:
						
						vaultEntry.setId(Integer.parseInt(content));
						break;
						
					case ELEMENT_ENTRY_DESCRIPTION:
						
						vaultEntry.setDescription(content);
						break;
						
					case ELEMENT_LOCATION:
						
						vaultEntry.setLocation(content);
						break;
						
					case ELEMENT_USER_ID:
						vaultEntry.setUserId(content);
						break;
						
					case ELEMENT_PASSWORD:
						
						vaultEntry.setPassword(content);
						break;
						
					default:
						
						//	Skip unknown elements
				}
			}
			
			vaultEntries.add(vaultEntry);
			if (logger.isLoggable(Level.FINE)) {
				
				logger.log(Level.FINE, "Processed Vault Entry: [id = " + vaultEntry.getId() + "][desription = " + vaultEntry.getDescription() + "][location = " + vaultEntry.getLocation() + "][userId = <hidden>][password = <hidden>]");
			}			
		}
		catch (Exception except) {
			
			String	message = "Error processing Vault Entry";
			if (logger.isLoggable(Level.SEVERE)) {
				
				logger.log(Level.SEVERE, message, except);
			}
			throw new PasswordVaultException(message, except);
		}
	}
	
	private void getEncryptionKey(String userId, String password) 
		throws PasswordVaultException {

		try {
		
			EcdsKeyStore keystore = env.getKeyStore();
			encryptionKey = keystore.fetchKey(normalizeUserId(userId), password);
			if (encryptionKey == null) {
				
				throw new PasswordVaultException("Invalid vault name or password");
			}
		}
		catch (EcdsSecurityException except) {
			
			throw new PasswordVaultException("Error getting vault encryption key", except);
		}
	}
	
	private void createEncryptionKey(String userId, String password)
		throws PasswordVaultException {
		
		try {
			
			String normalizedUserId = normalizeUserId(userId);
			EcdsKeyStore	keystore = env.getKeyStore();
			if (keystore.keyExists(normalizedUserId)) {
				
				throw new PasswordVaultException("Error creating encryption key - Key with name " + userId + " already exists");
			}
			
			KeySize		keySize;
			switch (encryptionAlgorithm) {
			
				case AES128:
					
					keySize = KeySize.KEY128;
					break;
					
				case AES256:
					
					keySize = KeySize.KEY256;
					break;
					
				default:
					
					throw new PasswordVaultException("Error creating encryption key - Invalid encryption algorithm " + getEncryptionAlgorithm());
			}
			Key newKey = keystore.createKey(keySize);
			
			keystore.storeKey(newKey, normalizedUserId, password);
			keystore.store();
			encryptionKey = newKey;
		}
		catch (EcdsSecurityException except) {
			
			throw new PasswordVaultException("Error creating encryption key",  except);
		}
	}
	
	private String normalizeUserId(String userId) {
		
		return userId.toLowerCase();
	}
	
	private Element createVaultEntryElement (VaultEntry vaultEntry, Document doc)
		throws PasswordVaultException {
		
		try {
			
			Element	vaultEntryElement = doc.createElement(ELEMENT_VAULT_ENTRY);
			
			Element idElement = doc.createElement(ELEMENT_ID);
			idElement.appendChild(doc.createTextNode(String.valueOf(vaultEntry.getId())));
			vaultEntryElement.appendChild(idElement);
			
			Element descriptionElement = doc.createElement(ELEMENT_ENTRY_DESCRIPTION);
			descriptionElement.appendChild(doc.createTextNode(vaultEntry.getDescription()));
			vaultEntryElement.appendChild(descriptionElement);
			
			Element locationElement = doc.createElement(ELEMENT_LOCATION);
			locationElement.appendChild(doc.createTextNode(vaultEntry.getLocation()));
			vaultEntryElement.appendChild(locationElement);
			
			Element userIdElement = doc.createElement(ELEMENT_USER_ID);
			userIdElement.appendChild(doc.createTextNode(vaultEntry.getUserId()));
			vaultEntryElement.appendChild(userIdElement);
			
			Element passwordElement = doc.createElement(ELEMENT_PASSWORD);
			passwordElement.appendChild(doc.createTextNode(vaultEntry.getPassword()));
			vaultEntryElement.appendChild(passwordElement);
			
			return vaultEntryElement;
		}
		catch (Exception except) {
			
			throw new PasswordVaultException("Error creating vault entry for entry " + vaultEntry.getLocation(), except);
		}
	}
	
	private void createVaultEntriesList() {
		
		Callback<VaultEntry, Observable[]> extractor = new Callback<VaultEntry, Observable[]>() {
	
		    @Override
		    public Observable[] call(VaultEntry entry) {
		        return new Observable[] {entry.idProperty(), entry.locationProperty(), entry.descriptionProperty(), entry.userIdProperty(), entry.passwordProperty()};
		    }
		};
		
		vaultEntries = FXCollections.observableArrayList(extractor);
		vaultEntries.addListener(new ListChangeListener<VaultEntry>() {
	
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends VaultEntry> c) {
				
				while (c.next()) {
					boolean wasAdded = c.wasAdded();
					boolean wasRemoved = c.wasRemoved();
					boolean wasReplaced = c.wasReplaced();
					boolean wasUpdated = c.wasUpdated();
	
					if (wasAdded || wasRemoved || wasReplaced || wasUpdated) {
					
						modified = true;
						env.getApplication().updateControls();
					}
				}
			}
		});
	}

	private void getEncryptorDecryptor() 
		throws PasswordVaultException  {
		
		switch (encryptionAlgorithm) {
		
			case DES:
				
				encryptorDecryptor = new EcdsDESEncryptorDecryptor(numSaltBytes);
				break;
				
			case AES128:
			case AES256:
				
				encryptorDecryptor = new EcdsAESEncryptorDecryptor(numSaltBytes);
				break;
				
			default:
				
				throw new PasswordVaultException("Invalid Encryption Algorithm: " + encryptionAlgorithm);
		}
	}
	
	public static UserVault loadUserVault(File vaultFile, String vaultName, String password) 
		throws PasswordVaultException {
		
		UserVault	vault = new UserVault();
		vault.load(vaultFile, vaultName, password);
		return vault;
	}
	
	public static class Builder {
		
		private String						vaultName;
		private String						vaultPassword;
		private String						description;
		private String						passwordHint;
		private EncryptionAlgorithm			encryptionAlgorithm;
		
		public Builder(String vaultName, String vaultPassword) {
			
			this.vaultName = vaultName;
			this.vaultPassword = vaultPassword;
		}
		
		public Builder description(String description) {
			
			this.description = description;
			return this;
		}

		public Builder passwordHint(String passwordHint) {
			
			this.passwordHint = passwordHint;
			return this;
		}
		
		public Builder encryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
			
			this.encryptionAlgorithm = encryptionAlgorithm;
			return this;
		}
		
		public UserVault build() 
			throws PasswordVaultException {
			
			return new UserVault(this);
		}
	}
}
