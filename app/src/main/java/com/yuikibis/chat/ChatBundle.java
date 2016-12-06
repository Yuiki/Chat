package com.yuikibis.chat;

/**
 * Created on 15/06/07.
 */
public class ChatBundle {
    private String authorName;
    private String message;
    private String time;

    @SuppressWarnings("unused")
    private ChatBundle() {
    }

    ChatBundle(String authorName, String message, String time) {
        this.authorName = authorName;
        this.message = message;
        this.time = time;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
