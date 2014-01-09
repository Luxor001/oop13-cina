package client;


import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<ChatMessage> {

  @Override
  public String encode(ChatMessage message) throws EncodeException {

    JsonObject jsonObject = Json.createObjectBuilder()
        .add("Type", message.getType().toString())
        .add("Message", message.getMessage()).build();
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

}