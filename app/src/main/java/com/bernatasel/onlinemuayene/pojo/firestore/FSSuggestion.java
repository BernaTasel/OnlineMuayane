package com.bernatasel.onlinemuayene.pojo.firestore;


import com.google.firebase.Timestamp;

import java.util.concurrent.atomic.AtomicInteger;

public class FSSuggestion {
    private String id;
    private String email;
    private String title;
    private String text;
    private com.google.firebase.Timestamp date;
    private boolean solved;

    public FSSuggestion() {
    }

    public FSSuggestion(String email, String title, String text, Timestamp date, boolean solved) {
        //id = count.incrementAndGet();
        id = email +"-"+String.valueOf(date.getSeconds())+"-"+String.valueOf(date.getNanoseconds());
        this.email = email;
        this.title = title;
        this.text = text;
        this.date = date;
        this.solved = solved;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "FSSuggestion{" +
                "email='" + email + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", isSolved=" + solved +
                '}';
    }
}
