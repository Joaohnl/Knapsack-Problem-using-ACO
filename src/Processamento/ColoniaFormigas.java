/** 
 *  Esta classe é responsável por resolver o problema da mochila através do 
 *  uso do algoritmo de colônia de formigas, implementando a interface DadosEntrada.
 */
package Processamento;

import Objetos.Item;
import Objetos.Formiga;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Interface.DadosEntrada;

/**
 *
 * @author João Loureiro
 */
public class ColoniaFormigas implements DadosEntrada {

    private int capacidadeMochila = PESO_MOCHILA;
    private int volumeMochila = VOLUME_MOCHILA;
    private int ID = 0;

    private List<Item> itens;
    private List<Item> itensDisponíveis;
    private List<Formiga> formigas;
    private List<Formiga> melhoresSolucoes;

    public ColoniaFormigas() {
        itens = new ArrayList<Item>();
        itensDisponíveis = new ArrayList<Item>();
        formigas = new ArrayList<>();
        melhoresSolucoes = new ArrayList<>();
    }

    // ler arquivo e salvar localizacao das cidades em uma matriz
    public void lerArquivo() {

        String[] aux = new String[5];

            try (FileReader arq = new FileReader(ARQUIVO)) {
                BufferedReader lerArq = new BufferedReader(arq);
                
                String linha = lerArq.readLine(); // lê primeira linha
                String descricao;
                double valor;
                double peso;
                double volume;
                
                while (linha != null) {
                    aux = linha.split(";");
                    descricao = aux[1];
                    valor = Double.parseDouble(aux[2]);
                    peso = Double.parseDouble(aux[3]);
                    volume = Double.parseDouble(aux[4]);
                    Item adicionarItem = new Item(ID, descricao, valor, peso, volume, INI_FEROMONIO);
                    itens.add(adicionarItem);
                    itensDisponíveis.add(adicionarItem);
                    ID++;
                    linha = lerArq.readLine(); // lê da segunda até a última linha
                }
                System.out.println("\tITEM CARREGADO COM SUCESSO\n" +
                        "_______________________________________________");
            } catch (FileNotFoundException ex) {
            Logger.getLogger(ColoniaFormigas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ColoniaFormigas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executarACO() {
        lerArquivo();
        Formiga formiga;
        Formiga melhorFormiga = null;

        for (int j = 0; j < NUM_ITERACOES; j++) {
            System.out.println("-----------------------------------------------"+
                                "\n\t\tITERAÇÃO " + (j + 1) + "\n" +
                                "-----------------------------------------------");

            for (int i = 0; i < NUM_FORMIGAS; i++) {
                formiga = new Formiga();
                //Cria nova lista de itens Disponíveis
                itensDisponíveis.clear();
                itensDisponíveis.addAll(itens);
                capacidadeMochila = PESO_MOCHILA;
                volumeMochila = VOLUME_MOCHILA;

                while (capacidadeMochila >= 0 && volumeMochila >= 0 && !itensDisponíveis.isEmpty()) {
                    AdicionaObjeto(formiga);
                    AtualizaItensDisponiveis();
                }
                formigas.add(formiga);
                // Armazena as melhores formigas por iteração
                if (melhorFormiga == null) {
                    melhorFormiga = formiga;
                } else if (formiga.getValorObtido() > melhorFormiga.getValorObtido()) {
                    melhorFormiga = formiga;
                }
            }

            // atualizar taxa de feromonio onde as formigas passaram
            int cont = 1;
            for (Formiga f : formigas) {
                System.out.println("Formiga " + cont + "\n");
                atualizarFeromonio(f);
                System.out.println(f);
                System.out.println("\n");
                cont++;
            }
            evaporarFeromonio();
            melhoresSolucoes.add(melhorFormiga);
            formigas.removeAll(formigas);
        }
        Resultados();
    }

    private void AdicionaObjeto(Formiga formiga) {
        double somaProb = CalcularProbabilidadeItens();
        double valorSorteado = Math.random() * somaProb;

        // Seleciona o próximo item de acordo com suas probabilidades
        Iterator<Item> ite = itensDisponíveis.iterator();
        Item item = null;
        double soma = 0;
        while (ite.hasNext() && soma < valorSorteado) {
            item = ite.next();
            soma += item.getProbabilidadeEscolhido();
        }

        int ID = item.getID();
        //Adiciona o item à mochila da Formiga e remove dos itens disponíveis
        formiga.getItens().add(itens.get(ID));
        capacidadeMochila -= item.getPeso();
        volumeMochila -= item.getVolume();
        itensDisponíveis.remove(item);

        //Soma o valor do item adicionado ao valor total da mochila da Formiga
        formiga.setValorObtido(item.getValor());
        formiga.setPesoCarregado(item.getPeso());
        formiga.setVolumeCarregado(item.getVolume());
    }

    //Caso de transição de estado da Formiga
    private double CalcularProbabilidadeItens() {
        double soma = 0.0;

        for (Item item : itensDisponíveis) {
            soma += ((Math.pow(item.getFeromonio(), ALFA))
                    * (Math.pow(item.getInfluenciaPesoVolume(), BETA)));
        }
        for (Item item : itensDisponíveis) {
            item.setProbabilidadeEscolhido(Math.pow(item.getFeromonio(), ALFA)
                    * Math.pow(item.getInfluenciaPesoVolume(), BETA) / soma);
        }

        return soma;
    }

    /* 
    Remove os itens que não podem mais ser adicionados na mochila
    devido ao peso ou volume elevados.
     */
    private void AtualizaItensDisponiveis() {
        List<Item> remover = new ArrayList<Item>();
        for (Item item : itensDisponíveis) {
            if (item.getPeso() > capacidadeMochila || item.getVolume() > volumeMochila) {
                remover.add(item);
            }
        }
        itensDisponíveis.removeAll(remover);
    }

    public void atualizarFeromonio(Formiga formiga) {
        //depositar feromonio
        double feromonio = Q / (Q + ((formiga.getValorObtido() - formiga.menorValor()) / formiga.getValorObtido()));
        for (Item item : formiga.getItens()) {
            item.setFeromonio(feromonio);
        }
    }

    public void evaporarFeromonio() {
        itens.forEach((item) -> {
            item.evaporarFeromonio(RHO);
        });
    }

    private Formiga MelhorSolucao() {
        Formiga aux = null;
        for (Formiga f : melhoresSolucoes) {
            if (aux == null) {
                aux = f;
            } else if (aux.getValorObtido() < f.getValorObtido()) {
                aux = f;
            }
        }
        return aux;
    }

    // verificar dentre as melhores soluções de cada iteração qual a melhor
    public void Resultados() {
        System.out.println("--------------------------------------------------" +
                            "\n\tMelhores formigas de cada Iteração\n" + 
                            "--------------------------------------------------");
        int cont = 1;
        for (Formiga f : melhoresSolucoes) {
            System.out.println("\nIteração " + cont);
            for (int i = 0; i < f.getItens().size(); i++) {
                System.out.print(f.getItens().get(i) + " | ");
            }
            System.out.println(f);
            cont++;
        }
        System.out.println("-----------------------------------------------"+ 
                            "\n\t\tMelhor Solução\n"+
                            "-----------------------------------------------");
        Formiga formiga = MelhorSolucao();
        for (int i = 0; i < formiga.getItens().size(); i++) {
            System.out.print(formiga.getItens().get(i) + " | ");
        }
        System.out.println(formiga);
    }
}
