package com.example.factory;

import java.net.InetSocketAddress;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import models.Estoque;
import models.EsteiraCircular;
import models.Funcionario;
import models.Mesa;

public class FactoryTask implements Runnable {
    public static Estoque estoque;
    public static EsteiraCircular esteiraProducao;
    public static boolean estoqueEsgotado = false;

    @Override
    public void run() {
        estoque = new Estoque();
        esteiraProducao = new EsteiraCircular();
        Vector<Mesa> estacoesProdutoras = new Vector<>();

        int totalEstacoes = 4;
        int totalFuncionarios = 5;
        for (int i = 0; i < totalEstacoes; i++) {
            Semaphore[] ferramentas = new Semaphore[totalFuncionarios];
            for (int y = 0; y < totalFuncionarios; y++) {
                ferramentas[y] = new Semaphore(1);
            }

            Mesa mesa = new Mesa(i, estoque);
            Semaphore butler = new Semaphore(totalFuncionarios - 1);
            for (int z = 0; z < totalFuncionarios; z++) {
                Semaphore ferramentaEsquerda = ferramentas[z];
                Semaphore ferramentaDireita = ferramentas[(z + 1) % totalFuncionarios];
                Funcionario funcionario = new Funcionario(z, mesa.getId(), estoque, 
                        ferramentaEsquerda, ferramentaDireita, esteiraProducao, butler);
                mesa.adicionarFuncionario(funcionario);
            }
            estacoesProdutoras.add(mesa);
        }
        for (Mesa mesa : estacoesProdutoras) {
            mesa.iniciarFuncionarios();
        }
        int port = 8080;
        FactoryWebSocketServer server = new FactoryWebSocketServer(new InetSocketAddress(port));
        server.start();
        System.out.println("Fabrica iniciou na porta " + port);
    }
}
