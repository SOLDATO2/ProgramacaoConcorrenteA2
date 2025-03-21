package models;

import java.util.concurrent.Semaphore;

public class Estoque{ //equivalente a um buffer
    
    private final int[] buffer;
    private int index = 500; // index começa em 500 para representar o buffer cheio

    //private final Semaphore empty;
    private final Semaphore full;
    private final Semaphore mutex;
    private final Semaphore limiter;

    public Estoque(){
        buffer = new int[index]; // tamanho do buffer
        full = new Semaphore(index); // inicia com 500 para representar que o buffer(estoque) está cheio
        limiter = new Semaphore(5);
        mutex = new Semaphore(1); // mutex para garantir exclusao mutua 
    }

    public void coletarItem(int idFuncionario, int idMesa) throws InterruptedException{

        limiter.acquire(); // limita a quantidade de itens que podem ser coletados
        full.acquire(); 
        mutex.acquire();
        //SECAO CRITICA
        index--; // decrementa o index
        buffer[index] = 0;

        if (index == 0) {
            System.out.println("Mesa: "+idMesa+" Funcionario: "+idFuncionario+" Coletou o ultimo item do Estoque..");
        }

        mutex.release(); // sai da região crítica
        //FIM SECAO CRITICA

        //full.release(); // não incrementa dnv pois o estoque é limitado
        limiter.release(); // libera a quantidade de itens que podem ser coletados
        System.out.println("Mesa: "+idMesa+" Funcionario: "+idFuncionario+" coletou um item do estoque, Items restantes: "+index);

        
    }
}
