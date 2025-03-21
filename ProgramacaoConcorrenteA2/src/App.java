import java.util.Vector;
import java.util.concurrent.Semaphore;

import models.Estoque;
import models.Estacao.EsteiraCircular;
import models.Estacao.Funcionario;
import models.Estacao.Mesa;

public class App {
    public static void main(String[] args) throws Exception {
        
        Estoque estoque = new Estoque();


        // total 4 estacoes com 1 mesa e 5 funcionarios por mesa
        Vector<Mesa> estacoesProdutoras = new Vector<Mesa>();
        int totalEstacoes = 4;
        int totalFuncionarios = 5;

        for (int i = 0; i < totalEstacoes; i++) {
            EsteiraCircular esteiraCircular = new EsteiraCircular();
            Semaphore[] ferramentas = new Semaphore[totalFuncionarios];
            for (int y = 0; y < totalFuncionarios; y++) {
                ferramentas[y] = new Semaphore(1);
            }
            Mesa mesa = new Mesa(i, estoque);

            // funcionarios recebem as ferramentas
            for (int z = 0; z < totalFuncionarios; z++) { // z = ferramenta
                Semaphore ferramentaEsquerda = ferramentas[z];
                Semaphore ferramentaDireita = ferramentas[(z + 1) % totalFuncionarios];
                Funcionario funcionario = new Funcionario(z, mesa.getId(), mesa.getEstoque(), ferramentaEsquerda, ferramentaDireita, esteiraCircular);
                mesa.adicionarFuncionario(funcionario);
            }
            estacoesProdutoras.add(mesa);
        }
        




        for (Mesa mesa : estacoesProdutoras) {
            mesa.iniciarFuncionarios();
        }

    }
}
