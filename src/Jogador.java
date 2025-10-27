//======Importação de bibliotecas======
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
//=====================================

//======Classe que representa o jogador======
public class Jogador {
    //======Posição do jogador======
    public int x = 1; // coordenada X inicial
    public int y = 1; // coordenada Y inicial
    //==============================

    //======Sprite do jogador======
    private BufferedImage imagem; // imagem do jogador
    //=============================

    //======Construtor======
    public Jogador() throws Exception {
        // carrega imagem do jogador
        imagem = ImageIO.read(new File("Imagens/jogador.png"));
    }
    //=======================

    //======Movimento do jogador======
    public void mover(int dx, int dy, Mapa mapa) {
        int novoX = x + dx;
        int novoY = y + dy;

        // verifica se o movimento é válido (não colide com paredes)
        if (mapa.valido(novoX, novoY) && !mapa.tiles[novoX][novoY].solida) {
            x = novoX;
            y = novoY;
        }
    }
    //================================

    //======Renderização do jogador======
    public void render(Graphics g, int tileSize) {
        g.drawImage(imagem, x * tileSize, y * tileSize, null);
    }
    //===================================
}
