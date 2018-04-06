/*
 * Bruno Alejandro
   Felipe Meireles
   Fernando Napoli 
   João Henrique 
   Lucas de Mauro 
   Robson do Nascimento

 */
package Interface;

import Objetos.Formiga;
import Objetos.Item;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ColoniaFormigas extends javax.swing.JFrame {

    private int volumeMochila;
    private int capacidadeMochila;

    private int ID = 0;

    private List<Item> itens;
    private List<Item> itensDisponíveis;
    private List<Formiga> formigas;
    private List<Formiga> melhoresSolucoes;

    // ler arquivo e salvar localizacao das cidades em uma matriz
    public void lerArquivo() {

        String[] aux = new String[5];

        try (FileReader arq = new FileReader(ARQUIVO.getText())) {
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
                Item adicionarItem = new Item(ID, descricao, valor, peso, volume, Integer.parseInt(INI_FEROMONIO.getText()));
                itens.add(adicionarItem);
                itensDisponíveis.add(adicionarItem);
                ID++;
                linha = lerArq.readLine(); // lê da segunda até a última linha
            }

            jTextArea1.append("\tITEM CARREGADO COM SUCESSO\n"
                    + "_______________________________________________\n");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ColoniaFormigas.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ColoniaFormigas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executarACO() {
        volumeMochila = Integer.parseInt(VOLUME_MOCHILA.getText());
        capacidadeMochila = Integer.parseInt(PESO_MOCHILA.getText());
        lerArquivo();
        Formiga formiga;
        Formiga melhorFormiga = null;

        for (int j = 0; j < Integer.parseInt(NUM_ITERACOES.getText()); j++) {
            jTextArea1.append("-------------------------------------------------------------------------------------------------------------------------------"
                    + "\n\t\tITERAÇÃO " + (j + 1) + "\n"
                    + "-------------------------------------------------------------------------------------------------------------------------------\n\n");

            // processo de cada formiga do sistema
            for (int i = 0; i < Integer.parseInt(NUM_FORMIGAS.getText()); i++) {
                formiga = new Formiga();
                //Cria nova lista de itens Disponíveis
                itensDisponíveis.clear();
                itensDisponíveis.addAll(itens);
                capacidadeMochila = Integer.parseInt(PESO_MOCHILA.getText());
                volumeMochila = Integer.parseInt(VOLUME_MOCHILA.getText());
                
                /*
                Formiga inicia a caminhada com um item inicial na mochila
                de acordo com a variável i.
                ALERTA: Nr de formigas deve ser igual ao número de itens = "50"
                
                formiga.getItens().add(itensDisponíveis.get(i));
                AtualizaItensDisponiveis();
                */
                
                
                //enquanto tiver capacidade e volume da mochila e itens disponiveis para adicionanr 
                while (capacidadeMochila >= 0 && volumeMochila >= 0 && !itensDisponíveis.isEmpty()) {
                    AdicionaObjeto(formiga); 
                    // adiciona objeto de acordo com influencia e feromonio
                    AtualizaItensDisponiveis(); 
                    // atualiza a lista de acordo com o item adicionado e os itens que nao cabem mais na mochila
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
                jTextArea1.append("Formiga " + cont + ": ");
                atualizarFeromonio(f);
                f.toString(jTextArea1);
                jTextArea1.append("\n");
                cont++;
            }
            evaporarFeromonio();
            melhoresSolucoes.add(melhorFormiga);
            formigas.removeAll(formigas); // remove todas as formigas para uma nova iteração
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
            soma += ((Math.pow(item.getFeromonio(), Double.parseDouble(ALFA.getText())))
                    * (Math.pow(item.getInfluenciaPesoVolume(), Double.parseDouble(BETA.getText()))));
        }
        for (Item item : itensDisponíveis) {
            item.setProbabilidadeEscolhido(Math.pow(item.getFeromonio(), Double.parseDouble(ALFA.getText()))
                    * Math.pow(item.getInfluenciaPesoVolume(), Double.parseDouble(BETA.getText())) / soma);
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
        double feromonio = Integer.parseInt(Q.getText()) / (Integer.parseInt(Q.getText()) + ((CalculaValorTotal() - formiga.getValorObtido()) / CalculaValorTotal()));
        for (Item item : formiga.getItens()) {
            item.setFeromonio(feromonio);
        }
    }

    public double CalculaValorTotal() {
        double soma = 0;
        for (Item item : itens) {
            soma += item.getValor();
        }
        return soma;
    }

    public void evaporarFeromonio() {
        itens.forEach((item) -> {
            item.evaporarFeromonio(Double.parseDouble(RHO.getText()));
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
        jTextArea1.append("-----------------------------------------------------------------------------------------------------------------------------"
                + "\n\tMelhores formigas de cada Iteração\n"
                + "-----------------------------------------------------------------------------------------------------------------------------");

        int cont = 1;
        for (Formiga f : melhoresSolucoes) {
            jTextArea1.append("\nIteração " + cont + "\n");

            for (int i = 0; i < f.getItens().size(); i++) {
                jTextArea1.append(f.getItens().get(i) + " | ");

            }
            f.toString(jTextArea1);
            jTextArea1.append("\n");

            cont++;
        }

        jTextArea1.append("\n-------------------------------------------------------------------------------------------------------------------------------"
                + "\n\t\tMelhor Solução\n"
                + "-------------------------------------------------------------------------------------------------------------------------------\n");
        Formiga formiga = MelhorSolucao();
        for (int i = 0; i < formiga.getItens().size(); i++) {
            jTextArea1.append(formiga.getItens().get(i) + " | ");
        }
        jTextArea1.append(formiga.toString());
        jTextArea1.append("\n");
        
    }

    public ColoniaFormigas() {

        initComponents();
        itens = new ArrayList<Item>();
        itensDisponíveis = new ArrayList<Item>();
        formigas = new ArrayList<>();
        melhoresSolucoes = new ArrayList<>();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        NUM_FORMIGAS = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        NUM_ITERACOES = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        RHO = new javax.swing.JTextField();
        ALFA = new javax.swing.JTextField();
        BETA = new javax.swing.JTextField();
        Q = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        INI_FEROMONIO = new javax.swing.JTextField();
        VOLUME_MOCHILA = new javax.swing.JTextField();
        PESO_MOCHILA = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ARQUIVO = new javax.swing.JTextField();
        ARQUIVO_BNT = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ACO");
        setBackground(new java.awt.Color(204, 204, 204));

        jButton1.setText("iniciar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        NUM_FORMIGAS.setText("50");
        NUM_FORMIGAS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NUM_FORMIGASActionPerformed(evt);
            }
        });

        jLabel1.setText("Quantidade de Formiga");

        NUM_ITERACOES.setText("10");

        jLabel2.setText("Quantidade de Interações");

        RHO.setText("0.5");
        RHO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RHOActionPerformed(evt);
            }
        });

        ALFA.setText("0.5");
        ALFA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ALFAActionPerformed(evt);
            }
        });

        BETA.setText("0.5");

        Q.setText("1");

        jLabel3.setText("Rho");

        jLabel4.setText("Beta");

        jLabel5.setText("Alfa");

        jLabel6.setText("Q");

        jLabel7.setText("Inicio feromonio");

        INI_FEROMONIO.setText("1");

        VOLUME_MOCHILA.setText("20000");

        PESO_MOCHILA.setText("20000");

        jLabel8.setText("Volume da Mochila");

        jLabel9.setText("Peso da Mochila");

        ARQUIVO_BNT.setText("Arquivo");
        ARQUIVO_BNT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ARQUIVO_BNTActionPerformed(evt);
            }
        });

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ArquivoItens/aco.png"))); // NOI18N
        jLabel10.setText("jLabel10");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(RHO, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(ALFA, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5))
                                    .addGap(25, 25, 25)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(BETA, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel6)
                                        .addComponent(Q, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(23, 23, 23)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addComponent(INI_FEROMONIO, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(ARQUIVO, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(ARQUIVO_BNT, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(VOLUME_MOCHILA, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(NUM_FORMIGAS, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                                            .addComponent(jLabel8))
                                        .addGap(35, 35, 35)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jLabel2)
                                                .addComponent(NUM_ITERACOES)
                                                .addComponent(PESO_MOCHILA, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)))))))
                        .addContainerGap(45, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ARQUIVO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ARQUIVO_BNT))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(VOLUME_MOCHILA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PESO_MOCHILA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(NUM_FORMIGAS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NUM_ITERACOES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(RHO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ALFA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BETA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Q, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(INI_FEROMONIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addComponent(jButton1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //jTextArea1.setText("");
        executarACO();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void NUM_FORMIGASActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NUM_FORMIGASActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NUM_FORMIGASActionPerformed

    private void RHOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RHOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RHOActionPerformed

    private void ALFAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ALFAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ALFAActionPerformed

    private void ARQUIVO_BNTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ARQUIVO_BNTActionPerformed
        // TODO add your handling code here:

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Procurar arquivo");
        //fileChooser.showOpenDialog(this);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("texto", "txt");
        fileChooser.setFileFilter(filter);

        int retorno = fileChooser.showOpenDialog(this);

        if (retorno == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();
            ARQUIVO.setText(file.getPath());
        }
    }//GEN-LAST:event_ARQUIVO_BNTActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ColoniaFormigas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ColoniaFormigas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ColoniaFormigas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ColoniaFormigas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ColoniaFormigas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ALFA;
    private javax.swing.JTextField ARQUIVO;
    private javax.swing.JButton ARQUIVO_BNT;
    private javax.swing.JTextField BETA;
    private javax.swing.JTextField INI_FEROMONIO;
    private javax.swing.JTextField NUM_FORMIGAS;
    private javax.swing.JTextField NUM_ITERACOES;
    private javax.swing.JTextField PESO_MOCHILA;
    private javax.swing.JTextField Q;
    private javax.swing.JTextField RHO;
    private javax.swing.JTextField VOLUME_MOCHILA;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
