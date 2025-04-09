package models;

import java.util.ArrayList;
import java.util.List;

public class Mesa {
    private int id;
    private List<Thread> threadsFuncionarios;
    private Estoque estoque;

    public Mesa(int id, Estoque estoque) {
        this.id = id;
        this.estoque = estoque;
        threadsFuncionarios = new ArrayList<>();
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        Thread thread = new Thread(funcionario);
        threadsFuncionarios.add(thread);
    }

    public void iniciarFuncionarios() {
        for (Thread t : threadsFuncionarios) {
            t.start();
        }
    }
    
    public void interromperFuncionarios() {
        for (Thread t: threadsFuncionarios) {
            t.interrupt();
        }
    }

    public int getId() {
        return id;
    }

    public Estoque getEstoque(){
        return estoque;
    }
}