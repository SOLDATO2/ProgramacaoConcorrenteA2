package models;

import java.util.concurrent.Semaphore;

import java.io.IOException;

public class EsteiraCircular {
    private final Carro[] esteira;
    private int head;   // índice para retirada
    private int tail;   // índice para inserção
    private final int capacidade = 40;
    private final Semaphore empty; // posições vazias
    private final Semaphore full;  // posições preenchidas
    private final Semaphore mutex; // exclusão mútua
    private Logger loggerProducao;
    private Logger loggerVenda;

    public EsteiraCircular() {
        esteira = new Carro[capacidade];
        head = 0;
        tail = 0;
        empty = new Semaphore(capacidade); // inicialmente todas as posições estão vazias
        full = new Semaphore(0);           // nenhum carro para retirada inicialmente
        mutex = new Semaphore(1);
    
        try {
            loggerProducao = new Logger("fabrica_producao.log");
            loggerVenda = new Logger("fabrica_venda.log");
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivos de log: " + e.getMessage());
        }
    }

    public void depositarCarro(Carro carro) throws InterruptedException {
        empty.acquire(); // aguarda posição vazia
        mutex.acquire(); // seção crítica

        int posicao = tail;
        esteira[tail] = carro;
        // Atualiza a posição do carro na esteira
        carro.setPosicaoEsteiraFabrica(posicao);
        tail = (tail + 1) % capacidade;

        if (loggerProducao != null) {
            loggerProducao.logProducao(carro);
        }

        mutex.release(); // finaliza seção crítica
        full.release();  // sinaliza que há um novo carro disponível

        System.out.println("Estação " + carro.getIdEstacaoProdutora() +
                         " Funcionario " + carro.getIdFuncionario() +
                         " depositou um " + carro + " na esteira na posição " + posicao);
    }

    public Carro retirarCarro(int idLoja, int posicaoEsteiraLoja) throws InterruptedException {
        full.acquire(); // aguarda existência de um carro
        mutex.acquire(); // seção crítica

        Carro carro = esteira[head];
        esteira[head] = null; // Libera a referência para o GC
        head = (head + 1) % capacidade;

        carro.setInfoVenda(idLoja, posicaoEsteiraLoja);

        if(loggerVenda != null) {
            loggerVenda.logVendaFabrica(carro);
        }

        mutex.release(); // finaliza seção crítica
        empty.release(); // sinaliza que uma posição ficou livre
        return carro;
    }
}