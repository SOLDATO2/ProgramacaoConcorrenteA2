package models;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Estoque {
    private final int[] buffer;
    private int index = 500;
    private final Semaphore full;
    private final Semaphore mutex;
    private final Semaphore limiter;

    public Estoque() {
        buffer = new int[index];
        full = new Semaphore(index);
        limiter = new Semaphore(5);
        mutex = new Semaphore(1);
    }

    public void coletarItem(int idFuncionario, int idMesa) throws InterruptedException, OutOfStockException {
        if (!limiter.tryAcquire(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout no limiter");
        }
        if (!full.tryAcquire(5, TimeUnit.SECONDS)) {
            limiter.release();
            throw new OutOfStockException("Estoque da fábrica esgotado");
        }
        mutex.acquire();
        index--;
        buffer[index] = 0;
        if (index == 0) {
            System.out.println("Mesa: " + idMesa + " Funcionario: " + idFuncionario + " Coletou o último item do Estoque..");
        }
        mutex.release();
        limiter.release();
        System.out.println("Mesa: " + idMesa + " Funcionario: " + idFuncionario + " coletou um item do estoque, Items restantes: " + index);
    }

    public int getRemainingStock() {
        return index;
    }
}
