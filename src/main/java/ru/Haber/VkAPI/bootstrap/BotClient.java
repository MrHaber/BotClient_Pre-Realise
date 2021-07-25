package ru.Haber.VkAPI.bootstrap;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.http.MethodNotSupportedException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.Validable;
import com.vk.api.sdk.objects.audio.Audio;
import com.vk.api.sdk.objects.base.BoolInt;
import com.vk.api.sdk.objects.board.TopicComment;
import com.vk.api.sdk.objects.callback.BoardPostDelete;
import com.vk.api.sdk.objects.callback.GroupChangePhoto;
import com.vk.api.sdk.objects.callback.GroupChangeSettings;
import com.vk.api.sdk.objects.callback.GroupJoin;
import com.vk.api.sdk.objects.callback.GroupLeave;
import com.vk.api.sdk.objects.callback.GroupOfficersEdit;
import com.vk.api.sdk.objects.callback.MarketComment;
import com.vk.api.sdk.objects.callback.MarketCommentDelete;
import com.vk.api.sdk.objects.callback.MessageAllow;
import com.vk.api.sdk.objects.callback.MessageDeny;
import com.vk.api.sdk.objects.callback.PhotoComment;
import com.vk.api.sdk.objects.callback.PhotoCommentDelete;
import com.vk.api.sdk.objects.callback.PollVoteNew;
import com.vk.api.sdk.objects.callback.UserBlock;
import com.vk.api.sdk.objects.callback.UserUnblock;
import com.vk.api.sdk.objects.callback.VideoComment;
import com.vk.api.sdk.objects.callback.VideoCommentDelete;
import com.vk.api.sdk.objects.callback.WallCommentDelete;
import com.vk.api.sdk.objects.enums.DocsType;
import com.vk.api.sdk.objects.messages.Keyboard;
import com.vk.api.sdk.objects.messages.KeyboardButton;
import com.vk.api.sdk.objects.messages.KeyboardButtonAction;
import com.vk.api.sdk.objects.messages.KeyboardButtonActionType;
import com.vk.api.sdk.objects.messages.KeyboardButtonColor;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageActionStatus;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.video.Video;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.Wallpost;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import lombok.ToString;
import lombok.Value;
import lombok.val;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.Haber.VkAPI.GoogleTextToSpeech;
import ru.Haber.VkAPI.GoogleTranslate;
import ru.Haber.VkAPI.TimeUtils;
import ru.Haber.VkAPI.Annotations.AsyncInit;
import ru.Haber.VkAPI.Annotations.BotConfigurableHandler;
import ru.Haber.VkAPI.Annotations.BotHandler;
import ru.Haber.VkAPI.Annotations.VkAction;
import ru.Haber.VkAPI.Captha.BasicCaptcha;
import ru.Haber.VkAPI.ConfigurationWrapper.Config;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate.AutomaticlyTranslation;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.VkCustomButton;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.VkLanguage.Language;
import ru.Haber.VkAPI.bootstrap.BotClient.VkHandler.VkEventHandler;
import ru.Haber.VkAPI.bootstrap.BotClient.VkHandler.VkEventHandler.IVkEventInstaller;



/*
 * 
 * BotClient интуитивная библиотека над официальной java vk sdk
 * 
 */
@Getter
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = false)
@ToString
public abstract class BotClient {

	@NotNull Integer id;
	
	private final AnnotationHandler handler = AnnotationHandler.newEmptyObject();
	
	private static VkEventHandler eventHandler;
	

	
	private static final String clientName = "BotClient";
	
	@NonFinal private static Boolean useConfigurationFile = false;
	
	private static final String version = "2.0";
	
	private static final String vkSDK = "https://github.com/VKCOM/vk-java-sdk";
	
	private static final String vkSDKVersion = "v1.0.6";
	
	private static final String charset = "UTF-8";
	
	private static final Fields[] defaultFields = {Fields.DOMAIN,Fields.PHOTO_100,Fields.DESCRIPTIONS, Fields.LAST_SEEN,Fields.BDATE, Fields.CITY, Fields.ONLINE, Fields.VERIFIED};
	
	private static IVkEventInstaller<VkEventObject> installer;
	
	@NotNull @ToString.Exclude String token;
	
	@NotNull @ToString.Exclude private VkApiClient client = new VkApiClient((TransportClient)new HttpTransportClient());
	
	@Nullable @ToString.Exclude private Config configurationFile;
	
	@NotNull @ToString.Include private GroupActor actor;
	
	@Nullable @ToString.Exclude @Getter PollHandler event;
	
	@Nullable static SafeClient safeClient;
	
	@NotNull private static Class<? extends BotClient> loaderClass;
	
	@NonFinal @Nullable private static Config config;
	
	@NotNull private static Boolean debug;
	
	
	protected static final Logger logger = LoggerFactory.getLogger(BotClient.class);
	
	@Setter
	String path;
	
	
	
	protected BotClient() {
		
	}
	
	public BotClient(@NotNull Boolean useSpecialConfigurationFile, @Nullable String configPath, @Nullable String idLink, @Nullable String token) {
		useConfigurationFile = useSpecialConfigurationFile;
		Preconditions.checkArgument(useConfigurationFile != null, "(" + clientName + " v"+ version +")" + " Configuration answer cannot be null.");
		if(useConfigurationFile) {
			onConfig(configPath);
			AnnotationHandler.setBotId(config.getInt(idLink));
			AnnotationHandler.setToken(config.getString(token));
		}
	}
	
	public static void onConfig(@Nullable String configPath) {
			if(configPath != null) {
				config = new Config(configPath + "/config.yml");
			}else {
				
				config = new Config("/config.yml");
			}

	}
	
	public static Config getConfig() {
		return config;
	}
	public abstract void onStart();
	
	public void initVariable() {
		
		handler.requireAnnotation(this);
		
	}
	
	public static IVkEventInstaller<VkEventObject> getInstaller() {
		if(installer == null) {
			
			return installer = getEventHandler().newInstaller();
		}
		
		return installer;
	}
	
	public void debug(@NotNull Boolean debugReference) {
		debug = debugReference;
	}
	
	@NotNull
	@Contract(pure = true)
	@ParametersAreNonnullByDefault
	public static <T, R extends BotClient> R newLocalExec(
			@NotNull Function<T,R> object, @NotNull Class<T> loader, @NotNull Boolean isDebbuging) {
			debug = isDebbuging;
			try {
			T appliedObject = (T)loader.newInstance();
			
			if(appliedObject instanceof BotClient) {
				return setupBotClient(object.apply(appliedObject));
			}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		return null;
		
	}
	
	@NotNull
	@Contract(pure = true)
	@ParametersAreNonnullByDefault
	public static <T extends BotClient> T newLocalExec(
			@NotNull Supplier<? extends T> object, @NotNull Boolean isDebbuging) {
			debug = isDebbuging;
		return setupBotClient(object.get());
		
	}
	
	@NotNull
	@Contract(pure = true)
	@ParametersAreNonnullByDefault
	public static <T extends BotClient> T newRemoteExec(
			@NotNull Supplier<? extends T> object, @NotNull Boolean isDebbuging) {
			debug = isDebbuging;
			setupBotClient((BotClient)object.get());
		
		return object.get();
		
	}
	
	public static <T extends BotClient> T setupBotClient(T currentLoader) {
		
		val loader = currentLoader.getClass();
		
		loaderClass = loader;
		
		return currentLoader;
		
		
	}
	
	
	public static void main(String[] args) throws ClientException, ApiException, InterruptedException, InstantiationException, IllegalAccessException {
		
		loaderClass.newInstance().start();
		
	}
	
	public static Logger getLogger() {
		return BotClient.logger;
	}
	
	private static VkHandler.VkEventHandler getEventHandler() {
		return eventHandler;
	}
	
	public AutomaticlyTranslation languageLocaleSerializer() {
		return new AutomaticlyTranslation();
	}
	

	
	@Contract(pure = true)
	
	public Config requireConfig(@NotNull String path) {
		this.path = path;
		val cfg = Optional.ofNullable(new Config(path));
		return cfg.orElseThrow(IllegalArgumentException::new);
	}
	
	public GroupActor setupGroupActorReference() {
		val gcElement = safe().botData();
		
		if(gcElement == null) {
			getLogger().error("GC Element not varificated");
		}
		
		val dataElement = gcElement.getAct();
		
		
		if(this.id == null & this.token == null) {
			this.id = gcElement.getId();
			this.token = gcElement.getToken();
			if (debug) {
				getLogger().warn("Objects token=" + this.token + " and id=" + this.id + " groupActor=" + this.actor);
			}
			
		}
		return this.actor == null ? this.actor = Optional.ofNullable(dataElement).orElse(new GroupActor(this.id, this.token)) : this.actor;
	}
	
	public void systemLocale(@NotNull String locale) {
		
		ConversationTranslate.systemLocale( Objects.requireNonNull(locale) );
		
	}
	
	public void events(@NotNull Consumer<PollHandler> handler) {
		handler.accept(event);
	}
	
	
	/*
	 * 
	 * 
	 * 
	 */
	protected GroupActor getGroupActor() {
		
		
		Preconditions.checkArgument(setupGroupActorReference() != null, "Group actor has null, this method calling cause is cancelled");
		return this.actor;
	}
	
	protected Config getConfiguration() {
		Preconditions.checkArgument(this.getConfigurationFile() != null, "Configuration has null, this method calling cause is cancelled");
		return configurationFile;
	}
	
	protected VkApiClient client() {
		
		
		Preconditions.checkArgument(this.getClient() != null, "Vk Api Client has null or garbage collector is stealing this, this method calling cause is cancelled.");
		return this.client;
	}
	
	public static SafeClient safe() {
		
			return safeClient;
			
	}
	
	public static class SafeClient {
		
		private static BotClient client;
		
		public static GroupData data;
		
		public SafeClient(BotClient client) {
			SafeClient.client = client;
		}
		@NotNull
		@Synchronized
		public static VkEventHandler asyncInit() {
			
			return eventHandler = VkEventHandler.newHandler();
			
		}
		
		@Synchronized
		public GroupActor getGroupActor() {
			
			return client.getGroupActor();
			
		}
		
		@Synchronized
		public Config getConfig() {
			
			return client.getConfiguration();
		}
		
		@Synchronized
		public VkApiClient getClient() {
			return client.client();
		}
		
		@Synchronized
		
		public void setupGroupData(@NotNull GroupData data_s) {
			data = data_s;
		}
		
		@Synchronized
		public GroupData botData() {
			return data;
		}
		
		@NotNull
		@Getter
		@ToString
		@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
		public static class GroupData {
			
			 Integer id;
			
			 String token;
			
			@Nullable GroupActor act;
			
			public GroupData(@NotNull Integer id, @NotNull String token) {
				this.id = id;
				this.token = token;
				
				this.act = new GroupActor(id,token);
				
			}
			@Contract(pure = true)
			@NotNull final GroupActor actor() {
				
				return this.act == null ? new GroupActor(this.id,this.token) : this.act;
				
			}
			
			
		}
		
	}
	
	
	
	public void start() {
		
		initVariable();
		
		this.id = AnnotationHandler.botId();
		
		this.token  = AnnotationHandler.botToken();
		if (debug) {
		System.out.println(this.id + " " + this.token);
		}
		logger.info("(" + clientName + " v"+ version +")" + " Invoking Group data element on server side");
		
		if (path != null) {
			this.configurationFile = requireConfig(path);
		}
		else {
			logger.warn("(" + clientName + " v"+ version +")" +"(BotClient) Configuration file, is exists in API client.");
		}
		try {
			safeClient = new SafeClient(loaderClass.newInstance());
		} catch (InstantiationException | IllegalAccessException e1) {
			logger.warn("(" + clientName + " v"+ version +")" + " Loader class instance is not verificated. Exception code: " + e1.getLocalizedMessage());
			e1.printStackTrace();
		}
		safe().setupGroupData(new BotClient.SafeClient.GroupData(id,token));
		this.actor = this.setupGroupActorReference();
		
		logger.info("(" + clientName + " v"+ version +")" + " Project using " + vkSDK);
		logger.info("(" + clientName + " v"+ version +")" + " Current SDK Version is " + vkSDKVersion);
		
		logger.info("GroupActor: " + this.actor);
		logger.info("(BotClient) Starting offsets...");
		event = new PollHandler(this.client, this.actor);
		SafeClient.asyncInit();
			logger.info("(BotClient) Check latest event wrappers...");
			
			
			this.onStart();
			
			logger.info("(BotClient) Bot started!");
			new Thread(() -> {
			try {
				event.run();
			} catch (ClientException | ApiException e) {
				
				logger.error("(BotClient) Caused crashed, when he tried loading");
			}
			}).start();
			
	}
	
	private interface Buildable<T> {
		
		T build();
		
	}
	
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = false)
    @NoArgsConstructor
    @ToString
    public static class AnnotationHandler{
    	@Nullable private static Integer id;
    	@Nullable private static String token;
    	
    	
    	public static Integer botId() {
    		return id;
    	}
    	
    	public static String botToken() {
    		return token;
    	}
    	
    	public static String setToken(@NotNull String botToken) {
    		return token = botToken;
    	}
    	
    	public static Integer setBotId(Integer botId) {
    		return id = botId;
    	}
    	
    	public static String setBotId(String botToken) {
    		return token = botToken;
    	}
    	
    	public static AnnotationHandler newEmptyObject() {
    		
    		return new AnnotationHandler();
    		
    	}
    	@Contract(pure = true)
    	public <T> Class<? extends Object> requireAnnotation(@NotNull T object) {
    		
    		val cls = object.getClass();
    		if(cls.isAnnotationPresent(BotHandler.class)) {
    			
    					Annotation anno = cls.getAnnotation(BotHandler.class);
    					
    					BotHandler adapt = (BotHandler) anno;
    					
    					id = adapt.id();
    					
    					token = adapt.token();
    					
    					
    					
    				}else if(cls.isAnnotationPresent(BotConfigurableHandler.class)){
    					BotConfigurableHandler cfg = (BotConfigurableHandler) cls.getAnnotation(BotConfigurableHandler.class);
    					
    					onConfig(cfg.path());
    					
    					id = getConfig().getInt(cfg.id());
    					
    					token = getConfig().getString(cfg.token());
    					
    				}else {
    				
    					throw new IllegalArgumentException("Main object is not annotated");
    				}
			return Optional.ofNullable(cls).orElseThrow(IllegalArgumentException::new);
    	}
    }
    
    

    public interface VkEventObject {
    	
    	
    	
    }
    
     public static interface IBotUtils {
    	 
     	static final VkApiClient client = BotClient.safe().getClient();
    	
     	static final GroupActor actor = BotClient.safe().getGroupActor();
    	
    	public static void sendMessageTo(@NotNull String text, @NotNull Integer userId) {
			try {
				client.messages().send(actor)
				.message(text.replace("%name%", "[id" + userId + "|" + getFullUserById(userId,
								(Fields[])null).getFirstName() + "]"))
				.randomId(new Random().nextInt(1000)).userId(userId)
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("[VKAPI] Message cannot be sent due to: " + e.toString());
			}
    	}
    	
		@Nullable
		public static UserFull getFullUserById(@NotNull Integer id, @Nullable Fields... fields) {
			try {
				return fields == null ?
						client.users().get(actor).userIds(String.valueOf(id)).fields(defaultFields).execute().get(0) :
						client.users().get(actor).userIds(String.valueOf(id)).fields(fields).execute().get(0);
			} catch (Throwable e) {		

				logger.info("[VKAPI] User cannot be taken due to: " + e.toString());
			}
			return null;
			
		}
    	
    	public static void sendMessageToByPeer(@NotNull String text, @NotNull Integer peerId) {
			try {
				client.messages().send(actor)
				.message(text)
				.randomId(new Random().nextInt(1000)).peerId(peerId)
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("[VKAPI] Message cannot be sent due to: " + e.toString());
			}
    	}
    	
    	static void sendMessageTo(@NotNull String text, @NotNull Integer[] userIds) {
			try {
				client.messages().send(actor)
				.message(text)
				.randomId(new Random().nextInt(1000)).userIds(userIds)
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("[VKAPI] Message cannot be sent due to: " + e.toString());
			}
    	}
    	
    	
    	static final String EMPTY_MESSAGE = "&#13;";
    	 
    	IBotUtils get(@NotNull Message message);
    	
    	void sendMessage(@NotNull String message);
    	
    	void sendTo(@NotNull String message, @NotNull Integer peerId);
    	
    	void sendToAttachment(@NotNull String message, @NotNull Integer peerId, @NotNull String attachment);
    	
    	void sendAttachmentMessage(@NotNull String message,@NotNull String attachment);
    	
    	@Deprecated
    	void sendKeyBoard(@NotNull Keyboard board);
    	
    	void sendPhoto(@NotNull String message,@NotNull File photo);
    	
    	void sendDoc(@NotNull String message, @NotNull File doc);
    	/*
    	 * В беседах где > 30 участников данный метод работает не корректно, из-за проблем на стороне вк
    	 */
    	void sendAudioFile(@NotNull String message, @NotNull File audioFile);
    	
    	void sendReplyConversationMessage(@NotNull String message, @NotNull Integer conversationMessageId);
    	
    	void sendVideoFile(@NotNull String video, @NotNull File videoFile);
    	
    	void sendReplyMessage(@NotNull String message, @NotNull Integer replyId);
    	
    	void sendForwardMessage(@NotNull String message, @NotNull Integer peerId, @NotNull Integer conversationId,@NotNull boolean isReply);
    	
    	int getReplyMessageIdFromConversation(@NotNull Integer conversationMessageId);
    	
    	int getConversationId();
    	
    	void sendSticker(@NotNull Integer stickerId);
    	
    	void sendMessageToAllDialogs(@NotNull Integer startMessageId, @NotNull Integer count, @NotNull String message);
    	
    	void sendTranslatedMessage(@NotNull String message, @NotNull Consumer<ConversationTranslate> onFail);
    	
    	void sendTranslatedMessage(@NotNull String message);
    	
    	void sendTranslatedMessage(@NotNull String message, @NotNull String translateLang);
    	
    	void pinMessage(@NotNull String messageId);
    	
    	void sendTranslatedMessage(@NotNull String message, @NotNull String translateLang, @NotNull Consumer<ConversationTranslate> onFail);
    	
    	String translatedString(@NotNull String string, @NotNull Consumer<ConversationTranslate> onFail);
    	
    	String translatedString(@NotNull String string);
    	
    	String translatedString(@NotNull String string, @NotNull String translateLang);
    	
    	String translatedString(@NotNull String string, @NotNull String translateLang, @NotNull Consumer<ConversationTranslate> onFail);
    	
    	void sendMessageWithKeyBoard(@NotNull String message, @NotNull Keyboard board);
    	
    	List<UserFull> getUsersFromConversation(@Nullable Fields... fields) throws ApiException, ClientException;
    	
    	UserFull getUserByMessage(@Nullable Fields... fields);
    	
    	UserFull getUserById(@NotNull Integer id, @Nullable Fields... fields);
    	
    	List<UserFull> getOnlineUsersFromConversation(@NotNull Fields...fields);
    	
    	UserFull getUserById(@NotNull String id, @Nullable Fields...fields);
    	
    	VkConversationEvents chatEvent();
    	
    	boolean isBotId(@NotNull Integer id);
    	
    	Integer senderId();
    	
    	
    	VkMainConversationBotFunctions functions();
    	
    	String toPayload(@NotNull Integer payload);
    	
    	int getChatId();
    	
    	int getPeerId();
    	
    	int getBotId();
    	
    	Integer currentButtonPayload(@NotNull String payload);
    	
    	String getUserPlatform(@NotNull UserFull user);
    	
    	void removeKeyboard(@NotNull String message);
    	
    	int getFromUser();
    	
    	boolean isOnline(@NotNull UserFull user);
    	
    	boolean isOnline(@NotNull Integer id);
    	
    	boolean senderIsGroup();
    	
    	boolean messageIsButton();
    	
    	boolean senderIsMember();
    	
    	boolean userIsMember(@NotNull Integer id);
    	
    	boolean objectIsGroup(@NotNull Integer id);
    	
    	boolean messageFromGroup();
    	
    	boolean isConversation();
    	
    	@Deprecated boolean isBotAdmin();
    	
    	
    	@Deprecated boolean isValidUserId(@NotNull Integer id);
    	
    	@Deprecated boolean isValidUserId(@NotNull String id);
    	
    	boolean isUserAdmin(@NotNull Integer user);
    	
    	boolean isUserInConversation(@NotNull Integer user);
    	
    	boolean isGroup(@NotNull Integer currentId);
    	
    	
    	unsafe unsafe();
    	
    	
    	@Value
    	@NotNull
    	public class VkConversationEvents {
    		
    		private final Message message;
    		
    		public void onAction(@NotNull Consumer<Integer> element, @NotNull MessageActionStatus messageAction) {
    			if(message.getAction() != null && message.getAction().getType() != null && message.getAction().getType() == messageAction) {
    				
						element.accept(message.getAction().getMemberId());
    			}
    		}
    		
    		public void onExoticMessage(@NotNull Consumer<Integer> sender, Consumer<ConversationTranslate> onFail, @NotNull String defaultLanguage) {
    			if(!Objects.equals(ConversationTranslate.textLanguageCode(message.getText(), onFail), defaultLanguage) && !message.getText().startsWith("/")) {
    				sender.accept(message.getFromId());
    			}
    		}
    		
    		public void onExoticMessage(@NotNull Consumer<Integer> sender, @NotNull String defaultLanguage) {
    			if(!Objects.equals(ConversationTranslate.textLanguageCode(message.getText()), defaultLanguage) && !message.getText().startsWith("/")) {
    				sender.accept(message.getFromId());
    			}
    		}
    		
    		public void onClick(@NotNull BiConsumer<Integer, String> clickedAndButtonId, @Nullable Integer buttonId) {
    			if(message.getPayload() == null) {
    				return;
    			}
    			   			   			
    			if(buttonId > 0) {
    			
        			if(message.getPayload().equals("\"{\\\"button\\\":\\\"" + buttonId +"\\\"}\"")) {
        				
        				clickedAndButtonId.accept(message.getFromId(), message.getPayload());
        			}
        			
    			}else {
    				clickedAndButtonId.accept(message.getFromId(), message.getPayload());
    			}
    			
    			
    		}
    		public void onClick(@NotNull BiConsumer<Integer, String> clickedAndButtonId, @Nullable KeyBoardBuilder builder) {
    			if(message.getPayload() == null) {
    				return;
    			}
    			builder.payloads.get(builder).forEach(buttons -> {
    				
    				buttons.forEach(button -> {
    					
    					if(button.getAction().getPayload().equalsIgnoreCase(message.getPayload())) {
    						clickedAndButtonId.accept(message.getFromId(), message.getPayload());
    					}
    					
    				});
    				
    			});
    			
    			
    		}
    	}
    	@Value
    	@NotNull
    	public class VkMainConversationBotFunctions {
    		
    		private final Message message;
    		
    		private final VkApiClient client;
    		
    		private final GroupActor actor;
    		
    		@Nullable @NonFinal private static VkLanguage language;
    		
    		
    		public void kickUser(@NotNull Integer userid) {
    			
    			val chatid = message.getPeerId() - 2000000000;
    			
    			try {
					client.messages().removeChatUser(actor, chatid).memberId(userid).execute();
				} catch (ApiException | ClientException e) {
					logger.warn("(" + clientName + " v"+ version +")" + " When user is kicked cause problem, please report or fix this problem. Exception code: ");
					e.printStackTrace();
				}
    			
    		}
    		public String vararg(@NotNull Integer argumentsLenght, @NotNull String[] args) {
        		val sb = new StringBuilder();
    	          for (int i = argumentsLenght; i < args.length; ++i) {
    	              sb.append(args[i]).append(" ");
    	          }
    	          return sb.toString();
    		}
    		
    		@NotNull
    		public VkTextToSpeech tts(@NotNull String languageIsoCode, @NotNull String path, @NotNull String text) {
    			return new VkTextToSpeech(languageIsoCode, path, text);
    		}
    		
    		public File speechText(@NotNull VkTextToSpeech speech, @NotNull Consumer<Exception> onFail) {

    			val google = new GoogleTextToSpeech(speech.getLanguageIsoCode());
    			
    			try {
					val is = google.getMP3Data(speech.getText());
					
					return copyInputStreamToFile(is, new File(speech.getPath()), onFail);
				} catch (Exception e) {
					onFail.accept(e);
				}
				return null;
    		}
    		
    		@ParametersAreNonnullByDefault
    	    private File copyInputStreamToFile(InputStream inputStream, File file, Consumer<Exception> onFail) throws Exception {
    			new Thread(() -> {
        	        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
        	            int read;
        	            byte[] bytes = new byte[8192];
        	            while ((read = inputStream.read(bytes)) != -1) {
        	                outputStream.write(bytes, 0, read);
        	            }
        	        } catch (FileNotFoundException e) {
        	        	onFail.accept(e);
    				} catch (IOException e) {
    					onFail.accept(e);
    				}
    			}).run();

				return file;

    	    }
    		@Value
    		private static class VkTextToSpeech {
    			@NotNull String languageIsoCode;
    			@NotNull String path;
    			@NotNull String text;
    		}
    		public KeyBoardBuilder keyboardBuilder() {
    			return new KeyBoardBuilder();
    		}
    		
    		public VkCustomButton buttonBuilder() {
    			return new VkCustomButton();
    		}
    		
    		public VkLanguage vklanguage() {
    			return new VkLanguage();
    		}
    		@Deprecated
    		public Language languageSelection(@NotNull Integer langId) { return Language.lang(langId); }
    		@Deprecated
    		public VkLanguage registerLanguage(@NotNull Language lang, @NotNull Integer id) {
				return language = vklanguage().registerLocale(lang, id);
    			
    		}
    		@Deprecated
    		public boolean isLocalizeInitialized(@NotNull Integer id) {
    			if(language == null) return false;
    			return language.getLanguageLocalizator().containsColumn(id);
    		}
    		
    		public VkLanguage getLanguage() {
    			return language;
    		}
    		
    		public BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate.AutomaticlyTranslation language() {
    			return new BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate.AutomaticlyTranslation();
    		}
    		
    		public ConversationTranslate conversationLanguage() {
    			return new ConversationTranslate();
    		}
    		
    		@NotNull
    		public List<UserFull> onlineUsers(@Nullable Fields...field_args){
    			
    			try {
					return client.messages().getConversationMembers(actor, message.getPeerId()).fields(field_args == null ? defaultFields : field_args)
							.execute().getProfiles().stream().filter(map -> map.isOnline()).collect(Collectors.toList());
				} catch (ApiException | ClientException e) {
					e.printStackTrace();
				}
				return new ArrayList<>();	
    		}
    		
    		public boolean isText(String text) {
    			
    			try {
    			Integer.parseInt(text);
    			
    			}catch(Exception ex) {
    				
    				return true;
    			}
    			
    			return false;
    		}
    		public String useUrl(@NotNull String url) {
    				if(!url.contains("vk.com")) {
    					url.replace("id", "");
    				}
    		    	return (url.contains("https") || url.contains("http") || url.contains("vk.com")) ? url.substring(url.indexOf("vk.com") + 7).replace("id", "") : url;
    		    	
    		}
    		
    			String extractId(String s) {
    			if(!s.contains("id") && !isText(s)) {
    				return s;
    			}else if(!s.contains("id") && isText(s)) {
    				return s;
    			}
    		    String[] n = s.split(""); 
    		    StringBuffer f = new StringBuffer();
    		    for (int i = 3; i < n.length; i++) {
    		        if((n[i].matches("[0-9]+"))) {
    		            f.append(n[i]);
    		        }else {
    		            
    		            return f.toString(); 
    		        }   
    		    }
    		    return "";
    		 }
    		
    		public void editChatName(@NotNull String name) {
    			
    			val chatid = message.getPeerId() - 2000000000;
    			
    			client.messages().editChat(actor, chatid, name);
    			
    		}
    		@Deprecated
    		private interface Localizable {
    			
    			@NotNull VkLanguage.Language get();
    			
    		}
    		
    		@Data
    		@NoArgsConstructor
			public
    		static final class ConversationTranslate {
    			
    			@Nullable String sourceLanguage;
    			@Nullable String targetLanguage;
    			@Nullable String text;
    			
    			public ConversationTranslate sourceLanguage(@Nullable String sourceLanguage) {
    				
    				this.sourceLanguage = sourceLanguage;
    				
    				return this;
    			}
    			
    			public ConversationTranslate targetLanguage(@Nullable String targetLanguage) {
    				
    				this.targetLanguage = targetLanguage;
    				
    				return this;
    			}
    			
    			public ConversationTranslate text(@Nullable String text) {
    				
    				this.text = text;
    				
    				return this;
    			}
    			public static String textLanguageCode(@NotNull String text,Consumer<ConversationTranslate> onFail) {
    				try {
						return GoogleTranslate.detectLanguage(text);
					} catch (IOException e) {
						
						onFail.accept(new ConversationTranslate());
						
					}
					return text;
    			}
    			public String textLanguageCode( Consumer<ConversationTranslate> onFail) {
    				try {
						return GoogleTranslate.detectLanguage(this.text);
					} catch (IOException e) {
						
						onFail.accept(this);
						
					}
					return text;
    			}
    			public String textLanguageCode() {
    				try {
						return GoogleTranslate.detectLanguage(this.text);
					} catch (IOException e) {
						
						e.printStackTrace();
						
					}
					return text;
    			}
    			public static String textLanguageCode(@NotNull String text) {
    				try {
						return GoogleTranslate.detectLanguage(text);
					} catch (IOException e) {
						
						e.printStackTrace();
						
					}
					return text;
    			}
    			public String transtale(Consumer<ConversationTranslate> onFail) {
    				try {
						return GoogleTranslate.translate(sourceLanguage, targetLanguage, text);
					} catch (IOException e) {
						
						onFail.accept(this);
					}
					return sourceLanguage;
    			}
    			
    			public String transtale() {
    				try {
						return GoogleTranslate.translate(sourceLanguage, targetLanguage, text);
					} catch (IOException e) {
						
					}
					return sourceLanguage;
    			}
    			public static AutomaticlyTranslation translationElement() {
    				return new AutomaticlyTranslation();
    			}
    			
    			public static void systemLocale(@NotNull String locale) {
    				AutomaticlyTranslation.systemLocale(locale);
    			}
    			
    			public static String getLocaleCode(@NotNull String languagecode) {
    				return GoogleTranslate.getDisplayLanguage(languagecode);
    			}
    			
    			
    			@NoArgsConstructor
				public
    			static class AutomaticlyTranslation implements Serializable{
    				
					/**
					 * 
					 */
					private static final long serialVersionUID = -6114130601466723827L;


					private static final BiMap<Integer,  LanguageEditor> userHash = HashBiMap.create();
    				
    				private static String defaultLanguage = "en";
    				
    				public static AutomaticlyTranslation registerReference(@NotNull LanguageEditor...editor) {
    					for (LanguageEditor editors : editor) {
    						
    						userHash.put(editors.getUserId(), editors);
    						
    					}
						return new AutomaticlyTranslation();
    					
    				}
    				
    				public void initLanguages(@NotNull LanguageEditor...editor) {};
    				
    				public static AutomaticlyTranslation registerReference(@NotNull LanguageEditor editor) {
    				
    						
    						userHash.put(editor.getUserId(), editor);
    						
						return new AutomaticlyTranslation();
    					
    				}
    				
    				public static void systemLocale(@NotNull String locale) {
    					
    					Preconditions.checkNotNull(locale, "(" + clientName + " v"+ version +")" + " Locale type cannot be null");
    					
    					defaultLanguage = locale;
    					logger.info("(" + clientName + " v"+ version +")" + " Successful locale setup.");
    				}
    				
    				public static String languageMessage(@NotNull String message, @NotNull Integer id, @NotNull Consumer<ConversationTranslate> onFail) {
    					val editor = userHash.getOrDefault(id, new LanguageEditor() {
							
    						
							@Override
							public Integer getUserId() {
								
								return id;
							}
							
							@Override
							public Integer getLanguageId() {
								
								return 1;
							}
							
							@Override
							public String getLanguageCode() {
								
								return defaultLanguage;
							}

							@Override
							public LanguageEditor complete() {
								
								return null;
							}

							@Override
							public void setUserId(Integer id) {}


						});
    					editor.installCodeId();
    					
						return new ConversationTranslate().sourceLanguage("auto").targetLanguage(editor.getCurrentLanguageCode()).text(message).transtale(onFail);
    					
    				}
    				
    				public static String languageMessage(@NotNull String message, @NotNull Integer id) {
    					val editor = userHash.getOrDefault(id, new LanguageEditor() {
							
							@Override
							public Integer getUserId() {
								
								return id;
							}
							
							@Override
							public Integer getLanguageId() {
								
								return 1;
							}
							
							@Override
							public String getLanguageCode() {
								
								return defaultLanguage;
							}

							@Override
							public LanguageEditor complete() {
								
								return null;
							}

							@Override
							public void setUserId(Integer id) {}


						});
    					editor.installCodeId();
    					
						return new ConversationTranslate().sourceLanguage("auto").targetLanguage(editor.getCurrentLanguageCode()).text(message).transtale();
    					
    				}
    				
    				public LanguageEditor getUserHash(@NotNull Integer id) {
    					return userHash.get(id);
    				}
    				public boolean currentUserIsRegistered(@NotNull Integer user) {
    					return userHash.containsKey(user);
    				}
    				public LanguageEditor getEditorById(@NotNull Integer id) {
    					return VkIdeficatedLanguage.getEditorById(id);
    				}
    				
    				public void userLanguage(@NotNull Integer languageId, @NotNull Integer userId) {
    					val editor = this.getEditorById(languageId);
    					
    					editor.setUserId(userId);
    					
    					registerReference(editor);
    					
    					
    				}
    				
    				public VkIdeficatedLanguage getIndefier() {
    					return new VkIdeficatedLanguage();
    				}
    				
    			}
    			
    			@NoArgsConstructor
				protected final static class VkIdeficatedLanguage {

					private static final Map<Integer, LanguageEditor> languagesById = new HashMap<Integer, LanguageEditor>();
					
					
					
					public static void setupEditor(@NotNull LanguageEditor editor) {
						
						languagesById.put(editor.getLanguageId(), editor);
						
					}
					
					public static LanguageEditor setupSimpleEditor(@NotNull LanguageEditor editor) {
						
						return languagesById.put(editor.getLanguageId(), editor);
						
					}
					
					public static LanguageEditor getEditorById(@NotNull Integer id) {
						return languagesById.get(id);
						
					}
					
					public static Map<Integer, LanguageEditor> getLanguageClasses() {
						return languagesById;
					}
					

				}
    			public static interface LanguageEditor {
    				
    				static final Map<Integer, String> getLanguageCodeById = Maps.newHashMap();
    				
    				default void installCodeId() {
    					getLanguageCodeById.put(getLanguageId(), getLanguageCode());
    				}
    				
    				default Map<Integer, String> getLanguageMap() {
    					return getLanguageCodeById;
    				}
    				
    				default String getCurrentLanguageCode() {
    					return getLanguageCodeById.get(getLanguageId());
    				}
    				
    				default LanguageEditor completeEditor() {
    					return VkIdeficatedLanguage.setupSimpleEditor(this);
    				}
    				
    				LanguageEditor complete();
    				
    				Integer getLanguageId();
    				
    				Integer getUserId();
    				
    				void setUserId(Integer id);
    				
    				/*
    				 * 
    				 * iso-639-1 language code
    				 * 
    				 */
    				String getLanguageCode();
    				
    				
    				
    			}
    		}
    		
    		@Deprecated
		    @NoArgsConstructor(access = AccessLevel.PRIVATE)
		    @Getter
    		public static class VkLanguage implements Localizable {
		    
		    	
		    @NotNull Language lang;
		    
		    @NotNull String locale;
		    
		    @NotNull Integer id;
		    
		    
		    
		    private final Table<Language,Integer,String> language = TreeBasedTable.create();
		    
		    
		    
		    public VkLanguage(@NotNull final Language lang, @NotNull Integer id) {
		    	this.lang = lang;
		    	this.id = id;
		    }
		    
		    
		    public VkLanguage registerLocale(@NotNull Language lang, @NotNull Integer id) {
		    	return new VkLanguage(lang, id);
		    }
		    
		    public void changeLang(@NotNull Language lang) {
		    	this.lang = lang;
		    }
		    
		    public void changeLang(@NotNull Integer id) {
		    	this.lang = Language.lang(id);
		    }
		     
		    /*
		     * Locale all string elements from Languages
		     * 1. English
		     * 2. Russian
		     * 3. Ukranian
		     * 4. French
		     * 
		     */
		    public VkLanguageResultSet localeStringAllLanguages(String...message) {
		    	if(message.length > Language.values().length || message.length < Language.values().length) {
		    		throw new RuntimeException("[VKAPI] Args less or more than languages.");
		    	}
		    	//Main.getLogger().info("LANGID: " + Language.lang((this.lang.getId())) + " ID: " + id);
		    	language.put(lang,
		    			id, 
		    			message[this.lang.getId()-1]);
		    	
				return new VkLanguageResultSet(this,lang, id, message); 
		    	
		    }
		    
		    
		    public Table<Language,Integer,String> getLanguageLocalizator() {
		    	return language;
		    }
		    
		    @AllArgsConstructor
		    
		    public static class VkLanguageResultSet implements Localizable{
		    	
		    	private VkLanguage vkLanguage;
		    	
		    	private Language currentLanguage;
		    	
		    	private Integer id;
		    	
		    	private String[] message;
		    	
		    	public String outStringByLocale() {
		    		if(currentLanguage == null || id == null) {
		    			this.currentLanguage = Language.ENGLISH;
		    			return this.message[0];
		    		}
		    		return vkLanguage.getLanguageLocalizator().get(currentLanguage, id).replace("%lang%", currentLanguage.getName());
		    	}
		    	
		    	public String outStringByLanguage(@NotNull Language lang) {
					return vkLanguage.getLanguageLocalizator().get(lang, id).replace("%lang%", lang.getName());
		    		
		    	}

				@Override
				public @NotNull VkLanguage.Language get() {
					
					return this.currentLanguage;
				}
		    }
		    @Deprecated
		    public static enum Language implements Localizable{
		    		
		    		ENGLISH(1, "English"),
		    		RUSSIAN(2, "Русский"),
		    		UKRAINIAN(3, "Український"),
		    		FRENCH(4, "Français");
		    		
		    		private Integer id;
		    		
		    		private String name;
		    		
		    		private static final Map<Integer,Language> plat = Maps.newHashMap();
		    		
		    		
		    	    static {
		    	    	for(Language pt : Language.values()) {
		    	    		plat.put(pt.getId(), pt);
		    	    	}
		    	    }
		    	    @Contract(pure = true)
		    	    public static Language lang(@NotNull Integer langId) {
		    	    	return plat.get(langId);
		    	    }
		    		
		    		private Language(@NotNull Integer id, @NotNull String name) {
		    			this.id = id;
		    			this.name = name;
		    		}
		    		
		    		public String getName() {
		    			return this.name;
		    		}
		    		
		    		public Integer getId() {
		    			return this.id;
		    		}

					@Override
					public @NotNull VkLanguage.Language get() {
						
						return this;
					}
		    		
		    	}

				@Override
				public @NotNull VkLanguage.Language get() {
					return this.lang;
				}
		    	
		    	
		    	
    		}
    		
		    
		    @NoArgsConstructor(access = AccessLevel.PRIVATE)
		    @ToString
		    @EqualsAndHashCode
		    @Getter
    		public static class KeyBoardBuilder implements Buildable<Keyboard>{
    			
    		    private Keyboard keyboard;
    		    
    		    private List<List<KeyboardButton>> buttons_y = new ArrayList<>();
    		    
    		    private List<KeyboardButton> buttons_x = new ArrayList<>();
    		    
    		    private boolean inline;
    		    
    		    private Integer authorId;
    		    
    		    private boolean onetime;
    		    
    		    
    		    public static LinkedHashMap<KeyBoardBuilder,List<List<KeyboardButton>>> payloads = Maps.newLinkedHashMap();
    		    
    		    public static LinkedHashMap<KeyBoardBuilder, List<List<KeyboardButton>>> buttonsPayload(){
    		    	return payloads;
    		    }
    		    
    		    public KeyBoardBuilder inline(@NotNull boolean inline) {
					this.inline = inline;
					
					return this;
    		    }
    		    
    		    public KeyBoardBuilder authorid(@NotNull Integer authorId) {
					this.authorId = authorId;
					return this;
    		    }
    		    
    		    public KeyBoardBuilder onetime(@NotNull boolean onetime) {
					this.onetime = onetime;
					return this;
    		    }
    		    
    		    public static VkCustomButton getButtonBuilder() {
    		    	return new VkCustomButton();
    		    }
    		    
    		    public KeyBoardBuilder addButtonX(@NotNull KeyboardButton button_y) {
    		    	
    		    	System.out.println("[KEYBoard] Added Button_X!");
    		    	
    		    	buttons_x.add(button_y);
    		    	payloads.put(this, Arrays.asList(Arrays.asList(button_y)));
    		    	return this;
    		    }
    		    public KeyBoardBuilder singleComplete() {
    		    	
    		    	addButtonY(buttons_x);
    		    	
    		    	return this;
    		    }
    		    public KeyBoardBuilder completeRow(@NotNull KeyboardButton... button_y) {
    		    	addButtonY(buttons_x);
    		    	addButtonY(Arrays.asList(button_y));
    		    	
    		    	return this;
    		    }
    		    
    		    
    		    public KeyBoardBuilder addButtonY(@NotNull List<KeyboardButton> button_x) {
    		    	
    		    	if(buttons_y.contains(button_x)) {
    		    		payloads.put(this, buttons_y);
    		    		return this;
    		    	}
    		    	
    		    	buttons_y.add(button_x);
    		    	payloads.put(this, buttons_y);
    		    	return this;
    		    }
    		    
    		    
    		    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    		    @ToString
    		    @EqualsAndHashCode  
    		    @Getter
    		    public static class VkCustomButton implements Buildable<KeyboardButton>{
    		    	
    		    	KeyboardButton button;
    		    	
    		    	String payload;
    		    	
    		    	String buttonTitle;
    		    	
    		    	String hash;
    		    	
    		    	Integer appId;
    		    	
    		    	Integer ownerId;
    		    	
    		    	KeyboardButtonActionType type;
    		    	
    		    	KeyboardButtonColor color;
    		    	
    		    	
    		    	
    		    	public VkCustomButton payload(@NotNull Integer simpleId) {
    		    		
    		    		this.payload = "\"{\\\"button\\\":\\\"" + simpleId.toString() +"\\\"}\"";
    		    	
    		    		return this;
    		    		
    		    	}
    		    	
    		    	public VkCustomButton customPayload(@NotNull String simpleId) {
    		    		
    		    		this.payload = "\"{\\\"button\\\":\\\"" + simpleId +"\\\"}\"";
    		    		
    		    		return this;
    		    		
    		    	}
    		    	
    		    	public VkCustomButton payload(@NotNull String complexId) {
    		    		
    		    		this.payload = complexId;
    		    		
    		    		return this;
    		    	}
    		    	
    		    	public VkCustomButton title(@NotNull String title) {
    		    		
    		    		this.buttonTitle = title;
    		    		
    		    		return this;
    		    	}
    		    	
    		    	
    		    	public VkCustomButton hash(@NotNull String hash) {
    		    		
    		    		this.hash = hash;
    		    		
    		    		return this;
    		    	}
    		    	
    		    	
    		    	public VkCustomButton type(@NotNull KeyboardButtonActionType type) {
    		    		
    		    		this.type = type;
    		    		
    		    		return this;
    		    	}
    		    	
    		    	public VkCustomButton appId(@NotNull Integer appId) {
    		    		
    		    		this.appId = appId;
    		    		
    		    		return this;
    		    	}
    		    	
    		    	public VkCustomButton ownerId(@NotNull Integer ownerId) {
    		    		
    		    		this.ownerId = ownerId;
    		    		
    		    		return this;
    		    	}
    		    	
    		    	public VkCustomButton color(@NotNull ColorButton color) {
    		    		
    		    		this.color = color.currentColor();
    		    		
    		    		return this;
    		    	}
    		    	
					@Override
					public KeyboardButton build() {
						
						return this.button = new KeyboardButton().setAction(new KeyboardButtonAction()
								.setPayload(payload)
								.setHash(hash)
								.setAppId(appId)
								.setOwnerId(ownerId)
								.setLabel(buttonTitle)
								.setType(type))
								.setColor(color);
					}
					
    		    	
    		    	
    		    }
    		    
    		    public enum ColorButton {
    		    	
    		    	GREEN(KeyboardButtonColor.POSITIVE),
    		    	
    		    	RED(KeyboardButtonColor.NEGATIVE),
    		    	
    		    	WHITE(KeyboardButtonColor.DEFAULT),
    		    	
    		    	BLUE(KeyboardButtonColor.PRIMARY);
    		    	
    		    	private KeyboardButtonColor color;
    		    	
    		    	private ColorButton(@NotNull KeyboardButtonColor color) {
    		    		this.color = color;
    		    		
    		    	}
    		    	
    		    	public KeyboardButtonColor currentColor() {
    		    		return this.color;
    		    	}
    		    	
    		    	
    		    }
    		    
    		    @Override
    		    public Keyboard build() {
					return keyboard = new Keyboard().setAuthorId(authorId).setOneTime(this.onetime).setButtons(buttons_y).setInline(inline);
    		    	
    		    }
    		    
    			
    		}
    		
    		
    	}
    	
    	public static final class unsafe {
    		
    		private final String FOAF = "foaf.vk.com/foaf.php?id=";
    		
    		public int idFromMention(String mention) {

    		    String[] n = mention.split(""); 
    		    StringBuffer f = new StringBuffer(); 

    		    for (int i = 3; i < n.length; i++) {
    		        if((n[i].matches("[0-9]+"))) {
    		            f.append(n[i]); 
    		        }else {
    		            
    		            return Integer.parseInt(f.toString()); 
    		        }   
    		    }
    		    return 0;
    		 }
    	   
    	public String foafSource(Integer id) throws IOException
    	    {
    	        URL urlObject = new URL(FOAF + "" + id);
    	        URLConnection urlConnection = urlObject.openConnection();
    	        urlConnection.setRequestProperty("User-Agent",
    	        		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

    	        return toString(urlConnection.getInputStream());
    	    }

    	    public String toString(InputStream inputStream) throws IOException
    	    {
    	        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")))
    	        {
    	            String inputLine;
    	            StringBuilder stringBuilder = new StringBuilder();
    	            while ((inputLine = bufferedReader.readLine()) != null)
    	            {
    	                stringBuilder.append(inputLine);
    	            }

    	            return stringBuilder.toString();
    	        }
    	    }
    	    @Deprecated
    	    public final VkUserCache unsafeUserCache() {
				return new VkUserCache();
    	    	
    	    }
    	    @Deprecated
    	    @Data
    	    @FieldDefaults(level = AccessLevel.PRIVATE)
    	    static class VkCaptcha {
    	    	private static final BiMap<Integer, Integer> captchaCache = HashBiMap.create();
    	    	
    	    	public void setupAttempts() {
    	    		
    	    		captchaCache.put(userId, attempts);
    	    	
    	    	}
    	    	
    	    	@NotNull private Integer attempts;
    	    	
    	    	@NotNull private Integer userId;
    	    	
    	    	public Integer attempts() {
    	    		return this.attempts;
    	    	}
    	    	
    	    	public Integer decrementsAttempts(@NotNull Integer user) {
    	    		return captchaCache.forcePut(user, captchaCache.get(user)-1);
    	    	}
    	    	
    	    	public Integer userId() {
    	    		return this.userId;
    	    	}
    	    	
    	    	public VkCaptchaGenerator generator() {
    	    		return new VkCaptchaGenerator(this.userId);
    	    	}
    	 
    	    	
    	    	@Getter
    	    	@NoArgsConstructor
    	    	@EqualsAndHashCode
    	    	@ToString
    	    	protected static final class VkCaptchaGenerator {
    	    		private static final Integer CAPTCHA_SIZE_X = 150;
    	    		
    	    		private static final Integer CAPTCHA_SIZE_Y = 150;
    	    		
    	    		private static final Color DEFAULT_CAPTHA_BACKGROUND = Color.BLACK;
    	    		
    	    		private static final VkCaptchaNoise DEFAULT_CAPTHA_NOISE = VkCaptchaNoise.COMMON_NOISE;
    	    		
    	    		private static final VkCaptchaForm DEFAULT_CAPTHA_FORM = VkCaptchaForm.SHEAR;
    	    		
    	    		private static final Integer DEFAULT_LENGHT = 5;
    	    		
    	    		private Integer captcha_lenght;
    	    		
    	    		private Integer size_x;
    	    		
    	    		private Integer size_y;
    	    		
    	    		private Color background;
    	    		
    	    		private VkCaptchaNoise noise;
    	    		
    	    		private VkCaptchaForm form;
    	    		
    	    		private static final Map<Integer, String> answer = Maps.newConcurrentMap();
    	    		
    	    		private Integer capchaHolder;
    	    		
    	    		public VkCaptchaGenerator size(@NotNull Integer x, @NotNull Integer y) {
    	    			
    	    			this.size_x = x;
    	    			
    	    			this.size_y = y;
    	    			
    	    		return this;
    	    	
    	    		}
    	    		
    	    		
    	    		public VkCaptchaGenerator(@NotNull Integer holder) {
    	    			this.capchaHolder = holder;
    	    		}
    	    		
    	    		public VkCaptchaGenerator lenght(@NotNull Integer lenght) {
    	    			this.captcha_lenght = lenght;
    	    			return this;
    	    		}
    	    		
    	    		public VkCaptchaGenerator background(@NotNull Color background) {
    	    			this.background = background;
    	    			return this;
    	    		}
    	    		
    	    		public VkCaptchaGenerator noise(@NotNull VkCaptchaNoise noise) {
    	    			this.noise = noise;
    	    			return this;
    	    		}
    	    		public VkCaptchaGenerator form(@NotNull VkCaptchaForm form) {
    	    			this.form = form;
    	    			return this;
    	    		}
    	    		@SafeVarargs
    	    		public final <T,R> R ifNullReturnThen(@NotNull Function<? super T[],? extends R> then, @NotNull R elseIf,
    	    				@Nullable Predicate<T[]> value, @NotNull T... objects) {
    	    			
    	    			if(value != null) {
    	    				return value.test(objects) && Arrays.stream(objects).allMatch(Objects::nonNull) ? then.apply(objects) : elseIf;
    	    			}
    	    			return Arrays.stream(objects).allMatch(Objects::nonNull) ?  then.apply(objects) : elseIf;
    	    		}
    	    		
    	    		@SafeVarargs
    	    		public final <T> void safeVoidResponse(@NotNull Function<? super T[], Runnable> then, 
    	    				@NotNull Runnable elseIf, @Nullable Predicate<T[]> value, @NotNull T... objects) {
    	    			if(value != null) {
    	    				if(value.test(objects) && Arrays.stream(objects).allMatch(Objects::nonNull)) {
    	    					then.apply(objects).run();
    	    				}else {
    	    					elseIf.run();
    	    				}
    	    			}
    	    		}
    	    		
    	    		public File build(@NotNull String path, @NotNull String fileName) {
    	    			BasicCaptcha captcha = ifNullReturnThen(obj -> new BasicCaptcha(obj[0], obj[1]),
    	    					new BasicCaptcha(CAPTCHA_SIZE_X, CAPTCHA_SIZE_Y), null,
    	    					this.size_x, this.size_y);
    	    			
    	    			safeVoidResponse(objects ->
    	    			() -> captcha.text(objects[0]), () -> captcha.text(DEFAULT_LENGHT), objects -> objects[0] == 0, this.captcha_lenght);

    	    			safeVoidResponse(objects -> 
    	    			() -> captcha.background(background), () -> captcha.background(DEFAULT_CAPTHA_BACKGROUND), null, this.background);
    	    			
    	    			safeVoidResponse(objects -> () -> {
    	    				if(objects[0] == VkCaptchaNoise.NOISE_STRAIGHT_LINE) {
    	    					captcha.noiseStraightLine();
    	    				}
    	    				if(objects[0] == VkCaptchaNoise.NOISE_CURVED_LINE) {
    	    					captcha.noiseCurvedLine();
    	    				}
    	    				if(objects[0] == VkCaptchaNoise.COMMON_NOISE) {
    	    					captcha.noiseStrokes();
    	    				}
    	    				
    	    			}, () -> captcha.noiseStrokes(), null, this.noise);
    	    			
    	    			safeVoidResponse(objects -> () -> {
    	    				if(objects[0] == VkCaptchaForm.STRETCH) {captcha.distortionStretch();}
    	    				
    	    				if(objects[0] == VkCaptchaForm.SHEAR) {captcha.distortionShear();}
    	    				
    	    				if(objects[0] == VkCaptchaForm.SHEAR2) {captcha.distortionShear2();}
    	    				
    	    				if(objects[0] == VkCaptchaForm.FISH_EYE) {captcha.distortionFishEye();}
    	    				
    	    				if(objects[0] == VkCaptchaForm.ELECTRIC) {captcha.distortionElectric();}
    	    				
    	    				if(objects[0] == VkCaptchaForm.ELECTRIC2) {captcha.distortionElectric2();}
    	    				
    	    				if(objects[0] == VkCaptchaForm.ELASTIC) {captcha.distortionElastic();}
    	    				
    	    			}, () -> captcha.distortionShear(), null, this.form);
    	    			
    	    			try {
    	    				
    	    				answer.put(this.capchaHolder, captcha.getText());
    	    				
							captcha.save(path + fileName + ".png");
							
						} catch (IOException e) {
							
							e.printStackTrace();
						}
    	    			
    	    			return new File(path + fileName + ".png");
    	    		}
    	    		
    	    		
    	    		public static enum VkCaptchaNoise {
    	    			COMMON_NOISE,
    	    			NOISE_STRAIGHT_LINE,
    	    			NOISE_CURVED_LINE
    	    		}
    	    		
    	    		public static enum VkCaptchaForm {
    	    			FISH_EYE,
    	    			STRETCH,
    	    			SHEAR,
    	    			SHEAR2,
    	    			ELASTIC,
    	    			ELECTRIC,
    	    			ELECTRIC2
    	    		}
    	    		
    	    	}	
    	    	
    	    }
    	    @Data
    	    @Deprecated
			public
			static class VkUserCache {
    	    	
    	    	private static final LinkedList<Integer> users = Lists.newLinkedList();
    	    	
    	    	public boolean cacheUser(@NotNull Integer user) {
    	    		return isUserCached(user) ? false : users.add(user);
    	    	}
    	    	
    	    	public boolean isUserCached(@NotNull Integer user) {
    	    		return users.contains(user);
    	    	}
    	    }
    	    
    	    @Data
    	    @Deprecated
    	    static class VkUserBlock {
    	    	private static final Map<Integer, TimeReference<Long>> blockMap = Maps.newConcurrentMap();
    	    	
    	    	public TimeReference<Long> blockUser(@NotNull Integer user) {
    	    		return timeBlockUser(user, 0L);
    	    	}
    	    	
    	    	public TimeReference<Long> timeBlockUser(@NotNull Integer user, Long time) {
    	    		return blockMap.put(user, new TimeReference<Long>(user, time));
    	    	}
    	    	
    	    	public boolean isBlocked(@NotNull Integer user) {
    	    		return blockMap.containsKey(user);
    	    	}
    	    	
    	    	public String leftTimeFormat(@NotNull Integer user) {
    	    		return TimeUtils.getTimeUnit(blockMap.get(user).getLeftTime());
    	    		
    	    	}
    	    	public boolean isLifetimeBlock(@NotNull Integer user) {
    	    		return blockMap.get(user).getTime() == 0L;
    	    	}
    	    	
    	    	public Set<Integer> getBlockedUsers() {
    	    		
    	    		return blockMap.keySet();
    	    	}
    	    	@NotNull
    	    	@ParametersAreNonnullByDefault
    	    	public boolean checkBlock(final Integer user) {
    	    		if(isBlocked(user)) {
    	    			if(isLifetimeBlock(user)) {return false;}
    	    			if(blockMap.get(user).isLeft()) {blockMap.remove(user); return true;};
    	    		}
					return false;
    	    	}
    	    	
    	    	@Data
    	    	@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    	    	private class TimeReference<T extends Number> {
    	    		@NotNull Integer user;
    	    		
    	    		@NotNull T time;
    	    		
    	    		@Nullable long startTime = System.currentTimeMillis();
    	    		
    	    	    public long getLeftTime() {
    	    	        return (Long)this.time - (System.currentTimeMillis() - this.startTime) / 1000L;
    	    	    }
    	    	    
    	    	    public boolean isLeft() {
    	    	        return this.getLeftTime() <= 0L;
    	    	    }
    	    		
    	    	}
    	    }
    		
    	}
    }
    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BotUtilsImpl implements IBotUtils {

    	
    	private Message message;
    	
    	
    	
		@Override
		public IBotUtils get(@NotNull Message message) {
			
			this.message = message;
			
			return this;
		}

		@Override
		public void sendMessage(@NotNull String message) {
			
	    	try {
				client.messages().send(actor)
				.message(message.replace("%name%", "[id" + this.message.getFromId() + "|" + this.getUserById(this.message.getFromId(),
								(Fields[])null).getFirstName() + "]"))
				.randomId(new Random().nextInt(100000))
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
			}
			
		}
		
		@Override
		public void sendMessageWithKeyBoard(@NotNull String message, @NotNull Keyboard board) {
			
			try {
				client.messages().send(actor)
				.message(message.replace("%name%", "[id" + this.message.getFromId() + "|" + this.getUserById(this.message.getFromId(),
						(Fields[])null).getFirstName() + "]")).keyboard(board)
				.randomId(new Random().nextInt(100000))
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
			}
			
		}
		
		@Override
		public VkConversationEvents chatEvent() {
			return new VkConversationEvents(message);
		}
		@Override
		public boolean senderIsGroup() {
			return getFromUser() < 0;
		}
		
		@Override
		public int getFromUser() {
			return message.getFromId();
		}
		
		

		@Override
		public void sendAttachmentMessage(@NotNull String message, @NotNull String attachment) {
			try {
				client.messages().send(actor)
				.message(message).attachment(attachment)
				.randomId(new Random().nextInt(100000))
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
				
			}
		}

		@Override
		public void sendKeyBoard(@NotNull Keyboard board) {
			try {
				
				client.photos().saveMessagesPhoto(actor, "");
				client.messages().send(actor).keyboard(board)
				.randomId(new Random().nextInt(100000))
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
				
			}
			
		}


		@Override
		public boolean isConversation() {
			
			return message.getPeerId() > 2000000000;
		}

		/*
		 * Костыль
		 * 
		 */
		@Deprecated
		@Override
		public boolean isBotAdmin() {
				getUsersFromConversation(null,null);
				return true;
			
		}

		@Override
		@Contract(pure = true)
		public boolean isUserAdmin(@NotNull Integer user) {
			
			try {
				return client.messages().getConversationMembers(actor, this.message.getPeerId()).execute().getItems().stream()
						.filter(conversationUser -> conversationUser.getIsAdmin() != null && conversationUser.getIsAdmin())
						.map(fn -> Math.abs(fn.getMemberId())).collect(Collectors.toList()).contains(user);
			} catch (ApiException | ClientException e) {
				
				return false;
				
			}
			
		}

		@Override
		
		public boolean isUserInConversation(@NotNull Integer user) {
			
			try {
				return client.messages().getConversationMembers(actor, this.message.getPeerId()).execute().getItems().stream()
						.map(conversationMember -> conversationMember.getMemberId())
						.collect(Collectors.toList()).contains(user);
			} catch (ApiException | ClientException e) {
				return false;
			}
			
		}

		@Override
		public unsafe unsafe() {
			
			return new unsafe();
		}

		@Override
		public int getChatId() {
			
			return message.getPeerId() - 2000000000;
		}

		@Override
		public int getPeerId() {
			
			return message.getPeerId();
		}
		
		@Nullable
		@Override
		public UserFull getUserByMessage(@Nullable Fields... fields) {
			
			try {
				return fields == null ?
						client.users().get(actor).userIds(String.valueOf(message.getFromId())).fields(defaultFields).execute().get(0) :
						client.users().get(actor).userIds(String.valueOf(message.getFromId())).fields(fields).execute().get(0);
			} catch (ApiException | ClientException e) {
				
				logger.info("[VKAPI] User cannot be taken due to: " + e.toString());
				
			}
			return null;
		}
		@Nullable
		@Override
		public UserFull getUserById(@NotNull Integer id, @Nullable Fields... fields) {
			try {
				return fields == null ?
						client.users().get(actor).userIds(String.valueOf(id)).fields(defaultFields).execute().get(0) :
						client.users().get(actor).userIds(String.valueOf(id)).fields(fields).execute().get(0);
			} catch (Throwable e) {		

				logger.info("[VKAPI] User cannot be taken due to: " + e.toString());
			}
			return null;
			
		}
		@Override
		public List<UserFull> getUsersFromConversation(@Nullable Fields... fields) {
				
				try {
					return fields == null ? client.messages().getConversationMembers(actor, message.getPeerId()).fields(defaultFields).execute().getProfiles() :
						client.messages().getConversationMembers(actor, message.getPeerId()).fields().execute().getProfiles();
				} catch (ApiException | ClientException clientException) {
					
				}
				return null;

		}

		@Override
		public boolean messageFromGroup() {
			
			return message.getFromId() < 0;
		}
		
		@Getter
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		
		public enum Platform {
			MOBILE(1,"(Mobile) 📱"),
			IPHONE(2, "(IPHONE) 🍏"),
			IPAD(3, "(IPAD) 🍎"),
			ANDROID(4, "(ANDROID) 📱"),
			WINDOWS_PHONE(5, "(WINDOWS_PHONE) 📘"),
			WINDOWS_10(6, "(WINDOWS 10) 🖥"),
			VKAPI(7, "(VK-API/DESKTOP) ⚙")
			;
			@Getter
			 final Integer number;
			 @Getter
			 final String description;
			 
			private static final Map<Integer,Platform> plat = Maps.newHashMap();
			
			
		    static {
		    	
		    	Stream.of(Platform.values()).forEach(pt -> plat.put(pt.getNumber(), pt));
		    	
		    }
		    @Contract(pure = true)
		    public static Platform findPlatform(@NotNull Integer platformId) {
		    	return plat.get(platformId);
		    }

		    
		}

		@Override
		public VkMainConversationBotFunctions functions() {
			return new VkMainConversationBotFunctions(message, client, actor);
		}

		@Override
		public int getBotId() {
			
			return actor.getGroupId();
		}

		@Override
		public boolean objectIsGroup(@NotNull Integer id) {
			return id < 0;
		}

		@Override
		public UserFull getUserById(@NotNull String id, @Nullable Fields... fields) {
			try {
				return fields == null ?
						client.users().get(actor).userIds(id).fields(defaultFields).execute().get(0) :
						client.users().get(actor).userIds(id).fields(fields).execute().get(0);
			} catch (Throwable e) {		
				
				logger.info("[VKAPI] User cannot be taken due to: " + e.toString());
				
			}
			return null;
			
		}

		@Override
		public boolean isValidUserId(@NotNull Integer id) {
				if(getUserById(id, (Fields[])null) == null) {
					return false;
				}
				
				return true;
		}

		@Override
		public boolean isValidUserId(@NotNull String id) {
			
			if( getUserById(id, (Fields[])null) == null ) {
				return false;
			}
			
			return true;
			
		}

		@Override
		public boolean isGroup(@NotNull Integer currentId) {
			return actor.getGroupId() == currentId;
		}

		@Override
		public String toPayload(@NotNull Integer payload) {
			
			return "\"{\\\"button\\\":\\\"" + payload +"\\\"}\"";
		}

		@Override
		public List<UserFull> getOnlineUsersFromConversation(@NotNull Fields... fields) {
				return getUsersFromConversation(fields).stream().filter(user -> user.isOnline()).collect(Collectors.toList());
		}

		@Override
		public void removeKeyboard(@NotNull String message) {
			val clearKeyBoard = this.functions().keyboardBuilder().onetime(true);
			this.sendMessageWithKeyBoard(message, clearKeyBoard.build());
					
			/*try {
				//client.messages().delete(actor).deleteForAll(true).messageIds(client.messages().getByConversationMessageId(this.actor, this.message.getPeerId(),(this.message.getConversationMessageId() + 1)).execute();
			} catch (ApiException | ClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}

		@Override
		public boolean isOnline(@NotNull UserFull user) {
			
			return user.isOnline();
		}

		@Override
		public boolean isOnline(@NotNull Integer id) {
			
			return getUserById(id, (Fields[])null).isOnline();
		}

		@Override
		public String getUserPlatform(@NotNull UserFull user) {
			
			return (user.getLastSeen().getPlatform() == null ? "(Mobile) 📱" : Platform.findPlatform(user.getLastSeen().getPlatform()).getDescription());
		}

		@Override
		public Integer senderId() {
			
			return message.getFromId();
		}

		@Override
		public Integer currentButtonPayload(@NotNull String payload) {
			
			return Integer.parseInt(payload.replaceAll("[^0-9]", ""));
		}

		@Override
		public void sendTranslatedMessage(@NotNull String message, @NotNull Consumer<ConversationTranslate> onFail) {
			sendMessage(BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate.AutomaticlyTranslation
					.languageMessage(message, this.getMessage().getFromId(), onFail));
			
		}

		@Override
		public void sendTranslatedMessage(@NotNull String message, @NotNull String translateLang, @NotNull Consumer<ConversationTranslate> onFail) {
			sendMessage(new ConversationTranslate().sourceLanguage("auto").targetLanguage(translateLang).text(message).transtale(onFail));
			
		}

		@Override
		public void sendTranslatedMessage(@NotNull String message) {
			
			sendMessage(BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate.AutomaticlyTranslation.
					languageMessage(message, this.getMessage().getFromId()));
			
		}

		@Override
		public void sendTranslatedMessage(@NotNull String message, @NotNull String translateLang) {
			sendMessage(new ConversationTranslate().sourceLanguage("auto").targetLanguage(translateLang).text(message).transtale());
			
		}

		@Override
		public boolean senderIsMember() {
			
			try {
				
				return client.groups().isMember(actor, String.valueOf(actor.getGroupId())).userId(message.getFromId()).execute() != BoolInt.NO;
			} catch (ApiException | ClientException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public String translatedString(@NotNull String string, @NotNull Consumer<ConversationTranslate> onFail) {
			return BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate.AutomaticlyTranslation
					.languageMessage(string, this.getMessage().getFromId(), onFail);
		}

		@Override
		public String translatedString(@NotNull String string) {
			return BotClient.IBotUtils.VkMainConversationBotFunctions.ConversationTranslate.AutomaticlyTranslation.
					languageMessage(string, this.getMessage().getFromId());
		}

		@Override
		public String translatedString(@NotNull String string, @NotNull String translateLang) {
			return new ConversationTranslate().sourceLanguage("auto").targetLanguage(translateLang).text(string).transtale();
		}

		@Override
		public String translatedString(@NotNull String string, @NotNull String translateLang,
				@NotNull Consumer<ConversationTranslate> onFail) {
			return new ConversationTranslate().sourceLanguage("auto").targetLanguage(translateLang).text(string).transtale(onFail);
		}

		@Override
		public boolean messageIsButton() {
			
			return message.getPayload() != null;
		}


		@Override
		public boolean isBotId(@NotNull Integer id) {
			
			return getBotId() == Math.abs(id);
		}

		@Override
		public void sendTo(@NotNull String message, @NotNull Integer peerId) {
			
			try {
				
				client.messages().send(actor)
				.message(message.replace("%name%", "[id" + this.message.getFromId() + "|" + this.getUserById(this.message.getFromId(),
								(Fields[])null).getFirstName() + "]"))
				.randomId(new Random().nextInt(100000))
				.peerId(peerId)
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
			}
		}

		@Override
		public void sendToAttachment(@NotNull String message, @NotNull Integer peerId, @NotNull String attachment) {
			try {
				client.messages().send(actor)
				.message(message).attachment(attachment)
				.randomId(new Random().nextInt(100000))
				.peerId(peerId)
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
				
			}
			
		}

		@Override
		public void sendPhoto(@NotNull String message, @NotNull File photo) {
			
			try {
				val url = client.photos().getMessagesUploadServer(actor).peerId(0).execute();
				
				val uploader = client.upload().photoMessage(url.getUploadUrl().toString(), photo).execute();
				
				val photoSaver = client.photos().saveMessagesPhoto(actor, uploader.getPhoto())
						.server(uploader.getServer()).hash(uploader.getHash()).execute().get(0);
				
				sendAttachmentMessage(message, "photo"+ photoSaver.getOwnerId() + "_" + photoSaver.getId());
				
			} catch (ApiException | ClientException e) {
				
				e.printStackTrace();
			}
			
			
		}

		@Override
		public void sendDoc(@NotNull String message, @NotNull File doc) {
			
			try {
				val docs = client.docs().getMessagesUploadServer(actor).peerId(this.message.getPeerId()).execute();
				
				val uploader = client.upload().doc(docs.getUploadUrl().toString(), doc).execute();
				
				val docsSaver = client.docs().save(actor, uploader.getFile()).execute();
				
				sendAttachmentMessage(message, "doc" + docsSaver.getDoc().getOwnerId() + "_" + docsSaver.getDoc().getId());
				
			} catch (ApiException | ClientException e) {
				
				e.printStackTrace();
			}
			
		}

		@Override
		public void sendAudioFile(@NotNull String message, @NotNull File audioFile) {
				new Thread(() -> {
					try {
					val docs = client.docs().getMessagesUploadServer(actor).type(DocsType.AUDIO_MESSAGE).peerId(this.message.getPeerId()).execute();
					
					val uploader = client.upload().doc(docs.getUploadUrl().toString(), audioFile).execute();
					
					val docsSaver = client.docs().save(actor, uploader.getFile()).execute();
					System.out.println(docsSaver.toString());
					sendAttachmentMessage(message, "audio_message" + 
					docsSaver.getAudioMessage().getOwnerId()+ "_" +
							docsSaver.getAudioMessage().getId());
					
					}catch(Exception exx) {
						exx.printStackTrace();
					}
				}).run();
			
		}

		@Override
		public void sendVideoFile(@NotNull String video, @NotNull File videoFile) {
			try {
				throw new MethodNotSupportedException("(BotClient) Method not supported");
			} catch (MethodNotSupportedException e) {
				
				e.printStackTrace();
			}//.getMessagesUploadServer(actor).type(DocsType.AUDIO_MESSAGE).peerId(this.message.getPeerId()).execute();
			
		}
		/*
		 * 
		 * @UNSAFE
		 * 
		 */
		@Override
		public void sendMessageToAllDialogs(@NotNull Integer startMessageId, @NotNull Integer count, @NotNull String message) {
			try {
				Lists.partition(client.messages().getConversations(actor).startMessageId(startMessageId).count(count).groupId(actor.getGroupId()).execute().getItems(), 150)
				.parallelStream().forEach(conversationMessages -> conversationMessages.forEach(conversations -> {
					sendTo(message, conversations.getConversation().getPeer().getId());
				}));

			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) UNSAFE METHOD: " + e.toString());
				
				e.printStackTrace();
			}
		}

		@Override
		public boolean userIsMember(@NotNull Integer id) {
			
			try {
				return client.groups().isMember(actor, String.valueOf(actor.getGroupId())).userId(id).execute() != BoolInt.NO;
			} catch (ApiException | ClientException e) {
				logger.info("(BotUtils) UserIsMember called exception from: " + e.toString());
				
				e.printStackTrace();
			}
			return false;
		}
		/*
		 * Version >= 0.93
		 */
		@Override
		public void sendReplyMessage(@NotNull String message, @NotNull Integer replyId) {
			try {
				client.messages().send(actor)
				.message(message.replace("%name%", "[id" + this.message.getFromId() + "|" + this.getUserById(this.message.getFromId(),
								(Fields[])null).getFirstName() + "]"))
				.randomId(new Random().nextInt(100000)).replyTo(replyId)
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public int getReplyMessageIdFromConversation(@NotNull Integer conversationMessageId) {
			
			try {
				return client.messages().getByConversationMessageId(actor, message.getPeerId(), conversationMessageId).execute().getItems().get(0).getId();
			} catch (ApiException | ClientException e) {
				logger.info("(BotUtils) Cannot get reply id: " + e.toString());
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public int getConversationId() {
			
			return message.getConversationMessageId();
		}

		@Override
		public void sendReplyConversationMessage(@NotNull String message, @NotNull Integer conversationMessageId) {
			
	    	try {
				client.messages().send(actor)
				.message(message.replace("%name%", "[id" + this.message.getFromId() + "|" + this.getUserById(this.message.getFromId(),
								(Fields[])null).getFirstName() + "]"))
				.randomId(new Random().nextInt(100000)).forward("{\"peer_id\":"+ this.message.getPeerId() +",\"conversation_message_ids\":["+conversationMessageId+"],\"is_reply\":true}")
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
			}
			
		}

		@Override
		public void sendForwardMessage(@NotNull String message, @NotNull Integer peerId,
				@NotNull Integer conversationId, @NotNull boolean isReply) {
	    	try {
				client.messages().send(actor)
				.message(message.replace("%name%", "[id" + this.message.getFromId() + "|" + this.getUserById(this.message.getFromId(),
								(Fields[])null).getFirstName() + "]"))
				.randomId(new Random().nextInt(100000)).forward("{\"peer_id\":"+ peerId +",\"conversation_message_ids\":["+conversationId+"],\"is_reply\":"+isReply+"}")
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
			}
			
		}

		@Override
		public void sendSticker(@NotNull Integer stickerId) {
	    	try {
				client.messages().send(actor)
				.stickerId(stickerId)
				.randomId(new Random().nextInt(100000))
				.peerId(this.message.getPeerId())
				.execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
			}
			
		}

		@Override
		public void pinMessage(@NotNull String conversationMessageId) {
			try {
				client.messages().pin(actor, this.message.getPeerId()).unsafeParam("conversation_message_id", conversationMessageId).execute();
			} catch (ApiException | ClientException e) {
				
				logger.info("(BotUtils) Message called exception from: " + e.toString());
				
			}
			
		}


    	
    	
    }
    
    public static interface VkCommandExecutor extends VkEventObject {
    	
    	public default void addCommandListener(@NotNull Message message, @NotNull Consumer<String[]> onMessage, @NotNull String...aliases) {
    		for (String command : aliases) {
    			if(message.getText().split(" ")[0].equalsIgnoreCase(command)) {
    				onMessage.accept(message.getText().split(" "));
    			}
    		}
    	};
    	public default void addCommand(@NotNull Consumer<String[]> onMessage, @NotNull String...aliases) {
    		for (String command : aliases) {
    			if(argumentsCache.get(this)[0].equalsIgnoreCase(command)) {
    				
    				onMessage.accept(argumentsCache.get(this));
    			}
    		}
    	};
    	
    	public final ConcurrentMap<VkCommandExecutor, String[]> argumentsCache = Maps.newConcurrentMap();
    	
    	public default void callArgs(@NotNull String[] elements) {
    		if(!argumentsCache.containsKey(this)) {
    			argumentsCache.put(this, elements);
    		}
    	};
    	
    	public default boolean getCommand(@NotNull String command, @NotNull String commandKey, @NotNull Integer element) {
    		
    		return argumentsCache.get(this)[element].equalsIgnoreCase(commandKey + command);
    	};
    	
    	public default boolean getCommand(@NotNull String command, @NotNull String commandKey) {
    		
    		return argumentsCache.get(this)[0].equalsIgnoreCase(commandKey + command);
    	};
    	
    	public default boolean getCommand(@NotNull String command) {
    		
    		return argumentsCache.get(this)[0].equalsIgnoreCase("/" + command);
    	};
    	
    	public void onCommand(@NotNull IBotUtils bot, @NotNull VkApiClient client, @NotNull GroupActor actor, @NotNull Message message, @NotNull String[] args);
    	
    }
    
   

    @FunctionalInterface
    public static interface VkHandler extends VkEventObject {
    	
    	public VkEventObject onAction(Integer groupId, Validable element);
    	
        @NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
        @Nullable
         final class VkEventHandler {
        	
        	private final Integer groupId = AnnotationHandler.botId();
        	private final SafeClient safe = BotClient.safe();
        	
        	private final static String spliteratorRegex = " ";
        	
        	Map<VkHandlerType, Validable> facade = Maps.newConcurrentMap();
        	
        	Validable vl;
        	
        	VkHandler handler;
        	
        	Consumer<Validable> element;
        	
        	VkHandlerType type;
        	
        	Integer keyElement;
        	
        	public static Map<Class<? extends VkEventObject>, Integer> asyncSet = new HashMap<>();
        	
        	
        	
        	private static final Map<VkEventType, Consumer<Validable>> map = Maps.newEnumMap(VkEventType.class);
        	
        	public VkEventInstaller newInstaller() {
				return new VkEventInstaller();
        		
        	}
        	
        	public static Integer asyncId(@NotNull Class<? extends VkEventObject> classSet, @NotNull Integer asyncId)
        	{ asyncSet.put(classSet, asyncId); return asyncId; };
        	
        	public static VkEventHandler newHandler() {
        		return new VkEventHandler();
        	}
        	
        	public VkEventHandler applyHandler(@NotNull VkHandler handler) {
        		
        		this.handler = handler;
        		
				return this;
        		
        		
        	}
        	
        	public VkEventHandler facade(@NotNull VkHandlerType type, @NotNull Validable element) {
        		
        	
        		if(!facade.containsKey(type)) facade.put(type, element);
        		
        		this.vl = element;
        		this.type = type;
        		
        		return this;
        		
        	}
        	
        	public VkEventHandler handle(@NotNull UnaryOperator<VkHandler> handler) {
        		
        		handler.apply(this.handler);
        		
        		return this;
        	}
        	
        	@Contract("fail->null")
        	@NotNull
        	public static Consumer<Validable> eventSetup(@NotNull VkEventType type, @NotNull Consumer<Validable> vl) {
				return map.put(type, vl);
				
        		
        	}
        	/*
        	 * 
        	 * Последовательный вызов всех функций
        	 * 
        	 * 
        	 */
			public void build() {
       
        		asyncSet.forEach((eventObject, id) ->{
        			if (debug) {
        			System.out.println("EVENT OBJECT: " + eventObject + " ID: " + id);
        			System.out.println("ID : " + id + " TYPE: " + this.type.toString());
        		}
        		val event = BotClient.getInstaller().enumirationMethods().get(Collections.singletonMap(id, this.type));
        		
        		if(event != null){
        			
        			if (debug) {
        			System.out.println("Object invocated");
        			}
        			event.forEach((clazz, methodStack) -> methodStack.forEach(methods -> {
        				
        				methods.setAccessible(true);
        				
        				if(methods.getName().equals("onCommand")) {
        					try {
        					if (debug) {
        					System.out.print("Trying invocation method: " + methods.getName() + " with " + methods.getParameterCount() + " From class " + methods.getClass().getSimpleName() + "\n " + methods);
        					}
        					methods.invoke(eventObject.newInstance(),
									(IBotUtils)new BotUtilsImpl().get((Message)this.vl),
									this.safe.getClient(), this.safe.getGroupActor(),
									(Message)this.vl,
									((Message)this.vl).getText().split(spliteratorRegex));
							
						
        					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
							logger.info("[VKAPI] Cause somethink wrong in reflection type, from invocation elements. Error: " + e.toString());
							e.printStackTrace();
						}
        				}else {try{methods.invoke(eventObject.newInstance(),
								this.groupId,
								this.vl);}catch(Exception ex) {
									logger.info("[VKAPI] Cause somethink wrong in reflection type, from invocation elements. Error: " + ex.toString());
									ex.printStackTrace();
								} };
        				
					}));
        			
        		}else {
        			// ignore
        			
        		}
        		});
        	}
        	
        	@SuppressWarnings("unchecked")
			@NotNull
        	@Contract(pure = true)
        	public static <T extends VkEventObject> IVkEventInstaller<T> registerInstaller() {
        		
				return (IVkEventInstaller<T>) new VkEventInstaller();
        		
        	}
        	
        	
        	@NotNull
        	public VkHandler handleEvent() {
        		this.handler.onAction(this.groupId, this.vl);
				return this.handler;
        	}
        
        	
        	@Contract(pure = true)
        	@NotNull
        	
        	public Consumer<? extends Validable> getEventObjects(@NotNull final VkEventType type) {
        		
        		val element = Optional.ofNullable(map.get(type));
        		
        		return element.orElseThrow(RuntimeException::new);
        	}
        	
        	public interface IVkEventInstaller<T extends VkEventObject> {
        		
        		static final LinkedHashMap<Map<Integer,VkHandler.VkHandlerType>, Map<Class<?>,List<Method>>> weakCache = new LinkedHashMap<>();
        		
        		
        		default LinkedHashMap<Map<Integer,VkHandler.VkHandlerType>, Map<Class<?>,List<Method>>> enumirationMethods(){
        			
        			return weakCache;
        			
        		}
        		
        		void installHandler(@NotNull T handler);
        	}
         
        	
        	@Getter
        	@FieldDefaults(level=AccessLevel.PRIVATE) 
        	@ToString
        	@EqualsAndHashCode 
        	private static class VkEventInstaller implements IVkEventInstaller<VkEventObject>, Cloneable {
        		
        		public static Map<Class<?>,List<Method>> methodsSet = Maps.newHashMap();
        	
        	protected List<Method> stackMethodsSet = new Stack<>();
        	
        	private List<VkEventObject> classesSet = new LinkedList<>();
        	
        	@NonFinal  private Integer asyncId;
        	
        		public LinkedHashMap<Map<Integer,VkHandler.VkHandlerType>, Map<Class<?>,List<Method>>> methodElement(){
        			
        			return enumirationMethods();
        			
        		}
        		@Contract(pure = false)
        		public Integer initAsync(@NotNull Class<? extends VkEventObject> handlerClass) {
        			
					
					if (handlerClass.isAnnotationPresent(AsyncInit.class)) {
						
    					Annotation anno = handlerClass.getAnnotation(AsyncInit.class);
    					
    					this.asyncId = VkEventHandler.asyncId(handlerClass, ((AsyncInit)anno).asyncId());
    					if (debug) {
    					BotClient.getLogger().info("Initialized: ID: " + this.asyncId);
    					System.out.println("Added object: " + handlerClass + "  ID: " + this.asyncId);
    					}
					}else {
						try {
							throw new IllegalArgumentException("[VKAPI] Current class not annotated async key");
						} catch (IllegalArgumentException iae) {
							
							iae.printStackTrace();
							
						}
					}
					if(this.asyncId <= 0) {
						
						throw new IllegalArgumentException("[VKAPI] ASYNC Reference return zero or lower.");
						
					}
					return this.asyncId;
        			
        		}
        		
        		
				@Override
				public void installHandler(@NotNull final VkEventObject handler) {
					
					logger.info("(" + clientName + " v"+ version +")" + " Trying include " + handler.getClass().getSimpleName() + ".class");
					val handlerClass = handler.getClass();
					
					initAsync(handlerClass);
					
					Method[] methods = handlerClass.getMethods();
					if(handler instanceof VkCommandExecutor) methodElement().put(Collections.singletonMap(this.asyncId,VkHandlerType.COMMAND),						
							Collections.singletonMap(handler.getClass(), Stream.of(handler.getClass().getDeclaredMethods())
									.filter(method -> method.getName().equals("onCommand")).collect(Collectors.toList())));
					
					for(Method mt : methods) {
						
						if (mt.isAnnotationPresent(VkAction.class)) {
	    					Annotation anno = mt.getAnnotation(VkAction.class);
	    					
	    					VkAction adapt = (VkAction) anno;
	    					if(!stackMethodsSet.contains(mt)) { stackMethodsSet.add(mt); };
	    					methodsSet.put(handler.getClass(), stackMethodsSet);
	    					methodElement().put(Collections.singletonMap(this.asyncId,adapt.type()), methodsSet);
	    					
						}
						
					}
					 
				}
				
				@Override
				public VkEventInstaller clone() {
					try {
						return (VkEventInstaller) super.clone();
					} catch (CloneNotSupportedException e) {
						
						return null;
					}

				}
        		
        	}
        	
        }

        public enum VkHandlerType {
        	
        	COMMAND,
        	MESSAGING,
        	PHOTO,
        	AUDIO,
        	VIDEO,
        	WALL,
        	BOARD,
        	MARKET,
        	GROUP,
        	POLL,
        	USER
        }
    	
    	public enum VkEventType{
    		ALL,
    		MESSAGE_NEW,
    		MESSAGE_REPLY,
    		MESSAGE_EDIT,
    		MESSAGE_ALLOW,
    		MESSAGE_DENY,
    		
    		
    		PHOTO_NEW,
    		PHOTO_COMMENT_NEW,
    		PHOTO_COMMENT_EDIT,
    		PHOTO_COMMENT_RESTORE,
    		PHOTO_COMMENT_DELETE,
    		
    		AUDIO_NEW,
    		
    		VIDEO_NEW,
    		VIDEO_COMMENT_NEW,
    		VIDEO_COMMENT_RESTORE,
    		VIDEO_COMMENT_EDIT,
    		VIDEO_COMMENT_DELETE,
    		WALL_REPOST,
    		WALL_RESTORE,
    		WALL_POST_NEW,
    		WALL_REPLY_EDIT,
    		WALL_REPLY_RESTORE,
    		WALL_REPLY_DELETE,
    		BOARD_POST_NEW,
    		BOARD_POST_EDIT,
    		BOARD_POST_DELETE,
    		BOARD_POST_RESTORE,
    		MARKET_COMMENT_NEW,
    		MARKET_COMMENT_EDIT,
    		MARKET_COMMENT_RESTORE,
    		MARKET_COMMENT_DELETE,
    		GROUP_LEAVE,
    		GROUP_JOIN,
    		GROUP_CHANGE_SETTINGS,
    		GROUP_CHANGE_PHOTO,
    		GROUP_OFFICERS_EDIT,
    		POLL_VOTE_NEW,
    		USER_BLOCK,
    		USER_UNBLOCK
    		;
    		
    		
    	}
    	
    } 
	
    public class PollHandler extends CallbackApiLongPoll{

        private final Logger LOG = LoggerFactory.getLogger(PollHandler.class);
        
        @NotNull private final VkEventHandler handler = VkEventHandler.newHandler();
    	
       @Getter private GroupActor actor;
        
       @Getter private VkApiClient client;
        
       
		public PollHandler(@NotNull VkApiClient client, @NotNull GroupActor actor) {
			super(client, actor);
			this.actor = actor;
			this.client = client;
			
			
		}

	    public void messageNew(Integer groupId, Message message) {
	    	if (debug) {
	        LOG.info("messageNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.COMMAND, message).build();
	       
	    }

	    public void messageReply(Integer groupId, Message message) {
	    	if (debug) {
	        LOG.info("messageReply: " + message.toString());
	    	}
	       // BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MESSAGING, message).build();
	    }

	    public void messageEdit(Integer groupId, Message message) {
	    	if (debug) {
	        LOG.info("messageReply: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MESSAGING, message).build();
	    }

	    public void messageAllow(Integer groupId, MessageAllow message) {
	    	if (debug) {
	        LOG.info("messageAllow: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MESSAGING, message).build();
	    }

	    public void messageDeny(Integer groupId, MessageDeny message) {
	    	if (debug) {
	        LOG.info("messageDeny: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MESSAGING, message).build();
	    }

	    public void photoNew(Integer groupId, Photo message) {
	    	if (debug) {
	        LOG.info("photoNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.PHOTO, message).build();
	    }

	    public void photoCommentNew(Integer groupId, PhotoComment message) {
	    	if (debug) {
	        LOG.info("photoCommentNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.PHOTO, message).build();
	    }

	    public void photoCommentEdit(Integer groupId, PhotoComment message) {
	    	if (debug) {
	        LOG.info("photoCommentEdit: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.PHOTO, message).build();
	    }

	    public void photoCommentRestore(Integer groupId, PhotoComment message) {
	    	if (debug) {
	        LOG.info("photoCommentRestore: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.PHOTO, message).build();
	    }

	    public void photoCommentDelete(Integer groupId, PhotoCommentDelete message) {
	    	if (debug) {
	        LOG.info("photoCommentDelete: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.PHOTO, message).build();
	    }

	    public void audioNew(Integer groupId, Audio message) {
	    	if (debug) {
	        LOG.info("audioNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.AUDIO, message).build();
	    }

	    public void videoNew(Integer groupId, Video message) {
	    	if (debug) {
	        LOG.info("videoNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.VIDEO, message).build();
	    }

	    public void videoCommentNew(Integer groupId, VideoComment message) {
	    	if (debug) {
	        LOG.info("videoCommentNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.VIDEO, message).build();
	    }

	    public void videoCommentEdit(Integer groupId, VideoComment message) {
	    	if (debug) {
	        LOG.info("videoCommentEdit: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.VIDEO, message).build();
	    }

	    public void videoCommentRestore(Integer groupId, VideoComment message) {
	    	if (debug) {
	        LOG.info("videoCommentRestore: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.VIDEO, message).build();
	    }

	    public void videoCommentDelete(Integer groupId, VideoCommentDelete message) {
	    	if (debug) {
	        LOG.info("videoCommentDelete: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.VIDEO, message).build();
	    }

	    public void wallPostNew(Integer groupId, Wallpost message) {
	    	if (debug) {
	        LOG.info("wallPostNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.WALL, message).build();
	    }

	    public void wallRepost(Integer groupId, Wallpost message) {
	    	if (debug) {
	        LOG.info("wallRepost: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.WALL, message).build();
	    }

	    public void wallReplyNew(Integer groupId, WallComment message) {
	    	if (debug) {
	        LOG.info("wallReplyNew: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.WALL, message).build();
	    }

	    public void wallReplyEdit(Integer groupId, WallComment message) {
	    	if (debug) {
	        LOG.info("wallReplyEdit: " + message.toString());
	    	}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.WALL, message).build();
	    }

	    public void wallReplyRestore(Integer groupId, WallComment message) {
	    	if (debug) {
	    	LOG.info("wallReplyRestore: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.WALL, message).build();
	    }

	    public void wallReplyDelete(Integer groupId, WallCommentDelete message) {
	    	if (debug) {
	    	LOG.info("wallReplyDelete: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.WALL, message).build();
	    }

	    public void boardPostNew(Integer groupId, TopicComment message) {
	    	if (debug) {LOG.info("boardPostNew: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.BOARD, message).build();
	    }

	    public void boardPostEdit(Integer groupId, TopicComment message) {
	    	if (debug) { LOG.info("boardPostEdit: " + message.toString()); }
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.BOARD, message).build();
	    }

	    public void boardPostRestore(Integer groupId, TopicComment message) {
	    	if (debug) {
	        LOG.info("boardPostRestore: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.BOARD, message).build();
	    }

	    public void boardPostDelete(Integer groupId, BoardPostDelete message) {
	    	if (debug) {
	        LOG.info("boardPostDelete: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.BOARD, message).build();
	    }

	    public void marketCommentNew(Integer groupId, MarketComment message) {
	    	if (debug) {
	        LOG.info("marketCommentNew: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MARKET, message).build();
	    }

	    public void marketCommentEdit(Integer groupId, MarketComment message) {
	    	if (debug) {
	        LOG.info("marketCommentEdit: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MARKET, message).build();
	    }

	    public void marketCommentRestore(Integer groupId, MarketComment message) {
	    	if (debug) {
	        LOG.info("marketCommentRestore: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MARKET, message).build();
	    }

	    public void marketCommentDelete(Integer groupId, MarketCommentDelete message) {
	    	if (debug) {
	        LOG.info("marketCommentDelete: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.MARKET, message).build();
	    }

	    public void groupLeave(Integer groupId, GroupLeave message) {
	    	if (debug) {
	        LOG.info("groupLeave: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.GROUP, message).build();
	    }

	    public void groupJoin(Integer groupId, GroupJoin message) {
	    	if (debug) {
	        LOG.info("groupJoin: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.GROUP, message).build();
	    }

	    public void groupChangeSettings(Integer groupId, GroupChangeSettings message) {
	    	if (debug) {
	        LOG.info("groupChangeSettings: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.GROUP, message).build();
	    }

	    public void groupChangePhoto(Integer groupId, GroupChangePhoto message) {
	    	if (debug) {
	        LOG.info("groupChangePhoto: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.GROUP, message).build();
	    }

	    public void groupOfficersEdit(Integer groupId, GroupOfficersEdit message) {
	    	if (debug) {
	        LOG.info("groupOfficersEdit: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.GROUP, message).build();
	    }

	    public void pollVoteNew(Integer groupId, PollVoteNew message) {
	    	if (debug) {
	        LOG.info("pollVoteNew: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.POLL, message).build();
	    }

	    public void userBlock(Integer groupId, UserBlock message) {
	    	if (debug) {
	        LOG.info("userBlock: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.USER, message).build();
	    }

	    public void userUnblock(Integer groupId, UserUnblock message) {
	    	if (debug) {
	        LOG.info("userUnblock: " + message.toString());}
	        BotClient.getEventHandler().facade(VkHandler.VkHandlerType.USER, message).build();
	    }
    	
    }
	

}
