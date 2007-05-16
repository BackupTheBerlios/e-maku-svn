package com.kazak.smi.client.control;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	public void getANewMessage(MessageEvent event); 
}
