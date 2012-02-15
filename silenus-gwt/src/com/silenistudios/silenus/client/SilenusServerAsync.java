package com.silenistudios.silenus.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.silenistudios.silenus.shared.AnimationDTO;

public interface SilenusServerAsync {
	
	void parseFLA(byte[] data, AsyncCallback<AnimationDTO> callback);

	void cleanup(String fileHash, AsyncCallback<Void> callback);

}
