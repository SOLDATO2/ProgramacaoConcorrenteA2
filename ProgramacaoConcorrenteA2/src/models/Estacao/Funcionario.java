package models.Estacao;

import java.util.concurrent.Semaphore;
import models.Estoque;
import models.Carro;

public class Funcionario implements Runnable {
    private int id;
    private Semaphore ferramentaEsquerda;
    private Semaphore ferramentaDireita;
    private int idMesa;
    private Estoque estoque;
    private EsteiraCircular esteiraCircular; // Esteira comum compartilhada entre todas as mesas

    public Funcionario(int id, int idMesa, Estoque estoque, Semaphore ferramentaEsquerda, Semaphore ferramentaDireita, EsteiraCircular esteiraCircular) {
        this.id = id;
        this.idMesa = idMesa;
        this.estoque = estoque;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
        this.esteiraCircular = esteiraCircular;
    }

    private void pensar() throws InterruptedException {
        System.out.println("O Funcionario nr: " + this.id + " da MESA " + this.idMesa + " vai pensar um pouco...");
        Thread.sleep(2000);
        System.out.println("O Funcionario nr: " + this.id + " da MESA " + this.idMesa + " pensou!!!");
    }

    private Carro trabalhar() throws InterruptedException {
        System.out.println("O Funcionario nr: " + this.id + " da MESA " + this.idMesa + " tem 2 ferramentas e vai trabalhar!!");
        Thread.sleep(2000);
        Carro carro = new Carro("Carro");
        System.out.println("O Funcionario nr: " + this.id + " da MESA " + this.idMesa + " produziu um carro e vai devolver as ferramentas!!!");
        return carro;
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();

                // Alterna a ordem de aquisição das ferramentas para prevenir deadlock
                if (id < 4) {
                    estoque.coletarItem(this.id, this.idMesa);
                    ferramentaEsquerda.acquire();
                    ferramentaDireita.acquire();
                } else {
                    estoque.coletarItem(this.id, this.idMesa);
                    ferramentaDireita.acquire();
                    ferramentaEsquerda.acquire();
                }

                Carro carro = trabalhar();
                ferramentaEsquerda.release();
                ferramentaDireita.release();

                // Deposita o carro produzido na esteira de produção comum
                esteiraCircular.depositarCarro(this.id, this.idMesa, carro);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}