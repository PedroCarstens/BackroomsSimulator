import java.awt.image.BufferedImage;

public class Tile {
    //======Variáveis======
    public BufferedImage imagem; // imagem do tile
    public boolean solida;       // define se o tile bloqueia movimento
    public boolean saida;        // define se o tile é uma saída
    //====================

    //======Construtor======
    public Tile(BufferedImage imagem, boolean solida, boolean saida) {
        this.imagem = imagem; // define a imagem
        this.solida = solida; // define se é sólido
        this.saida = saida;   // define se é saída
    }
    //======================
}
