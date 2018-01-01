package com.ecdsinc.passwordvault;

import javafx.scene.control.ButtonType;
import javafx.util.Callback;

public interface PvDialogController<T> {

	Callback<ButtonType, T> getResponseConverter();
	void setInitialValues(String... initialValues);
}
