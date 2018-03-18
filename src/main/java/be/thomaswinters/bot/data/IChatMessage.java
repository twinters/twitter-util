package be.thomaswinters.bot.data;

import java.util.Optional;

public interface IChatMessage {

    String getMessage(String message);

    Optional<IChatMessage> getPrevious();

    IChatUser getUser();
}
