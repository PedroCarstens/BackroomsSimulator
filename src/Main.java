//======Importação de bibliotecas======
//Steering Behaviors
// e para bugs com listas  java.util.List
//=====================================
import javax.swing.*;
//=====================================
//======Classe principal que inicia o jogo======
public class Main {
    //======Método principal======
    public static void main(String[] args) throws Exception {
        //======Criação da janela principal======
        JFrame janela = new JFrame("Backrooms Simulator"); // cria a janela
        Jogo jogo = new Jogo(); // instancia o painel do jogo
        //=======================================

        //======Define tamanho da janela com base no mapa======
        int largura = jogo.getLarguraMapa() * jogo.getTileSize();
        int altura = jogo.getAlturaMapa() * jogo.getTileSize();

        janela.setContentPane(jogo); // define o painel do jogo como conteúdo
        janela.setSize(largura + 16, altura + 39); // ajusta tamanho com bordas
        janela.setResizable(false); // impede redimensionamento
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // encerra ao fechar
        janela.setLocationRelativeTo(null); // centraliza na tela
        janela.setVisible(true); // torna a janela visível
        //=====================================================

        //======Inicia som ambiente======
        new Thread(new Som("Sons/ambience.wav")).start(); // toca som em loop
        //==================================
    }
    //==================================
}
