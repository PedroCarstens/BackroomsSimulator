//======Importação de bibliotecas======
import java.awt.image.BufferedImage;
//=====================================

//======Classe que representa um tile do mapa======
public class Tile {
    //======Variáveis======
    public BufferedImage imagem; // imagem do tile
    public boolean solida;       // define se o tile bloqueia movimento
    public boolean saida;        // define se o tile é uma saída
    //====================

    //======Construtor======
    public Tile(BufferedImage imagem, boolean solida, boolean saida) {
        this.imagem = imagem; // define a imagem do tile
        this.solida = solida; // define se é sólido
        this.saida = saida;   // define se é saída
    }
    //======================
}
