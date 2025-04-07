package models;
import models.Loja.Loja;
import java.util.List;
import java.util.Random;

public class Cliente implements Runnable {
    private int id;
    private List<Loja> lojas;
    private Garagem garagem;
    private Random random;

    public Cliente(int id, List<Loja> lojas) {
        this.id = id;
        this.lojas = lojas;
        this.garagem = new Garagem(id);
        this.random = new Random();
    }

    public int getId(){
        return id;
    }

    @Override
    public void run() {

        try {
            // Pequeno delay inicial para evitar que todos os clientes tentem comprar ao mesmo tempo
            Thread.sleep(random.nextInt(2000));
        
            while (true) {
                // quantidade de carros a comprar (por exemplo, entre 1 e 2)
                int quantidade = random.nextInt(2) + 1;
                
                // Compra a quantidade definida de carros
                for (int i = 0; i < quantidade; i++) {
                    // Escolhe aleatoriamente qual loja visitar conforme requisito IV
                    int lojaIndex = random.nextInt(lojas.size());
                    Loja loja = lojas.get(lojaIndex);
                    
                    System.out.println("Cliente " + id + " está tentando comprar um carro na Loja " + loja.getId());
                    
                    // Tenta comprar o carro (método bloqueante conforme requisito IV)
                    // Se a loja não tiver carros, o cliente espera
                    Carro carro = loja.venderCarro(this.id);
                    
                    // Adiciona o carro à garagem do cliente (conceito de garagem no requisito VI)
                    garagem.adicionarCarro(carro);
                    
                    System.out.println("Cliente " + id + " comprou " + carro + " na Loja " + loja.getId());
                    
                    // Pequena pausa entre compras
                    Thread.sleep(random.nextInt(1000) + 500);
                }

                // Aguarda um tempo aleatório antes da próxima compra
                Thread.sleep(3000 + random.nextInt(2000));
            }

        } catch (InterruptedException e) {
            System.out.println("Cliente " + id + " foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }
}