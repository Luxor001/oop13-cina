package client_chat;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import client_chat.ChatMessage.Param;

public class MessageDecoder implements Decoder.Text<ChatMessage> {

	@Override
	public ChatMessage decode(String jsonMessage) {

		JsonObject jsonObject = Json
				.createReader(new StringReader(jsonMessage)).readObject();

		String Type = jsonObject.getString("Type"); // gets type of the request
													// sent by the user
		String Message = jsonObject.getString("Message"); // gets the textual
															// message.

		System.out.println("Message Received " + Type);
		ChatMessage message = new ChatMessage(Message,
				ChatMessage.Type.valueOf(Type));

		if (jsonObject.containsKey("addParams")) {
			Param addlParams = JsonArrayToParam(jsonObject
					.getJsonArray("addParams")); // gets any other params need
													// to be sent
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

	private static Param JsonArrayToParam(JsonArray jsonArray) {
		Param additionalParams = new ChatMessage.Param();
		if (jsonArray.size() != 0) // if it's not empty
			for (int i = 0; i < jsonArray.size(); i++) { // cycle all elements.
				JsonObject currObject = jsonArray.getJsonObject(i);

				if (currObject.containsKey("Nickname"))
					additionalParams.setNickname(currObject
							.getString("Nickname"));

				if (currObject.containsKey("Visibility"))
					additionalParams.SetVisibility(Boolean
							.getBoolean((currObject.getString("Visibility"))));

				if (currObject.containsKey("usersList")) {
					JsonArray array = currObject.getJsonArray("usersList");
					for (int i2 = 0; i2 < array.size(); i2++) {
						additionalParams.appendUser(array.getString(i2));
					}
				}

				if (currObject.containsKey("ip")) {
					additionalParams.setIP(currObject.getString("ip"));
				}

				if (currObject.containsKey("FileName")) {
					additionalParams.setFileName(currObject
							.getString("FileName"));
				}

				if(currObject.containsKey("SSLPort")){
					additionalParams.setSSLPort(currObject.getString("SSLPort"));					
				}
				if(currObject.containsKey("KEYPort")){
					additionalParams.setKEYPort(currObject.getString("KEYPort"));					
				}
			}
		return additionalParams;
	}
}