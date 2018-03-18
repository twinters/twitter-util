package be.thomaswinters.bot.data;

public interface IChatMessage {

    String getMessage(String message);

    IChatMessage getPrevious();

    IChatUser getUser();
}
