package models.Loja;

import models.Carro;
import java.util.concurrent.Semaphore;

//MESMO CODIGO DA ESTEIRA CIRCULAR DA FABRICA
public class EsteiraCircularLoja {
    private final Carro[] esteira;
    private int head;
    private int tail;
    private final int capacidade = 40; // capacidade pode ser ajustada se necessário
    private final Semaphore empty; // posições vazias
    private final Semaphore full; // posições preenchidas
    private final Semaphore mutex; // exclusão mútua

    public EsteiraCircularLoja() {
        esteira = new Carro[capacidade];
        head = 0;
        tail = 0;
        empty = new Semaphore(capacidade);
        full = new Semaphore(0);
        mutex = new Semaphore(1);
    }

    public void depositarCarro(Carro carro) throws InterruptedException {
        empty.acquire(); // espera por posição livre
        mutex.acquire();
        esteira[tail] = carro;
        tail = (tail + 1) % capacidade;
        mutex.release();
        full.release(); // sinaliza que um carro foi adicionado
    }

    public Carro retirarCarro() throws InterruptedException {
        full.acquire(); // espera por um carro disponível
        mutex.acquire();
        Carro carro = esteira[head];
        head = (head + 1) % capacidade;
        mutex.release();
        empty.release();
        return carro;
    }
}
