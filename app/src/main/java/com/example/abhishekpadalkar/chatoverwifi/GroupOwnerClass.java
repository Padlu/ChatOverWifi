package com.example.abhishekpadalkar.chatoverwifi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by abhishekpadalkar on 4/19/18.
 */

public class GroupOwnerClass extends Thread {

    Socket socket;
    ServerSocket serverSocket;
    HandlerClass handlerClass;
    SendReceiveClass sendReceiveClass;

    public GroupOwnerClass(SendReceiveClass sendReceiveClass1, HandlerClass handlerClass1){
        this.sendReceiveClass = sendReceiveClass1;
        this.handlerClass = handlerClass1;
    }

    @Override
    public void run() {
        try{
            serverSocket = new ServerSocket(8888);
            serverSocket.setReuseAddress(true);
            socket = serverSocket.accept();
//                synchronized (this) {
//                    wait(10000);
//                }
            sendReceiveClass = new SendReceiveClass(socket, handlerClass);
            sendReceiveClass.start();
        } catch(IOException e){
            e.printStackTrace();
        }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
    }

}
