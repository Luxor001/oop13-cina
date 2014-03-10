package client_chat;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


/**
 * 
 * This class handles the outgoing message from the WebSocketHandler Class.
 * In fact, it ENCODES the messages from a "ChatMessage" istance to an outgoing
 * JSON string.
 * Be aware: this decoder is similar to the server correspective. 
 * 
 * @author Stefano Belli
 **/
public class MessageEncoder implements Encoder.Text<ChatMessage> {


	/**
	 * MessageDecoder main method. It's an entry point for every incoming
	 * message for the websockethandler class.
	 * @return the encoded chatmessage in JSON format.
	 * */
	@Override
	public String encode(ChatMessage message) throws EncodeException {
		JsonObject jsonObject;
		if (message.isParamSet()) {
			jsonObject = Json.createObjectBuilder()
					.add("Type", message.getType().toString())
					.add("Message", message.getMessage())
					.add("addParams", ParamToJsonArray(message)).build();
		} else {
			jsonObject = Json.createObjectBuilder()
					.add("Type", message.getType().toString())
					.add("Message", message.getMessage()).build();
		}
		return jsonObject.toString();

	}

	@Override
	public void init(EndpointConfig ec) {
	}
	@Override
	public void destroy() {
	}

	/**
	 * This method creates a JsonArray based on a chatmessage istance
	 * @return A JsonArray istance based on a chatmessage istance.
	 * */
	private static JsonArray ParamToJsonArray(ChatMessage message) {

		ChatMessage.Param param = message.getAdditionalParams();
		JsonArrayBuilder builder = Json.createArrayBuilder();

		if (message.getType() == ChatMessage.Type.USERLIST) {
			JsonArrayBuilder listbuilder = Json.createArrayBuilder();
			for (String cUser : message.getAdditionalParams().getUsersList())
				listbuilder.add(cUser);

			builder.add(Json.createObjectBuilder().add("usersList",
					listbuilder.build()));
			return builder.build();
		}
		if (message.getType() == ChatMessage.Type.NEWUSER
				|| message.getType() == ChatMessage.Type.USERDISCONNECTED
				|| message.getType() == ChatMessage.Type.REQUESTPRIVATECHAT 
				|| message.getType() == ChatMessage.Type.NOPRIVATECHAT
				|| message.getType() == ChatMessage.Type.NOSENDFILE) {
			builder.add(Json.createObjectBuilder().add("Nickname",
					param.getNickname()));
			return builder.build();
		}

		if (message.getType() == ChatMessage.Type.TEXT) {
			builder.add(Json.createObjectBuilder().add("Nickname",
					param.getNickname()));
			return builder.build();
		}

		if (message.getType() == ChatMessage.Type.YESPRIVATECHAT 
				|| message.getType() == ChatMessage.Type.YESSENDFILE) {
			builder.add(Json.createObjectBuilder().add("Nickname",
					param.getNickname()));
			builder.add(Json.createObjectBuilder().add("ip", param.getIP()));			

			if(message.getType() == ChatMessage.Type.YESSENDFILE){
				builder.add(Json.createObjectBuilder().add("FileName", param.getFileName()));
			}
			
			builder.add(Json.createObjectBuilder().add("SSLPort", param.getSSLPort()));
			builder.add(Json.createObjectBuilder().add("KEYPort", param.getKEYPort()));
			return builder.build();
		}

		if(message.getType() == ChatMessage.Type.REQUESTEDSENDFILE || 
				message.getType() ==  ChatMessage.Type.REQUESTSENDFILE){			
			builder.add(Json.createObjectBuilder().add("Nickname",
					param.getNickname()));
			builder.add(Json.createObjectBuilder().add("FileName",
					param.getFileName()));
			return builder.build();		
		}

		builder.add(Json.createObjectBuilder().add("Visibility",
				param.getVisibility().toString()));
		builder.add(Json.createObjectBuilder().add("Nickname",
				param.getNickname()));

		JsonArray jsonArray = builder.build(); // build the resultant Json Array

		return jsonArray;
	}
}