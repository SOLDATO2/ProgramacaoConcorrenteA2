package models.Loja;
import models.Estacao.EsteiraCircular;
import models.Carro;

import java.util.List;
import java.util.Random;

public class Loja implements Runnable {
    private int id;
    private EsteiraCircular esteiraProducao; //  esteira de produção da fábrica
    private EsteiraCircularLoja esteiraLoja; // esteira da loja para armazenar os carros
    private Random random;

    // A loja recebe uma lista de esteiras da fábrica
    public Loja(int id, List<EsteiraCircular> esteirasProducao) {
        this.id = id;
        this.esteiraProducao = esteirasProducao.get(0);
        this.esteiraLoja = new EsteiraCircularLoja(id);
        this.random = new Random();
    }

    public int getId() {
        return id;
    }

    // Método utilizado pelos clientes para comprar um carro na loja.
    // Se não houver carro disponível, a thread fica bloqueada até que haja.
    public Carro venderCarro(int idCliente) throws InterruptedException {
        return esteiraLoja.retirarCarro(idCliente);
    }

    @Override
    public void run() {
        try {
            while (true) {

                int posicaoLoja = esteiraLoja.getProximaPosicao();
                Carro carro = esteiraProducao.retirarCarro(this.id, posicaoLoja);

                System.out.println("Loja " + id + " comprou " + carro + " da fábrica");

                esteiraLoja.depositarCarro(carro);

                Thread.sleep(500 + random.nextInt(500));
            }
        } catch (InterruptedException e) {
            System.out.println("Loja " + id + " foi interrompida.");
            Thread.currentThread().interrupt();
        }
    }
}