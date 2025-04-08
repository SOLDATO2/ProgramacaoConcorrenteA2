package models.Fabrica;

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
        System.out.println("O Funcionario nr: " + this.id + " da Estação " + this.idMesa + " está pensando...");
        Thread.sleep(2000);
        System.out.println("O Funcionario nr: " + this.id + " da MESA " + this.idMesa + " pensou!!!");
    }

    private Carro trabalhar() throws InterruptedException {
        System.out.println("O Funcionario nr: " + this.id + " da Estação " + this.idMesa + " está trabalhando com as duas ferramentas!");
        Thread.sleep(2000);

        // O -1 será substituído pela posição correta quando o carro for depositado na esteira
        Carro carro = new Carro(this.idMesa, this.id, -1);
        System.out.println("O Funcionario nr: " + this.id + " da Estação " + this.idMesa + " produziu um " + carro +" e vai devolver as ferramentas!!!");
        return carro;
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();

                // Coleta peças do estoque
                estoque.coletarItem(this.id, this.idMesa);

                // Alterna a ordem de aquisição das ferramentas para prevenir deadlock
                if (id % 2 == 0) {
                    ferramentaEsquerda.acquire();
                    ferramentaDireita.acquire();
                } else {
                    ferramentaDireita.acquire();
                    ferramentaEsquerda.acquire();
                }

                Carro carro = trabalhar();
                
                // Libera as ferramentas na ordem inversa da aquisição
                if (id % 2 == 0) {
                    ferramentaDireita.release();
                    ferramentaEsquerda.release();
                } else {
                    ferramentaEsquerda.release();
                    ferramentaDireita.release();
                }

                // Deposita o carro produzido na esteira de produção comum
                esteiraCircular.depositarCarro(carro);
            }
        } catch (InterruptedException e) {
            System.out.println("Funcionário " + this.id + " da Estação " + this.idMesa + " foi interrompido");
            Thread.currentThread().interrupt();
        }
    }
}