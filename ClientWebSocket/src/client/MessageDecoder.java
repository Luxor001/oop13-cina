package client;

import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import client.ChatMessage.Param;


public class MessageDecoder implements Decoder.Text<ChatMessage> {


	  @Override
	  public ChatMessage decode(String jsonMessage) throws DecodeException {

	    JsonObject jsonObject = Json
	        .createReader(new StringReader(jsonMessage)).readObject();

	   String Type= jsonObject.getString("Type");
	   String Message= jsonObject.getString("Message");
	   ArrayList<Param> addlParams = JsonArrayToParam(jsonObject
				.getJsonArray("addParams"));
	
	   
		ChatMessage message = new ChatMessage(Message,
				ChatMessage.Type.INITIALIZE, addlParams); /* WE NEED TO FIX TYPE RECOGNIZATION */  
	    return message;

	  }

	  @Override
	  public boolean willDecode(String jsonMessage) {
	    try {
	      // Check if incoming message is valid JSON
	      Json.createReader(new StringReader(jsonMessage)).readObject();
	      return true;
	    } catch (Exception e) {
	      return false;
	    }
	  }

	  @Override
	  public void init(EndpointConfig ec) {
	    System.out.println("MessageDecoder -init method called");
	  }

	  @Override
	  public void destroy() {
	    System.out.println("MessageDecoder - destroy method called");
	  }

	  private static ArrayList<Param> JsonArrayToParam(JsonArray jsonArray) {
			ArrayList<Param> additionalParams = new ArrayList<Param>();
			if (jsonArray.size() != 0) //if it's not empty
				for (int i = 0; i < jsonArray.size(); i++){ //cycle all elements.
					JsonObject currObject = jsonArray.getJsonObject(i);
				
					String[] keys=currObject.keySet().toArray(new String[0]);
					for(String currKey:keys){
						String value=currObject.getString(currKey);
						additionalParams.add(new ChatMessage().new Param(
								currKey,value));
					}
				}

			return additionalParams;
		  }
	}