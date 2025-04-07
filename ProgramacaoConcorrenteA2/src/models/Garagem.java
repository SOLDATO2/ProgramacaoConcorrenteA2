package models;

import java.util.ArrayList;
import java.util.List;

public class Garagem {
    private final int idCliente;
    private final List<Carro> carros;

    public Garagem(int idCliente) {
        this.idCliente = idCliente;
        this.carros = new ArrayList<>();
    }

    public void adicionarCarro(Carro carro) {
        carros.add(carro);
        System.out.println("Cliente " + idCliente + " adicionou " + carro + " à sua garagem. Total: " + carros.size());
    }

    public List<Carro> getCarros() {
        return new ArrayList<>(carros);
    }

    public int getQuantidadeCarros() {
        return carros.size();
    }
}
