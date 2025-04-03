package models;
import models.Loja.Loja;
import java.util.List;
import java.util.Random;

public class Cliente implements Runnable {
    private int id;
    private List<Loja> lojas;

    public Cliente(int id, List<Loja> lojas) {
        this.id = id;
        this.lojas = lojas;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                // quantidade de carros a comprar (por exemplo, entre 1 e 2)
                int quantidade = random.nextInt(2) + 1;
                // seleciona aleatoriamente uma loja
                int lojaIndex = random.nextInt(lojas.size());
                Loja loja = lojas.get(lojaIndex);

                System.out.println("Cliente " + id + " deseja comprar " + quantidade + " carro(s) na Loja " + loja.getId());

                for (int i = 0; i < quantidade; i++) {
                    // o metodo é bloqueado caso não haja carros disponivel
                    Carro carro = loja.venderCarro();
                    System.out.println("Cliente " + id + " comprou um " + carro.getNome() + " na Loja " + loja.getId());
                }

                // Aguarda um tempo aleatiorio antes da próxima compra
                Thread.sleep(3000 + random.nextInt(2000));
            } catch (InterruptedException e) {
                System.out.println("Cliente " + id + " foi interrompido.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
