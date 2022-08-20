import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.protobuf.Value;
import com.vk.api.sdk.actions.Users;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.KeyboardButtonActionType;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.Fields;

import lombok.val;
import ru.Haber.VkAPI.Annotations.AsyncInit;
import ru.Haber.VkAPI.Annotations.BotConfigurableHandler;
import ru.Haber.VkAPI.Annotations.BotHandler;
import ru.Haber.VkAPI.CustomCallback.CallbackMessagePayload;
import ru.Haber.VkAPI.CustomCallback.VkCallbackMessage;
import ru.Haber.VkAPI.CustomCallback.CallbackMessagePayload.VkMessagePayloadType;
import ru.Haber.VkAPI.bootstrap.BotClient;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.CustomKeyboard;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.CarouselAction;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.ColorButton;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.CustomKeyboardButton;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.CustomKeyboardButtonActionType;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.VkCustomButton;

@BotHandler(token = "-", id=191873806)
public class BotClientTest1 extends BotClient{

	private static final BotClientTest1 bot = newLocalExec(BotClientTest1::new, true);
	
	//protected static final Logger LOG = LoggerFactory.getLogger(HttpTransportClient.class);
	private static  Logger log4j = LogManager.getLogger(HttpTransportClient.class);
	@SuppressWarnings("unchecked")
	private static List<String> animeList = new ArrayList<String>() {{
		add("\nАниме: Ангельские ритмы. \nЖанр: Трагедия, Драма 😭\n");
		add("\nАниме: Тетрадь смерти. \nЖанр: Триллер, Детектив 🔎\n");
		add("\nАниме: Демон страшей школы. \nЖанр: Этти,Школа 🧡\n");
		add("\nАниме: Оверлорд \nЖанр: Исекай 🧝‍♀\n");
		add("\nАниме: Наруто \nЖанр: Боевые искусства 🥋\n");
		add("\nАниме: Блич \nЖанр: Сверхъестественное 🔮\n");
		add("\nАниме: Евагелион \nЖанр: Меха, Драма 🤖\n");
	}};
	public BotClientTest1() {	
	}
	
	public static Connection connection;
	
	private static Statement states;
	
	public static final Map<String, Integer> map = Maps.newConcurrentMap();
	
	//private static Logger logger = LoggerFactory.getLogger(SQLite.class);
	
	@Override
	public void onStart() {
		
		debug(true);
		
		systemLocale("ru");
		
		getInstaller().installHandler(new CommandDispatch());
		
		getInstaller().installCallbackEventHandler(new CommandDispatch());
		
		log4j.isTraceEnabled();
		
		Configurator.setLevel("com.vk.api.sdk.httpclient.HttpTransportClient",Level.WARN);
	
	}
	@AsyncInit(asyncId = 1)
	public static class CommandDispatch implements VkCommandExecutor,VkCallbackListener {
		
		@Override
		public void onCommand(@NotNull IBotUtils bot, @NotNull VkApiClient client, @NotNull GroupActor actor,
				@NotNull Message message, @NotNull String[] args) {
			this.callArgs(args);
			this.addCommandListener(message, argumentsCache -> {
				if(bot.isConversation()) {bot.sendMessage("Я работаю только из личных сообщений."); return;}
				val b1 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.CALLBACK).title("Смотреть аниме")
						.callbackPayload(new CallbackMessagePayload().setType(VkMessagePayloadType.SHOW_SNACKBAR)
								.setText("Вы открыли меню аниме")).color(ColorButton.GREEN).build();
				

				val keyVal = bot.functions().keyboardBuilder().inline(true).addButtonX(b1).singleComplete().build();
				
				bot.sendMessageWithKeyBoard("Вот ваша клавиатура семпай", keyVal);
				
			}, "/callback");
			
		}
		private static Map<Integer, Integer> users = Maps.newConcurrentMap();
		@Override
		public void onCallbackEvent(@NotNull Integer groupId, @NotNull VkCallbackMessage message,
				@NotNull IBotUtils bot) {
			
			if(bot.isConversation()) {bot.sendMessage("Я работаю только из личных сообщений."); return;}
			
			if(message.getPayload().getType().equalsIgnoreCase("show_snackbar")) {
				bot.sendMessageEventAnswer(message.getEvent_id(), message.getUser_id(),
						
						message.getPeer_id(), message.getPayload().toString());
				users.put(bot.getFromUser(), 1);
			}
			if(message.getPayload().getType().equalsIgnoreCase("finish")) {
				val b1 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.CALLBACK).title("Смотреть аниме")
						.callbackPayload(new CallbackMessagePayload().setType(VkMessagePayloadType.SHOW_SNACKBAR)
								.setText("Вы открыли меню аниме")).color(ColorButton.GREEN).build();

				val keyVal = bot.functions().keyboardBuilder().inline(true).addButtonX(b1).singleComplete().build();
				bot.editMessageKeyboard("Надеемся что вы нашли себе аниме по вкусу.😋 Если заходите посмотреть ещё, "
						+ "мы оставили для вас эту кнопку 📌",keyVal
				,message.getPeer_id(),String.valueOf(message.getConversation_message_id()));
				
				users.put(bot.getFromUser(), 1);
				
				return;
			}
			val type = message.getPayload().getType().split("_");
			
			if(type[0].equalsIgnoreCase("n")) {
				users.put(bot.getFromUser(), users.get(bot.getFromUser())+1);
			}else {
				users.put(bot.getFromUser(), users.get(bot.getFromUser())-1);
			}
			CustomKeyboard keyboard = null;
			CustomKeyboardButton b1 = null;
			CustomKeyboardButton b2 = null;
			CustomKeyboardButton b3 = bot.functions().buttonBuilder()
					.type(CustomKeyboardButtonActionType.CALLBACK).title("Закончить")
					.callbackPayload(new CallbackMessagePayload().setType("finish")).color(ColorButton.WHITE).build();
			if(users.get(bot.getFromUser()) < 6) {
				b1 = bot.functions().buttonBuilder()
					.type(CustomKeyboardButtonActionType.CALLBACK).title("Далее >")
					.callbackPayload(new CallbackMessagePayload().setType("n_" + users.get(bot.getFromUser()))).color(ColorButton.GREEN).build();
			
			}
			if(users.get(bot.getFromUser()) >= 1) {
				b2 = bot.functions().buttonBuilder()
						.type(CustomKeyboardButtonActionType.CALLBACK).title("< Назад")
						.callbackPayload(new CallbackMessagePayload().setType("d_" + (users.get(bot.getFromUser())-1))).color(ColorButton.GREEN).build();
			}
			
			if(b1 != null && b2 != null) {
				keyboard = bot.functions().keyboardBuilder().inline(true).addButtonX(b1).addButtonX(b2).completeRow(b3).build();
			}
			if(b1 != null && b2 == null) {
				keyboard = bot.functions().keyboardBuilder().inline(true).addButtonX(b1).completeRow(b3).build();
			}
			if(b1 == null && b2 != null) {
				keyboard = bot.functions().keyboardBuilder().inline(true).addButtonX(b2).completeRow(b3).build();
			}
			bot.editMessageKeyboard("Ваше меню ниже, выбирайте какое аниме хотите посмотреть."
					+ " 😃 Мы подобрали для вас специальные жанры: "
					+ "\n"+animeList.get(users.get(bot.getFromUser())) + "\n👉🏻 Если какое-то аниме вас не устроило можете, поискать на нашем сайте animevost.org. 😍",keyboard
			,message.getPeer_id(),String.valueOf(message.getConversation_message_id()));
		}
		
	}

}
