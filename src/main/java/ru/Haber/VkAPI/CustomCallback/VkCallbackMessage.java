package ru.Haber.VkAPI.CustomCallback;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class VkCallbackMessage {
	@Getter
	@SerializedName("user_id")
	Integer user_id;
	@Getter
	@SerializedName("peer_id")
	Integer peer_id;
	@Getter
	@SerializedName("event_id")
	String event_id;
	@Getter
	@SerializedName("payload")
	CallbackMessagePayload payload;
	@Getter
	@SerializedName("conversation_message_id")
	Integer conversation_message_id;
	
	public VkCallbackMessage setUserId(@NotNull Integer userId) {
		this.user_id = userId;
		return this;
		
	}
	
	public VkCallbackMessage setPeerId(@NotNull Integer peerid) {
		this.peer_id = peerid;
		return this;
	}
	
	public VkCallbackMessage setEventId(@NotNull String eventid) {
		this.event_id = eventid;
		return this;
	}
	
	public VkCallbackMessage setCallbackPayload(@NotNull CallbackMessagePayload payload) {
		this.payload = payload;
		return this;
	}
	
	public VkCallbackMessage setConversationMessageId(@NotNull Integer conversation_message_id) {
		this.conversation_message_id = conversation_message_id;
		return this;
	}
	
	@Override
	public String toString() {
		
		final Gson gson = new Gson();
		
		return gson.toJson(this);
		
	}
	

	public VkCallbackMessage() {
		// TODO Auto-generated constructor stub
	}
	
}
