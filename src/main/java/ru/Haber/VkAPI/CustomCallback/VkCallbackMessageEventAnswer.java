package ru.Haber.VkAPI.CustomCallback;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;

public class VkCallbackMessageEventAnswer extends AbstractQueryBuilder<VkCallbackMessageEventAnswer, Integer>{

    public VkCallbackMessageEventAnswer(VkApiClient client, GroupActor actor) {
        super(client, "messages.sendMessageEventAnswer", Integer.class);
        accessToken(actor.getAccessToken());
    }
    
    public VkCallbackMessageEventAnswer peerId(Integer value) {
        return unsafeParam("peer_id", value);
    }
    
    public VkCallbackMessageEventAnswer eventId(String value) {
        return unsafeParam("event_id", value);
    }
    
    public VkCallbackMessageEventAnswer userId(Integer value) {
        return unsafeParam("user_id", value);
    }
    
    public VkCallbackMessageEventAnswer event_data(String data) {
        return unsafeParam("event_data", data);
    }
    
    @Override
    protected VkCallbackMessageEventAnswer getThis() {
        return this;
    }

    @Override
    protected List<String> essentialKeys() {
        return Arrays.asList("access_token");
    }

}
