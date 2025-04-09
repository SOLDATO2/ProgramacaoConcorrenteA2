package com.example;

import com.example.factory.FactoryTask;
import com.example.shop.ShopTask;
import com.example.client.ClientTask;

public class ApplicationMain {
    public static void main(String[] args) {
        //fabricante
        new Thread(new FactoryTask()).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //lojas
        int totalShops = 3;
        for (int i = 0; i < totalShops; i++) {
            new Thread(new ShopTask(i)).start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //clientes
        int totalClients = 20;
        for (int i = 0; i < totalClients; i++) {
            new Thread(new ClientTask(i)).start();
        }
    }
}
