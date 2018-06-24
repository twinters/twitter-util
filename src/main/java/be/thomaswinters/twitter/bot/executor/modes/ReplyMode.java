package be.thomaswinters.twitter.bot.executor.modes;

import be.thomaswinters.chatbot.IChatBot;
import be.thomaswinters.chatbot.ui.ChatbotGUI;
import be.thomaswinters.twitter.bot.TwitterBot;
import be.thomaswinters.twitter.bot.arguments.TwitterBotArguments;
import be.thomaswinters.twitter.util.IExtractableChatBot;
import twitter4j.TwitterException;

import java.util.Optional;

public class ReplyMode implements ITwitterBotMode {
    @Override
    public void execute(TwitterBot bot, TwitterBotArguments arguments) throws TwitterException {
        if (arguments.isDebug()) {
            if (bot instanceof IExtractableChatBot) {
                Optional<IChatBot> botOptional = ((IExtractableChatBot) bot).getChatBot();
                String botName = bot.getTwitterConnection().getScreenName();
                botOptional.ifPresent(chatbot -> new ChatbotGUI(chatbot, botName).run());
            } else {
                throw new RuntimeException("Debugging replies not fully supported yet. " +
                        "Please implement IExtractableChatBot to debug replies");
            }
        } else {
            bot.replyToAllUnrepliedMentions();
        }
    }
}
