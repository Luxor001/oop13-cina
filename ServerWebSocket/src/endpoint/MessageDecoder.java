package endpoint;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.JsonParser;

import endpoint.ChatMessage.Param;


public class MessageDecoder implements Decoder.Text<ChatMessage> {


	  @Override
	  public ChatMessage decode(String jsonMessage){

	    JsonObject jsonObject = Json
	        .createReader(new StringReader(jsonMessage)).readObject();

	   String Type= jsonObject.getString("Type"); //gets type of the request sent by the user
		String Message = jsonObject.getString("Message"); // gets the textual message.
		
		
		ChatMessage message = new ChatMessage(Message,
				ChatMessage.Type.valueOf(Type));
		
		
		if (jsonObject.containsKey("addParams")) {
			Param addlParams = JsonArrayToParam(jsonObject
					.getJsonArray("addParams")); // gets any other params need to be sent

			message.setAdditionalParams(addlParams);
		}
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



	  /*this class rebuilds the original chatmessage decoding the json.*/
	private static Param JsonArrayToParam(JsonArray jsonArray) {
		Param additionalParams = new ChatMessage.Param();
		if (jsonArray.size() != 0) //if it's not empty
			for (int i = 0; i < jsonArray.size(); i++){ //cycle all elements.
				JsonObject currObject = jsonArray.getJsonObject(i);
				
				if(currObject.containsKey("Nickname")){
					additionalParams.setNickname(currObject.getString("Nickname"));
				}
	

				/*http://toadbalancing.blogspot.it/2005/10/java-api-pitfalls-booleangetbooleanstr.html
				take your time to read it, and DONT EVER FOR GOD SAKE
				use Boolean.getBoolean(), i wasted 2 hours on it*/
				if(currObject.containsKey("Visibility")){
					additionalParams.SetVisibility(Boolean.valueOf(
							((currObject.getString("Visibility")))));	
				}			
							
				
				if(currObject.containsKey("usersList")){
					JsonArray array=currObject.getJsonArray("usersList");					
					for(int i2=0;i < array.size();i++){
						additionalParams.appendUser(array.getString(i2));
					}
				}

				if(currObject.containsKey("SSLPort")){
					additionalParams.setSSLPort(currObject.getString("SSLPort"));					
				}
				if(currObject.containsKey("KEYPort")){
					additionalParams.setKEYPort(currObject.getString("KEYPort"));					
				}
				if(currObject.containsKey("ip")){
						additionalParams.setIP(currObject.getString("ip"));					
				}
				
				if(currObject.containsKey("FileName")){
					additionalParams.setFileName(currObject.getString("FileName"));					
				}
			}
		return additionalParams;
	  }
  }