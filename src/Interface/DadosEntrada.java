/** 
 * Interface responsável pelos dados de entrada para o problema da mochila com 
 * o uso do algoritmo de colônia de formigas.
 */
package Interface;

/**
 *
 * @author João Loureiro
 */
public interface DadosEntrada {
    public final String ARQUIVO = "src\\ArquivoItens\\itens_teste.txt";
    public final int NUM_FORMIGAS = 10;
    public final int NUM_ITERACOES = 10;
    public final double RHO = 0.5;
    public final double ALFA = 0.5;
    public final double BETA = 0.5;
    public final double Q = 1; 
    public final double INI_FEROMONIO = 1;
    public final int PESO_MOCHILA = 20000;
    public final int VOLUME_MOCHILA = 20000;
}
