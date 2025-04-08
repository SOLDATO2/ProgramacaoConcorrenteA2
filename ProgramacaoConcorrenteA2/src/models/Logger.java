package models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final String arquivo;
    private final PrintWriter printWriter;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_DIRECTORY = "Loggers";

    public Logger(String nomeArquivo) throws IOException {
        // Cria o diretório Loggers se não existir
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (!created) {
                System.err.println("Não foi possível criar o diretório de logs: " + LOG_DIRECTORY);
            }
        }
        
        // Caminho completo para o arquivo
        this.arquivo = LOG_DIRECTORY + File.separator + nomeArquivo;
        FileWriter fileWriter = new FileWriter(arquivo, true);
        this.printWriter = new PrintWriter(fileWriter, true);
    }

    // Log de produção da fábrica conforme requisito V
    public void logProducao(Carro carro) {
        String mensagem = String.format("[%s] PRODUÇÃO - ID: %d, Tipo: %s, Cor: %s, Estação: %d, Funcionário: %d, Posição Esteira: %d",
                LocalDateTime.now().format(formatter),
                carro.getId(),
                carro.getTipo(),
                carro.getCor(),
                carro.getIdEstacaoProdutora(),
                carro.getIdFuncionario(),
                carro.getPosicaoEsteiraFabrica());

        printWriter.println(mensagem);
        // Removido System.out.println(mensagem);
    }

    // Log de venda da fábrica para a loja conforme requisito V
    public void logVendaFabrica(Carro carro) {
        String mensagem = String.format("[%s] VENDA FÁBRICA - ID: %d, Tipo: %s, Cor: %s, Estação: %d, Funcionário: %d, Posição Esteira Fábrica: %d, Loja: %d, Posição Esteira Loja: %d",
                LocalDateTime.now().format(formatter),
                carro.getId(),
                carro.getTipo(),
                carro.getCor(),
                carro.getIdEstacaoProdutora(),
                carro.getIdFuncionario(),
                carro.getPosicaoEsteiraFabrica(),
                carro.getIdLoja(),
                carro.getPosicaoEsteiraLoja());
        
        printWriter.println(mensagem);
        // Removido System.out.println(mensagem);
    }

    // Log de recebimento de carros pela loja conforme requisito VI
    public void logRecebimentoLoja(Carro carro) {
        String mensagem = String.format("[%s] RECEBIMENTO LOJA %d - ID: %d, Tipo: %s, Cor: %s, Estação: %d, Funcionário: %d, Posição Esteira Loja: %d",
                LocalDateTime.now().format(formatter),
                carro.getIdLoja(),
                carro.getId(),
                carro.getTipo(),
                carro.getCor(),
                carro.getIdEstacaoProdutora(),
                carro.getIdFuncionario(),
                carro.getPosicaoEsteiraLoja());
        
        printWriter.println(mensagem);
        // Removido System.out.println(mensagem);
    }

    // Log de venda da loja para o cliente conforme requisito VI
    public void logVendaLoja(Carro carro, int idCliente) {
        String mensagem = String.format("[%s] VENDA LOJA %d - ID: %d, Tipo: %s, Cor: %s, Estação: %d, Funcionário: %d, Posição Esteira Loja: %d, Cliente: %d",
                LocalDateTime.now().format(formatter),
                carro.getIdLoja(),
                carro.getId(),
                carro.getTipo(),
                carro.getCor(),
                carro.getIdEstacaoProdutora(),
                carro.getIdFuncionario(),
                carro.getPosicaoEsteiraLoja(),
                idCliente);
        
        printWriter.println(mensagem);
        // Removido System.out.println(mensagem);
    }

    public void fechar() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}