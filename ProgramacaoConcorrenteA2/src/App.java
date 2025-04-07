import java.util.Vector;
import java.util.concurrent.Semaphore;
import models.Estoque;
import models.Estacao.EsteiraCircular;
import models.Estacao.Funcionario;
import models.Estacao.Mesa;
import java.util.List;
import java.util.ArrayList;
import models.Loja.Loja;
import models.Cliente;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando simulação de cadeia de produção de veículos");

        Estoque estoque = new Estoque();

        //cria a esteira circular compartilhada com todas as mesas (capacidade 40 veiculos)
        EsteiraCircular esteiraProducao = new EsteiraCircular();

        //criação das estações produtoras(mesas)
        //total de 4 estações com 1 mesa e 5 fucnionaros por mesa
        Vector<Mesa> estacoesProdutoras = new Vector<>();
        int totalEstacoes = 4;
        int totalFuncionarios = 5;

        for (int i = 0; i < totalEstacoes; i++) {
            Semaphore[] ferramentas = new Semaphore[totalFuncionarios];
            for (int y = 0; y < totalFuncionarios; y++) {
                ferramentas[y] = new Semaphore(1);
            }
            Mesa mesa = new Mesa(i, estoque);

            //funcinarios recebem as ferramentas
            for (int z = 0; z < totalFuncionarios; z++) {
                Semaphore ferramentaEsquerda = ferramentas[z];
                Semaphore ferramentaDireita = ferramentas[(z + 1) % totalFuncionarios];
                Funcionario funcionario = new Funcionario(
                        z,
                        mesa.getId(),
                        mesa.getEstoque(),
                        ferramentaEsquerda,
                        ferramentaDireita,
                        esteiraProducao
                );
                mesa.adicionarFuncionario(funcionario);
            }
            estacoesProdutoras.add(mesa);
        }

        //inicia os funcionários das mesas de produção
        for (Mesa mesa : estacoesProdutoras) {
            mesa.iniciarFuncionarios();
        }

        //lista com um unico elemento para a esteira de produção compartilhada
        List<EsteiraCircular> esteirasProducao = new ArrayList<>();
        esteirasProducao.add(esteiraProducao);

        //criar e iniciar as lojas (3 lojas)
        List<Loja> lojas = new ArrayList<>();
        List<Thread> threadsLojas = new ArrayList<>();
        int totalLojas = 3; // Conforme requisito III
        for (int i = 0; i < totalLojas; i++) {
            Loja loja = new Loja(i, esteirasProducao);
            lojas.add(loja);
            Thread threadLoja = new Thread(loja);
            threadsLojas.add(threadLoja);
            threadLoja.start();
        }

        Thread.sleep(2000);
        //cria e inicia os clientes (20 clientes)
        List<Thread> threadsClientes = new ArrayList<>();
        int totalClientes = 20; // Conforme requisito IV
        for (int i = 0; i < totalClientes; i++) {
            Cliente cliente = new Cliente(i, lojas);
            Thread threadCliente = new Thread(cliente);
            threadsClientes.add(threadCliente);
            threadCliente.start();
        }

        System.out.println("Simulação iniciada com sucesso!");
        System.out.println("Pressione Ctrl+C para encerrar a simulação");
    }
}