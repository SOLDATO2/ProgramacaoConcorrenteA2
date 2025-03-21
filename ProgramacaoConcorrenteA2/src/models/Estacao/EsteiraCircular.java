package models.Estacao;

import java.util.concurrent.Semaphore;
import models.Carro;

public class EsteiraCircular {
    private final Carro[] esteira;
    private int head;   // índice para retirada
    private int tail;   // índice para inserção
    private final int capacidade = 40;
    private final Semaphore empty; // posições vazias
    private final Semaphore full;  // posições preenchidas
    private final Semaphore mutex; // exclusão mútua

    public EsteiraCircular() {
        esteira = new Carro[capacidade];
        head = 0;
        tail = 0;
        empty = new Semaphore(capacidade); // inicialmente todas as posições estão vazias
        full = new Semaphore(0);             // nenhum carro para retirada inicialmente
        mutex = new Semaphore(1);
    }

    public void depositarCarro(int idFuncionario, int idMesa,Carro carro) throws InterruptedException {
        System.out.println("Mesa: "+idMesa+" Funcionario: "+idFuncionario+" depositou um carro na esteira na mesa" + idMesa);
        empty.acquire(); // aguarda posição vazia
        mutex.acquire(); // seção crítica

        esteira[tail] = carro;
        tail = (tail + 1) % capacidade;

        mutex.release(); // finaliza seção crítica
        full.release();  // sinaliza que há um novo carro disponível
    }

    public Carro retirarCarro() throws InterruptedException {
        full.acquire(); // aguarda existência de um carro
        mutex.acquire(); // seção crítica

        Carro carro = esteira[head];
        head = (head + 1) % capacidade;

        mutex.release(); // finaliza seção crítica
        empty.release(); // sinaliza que uma posição ficou livre
        return carro;
    }
}