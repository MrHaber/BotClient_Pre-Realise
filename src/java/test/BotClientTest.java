import java.awt.print.Printable;

import org.jetbrains.annotations.NotNull;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.messages.KeyboardButtonActionType;
import com.vk.api.sdk.objects.messages.Message;

import lombok.val;
import ru.Haber.VkAPI.Annotations.AsyncInit;
import ru.Haber.VkAPI.Annotations.BotHandler;
import ru.Haber.VkAPI.bootstrap.BotClient;
import ru.Haber.VkAPI.bootstrap.BotClient.IBotUtils.VkMainConversationBotFunctions.KeyBoardBuilder.ColorButton;

@BotHandler(token = "a5886027a9b945ef54037fdcc52f6af433e73b9023c5d5a3e63ae99efe564641c796f252a3235a20b4604", id = 191873806)
public class BotClientTest extends BotClient{

	private static final BotClientTest bot = newLocalExec(BotClientTest::new, false);
	
	public BotClientTest() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onStart() {
		
		debug(false);
		
		systemLocale("ru");
		
		getInstaller().installHandler(new CommandDispatch());
		
	}
	public static BotClientTest getBot() {
		return bot;
	}
	@AsyncInit(asyncId = 1)
	public static class CommandDispatch implements VkCommandExecutor {

		@Override
		public void onCommand(@NotNull IBotUtils bot, @NotNull VkApiClient client, @NotNull GroupActor actor,
				@NotNull Message message, @NotNull String[] args) {
			
			this.callArgs(args);
			val keyboard = bot.functions().keyboardBuilder().inline(true).addButtonX(bot.functions().buttonBuilder()
					.color(ColorButton.WHITE)
					.payload(1)
					.type(KeyboardButtonActionType.TEXT).title("Русский").build()).singleComplete();
			
			this.addCommandListener(message, arguments -> {
				bot.removeKeyboard("%name%, Клавиатура была удалена");
				
			}, "/clear", "/clearkeyboard","/очистить");
			/*if(!bot.isConversation() && !bot.isBotId(message.getFromId())) {
				bot.sendMessage("Только в беседах.");
				return;
			}*/
		
			this.addCommand(arges -> {
				
				bot.sendSticker(19634);
				bot.sendReplyConversationMessage("Test", bot.getConversationId());
				bot.sendMessageWithKeyBoard("Gradle test", keyboard.build());
				bot.sendAudioFile("Gradle Version is 6.7.0",
						bot.functions().speechText(bot.functions()
					.tts("en", "C:\\Users\\Mi\\AppData\\Local\\ArmA 2 OA\\MPMissionsCache\\audio.ogg", "Gradle Version is 6.7.0"), fail -> {
					bot.sendMessage("Неудалось отправить сообщение");
				}));
				
				
			}, "!gradlew", "gradle");
			
		}
		
	}

}
