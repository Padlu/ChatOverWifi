package com.example.abhishekpadalkar.chatoverwifi;

import android.os.Handler;
import android.os.Message;

/**
 * Created by abhishekpadalkar on 4/19/18.
 */

public class HandlerClass {

    Handler handler;

    public HandlerClass(){
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {

                switch (message.what){
                    case 1 :
                        byte [] readBuff = (byte []) message.obj;
                        String tempMessage = new String(readBuff,0,message.arg1);
                        break;
                }

                return true;
            }
        });
    }

}
