package com.example.factory;

import java.net.InetSocketAddress;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.Carro;

public class FactoryWebSocketServer extends WebSocketServer {
    private Gson gson = new Gson();
    
    public FactoryWebSocketServer(InetSocketAddress address) {
        super(address);
    }
    
    @Override
    public void onStart() {
        System.out.println("Fábrica WebSocket iniciou");
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Loja conectou: " + conn.getRemoteSocketAddress());
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Conexão fechou: " + conn.getRemoteSocketAddress());
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();
            if (type.equals("GET_CAR")) {
                int shopId = json.get("shopId").getAsInt();
                int posicaoLoja = json.get("posicaoLoja").getAsInt();
                Carro carro = FactoryTask.esteiraProducao.retirarCarro(shopId, posicaoLoja);
                JsonObject response = new JsonObject();
                response.addProperty("type", "CAR_DATA");
                response.add("carro", gson.toJsonTree(carro));
                conn.send(response.toString());
                System.out.println("Sent " + carro + " to Shop " + shopId);
            }
        } catch (Exception e) {
            System.err.println("Error in FactoryWebSocketServer: " + e.getMessage());
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error in FactoryWebSocketServer: " + ex.getMessage());
    }
}
