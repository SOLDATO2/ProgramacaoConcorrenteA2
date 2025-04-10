package models;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class EsteiraCircularLoja {
    private final Carro[] esteira;
    private int head;
    private int tail;
    private final int capacidade = 40; 
    private final Semaphore empty; // posições vazias
    private final Semaphore full;  // posições preenchidas
    private final Semaphore mutex; // exclusão mútua
    private final int idLoja;
    private Logger loggerRecebimento;
    private Logger loggerVenda;

    public EsteiraCircularLoja(int idLoja) {
        this.idLoja = idLoja;
        esteira = new Carro[capacidade];
        head = 0;
        tail = 0;
        empty = new Semaphore(capacidade);
        full = new Semaphore(0);
        mutex = new Semaphore(1);

        try {
            loggerRecebimento = new Logger("loja_" + idLoja + "_recebimento.log");
            loggerVenda = new Logger("loja_" + idLoja + "_venda.log");
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivos de log para Loja " + idLoja + ": " + e.getMessage());
        }
    }

    public void depositarCarro(Carro carro) throws InterruptedException {
        empty.acquire(); // espera por posição livre
        mutex.acquire();

        int posicao = tail;
        esteira[tail] = carro;
        tail = (tail + 1) % capacidade;

        // Atualiza a posição do carro na esteira da loja
        carro.setInfoVenda(idLoja, posicao);

        if (loggerRecebimento != null) {
            loggerRecebimento.logRecebimentoLoja(carro);
        }

        mutex.release();
        full.release(); // sinaliza que um carro foi adicionado

        System.out.println("Loja " + idLoja + " armazenou " + carro + " na posição " + posicao);
    }

    public Carro retirarCarro(int idCliente) throws InterruptedException, Exception {
        // Tenta adquirir uma permissão por até 5 segundos
        boolean acquired = full.tryAcquire(5, TimeUnit.SECONDS);
        if (!acquired) {
            throw new Exception("No stock available on shop conveyor");
        }
        mutex.acquire();

        Carro carro = esteira[head];
        esteira[head] = null; // Libera a referência para o GC
        int posicaoAtual = head;
        head = (head + 1) % capacidade;

        if (loggerVenda != null) {
            loggerVenda.logVendaLoja(carro, idCliente);
        }

        mutex.release();
        empty.release();

        System.out.println("Loja " + idLoja + " vendeu " + carro + " da posição " + posicaoAtual + " para o Cliente " + idCliente);
        return carro;
    }

    public int getProximaPosicao() {
        return tail;
    }

    public boolean isEmpty() {
        return full.availablePermits() == 0;
    }
}
