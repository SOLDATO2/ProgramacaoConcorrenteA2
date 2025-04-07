package models;

public class Carro{
    private static int contadorCarros = 0;
    private static final Object lock = new Object(); // Para sincronizar o acesso ao contador

    private int id;
    private String cor;
    private String tipo;
    private int idEstacaoProdutora;
    private int idFuncionario;
    private int posicaoEsteiraFabrica;
    private int idLoja;
    private int posicaoEsteiraLoja;

    public Carro(int idEstacaoProdutora, int idFuncionario, int posicaoEsteira) {
        synchronized (lock) {
            this.id = ++contadorCarros;
        }
        // Alterna entre as cores RGB conforme requisito V
        switch (id % 3) {
            case 0:
                this.cor = "Vermelho";
                break;
            case 1:
                this.cor = "Verde";
                break;
            case 2:
                this.cor = "Azul";
                break;
        }

        // Alterna entre SUV ou SEDAN conforme requisito V
        this.tipo = (id % 2 == 0) ? "SUV" : "SEDAN";

        this.idEstacaoProdutora = idEstacaoProdutora;
        this.idFuncionario = idFuncionario;
        this.posicaoEsteiraFabrica = posicaoEsteira;
    }

    public int getId() {
        return this.id;
    }

    public String getCor() {
        return this.cor;
    }

    public String getTipo() {
        return this.tipo;
    }

    public int getIdEstacaoProdutora() {
        return this.idEstacaoProdutora;
    }

    public int getIdFuncionario() {
        return this.idFuncionario;
    }

    public int getPosicaoEsteiraFabrica() {
        return this.posicaoEsteiraFabrica;
    }
    
    public void setPosicaoEsteiraFabrica(int posicao) {
        this.posicaoEsteiraFabrica = posicao;
    }

    public void setInfoVenda(int idLoja, int posicaoEsteiraLoja) {
        this.idLoja = idLoja;
        this.posicaoEsteiraLoja = posicaoEsteiraLoja;
    }

    public int getIdLoja() {
        return this.idLoja;
    }

    public int getPosicaoEsteiraLoja() {
        return this.posicaoEsteiraLoja;
    }

    @Override
    public String toString() {
        return tipo + " " + cor + " (ID: " + id + ")";
    }
}