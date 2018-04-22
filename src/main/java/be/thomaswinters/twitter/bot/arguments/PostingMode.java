package be.thomaswinters.twitter.bot.arguments;

public enum PostingMode {
    REPLY(false, true), POST(true, false), POST_REPLY(true, true);

    private final boolean post;
    private final boolean reply;

    PostingMode(boolean post, boolean reply) {
        this.post = post;
        this.reply = reply;
    }

    public boolean allowsPosting() {
        return post;
    }

    public boolean allowsReplying() {
        return reply;
    }
}
