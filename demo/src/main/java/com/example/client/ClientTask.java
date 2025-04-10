package com.example.client;

import java.net.URI;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

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
        this.garagem = new Vector<>();
    }
    
    // Cliente que se conecta a uma loja (via WebSocket) para comprar um carro.
    public class ShopClient extends WebSocketClient {
        private int clientId;
        private Gson gson = new Gson();
        // CountDownLatch para sinalizar que a conexão encerrou (após a tentativa de compra)
        private CountDownLatch latch = new CountDownLatch(1);
        
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
                garagem.add(carro); // Adiciona o carro à garagem do cliente
                System.out.println("Cliente " + clientId + " recebeu " + carro);
                close();
            } else if (type.equals("NO_STOCK")) {
                System.out.println("Cliente " + clientId + ": " + json.get("message").getAsString());
                close();
            }
        }
        
        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Cliente " + clientId + " desconectou");
            latch.countDown();
        }
        
        @Override
        public void onError(Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            latch.countDown();
        }
        
        public void sendBuyRequest() {
            JsonObject request = new JsonObject();
            request.addProperty("type", "BUY_CAR");
            request.addProperty("clientId", clientId);
            send(request.toString());
        }
        
        public void awaitClosure() throws InterruptedException {
            latch.await();
        }
    }
    
    @Override
    public void run() {

        while (true){
            try {
                int[] shopPorts = {9000, 9001, 9002};
                Random random = new Random();
                // Escolhe aleatoriamente se o cliente tentará comprar 1 ou 2 carros
                int carsToBuy = random.nextInt(2) + 1;
                System.out.println("Cliente " + clientId + " deseja comprar " + carsToBuy + " carro(s).");
                
                for (int i = 0; i < carsToBuy; i++) {
                    boolean purchased = false;
                    // Tenta comprar até obter o carro (caso a loja atual esteja sem estoque, ele tenta outra)
                    while (!purchased) {
                        int shopPort = shopPorts[random.nextInt(shopPorts.length)];
                        String shopUri = "ws://localhost:" + shopPort;
                        ShopClient client = new ShopClient(new URI(shopUri), clientId);
                        
                        int initialCount = garagem.size();
                        client.connectBlocking();
                        client.awaitClosure();  // Aguarda que a tentativa se conclua (compra ou não)
                        
                        // Se um carro foi adicionado, a compra foi bem sucedida
                        if (garagem.size() > initialCount) {
                            purchased = true;
                        } else {
                            System.out.println("Cliente " + clientId + " não conseguiu comprar carro na loja em " + shopUri + ", tentando novamente...");
                            // Aguarda um tempo aleatório antes de tentar outra loja
                            Thread.sleep(1000 + random.nextInt(1000));
                        }
                    }
                }
                System.out.println("Cliente " + clientId + " concluiu a compra de " + carsToBuy + " carro(s).");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Cliente " + clientId + " finalizou.");
            }
            System.out.println("Cliente " + clientId + " finalizou.");
        }
    }
}
