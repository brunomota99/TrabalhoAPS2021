/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.traballhoaps2021;

import com.mycompany.traballhoaps2021.back.SocketClient;

/**
 *
 * @author bruno
 */
public class Session {
    
    public static final SocketClient socketClient;
    
    static {
        socketClient = new SocketClient();
        try{
        socketClient.startConnection("127.0.0.1", 4444);
        } catch(Exception e) {}
           
    }
    
}
