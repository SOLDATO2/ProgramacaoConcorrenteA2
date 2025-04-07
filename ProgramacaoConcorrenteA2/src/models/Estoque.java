package models;

import java.util.concurrent.Semaphore;

public class Estoque { // Equivalente a um buffer com 500 unidades
    
    private final int[] buffer;
    private int index = 500; // Inicia com 500 unidades conforme requisito I

    private final Semaphore full;    // Controla as peças disponíveis
    private final Semaphore mutex;   // Exclusão mútua para acesso ao buffer
    private final Semaphore limiter; // Limitador para a esteira que atende até 5 solicitações por vez (requisito I)

    public Estoque() {
        buffer = new int[index]; // Tamanho do buffer conforme requisito I
        full = new Semaphore(index); // Inicia com 500 peças disponíveis
        limiter = new Semaphore(5);  // Esteira atende até 5 solicitações por vez conforme requisito I
        mutex = new Semaphore(1);    // Mutex para garantir exclusão mútua
    }

    public void coletarItem(int idFuncionario, int idMesa) throws InterruptedException {
        limiter.acquire(); // Limita a 5 solicitações simultâneas na esteira conforme requisito I
        full.acquire();    // Verifica se há peças disponíveis
        mutex.acquire();   // Entra na região crítica
        
        index--;           // Decrementa o índice (consome uma peça)
        buffer[index] = 0; // Marca a posição como vazia

        if (index == 0) {
            System.out.println("Mesa: " + idMesa + " Funcionario: " + idFuncionario + " Coletou o último item do Estoque..");
        }

        mutex.release(); // Sai da região crítica
        limiter.release(); // Libera a esteira para atender outras solicitações
        
        System.out.println("Mesa: " + idMesa + " Funcionario: " + idFuncionario + " coletou um item do estoque, Items restantes: " + index);
    }
}