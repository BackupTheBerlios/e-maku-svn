package com.kazak.comeet.client.control;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	public void getANewMessage(MessageEvent event); 
}
