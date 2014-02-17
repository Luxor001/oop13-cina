package client_chat;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<ChatMessage> {

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
		System.out.println("MessageEncoder - init method called");
	}

	@Override
	public void destroy() {
		System.out.println("MessageEncoder - destroy method called");
	}

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
				|| message.getType() == ChatMessage.Type.REQUESTPRIVATECHAT) {
			builder.add(Json.createObjectBuilder().add("Nickname",
					param.getNickname()));
			return builder.build();
		}

		if (message.getType() == ChatMessage.Type.TEXT) {
			builder.add(Json.createObjectBuilder().add("Nickname",
					param.getNickname()));
			return builder.build();
		}

		if (message.getType() == ChatMessage.Type.YESPRIVATECHAT) {
			builder.add(Json.createObjectBuilder().add("Nickname",
					param.getNickname()));
			builder.add(Json.createObjectBuilder().add("ip", param.getIP()));
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