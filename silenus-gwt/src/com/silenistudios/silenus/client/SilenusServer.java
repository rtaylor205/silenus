package com.silenistudios.silenus.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.silenistudios.silenus.shared.AnimationDTO;
import com.silenistudios.silenus.shared.ParseException;

@RemoteServiceRelativePath("silenusServer")
public interface SilenusServer extends RemoteService {
	
	// upload a CS5 .FLA file
	public AnimationDTO parseFLA(byte[] data) throws ParseException;
}
