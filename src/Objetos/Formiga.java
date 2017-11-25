/**
 * Classe que representa uma formiga do algoritmo de colônia de formigas.
 */
package Objetos;

import java.util.ArrayList;
import java.util.List;
import Interface.DadosEntrada;

/**
 *
 * @author João Loureiro
 */
public class Formiga implements DadosEntrada {
    private List<Item> itens; //Mochila da Formiga
    private double valorObtido;
    private double pesoCarregado;
    private double volumeCarregado;
    
    public Formiga() {
        itens = new ArrayList<>();
        valorObtido = 0.0;
        pesoCarregado = 0.0;
        volumeCarregado = 0.0;
    }

    public List<Item> getItens() {
        return itens;
    }

    public void setItens(List<Item> itens) {
        this.itens = itens;
    }

    public double getValorObtido() {
        return valorObtido;
    }

    public void setValorObtido(double valorObtido) {
        this.valorObtido += valorObtido;
    }

    public double getPesoCarregado() {
        return pesoCarregado;
    }

    public void setPesoCarregado(double pesoCarregado) {
        this.pesoCarregado += pesoCarregado;
    }

    public double getVolumeCarregado() {
        return volumeCarregado;
    }

    public void setVolumeCarregado(double volumeCarregado) {
        this.volumeCarregado += volumeCarregado;
    }
    
    public double menorValor() {
        double menorValor = 0;
        for (Item item : itens) {
            if (menorValor == 0) {
                menorValor = item.getValor();
            } else if (menorValor > item.getValor()) {
                menorValor = item.getValor();
            }
        }
        return menorValor;
    }

    @Override
    public String toString() {
        System.out.println(itens);
        return "A: " + valorObtido + ", " + "P: " + pesoCarregado + ", "+ "V: " + volumeCarregado;
    }
    
    
}
