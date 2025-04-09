package com.example.shop;

import java.net.InetSocketAddress;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.Carro;
import models.EsteiraCircularLoja;
import com.example.factory.FactoryTask;

public class ShopWebSocketServer extends WebSocketServer {
    private Gson gson = new Gson();
    private int shopId;
    private EsteiraCircularLoja esteiraLoja;
    public ShopWebSocketServer(int shopId, int port, EsteiraCircularLoja esteiraLoja) {
        super(new InetSocketAddress(port));
        this.shopId = shopId;
        this.esteiraLoja = esteiraLoja;
    }
    @Override
    public void onStart() {
        System.out.println("Shop WebSocket Server " + shopId + " started");
    }
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connected to Shop " + shopId + ": " + conn.getRemoteSocketAddress());
    }
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client disconnected from Shop " + shopId);
    }
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject json = gson.fromJson(message, JsonObject.class);
            String type = json.get("type").getAsString();
            if (type.equals("BUY_CAR")) {
                if (FactoryTask.estoqueEsgotado && esteiraLoja.isEmpty()) {
                    JsonObject resp = new JsonObject();
                    resp.addProperty("type", "NO_STOCK");
                    resp.addProperty("message", "Estoque da fábrica esgotado, nenhum carro disponível.");
                    // Verifica se a conexão está aberta antes de enviar
                    if (conn != null && conn.isOpen()) {
                        conn.send(resp.toString());
                    }
                } else {
                    Carro carro = esteiraLoja.retirarCarro(json.get("clientId").getAsInt());
                    JsonObject response = new JsonObject();
                    response.addProperty("type", "CAR_DATA");
                    response.add("carro", gson.toJsonTree(carro));
                    if (conn != null && conn.isOpen()) {
                        conn.send(response.toString());
                    }
                    System.out.println("Shop " + shopId + " sold " + carro + " to Client " + json.get("clientId").getAsInt());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in ShopWebSocketServer: " + e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error in ShopWebSocketServer: " + ex.getMessage());
    }
}
