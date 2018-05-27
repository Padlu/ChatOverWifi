package com.example.abhishekpadalkar.chatoverwifi;

import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Handler;

/**
 * Created by abhishekpadalkar on 4/19/18.
 */

public class SendReceiveClass extends Thread {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private HandlerClass handlerClass;

    final static int MESSAGE_READ = 1;

    public SendReceiveClass(Socket socket1, HandlerClass handlerClass1) {
        this.socket = socket1;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            this.handlerClass = handlerClass1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (socket != null) {
            try {
                bytes = inputStream.read(buffer);
                if (bytes > 0) {
                    handlerClass.handler.obtainMessage(MESSAGE_READ,bytes,-1,buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

}
