package models.Loja;
import models.Estacao.EsteiraCircular;
import models.Carro;

import java.util.List;

public class Loja implements Runnable {
    private int id;
    private EsteiraCircular esteiraProducao; //  esteira de produção
    private EsteiraCircularLoja esteiraLoja;   // esteira da loja para armazenar os carros

    // a loja recebe uma lista de esteiras
    public Loja(int id, List<EsteiraCircular> esteirasProducao) {
        this.id = id;
        this.esteiraProducao = esteirasProducao.get(0);
        this.esteiraLoja = new EsteiraCircularLoja();
    }

    public int getId() {
        return id;
    }

    // metodo utilizado pelos clientes para comprar um carro na loja.
    // se nao houver carro disponivel, a thread fica bloqueada até que haja.
    public Carro venderCarro() throws InterruptedException {
        Carro carro = esteiraLoja.retirarCarro();
        System.out.println("Loja " + id + " vendeu um " + carro.getNome());
        return carro;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //a loja retira um carro da esteira de producao
                Carro carro = esteiraProducao.retirarCarro();
                System.out.println("Loja " + id + " comprou um " + carro.getNome() + " da fábrica.");

                // deposita o carro na sua esteira interna
                esteiraLoja.depositarCarro(carro);

                // pausa para simular o processamento
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Loja " + id + " foi interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}