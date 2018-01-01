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
