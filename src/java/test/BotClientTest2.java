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

@BotHandler(token = "43efe317478cf7eb17a71a352341a3a12fdc432584b807b23e761049c367089cd3a52daf080eeb8cbecc9", id=191873806)
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
			
			this.addCommandListener(message, arguments -> {
				bot.removeKeyboard("%name%, Клавиатура была удалена");
				
			}, "/clear", "/clearkeyboard","/очистить");
			/*if(!bot.isConversation() && !bot.isBotId(message.getFromId())) {
				bot.sendMessage("Только в беседах.");
				return;
			}*/
			
			/*this.addCommand(arges -> {
				
				if(arges.length <= 1) {
					bot.sendMessage("Нормально уже напиши блядота !оповещения [аргументы блять]");
					return;
				}
				if(arges.length <= 2) {
					bot.sendMessage("!оповещения привязать [ваш код блять] \n!оповещения обоссать [адресс вашего лица]");
					return;
				}
				if(arges.length <= 3 && arges[1].equalsIgnoreCase("привязать")) {
					bot.sendMessage("!оповещения привязать [поводком к столбу]");
					return;
				}
				if(arges.length <= 3 && arges[1].equalsIgnoreCase("обоссать")) {
					bot.sendMessage("!оповещения обоссать [ваше лицо]");
					return;
				}
				
				if (arges.length > 3) {
					if (args[1].equalsIgnoreCase("обоссать")) {
						bot.sendMessage("Мы обоссали ваше лицо по адресу: "+ arges[2]);
						return;
					}
					if (args[1].equalsIgnoreCase("привязать")) {
						bot.sendMessage("Мы привязали вас свингерской удавкой к столбу под номером: "+ arges[2]);
						return;
					}
				}
				//String playerName = arges[1];
				
				//bot.sendMessage("Вы успешно вошли в меня семпай " + playerName);
				// TODO 
				
				
			}, "!оповещения");
			*/
			this.addCommandListener(message, argumentsCache -> {
				if(bot.isConversation()) {bot.sendMessage("Я работаю только из личных сообщений."); return;}
				val b1 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.CALLBACK).title("Смотреть аниме")
						.callbackPayload(new CallbackMessagePayload().setType(VkMessagePayloadType.SHOW_SNACKBAR)
								.setText("Вы открыли меню аниме")).color(ColorButton.GREEN).build();
				

				val keyVal = bot.functions().keyboardBuilder().inline(true).addButtonX(b1).singleComplete().build();
				
				bot.sendMessageWithKeyBoard("Вот ваша клавиатура семпай", keyVal);
				
			}, "/callback");
			this.addCommandListener(message, arguments -> {
				
				val b1 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.OPEN_LINK)
						.link("https://primordial.dev").title("Yougame").payload(1).build();
				
				val b2 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.TEXT)
						.title("Добавить").color(ColorButton.BLUE).payload(2).build();
				
				val b3 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.TEXT)
						.title("Удалить").color(ColorButton.RED).payload(3).build();
				
				val b4 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.TEXT)
						.title("Добавить").color(ColorButton.BLUE).payload(4).build();
				
				val b5 = bot.functions().buttonBuilder().type(CustomKeyboardButtonActionType.TEXT)
						.title("Удалить").color(ColorButton.RED).payload(5).build();
				val f1 = bot.functions().elementBuilder()
						.addButton(b2).addButton(b3).addButton(b1).link("https://primordial.dev")
						.description("Выберите сообщество в котором \nхотите сказать Millida Grash\n - 550 баксов")
						.type(CarouselAction.OPEN_LINK).photo_id("-191873806_457239094");
				
				val f2 = bot.functions().elementBuilder().addButton(b4).addButton(b5).addButton(b1).link("https://primordial.dev")
						.description("Выберите сообщество в котором хотите \nсказать Millida Trade Legacy \n- 10 баксов")
						.type(CarouselAction.OPEN_LINK).photo_id("-191873806_457239097");
				
				val source = bot.functions().carouselBuilder().addElementsBuilder(f1)
						.addElementsBuilder(f2).build();
				bot.functions().carouselBuilder().clearCache();
				
				bot.sendCarouselMessage("Выберите паблик где хотите себе заказать скин за 5 рублей", source);
			},"/test");
			
			bot.chatEvent().onClick((userId,buttonId)->{
				
				//val payload = bot.currentButtonPayload(buttonId);
				if(buttonId.equals(bot.toPayload(2))) {
					bot.sendMessage("%name%, Вы добавили себе Millida Grash в список");
				}
				if(buttonId.equals(bot.toPayload(3))) {
					bot.sendMessage("%name%, Вы удалили Millida Grash из списка");
				}
				if(buttonId.equals(bot.toPayload(4))) {
					bot.sendMessage("%name%, Вы добавили себе Millida Trade в список");
				}
				if(buttonId.equals(bot.toPayload(5))) {
					bot.sendMessage("%name%, Вы удалили Millida Trade из списка");
				}
			}, 0);
		
			this.addCommand(arges -> {
				
				bot.sendSticker(17620);
				bot.sendReplyConversationMessage("Test", bot.getConversationId());
				//bot.sendMessageWithKeyBoard("Gradle test", keyboard.build());
				bot.sendAudioFile("Gradle Version is 6.7.0",
						bot.functions().speechText(bot.functions()
					.tts("en", "C:\\Users\\Mi\\AppData\\Local\\ArmA 2 OA\\MPMissionsCache\\audio.ogg", "Gradle Version is 6.7.0"), fail -> {
					bot.sendMessage("Неудалось отправить сообщение");
				}));
				
				
			}, "!gradlew", "gradle");
			
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
