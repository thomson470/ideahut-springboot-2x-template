package net.ideahut.springboot.template.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import net.ideahut.springboot.message.MessageHandler;
import net.ideahut.springboot.object.Option;

public interface MessageService extends MessageHandler {
	
	void setRequestLanguage();
	List<Option> getActiveLanguages();
	String getDefaultLanguage();
	
	JsonNode getResource(String type);
	
}
