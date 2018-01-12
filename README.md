# passwordvault
## Overview
Save account credentials in encrypted vault files.

This is a simple app to store account credentials in encryted files.  The user interface allows the user to browse the list of credentials viewing the location of the account, the user id for the account and a brief description.  The password for the account is not displayed in the account list.  For each account the password can be copied to the clipboard without actaully displaying the password.  There is also an option to display the password for the account.

The user interface makes it easy for a user to add, modify and delete entries in a vault file.  The list of entries can be sorted buy location, user id or description.  The app can manage multiple vault files.  

## Encryption
The credential information (the user id and password) are encrypted in each vault file.  Each vault file has its own encryption key and password.  By default the information in the vault files uses AES-128 bit encryption.  If the Java VM used to run the application supports
unlimited strength encryption, AES-256 encryption may be used to encrypt the vault information.  Each vault file is protected by a password.  

**Warning:** Remember the password for each vault file.  For security reasons, there is no way to recover or reset the vault password without the current password.  The vault password can be changed, but requires the current password. 

## Prerequisites

Password Vault is a Java application and requires Java 8 or higher to run.  

## Installation

Download the appropriate package for the latest release at

https://github.com/markbnc/passwordvault/releases/latest

1. On Windows Unzip the package zip file into a directory and on Linux or Mac untar the package compressed tar file.
1. Change to the directory and double click the passwordvault-2.0.1.jar file
1. The application will open up to the OpenVault Screen.  
   1. The first time the application is open there will not be any vaults in the Vault list
      1. Create a new vault by selecting File > Create New Vault
      1. Enter the information in the Create New Vault dialog.  Remember the password.  If you forget the password, the contents of the vault will not be accessible.  You can specify a hint to help remember the password.  The hint shows in clear text so it should not be the actual password.
   1. After the first time, the created vault(s) will be shown in the Vault drop down list.  Select the desired vault and enter the password for that vault.
   
## Backing up Vaults

The Vault data and the keystore containing the vault encryption keys are stored in the Vault Directory.  The location of the Vault Direcory can be seen and modified using the Password Vault Options dialog.  The Options dialog can be displayed by selecting Edit > Options.  The Vault Directory path can be either an absolute or relative path.  If the path is relative it will be relative to the

%appdata%\PasswordVault   on Windows

and 

~/.password_vault         on Linux and Mac

To back up your vault data, make a copy of all of the files in the Vault Directory.
