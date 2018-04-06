/**
 * Bruno Alejandro
   Felipe Meireles
   Fernando Napoli 
   João Henrique 
   Lucas de Mauro 
   Robson do Nascimento 
 * 
 * Esta classe representa um Item que pode ser adicionado à mochila.
 */
package Objetos;

/**
 *
 * @author João Loureiro
 */
public class Item {
    private int ID;
    private String descricao;
    private double valor;
    private double peso;
    private double volume;
    
    private double feromonio;
    private double influenciaPesoVolume;
    private double probabilidadeEscolhido;

    public Item(int ID, String descricao, double valor, double peso, double volume, double feromonio) {
        this.ID = ID;
        this.descricao = descricao;
        this.valor = valor;
        this.peso = peso;
        this.volume = volume;
        this.feromonio = feromonio;
        influenciaPesoVolume = valor / (peso + volume);
    }
    
    public void evaporarFeromonio (double RHO) {
        this.feromonio *= 1 - RHO;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    
    public double getProbabilidadeEscolhido() {
        return probabilidadeEscolhido;
    }

    public void setProbabilidadeEscolhido(double probabilidadeEscolhido) {
        this.probabilidadeEscolhido = probabilidadeEscolhido;
    }
    
    public double getInfluenciaPesoVolume() {
        return influenciaPesoVolume;
    }

    public void setInfluenciaPesoVolume(double influenciaPesoVolume) {
        this.influenciaPesoVolume = influenciaPesoVolume;
    }
    
    public double getFeromonio() {
        return feromonio;
    }

    public void setFeromonio(double feromonio) {
        this.feromonio += feromonio;
    }
    
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "(" + descricao + ", " + "V: " + valor + ')';
    }
    
    
    
}
