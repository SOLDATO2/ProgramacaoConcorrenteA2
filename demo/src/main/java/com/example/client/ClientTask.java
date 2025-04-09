package com.example.client;

import java.net.URI;
import java.util.Random;
import java.util.Vector;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.Carro;

public class ClientTask implements Runnable {
    private int clientId;
    private Vector<Carro> garagem;
    public ClientTask(int clientId) {
        this.clientId = clientId;
    }
    public class ShopClient extends WebSocketClient {
        private int clientId;
        private Gson gson = new Gson();
        public ShopClient(URI serverUri, int clientId) {
            super(serverUri);
            this.clientId = clientId;
        }
        @Override
        public void onOpen(ServerHandshake handshake) {
            System.out.println("Cliente " + clientId + " conectou");
            sendBuyRequest();
        }

        @Override
        public void onMessage(String message) {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();
            if (type.equals("CAR_DATA")) {
                Carro carro = gson.fromJson(json.get("carro"), Carro.class);
                garagem.add(carro); //adiciona carro
                System.out.println("Cliente " + clientId + " recebeu " + carro);
            } else if (type.equals("NO_STOCK")) {
                System.out.println("Cliente " + clientId + ": " + json.get("message").getAsString());
                close();
            }
        }
        

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Cliente " + clientId + " desconectou");
        }
        @Override
        public void onError(Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        public void sendBuyRequest() {
            JsonObject request = new JsonObject();
            request.addProperty("type", "BUY_CAR");
            request.addProperty("clientId", clientId);
            send(request.toString());
        }
    }
    @Override
    public void run() {
        try {
            int[] shopPorts = {9000, 9001, 9002};
            Random random = new Random();
            int shopPort = shopPorts[random.nextInt(shopPorts.length)];
            String shopUri = "ws://localhost:" + shopPort;
            ShopClient client = new ShopClient(new URI(shopUri), clientId);
            client.connectBlocking();
            while (client.isOpen()) {  // executa enquanto a conexão estiver ativa
                client.sendBuyRequest();
                Thread.sleep(3000 + random.nextInt(2000));
            }
            System.out.println("Client " + clientId + " encerrou as tentativas, pois não há mais estoque.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
