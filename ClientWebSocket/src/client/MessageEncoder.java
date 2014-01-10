package client;


import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import client.ChatMessage.Param;


public class MessageEncoder implements Encoder.Text<ChatMessage> {

  @Override
  public String encode(ChatMessage message) throws EncodeException {

    JsonObject jsonObject = Json.createObjectBuilder()
        .add("Type", message.getType().toString())
        .add("Message", message.getMessage())
        .add("addParams",ParamToJsonArray(message.getAdditionalParams()))
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
  
  private static JsonArray ParamToJsonArray(ArrayList<ChatMessage.Param> paramlist){
	    /*builder: helper to create a jsonarray*/
		JsonArrayBuilder builder = Json.createArrayBuilder(); 

		if (paramlist.size() != 0) //if there are no params, don't mind about
			for (Param actualParam : paramlist) {
				/*foreach param, create a Param object with a key and a value*/
				builder.add(Json.createObjectBuilder().add(
						actualParam.getKey(), actualParam.getValue()));
			}

		JsonArray jsonArray = builder.build(); //build the resultant Json Array
		return jsonArray;	  
}
}