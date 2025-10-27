//======Importação de bibliotecas======
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
//=====================================

//======Classe que representa o jogador======
public class Jogador {
    //======Posição do jogador======
    public int x = 1; // coordenada X inicial
    public int y = 1; // coordenada Y inicial
    //==============================

    //======Sprite do jogador======
    private BufferedImage imagem; // imagem do jogador
    private int angulo = 0;       // ângulo de rotação (0 = virado para baixo)
    //=============================

    //======Construtor======
    public Jogador() throws Exception {
        imagem = ImageIO.read(new File("Imagens/jogador.png")); // carrega sprite
    }
    //=======================

    //======Movimento do jogador======
    public boolean mover(int dx, int dy, Mapa mapa) {
        //======Atualiza direção do sprite======
        if (dx == -1) angulo = 90;      // esquerda (A)
        else if (dx == 1) angulo = 270; // direita (D)
        else if (dy == 1) angulo = 0;   // baixo (S)
        else if (dy == -1) angulo = 180; // cima (W)
        //======================================

        int novoX = x + dx;
        int novoY = y + dy;

        // verifica se o movimento é válido (não colide com paredes)
        if (mapa.valido(novoX, novoY) && !mapa.tiles[novoX][novoY].solida) {
            x = novoX;
            y = novoY;
            return true; // movimento bem-sucedido
        }

        return false; // movimento bloqueado (parede)
    }
    //================================

    //======Renderização do jogador======
    public void render(Graphics g, int tileSize) {
        Graphics2D g2d = (Graphics2D) g;

        int px = x * tileSize;
        int py = y * tileSize;

        //======Transformação para rotacionar sprite======
        AffineTransform transform = new AffineTransform();
        transform.translate(px + tileSize / 2, py + tileSize / 2); // centraliza
        transform.rotate(Math.toRadians(angulo));                  // aplica rotação
        transform.translate(-imagem.getWidth() / 2, -imagem.getHeight() / 2); // reposiciona
        //=================================================

        g2d.drawImage(imagem, transform, null); // desenha imagem rotacionada
    }
    //===================================
}
