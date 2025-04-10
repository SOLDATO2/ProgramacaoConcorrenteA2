package models;

import java.util.concurrent.Semaphore;

public class Funcionario implements Runnable {
    private int id;
    private Semaphore ferramentaEsquerda;
    private Semaphore ferramentaDireita;
    private int idMesa;
    private Estoque estoque;
    private EsteiraCircular esteiraProducao;
    private Semaphore butler;

    public Funcionario(int id, int idMesa, Estoque estoque, Semaphore ferramentaEsquerda, Semaphore ferramentaDireita,
                       EsteiraCircular esteiraProducao, Semaphore butler) {
        this.id = id;
        this.idMesa = idMesa;
        this.estoque = estoque;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
        this.esteiraProducao = esteiraProducao;
        this.butler = butler;
    }

    private void pensar() throws InterruptedException {
        System.out.println("O Funcionario nr: " + id + " da Estação " + idMesa + " está pensando...");
        Thread.sleep(2000);
        System.out.println("O Funcionario nr: " + id + " da Estação " + idMesa + " pensou!!!");
    }

    private Carro trabalhar() throws InterruptedException {
        System.out.println("O Funcionario nr: " + id + " da Estação " + idMesa + " está trabalhando com as duas ferramentas!");
        Thread.sleep(2000);
        Carro carro = new Carro(idMesa, id, -1);
        System.out.println("O Funcionario nr: " + id + " da Estação " + idMesa + " produziu um " + carro + " e vai devolver as ferramentas!!!");
        return carro;
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();
                try {
                    estoque.coletarItem(id, idMesa);
                } catch (OutOfStockException e) {
                    System.out.println("Estoque esgotado na fábrica. Funcionário " + id + " da Estação " + idMesa);
                    // Não finaliza o serviço; aguarda um tempo e tenta novamente
                    Thread.sleep(2000);
                    continue;
                }
                butler.acquire();
                if (id % 2 == 0) {
                    ferramentaEsquerda.acquire();
                    ferramentaDireita.acquire();
                } else {
                    ferramentaDireita.acquire();
                    ferramentaEsquerda.acquire();
                }
                Carro carro = trabalhar();
                if (id % 2 == 0) {
                    ferramentaDireita.release();
                    ferramentaEsquerda.release();
                } else {
                    ferramentaEsquerda.release();
                    ferramentaDireita.release();
                }
                butler.release();
                esteiraProducao.depositarCarro(carro);
            }
        } catch (InterruptedException e) {
            System.out.println("Funcionario " + id + " da Estação " + idMesa + " foi interrompido");
            Thread.currentThread().interrupt();
        }
    }
}
