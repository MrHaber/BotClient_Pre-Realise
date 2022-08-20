package ru.Haber.VkAPI.CustomCallback;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.vk.api.sdk.objects.Validable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
@EqualsAndHashCode
public class CallbackMessagePayload implements Validable{
	@Getter
	@SerializedName("type")
	String type;
	@Getter
	@SerializedName("link")
	String link;
	@Getter
	@SerializedName("text")
	String text;
	
	@Getter
	@SerializedName("app_id")
	Integer app_id;
	@Getter
	@SerializedName("owner_id")
	Integer owner_id;
	@Getter
	@SerializedName("hash")
	String hash;
	
	public CallbackMessagePayload setType(@NotNull VkMessagePayloadType type) {
		this.type = type.toString().toLowerCase();
		return this;
	}
	
	public CallbackMessagePayload setType(@NotNull String type) {
		this.type = type.toString();
		return this;
	}
	
	public CallbackMessagePayload setLink(@NotNull String link) {
		this.link = link;
		return this;
	}
	public CallbackMessagePayload setText(@NotNull String text) {
		this.text = text;
		
		return this;
	}
	
	public CallbackMessagePayload setAppId(@NotNull Integer app_id) {
		this.app_id = app_id;
		
		return this;
	}
	
	public CallbackMessagePayload setOwnerId(@NotNull Integer owner_id) {
		this.owner_id = owner_id;
		
		return this;
	}
	
	public CallbackMessagePayload setHash(@NotNull String hash) {
		this.hash = hash;
		
		return this;
	}
	
	@Override
	public String toString() {
		
		final Gson gson = new Gson();
		
		return gson.toJson(this);
		
	}
	
	public CallbackMessagePayload() {
		
	}
	
	public static enum VkMessagePayloadType {
		@SerializedName("open_app")
		OPEN_APP("open_app"),
		@SerializedName("open_link")
		OPEN_LINK("open_link"),
		@SerializedName("show_snackbar")
		SHOW_SNACKBAR("show_snackbar");
		@Getter
		private final String data;
		
		
		private VkMessagePayloadType(@NotNull String data) {
			this.data = data;
		}
	}

}
