import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
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
import ru.Haber.VkAPI.bootstrap.BotClient;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.CarouselAction;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.ColorButton;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.CustomKeyboardButtonActionType;

@BotHandler(token = "43efe317478cf7eb17a71a352341a3a12fdc432584b807b23e761049c367089cd3a52daf080eeb8cbecc9", id=191873806)
public class BotClientTest1 extends BotClient{

	private static final BotClientTest1 bot = newLocalExec(BotClientTest1::new, true);
	
	//protected static final Logger LOG = LoggerFactory.getLogger(HttpTransportClient.class);
	private static  Logger log4j = LogManager.getLogger(HttpTransportClient.class);
	
	public BotClientTest1() {
		// TODO Auto-generated constructor stub
	}
	
	public static Connection connection;
	
	private static Statement states;
	
	public static final Map<String, Integer> map = Maps.newConcurrentMap();
	
	//private static Logger logger = LoggerFactory.getLogger(SQLite.class);
	
	public static void connect() {
		try {
			//Class.forName("org.sqlite.JDBC").newInstance();
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://sql308.epizy.com:3306/epiz_32204802_somebase","epiz_32204802","DmIj35H6EzL");
			val state = connection.createStatement();
			
			System.out.println("Connected!");
		} catch (SQLException | ClassNotFoundException e) {
			
			e.printStackTrace();

		}
		
	}
	
	@Override
	public void onStart() {
		
		debug(true);
		
		systemLocale("ru");
		
		getInstaller().installHandler(new CommandDispatch());
		
		log4j.isTraceEnabled();
		
		Configurator.setLevel("com.vk.api.sdk.httpclient.HttpTransportClient",Level.WARN);
		
	
	
	}
	
	
	@AsyncInit(asyncId = 1)
	public static class CommandDispatch implements VkCommandExecutor {
		
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
				
				val payload = bot.currentButtonPayload(buttonId);
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
				
				bot.sendSticker(19634);
				bot.sendReplyConversationMessage("Test", bot.getConversationId());
				//bot.sendMessageWithKeyBoard("Gradle test", keyboard.build());
				bot.sendAudioFile("Gradle Version is 6.7.0",
						bot.functions().speechText(bot.functions()
					.tts("en", "C:\\Users\\Mi\\AppData\\Local\\ArmA 2 OA\\MPMissionsCache\\audio.ogg", "Gradle Version is 6.7.0"), fail -> {
					bot.sendMessage("Неудалось отправить сообщение");
				}));
				
				
			}, "!gradlew", "gradle");
			
		}
		
	}

}
