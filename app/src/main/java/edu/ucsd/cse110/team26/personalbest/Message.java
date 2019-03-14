package edu.ucsd.cse110.team26.personalbest;

import android.support.annotation.NonNull;

public class Message {
    private String from;
    private String text;
    long timestamp;

    public Message() {
        timestamp = 0;
    }

    public Message(String from, String text) {
        this.from = from;
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + new ConcreteTimeStamper().timestampToString(timestamp) + ") " + from + ": " + text + "\n";
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
