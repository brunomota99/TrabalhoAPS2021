/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.traballhoaps2021.back;

/**
 *
 * @author bruno
 */
@FunctionalInterface
public interface FileReceiveCallback {
    void onFileReceive(String user, String fileName);
}
