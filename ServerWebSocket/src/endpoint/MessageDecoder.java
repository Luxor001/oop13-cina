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

import endpoint.ChatMessage.Param;


public class MessageDecoder implements Decoder.Text<ChatMessage> {


	  @Override
	  public ChatMessage decode(String jsonMessage) throws DecodeException {

	    JsonObject jsonObject = Json
	        .createReader(new StringReader(jsonMessage)).readObject();

	   String Type= jsonObject.getString("Type"); //gets the type of the request sent by the user
	   String Message= jsonObject.getString("Message");	  //gets the textual message.
		ArrayList<Param> addlParams = JsonArrayToParam(jsonObject
				.getJsonArray("addParams"));	// gets any other params need to be sent
	
	   
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

	  /*Specular to MessageEncoder encoding part.See it for reference*/
	  
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