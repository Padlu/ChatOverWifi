package com.example.abhishekpadalkar.chatoverwifi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by abhishekpadalkar on 4/19/18.
 */

public class GroupMemberClass extends Thread {

    Socket socket;
    String hostAdd;
    SendReceiveClass sendReceiveClass;
    HandlerClass handlerClass;

    public GroupMemberClass(InetAddress hostAdd, SendReceiveClass sendReceiveClass1, HandlerClass handlerClass1){
        this.sendReceiveClass = sendReceiveClass1;
        this.handlerClass = handlerClass1;
        this.hostAdd = hostAdd.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAdd,8888));
//                synchronized (this) {
//                    wait(5000);
//                }
            sendReceiveClass= new SendReceiveClass(socket, handlerClass);
            sendReceiveClass.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
    }

}
