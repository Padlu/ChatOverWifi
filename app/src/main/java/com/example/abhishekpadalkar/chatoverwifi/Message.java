package com.example.abhishekpadalkar.chatoverwifi;

import java.security.Timestamp;

/**
 * Created by abhishekpadalkar on 4/19/18.
 */

public class Message {

    private String name;
    private String msg;
    private Timestamp timestamp;

    public Message() {}

    public Message(String name, String msg, Timestamp timestamp) {
        this.name = name;
        this.msg = msg;
        this.timestamp = timestamp;
    }

    public String getName(){
       return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getMsg(){
        return msg;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    public String getTimestamp(){
        return timestamp.toString();
    }

}
