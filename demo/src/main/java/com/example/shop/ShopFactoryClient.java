package com.example.shop;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class ShopFactoryClient extends WebSocketClient {
    private BlockingQueue<String> factoryResponses;
    public ShopFactoryClient(URI serverUri, BlockingQueue<String> factoryResponses) {
        super(serverUri);
        this.factoryResponses = factoryResponses;
    }
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to Factory");
    }
    @Override
    public void onMessage(String message) {
        try {
            factoryResponses.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Factory connection closed: " + reason);
    }
    @Override
    public void onError(Exception ex) {
        System.err.println("Error in ShopFactoryClient: " + ex.getMessage());
    }
}
