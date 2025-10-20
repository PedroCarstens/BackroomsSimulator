import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

public class Mapa {
    //======Variáveis de mapa======
    public int largura = 21; // largura do mapa (ímpar para estrutura de labirinto)
    public int altura = 15;  // altura do mapa (ímpar para estrutura de labirinto)
    public int tileSize = 32; // tamanho de cada tile em pixels
    public Tile[][] tiles; // matriz de tiles que representa o mapa
    public Tile tileChao, tileParede, tileSaida; // tipos de tile disponíveis
    public int saidaX, saidaY; // coordenadas da saída
    //=============================

    //======Variáveis de controle======
    public int quantidadeInimigos = 1; // número de inimigos gerados
    public int quantidadeItens = 3;    // número de itens gerados
    //=================================

    //======Listas de entidades======
    public java.util.List<Enemy> inimigos = new ArrayList<>(); // lista de inimigos
    public java.util.List<Item> itens = new ArrayList<>();     // lista de itens
    //===============================

    //======Sprites======
    public BufferedImage imgEnemy, imgItem; // imagens dos inimigos e itens
    //===================

    //======Construtor======
    public Mapa() throws Exception {
        carregaTiles();   // carrega imagens
        gerarLabirinto(); // gera mapa procedural com caminho garantido
    }
    //======================

    //======Carregar imagens======
    private void carregaTiles() throws Exception {
        tileChao = new Tile(ImageIO.read(new File("Imagens/chão.png")), false, false); // chão passável
        tileParede = new Tile(ImageIO.read(new File("Imagens/parede.png")), true, false); // parede sólida
        tileSaida = new Tile(ImageIO.read(new File("Imagens/Saida.png")), false, true); // saída
        imgEnemy = ImageIO.read(new File("Imagens/enemy.png")); // sprite do inimigo
        imgItem = ImageIO.read(new File("Imagens/item.png"));   // sprite do item
    }
    //============================

    //======Geração de labirinto procedural======
    private void gerarLabirinto() {
        tiles = new Tile[largura][altura]; // inicializa matriz

        // Preenche tudo com parede
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                tiles[x][y] = tileParede;
            }
        }

        // ponto inicial do jogador
        int inicioX = 1;
        int inicioY = 1;
        tiles[inicioX][inicioY] = tileChao;

        // cavar caminhos com DFS
        dfs(inicioX, inicioY);

        // define saída no canto oposto
        saidaX = largura - 2;
        saidaY = altura - 2;
        tiles[saidaX][saidaY] = tileSaida;

        Random rand = new Random();

        //======Gerar inimigos======
        inimigos.clear(); // limpa lista anterior
        while (inimigos.size() < quantidadeInimigos) {
            int x = rand.nextInt(largura);
            int y = rand.nextInt(altura);
            if (tiles[x][y] == tileChao && (x != inicioX || y != inicioY)) {
                inimigos.add(new Enemy(x, y)); // adiciona inimigo
            }
        }

        //======Gerar itens======
        itens.clear(); // limpa lista anterior
        while (itens.size() < quantidadeItens) {
            int x = rand.nextInt(largura);
            int y = rand.nextInt(altura);
            boolean ocupado = inimigos.stream().anyMatch(e -> e.x == x && e.y == y);
            if (tiles[x][y] == tileChao && !ocupado && (x != inicioX || y != inicioY)) {
                itens.add(new Item(x, y)); // adiciona item
            }
        }
    }
    //============================================

    //======Algoritmo DFS recursivo======
    private void dfs(int x, int y) {
        tiles[x][y] = tileChao;

        java.util.List<int[]> direcoes = Arrays.asList(
                new int[]{0, -2}, new int[]{0, 2},
                new int[]{-2, 0}, new int[]{2, 0}
        );
        Collections.shuffle(direcoes); // embaralha direções

        for (int[] dir : direcoes) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx > 0 && ny > 0 && nx < largura - 1 && ny < altura - 1 && tiles[nx][ny] == tileParede) {
                tiles[x + dir[0] / 2][y + dir[1] / 2] = tileChao; // remove parede entre
                dfs(nx, ny); // continua recursivamente
            }
        }
    }
    //===================================

    //======Renderizar mapa e entidades======
    public void render(Graphics g) {
        // desenha tiles
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                g.drawImage(tiles[x][y].imagem, x * tileSize, y * tileSize, null);
            }
        }

        // desenha inimigos
        for (Enemy e : inimigos) {
            g.drawImage(imgEnemy, e.x * tileSize, e.y * tileSize, null);
        }

        // desenha itens
        for (Item i : itens) {
            g.drawImage(imgItem, i.x * tileSize, i.y * tileSize, null);
        }
    }
    //=======================================
}
