//======Importação de bibliotecas======
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;
//=====================================

//======Classe que representa um inimigo======
public class Enemy {
    //======Posição do inimigo======
    public int x, y; // coordenadas no mapa
    private int ultimoX, ultimoY; // última posição para evitar retorno
    //=============================

    //======Sprite do inimigo======
    private BufferedImage imagem; // imagem do inimigo
    private int angulo = 0;       // ângulo de rotação
    //=============================

    //======Construtor======
    public Enemy(int x, int y) throws Exception {
        this.x = x;
        this.y = y;
        this.ultimoX = x;
        this.ultimoY = y;
        imagem = ImageIO.read(new File("Imagens/enemy.png")); // carrega sprite
    }
    //======================

    //======Atualiza comportamento do inimigo======
    public void atualizar(Mapa mapa, Jogador jogador) {
        //======1. Se vê o jogador, apenas vira para ele======
        if (podeVerJogador(mapa, jogador)) {
            int dx = jogador.x - x;
            int dy = jogador.y - y;

            if (dx < 0) angulo = 90;      // jogador à esquerda
            else if (dx > 0) angulo = 270; // jogador à direita
            else if (dy > 0) angulo = 0;   // jogador abaixo
            else if (dy < 0) angulo = 180; // jogador acima

            System.out.println("Inimigo [" + x + "," + y + "] vê o jogador e vira para ele.");
            return; // não se move
        }

        //======2. Obtém vizinhos válidos======
        java.util.List<int[]> vizinhos = getVizinhosValidos(mapa);
        int[] destino = null;

        //======3. Evita voltar ao tile anterior======
        vizinhos.removeIf(v -> v[0] == ultimoX && v[1] == ultimoY);
        if (!vizinhos.isEmpty()) {
            destino = vizinhos.get(new Random().nextInt(vizinhos.size()));
            System.out.println("Inimigo [" + x + "," + y + "] vaga para [" + destino[0] + "," + destino[1] + "]");
        } else {
            destino = new int[]{ultimoX, ultimoY};
            System.out.println("Inimigo [" + x + "," + y + "] sem opção, retorna para [" + destino[0] + "," + destino[1] + "]");
        }

        //======4. Atualiza direção do sprite======
        int dx = destino[0] - x;
        int dy = destino[1] - y;

        if (dx == -1) angulo = 90;      // esquerda
        else if (dx == 1) angulo = 270; // direita
        else if (dy == 1) angulo = 0;   // baixo
        else if (dy == -1) angulo = 180; // cima
        //=========================================

        //======5. Move para destino escolhido======
        ultimoX = x;
        ultimoY = y;
        x = destino[0];
        y = destino[1];
        //==========================================
    }
    //============================================

    //======Obtém vizinhos válidos======
    private java.util.List<int[]> getVizinhosValidos(Mapa mapa) {
        java.util.List<int[]> vizinhos = new ArrayList<>();
        int[][] direcoes = {{0,-1},{0,1},{-1,0},{1,0}};
        for (int[] d : direcoes) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
                vizinhos.add(new int[]{nx, ny});
            }
        }
        return vizinhos;
    }
    //==================================

    //======Verifica se jogador está visível======
    private boolean podeVerJogador(Mapa mapa, Jogador jogador) {
        if (x == jogador.x) {
            int minY = Math.min(y, jogador.y);
            int maxY = Math.max(y, jogador.y);
            for (int i = minY + 1; i < maxY; i++) {
                if (mapa.tiles[x][i].solida) return false;
            }
            return true;
        } else if (y == jogador.y) {
            int minX = Math.min(x, jogador.x);
            int maxX = Math.max(x, jogador.x);
            for (int i = minX + 1; i < maxX; i++) {
                if (mapa.tiles[i][y].solida) return false;
            }
            return true;
        }
        return false;
    }
    //===========================================

    //======Renderização do inimigo======
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
    //===========================================
}
