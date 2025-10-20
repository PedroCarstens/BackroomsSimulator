//======Importação de bibliotecas======
import javax.swing.*; // importa o pacote Swing para criar interfaces gráficas
//=====================================

public class Main {
    public static void main(String[] args) throws Exception {
        //======Criação da janela principal======
        JFrame janela = new JFrame("Mapa Procedural");// painel principal // cria uma janela com o título "Mapa Procedural"
        Jogo jogo = new Jogo(); // instancia o painel do jogo, que contém o mapa e o jogador
        //=======================================

        //======Define tamanho exato da janela com base no mapa======
        int largura = jogo.mapa.largura * jogo.mapa.tileSize;
        int altura = jogo.mapa.altura * jogo.mapa.tileSize;

        janela.setContentPane(jogo);
        janela.setSize(largura + 16, altura + 39); // compensação para bordas da janela
        janela.setResizable(false);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null); // centraliza na tela
        janela.setVisible(true);


        //==================================
    }
}
