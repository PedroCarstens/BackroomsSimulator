//======Importação de bibliotecas======
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;
//=====================================

//======Classe que representa um inimigo======
public class Enemy {
    //======Posição do inimigo (vetorial)======
    public double x, y; // coordenadas reais no mapa
    private int tileX, tileY; // posição atual em tiles
    private BufferedImage imagem; // sprite do inimigo
    private double velocidade = 0.2; // velocidade de movimento
    private String modo = "vagar"; // modo atual ("vagar" ou "perseguir")
    private Stack<int[]> caminhoDFS = new Stack<>(); // trilha de exploração
    private Set<String> visitados = new HashSet<>(); // memória de tiles já visitados
    //=========================================

    //======Construtor======
    public Enemy(int x, int y) throws Exception {
        this.x = x;
        this.y = y;
        imagem = ImageIO.read(new File("Imagens/enemy.png")); // carrega sprite
    }
    //======================

    //======Atualiza comportamento do inimigo======
    public void atualizar(Mapa mapa, Jogador jogador) {
        //======1. Atualiza posição em tiles======
        tileX = (int)Math.round(x);
        tileY = (int)Math.round(y);
        String posAtual = tileX + "," + tileY;
        visitados.add(posAtual); // marca como visitado
        //========================================

        //======2. Verifica se vê o jogador======
        if (podeVerJogador(mapa, jogador)) {
            if (!modo.equals("perseguir")) {
                modo = "perseguir";
                System.out.println("Modo alterado para PERSEGUIR");
            }

            //======2.1 Calcula vetor direção até jogador======
            double dx = jogador.x - x;
            double dy = jogador.y - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia > 0.01) {
                double dirX = dx / distancia;
                double dirY = dy / distancia;

                x += dirX * velocidade;
                y += dirY * velocidade;

                System.out.printf("Inimigo [%.2f, %.2f] persegue jogador [%d, %d]%n", x, y, jogador.x, jogador.y);
            }
            return;
        }
        //==========================================

        //======3. Modo padrão: exploração DFS======
        if (!modo.equals("vagar")) {
            modo = "vagar";
            System.out.println("Modo alterado para VAGAR");
        }

        //======4. Se não há caminho, gera vizinhos======
        if (caminhoDFS.isEmpty()) {
            java.util.List<int[]> vizinhos = getVizinhosDFS(mapa);
            for (int[] v : vizinhos) {
                String chave = v[0] + "," + v[1];
                if (!visitados.contains(chave)) {
                    caminhoDFS.push(v); // empilha vizinho não visitado
                }
            }
        }

        //======5. Se há caminho, segue para o próximo tile======
        if (!caminhoDFS.isEmpty()) {
            int[] destino = caminhoDFS.peek(); // olha o topo da pilha
            double dx = destino[0] - x;
            double dy = destino[1] - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia > 0.01) {
                double dirX = dx / distancia;
                double dirY = dy / distancia;

                x += dirX * velocidade;
                y += dirY * velocidade;

                System.out.printf("Inimigo [%.2f, %.2f] explora para [%d, %d]%n", x, y, destino[0], destino[1]);
            } else {
                caminhoDFS.pop(); // chegou ao destino, remove da pilha
            }
        }
    }
    //============================================

    //======Obtém vizinhos válidos estilo DFS (evita itens e saída)======
    private java.util.List<int[]> getVizinhosDFS(Mapa mapa) {
        java.util.List<int[]> vizinhos = new ArrayList<>();
        int[][] direcoes = {{0,-1},{0,1},{-1,0},{1,0}}; // cima, baixo, esquerda, direita

        for (int[] d : direcoes) {
            int nx = tileX + d[0];
            int ny = tileY + d[1];

            //======Verifica se posição é válida e não é parede======
            if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
                boolean temItem = false;

                //======Verifica se há item na posição======
                for (Item item : mapa.itens) {
                    if (item.x == nx && item.y == ny) {
                        temItem = true;
                        break;
                    }
                }

                //======Verifica se é a saída======
                boolean ehSaida = (mapa.saidaX == nx && mapa.saidaY == ny);

                //======Se não tem item nem é saída, adiciona como vizinho válido======
                if (!temItem && !ehSaida) {
                    vizinhos.add(new int[]{nx, ny});
                }
            }
        }

        Collections.shuffle(vizinhos); // embaralha para simular DFS
        return vizinhos;
    }
//===================================================================


    //======Verifica se jogador está visível (linha reta sem parede)======
    private boolean podeVerJogador(Mapa mapa, Jogador jogador) {
        int jx = jogador.x;
        int jy = jogador.y;

        if (tileX == jx) {
            int minY = Math.min(tileY, jy);
            int maxY = Math.max(tileY, jy);
            for (int i = minY + 1; i < maxY; i++) {
                if (mapa.tiles[tileX][i].solida) return false;
            }
            return true;
        } else if (tileY == jy) {
            int minX = Math.min(tileX, jx);
            int maxX = Math.max(tileX, jx);
            for (int i = minX + 1; i < maxX; i++) {
                if (mapa.tiles[i][tileY].solida) return false;
            }
            return true;
        }
        return false;
    }
    //===========================================

    //======Renderização do inimigo======
    public void render(Graphics g, int tileSize) {
        int px = (int)(x * tileSize);
        int py = (int)(y * tileSize);
        g.drawImage(imagem, px, py, tileSize, tileSize, null); // desenha imagem sem rotação
    }
    //===========================================
}
