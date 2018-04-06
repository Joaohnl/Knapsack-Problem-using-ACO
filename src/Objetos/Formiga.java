/**
 * Bruno Alejandro
   Felipe Meireles
   Fernando Napoli 
   João Henrique 
   Lucas de Mauro 
   Robson do Nascimento
 * 
 * 
 * Classe que representa uma formiga do algoritmo de colônia de formigas.
 */
package Objetos;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author João Loureiro
 */
public class Formiga {
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

    public void toString(JTextArea areaTexto) {
        for (Item item : itens) {
            areaTexto.append(item.toString());
        }
        areaTexto.append("\nA: " + valorObtido + ", " + "P: " + pesoCarregado + ", "+ "V: " + volumeCarregado + "\n");
    }
    
    
}
