//======Importação de bibliotecas======
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
//=====================================

//======Classe que representa um inimigo======
public class Enemy {
    //======Posição do inimigo (vetorial)======
    public double x, y;
    private int tileX, tileY;
    private BufferedImage imagem;
    private double velocidade = 0.08;
    private final double velocidadeNormal = 0.08;
    private final double velocidadePerseguir = 0.2;
    private String modo = "vagar";
    private Stack<int[]> caminhoDFS = new Stack<>();
    private Set<String> visitados = new HashSet<>();
    //=========================================

    //======Construtor======
    public Enemy(int x, int y) throws Exception {
        this.x = x;
        this.y = y;
        imagem = ImageIO.read(new File("Imagens/enemy.png"));
    }
    //======================

    //======Atualiza comportamento do inimigo======
    public void atualizar(Mapa mapa, Jogador jogador) {
        tileX = (int)Math.round(x);
        tileY = (int)Math.round(y);
        String posAtual = tileX + "," + tileY;
        visitados.add(posAtual);

        if (podeVerJogador(mapa, jogador)) {
            if (!modo.equals("perseguir")) {
                modo = "perseguir";
                System.out.println("Modo alterado para PERSEGUIR");
            }

            velocidade = velocidadePerseguir;

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
        } else {
            velocidade = velocidadeNormal;
        }

        if (!modo.equals("vagar")) {
            modo = "vagar";
            System.out.println("Modo alterado para VAGAR");
        }

        if (caminhoDFS.isEmpty()) {
            java.util.List<int[]> vizinhos = getVizinhosDFS(mapa);
            boolean encontrouNovo = false;

            for (int[] v : vizinhos) {
                String chave = v[0] + "," + v[1];
                if (!visitados.contains(chave)) {
                    caminhoDFS.push(v);
                    encontrouNovo = true;
                }
            }

            if (!encontrouNovo) {
                visitados.clear();
                System.out.println("Inimigo reiniciou exploração");
            }
        }

        if (!caminhoDFS.isEmpty()) {
            int[] destino = caminhoDFS.peek();
            double destinoX = destino[0];
            double destinoY = destino[1];

            if (verificaItemOuSaida(mapa, destinoX, destinoY)) {
                double dx = x - destinoX;
                double dy = y - destinoY;
                double distancia = Math.sqrt(dx * dx + dy * dy);

                if (distancia > 0.01) {
                    double dirX = dx / distancia;
                    double dirY = dy / distancia;

                    x += dirX * velocidade;
                    y += dirY * velocidade;

                    System.out.printf("Inimigo [%.2f, %.2f] evita item/saída%n", x, y);
                }
                return;
            }

            if (Math.abs(x - destinoX) < 0.05 && Math.abs(y - destinoY) < 0.05) {
                caminhoDFS.pop();
                return;
            }

            double dx = destinoX - x;
            double dy = destinoY - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia > 0.01) {
                double dirX = dx / distancia;
                double dirY = dy / distancia;

                x += dirX * velocidade;
                y += dirY * velocidade;

                System.out.printf("Inimigo [%.2f, %.2f] explora para [%d, %d]%n", x, y, destino[0], destino[1]);
            }
        }
    }
    //============================================

    //======Verifica se há item ou saída na posição destino======
    private boolean verificaItemOuSaida(Mapa mapa, double destinoX, double destinoY) {
        int dx = (int)Math.round(destinoX);
        int dy = (int)Math.round(destinoY);

        for (Item item : mapa.itens) {
            if (item.x == dx && item.y == dy) {
                return true;
            }
        }

        if (mapa.saidaX == dx && mapa.saidaY == dy) {
            return true;
        }

        return false;
    }
    //===========================================================

    //======Obtém vizinhos válidos estilo DFS (evita itens e saída)======
    private java.util.List<int[]> getVizinhosDFS(Mapa mapa) {
        java.util.List<int[]> vizinhos = new ArrayList<>();
        int[][] direcoes = {{0,-1},{0,1},{-1,0},{1,0}};

        for (int[] d : direcoes) {
            int nx = tileX + d[0];
            int ny = tileY + d[1];

            if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
                boolean temItem = false;

                for (Item item : mapa.itens) {
                    if (item.x == nx && item.y == ny) {
                        temItem = true;
                        break;
                    }
                }

                boolean ehSaida = (mapa.saidaX == nx && mapa.saidaY == ny);

                if (!temItem && !ehSaida) {
                    vizinhos.add(new int[]{nx, ny});
                }
            }
        }

        Collections.shuffle(vizinhos);
        vizinhos.sort((a, b) -> {
            String chaveA = a[0] + "," + a[1];
            String chaveB = b[0] + "," + b[1];
            boolean visitadoA = visitados.contains(chaveA);
            boolean visitadoB = visitados.contains(chaveB);
            return Boolean.compare(visitadoA, visitadoB);
        });

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

    //======Gera direção diagonal aleatória======
    private int[] direcaoAleatoria() {
        int r = (int)(Math.random() * 3); // cast para inteiro entre 0 e 2

        return switch (r) {
            case 0 -> new int[]{1, 1};
            case 1 -> new int[]{1, -1};
            case 2 -> new int[]{-1, 1};
            default -> new int[]{-1, -1};
        };
    }
    //===========================================

    //======Renderização do inimigo======
    public void render(Graphics g, int tileSize) {
        int px = (int)(x * tileSize);
        int py = (int)(y * tileSize);
        g.drawImage(imagem, px, py, tileSize, tileSize, null);
    }
    //===========================================
}
