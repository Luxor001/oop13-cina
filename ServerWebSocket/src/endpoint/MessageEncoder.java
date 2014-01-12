package endpoint;

import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.JsonParser;

import endpoint.ChatMessage.Param;

public class MessageEncoder implements Encoder.Text<ChatMessage> {

  @Override
  public String encode(ChatMessage message) throws EncodeException {

	  
    JsonObject jsonObject = Json.createObjectBuilder()
        .add("Type", message.getType().toString())
        .add("Message", message.getMessage())
        .add("addParams",ParamToJsonArray(message))
        .build();    
  
    return jsonObject.toString();

  }

  @Override
  public void init(EndpointConfig ec) {
    System.out.println("MessageEncoder - init method called");
    
  }

  @Override
  public void destroy() {
    System.out.println("MessageEncoder - destroy method called");
  }
  
  private static JsonArray ParamToJsonArray(ChatMessage message){
	     
	    ChatMessage.Param param=message.getAdditionalParams();
		JsonArrayBuilder builder = Json.createArrayBuilder(); 
		
		/*if it's a "users list" request, forget about other params and fill the list of users*/
		if(message.getType() == ChatMessage.Type.USERLIST){	
			JsonArrayBuilder listbuilder=Json.createArrayBuilder();
			for(String cUser:message.getAdditionalParams().getUsersList())
				listbuilder.add(cUser);

			builder.add(Json.createObjectBuilder().add("usersList",listbuilder.build()));
			return builder.build();
		}
		
		builder.add(Json.createObjectBuilder().add("Nickname",param.getNickname()));
		builder.add(Json.createObjectBuilder().add("Visibility",param.getVisibility().toString()));
				
		JsonArray jsonArray = builder.build(); //build the resultant Json Array
		
		return jsonArray;	  
  }

}