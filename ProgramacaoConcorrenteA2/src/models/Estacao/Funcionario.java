package models.Estacao;

import java.util.concurrent.Semaphore;

import models.Estoque;
import models.Carro;

public class Funcionario implements Runnable{
    private int id;
    private Semaphore ferramentaEsquerda = null;
    private Semaphore ferramentaDireita = null;
    private int idMesa;
    private Estoque estoque;
    private EsteiraCircular esteiraCircular;

    public Funcionario(
    int id, 
    int idMesa, 
    Estoque estoque, 
    Semaphore ferramentaEsquerda, 
    Semaphore ferramentaDireita,
    EsteiraCircular esteiraCircular){
        this.id = id;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
        this.idMesa = idMesa;
        this.estoque = estoque;
        this.esteiraCircular = esteiraCircular;
    }

    private void pensar() throws InterruptedException{
       
        System.out.println("O Funcionario nr: "+this.id+" da MESA "+ this.idMesa +" vai pensar um pouco...");
        Thread.sleep(2000);
        System.out.println("O Funcionario nr: "+this.id+" da MESA "+ this.idMesa +"pensou!!!");
    }

    private Carro trabalhar() throws InterruptedException{ //equivalente a comer
  
        System.out.println("O Funcionario nr: "+this.id+" da MESA "+ this.idMesa +" tem 2 ferramentas e vai trabalhar!!");
        Thread.sleep(2000);
        Carro carro = new Carro("Carro");
        System.out.println("O Funcionario nr: "+this.id+" da MESA "+ this.idMesa +" produziu um carro e vai devolver as ferramentas!!!");
        
        return carro;
    }

    public void run(){

        try{
            while(true){
                pensar();
                
                if(id<4){
                    estoque.coletarItem(this.id, this.idMesa);
                    ferramentaEsquerda.acquire();
                    ferramentaDireita.acquire();
                }else{
                    estoque.coletarItem(this.id, this.idMesa);
                    ferramentaDireita.acquire();
                    ferramentaEsquerda.acquire(); 
                }
                Carro carro = trabalhar();
                ferramentaEsquerda.release();
                ferramentaDireita.release();
                
                // enviar para esteira circular
                esteiraCircular.depositarCarro(this.id, this.idMesa, carro);
            }
 
 
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

}
