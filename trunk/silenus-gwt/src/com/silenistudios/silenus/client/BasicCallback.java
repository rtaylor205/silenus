package com.silenistudios.silenus.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.silenistudios.silenus.shared.ParseException;

public abstract class BasicCallback<T> implements AsyncCallback<T> {
	
	// main
	MainCallback fMain;
	
	// basic error handling
	public BasicCallback(MainCallback main) {
		fMain = main;
	}
	
	@Override
	public void onFailure(Throwable caught) {
		if (caught instanceof ParseException) {
			onInputError((ParseException)caught);
		}
		else {
			caught.printStackTrace();
			fMain.error(caught.getMessage());
			// TODO change when in production
			//fMain.popup(fMain.getLocale().connectError());
		}
	}
	
	// can be overridden if desired
	public void onInputError(ParseException caught) {
		fMain.error(caught.getMessage());
	}

}
