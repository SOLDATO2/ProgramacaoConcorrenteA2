package com.example.shop;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.EsteiraCircularLoja;

public class ShopTask implements Runnable {
    private int shopId;
    public ShopTask(int shopId) {
        this.shopId = shopId;
    }
    @Override
    public void run() {
        try {
            EsteiraCircularLoja esteiraLoja = new EsteiraCircularLoja(shopId);
            BlockingQueue<String> factoryResponses = new LinkedBlockingQueue<>();
            Gson gson = new Gson();
            String factoryUri = "ws://localhost:8080";
            ShopFactoryClient factoryClient = new ShopFactoryClient(new URI(factoryUri), factoryResponses);
            factoryClient.connectBlocking();
            Thread requestThread = new Thread(() -> {
                try {
                    while (true) {
                        int posicaoLoja = esteiraLoja.getProximaPosicao();
                        JsonObject request = new JsonObject();
                        request.addProperty("type", "GET_CAR");
                        request.addProperty("shopId", shopId);
                        request.addProperty("posicaoLoja", posicaoLoja);
                        factoryClient.send(request.toString());
                        String response = factoryResponses.take();
                        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                        String type = jsonResponse.get("type").getAsString();
                        if (type.equals("CAR_DATA")) {
                            models.Carro carro = gson.fromJson(jsonResponse.get("carro"), models.Carro.class);
                            esteiraLoja.depositarCarro(carro);
                        }
                        Thread.sleep(500);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            });
            requestThread.start();
            int shopPort = 9000 + shopId;
            ShopWebSocketServer shopServer = new ShopWebSocketServer(shopId, shopPort, esteiraLoja);
            shopServer.start();
            System.out.println("Shop " + shopId + " started on port " + shopPort);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
