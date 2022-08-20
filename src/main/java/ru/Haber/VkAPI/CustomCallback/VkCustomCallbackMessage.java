package ru.Haber.VkAPI.CustomCallback;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
@EqualsAndHashCode
public class VkCustomCallbackMessage<T> {
	@Getter
	@SerializedName("group_id")
	Integer group_id;
	@Getter
	@SerializedName("type")
	final String type = "message_event";
	@Getter
    @SerializedName("object")
    private T object;
	@Getter
    @SerializedName("secret")
    private String secret;

	public VkCustomCallbackMessage() {
		
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		
		return gson.toJson(this);
	}

}
