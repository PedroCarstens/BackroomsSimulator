import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Jogador {
    //======Variáveis======
    public int x = 1; // posição X inicial
    public int y = 1; // posição Y inicial
    public BufferedImage imagem; // sprite do jogador
    //====================

    //======Construtor======
    public Jogador() throws Exception {
        imagem = ImageIO.read(new File("Imagens/jogador.png")); // carrega sprite
    }
    //======================

    //======Movimentação======
    public void mover(int dx, int dy, Mapa mapa) {
        int novoX = x + dx;
        int novoY = y + dy;

        // verifica limites
        if (novoX < 0 || novoY < 0 || novoX >= mapa.largura || novoY >= mapa.altura) return;

        Tile destino = mapa.tiles[novoX][novoY];

        if (destino.solida) return; // bloqueia se for parede

        x = novoX;
        y = novoY;

        if (destino.saida) {
            System.out.println("Saída alcançada. Encerrando...");
            System.exit(0); // encerra programa
        }
    }
    //=========================

    //======Renderizar jogador======
    public void render(Graphics g, int tileSize) {
        g.drawImage(imagem, x * tileSize, y * tileSize, null); // desenha jogador
    }
    //==============================
}
