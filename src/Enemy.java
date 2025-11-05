//======Importação de bibliotecas======
import java.awt.Graphics; // Utilizado para desenhar o inimigo na tela
import java.awt.image.BufferedImage; // Representa a imagem do inimigo
import java.io.File; // Permite acessar arquivos do sistema
import javax.imageio.ImageIO; // Utilizado para carregar imagens
import java.util.Stack; // Estrutura de dados para armazenar caminho DFS
import java.util.HashSet; // Conjunto para armazenar posições visitadas
import java.util.Set; // Interface para o conjunto de posições
import java.util.ArrayList; // Lista para armazenar vizinhos
import java.util.Collections; // Utilizado para embaralhar e ordenar vizinhos
//=====================================

//======Classe que representa um inimigo======
public class Enemy {
    //======Posição do inimigo (vetorial)======
    public double x, y; // Coordenadas contínuas do inimigo no mapa
    private int tileX, tileY; // Coordenadas discretas (tiles) do inimigo
    private BufferedImage imagem; // Imagem que representa o inimigo
    private double velocidade = 0.08; // Velocidade atual do inimigo
    private final double velocidadeNormal = 0.08; // Velocidade padrão ao vagar
    private final double velocidadePerseguir = 0.2; // Velocidade ao perseguir jogador
    private String modo = "vagar"; // Estado atual do inimigo: "vagar" ou "perseguir"
    private Stack<int[]> caminhoDFS = new Stack<>(); // Caminho de exploração DFS
    private Set<String> visitados = new HashSet<>(); // Posições já visitadas
    //=========================================

    //======Construtor======
    public Enemy(int x, int y) throws Exception {
        this.x = x;
        this.y = y;
        imagem = ImageIO.read(new File("Imagens/enemy.png")); // Carrega imagem do inimigo
    }
    //======================

    //======Atualiza comportamento do inimigo======
    public void atualizar(Mapa mapa, Jogador jogador) {
        // Atualiza posição discreta do inimigo
        tileX = (int)Math.round(x);
        tileY = (int)Math.round(y);
        String posAtual = tileX + "," + tileY;
        visitados.add(posAtual); // Marca posição atual como visitada

        //======Verifica se jogador está visível======
        if (podeVerJogador(mapa, jogador)) {
            // Muda para modo perseguição
            if (!modo.equals("perseguir")) {
                modo = "perseguir";
                System.out.println("Modo alterado para PERSEGUIR");
            }

            velocidade = velocidadePerseguir;

            //======Calcula direção vetorial até jogador======
            // dx e dy representam o vetor entre inimigo e jogador

            double dx = jogador.x - x;
            double dy = jogador.y - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);// Distância euclidiana entre os dois

            //======Movimento vetorial normalizado (Steering: Seek)======
            if (distancia > 0.01) {
                double dirX = dx / distancia; // Componente X do vetor unitário
                double dirY = dy / distancia; // Componente Y do vetor unitário

                // Atualiza posição aplicando vetor direção e velocidade
                x += dirX * velocidade;
                y += dirY * velocidade;

                System.out.printf("Inimigo [%.2f, %.2f] persegue jogador [%d, %d]%n", x, y, jogador.x, jogador.y);
            }
            return;
        } else {
            // Retorna ao modo de exploração
            velocidade = velocidadeNormal;
        }

        if (!modo.equals("vagar")) {
            modo = "vagar";
            System.out.println("Modo alterado para VAGAR");
        }

        //======Exploração DFS======
        if (caminhoDFS.isEmpty()) {
            java.util.List<int[]> vizinhos = getVizinhosDFS(mapa);
            boolean encontrouNovo = false;

            // Adiciona vizinhos não visitados à pilha
            for (int[] v : vizinhos) {
                String chave = v[0] + "," + v[1];
                if (!visitados.contains(chave)) {
                    caminhoDFS.push(v);
                    encontrouNovo = true;
                }
            }

            // Reinicia exploração se todos vizinhos foram visitados
            if (!encontrouNovo) {
                visitados.clear();
                System.out.println("Inimigo reiniciou exploração");
            }
        }

        //======Movimento para próximo destino DFS======
        if (!caminhoDFS.isEmpty()) {
            int[] destino = caminhoDFS.peek();
            double destinoX = destino[0];
            double destinoY = destino[1];

            //======Calcula direção vetorial oposta ao destino (fuga)======
            if (verificaItemOuSaida(mapa, destinoX, destinoY)) {
                double dx = x - destinoX;
                double dy = y - destinoY;
                double distancia = Math.sqrt(dx * dx + dy * dy);// Distância euclidiana invertida

                //======Movimento vetorial normalizado (Steering: Flee)======
                if (distancia > 0.01) {
                    double dirX = dx / distancia; // Componente X do vetor unitário
                    double dirY = dy / distancia; // Componente Y do vetor unitário

                    // Atualiza posição aplicando vetor direção e velocidade
                    x += dirX * velocidade;
                    y += dirY * velocidade;

                    System.out.printf("Inimigo [%.2f, %.2f] evita item/saída%n", x, y);
                }
                return;
            }

            // Remove destino da pilha se chegou próximo
            if (Math.abs(x - destinoX) < 0.05 && Math.abs(y - destinoY) < 0.05) {
                caminhoDFS.pop();
                return;
            }

            //======Calcula direção vetorial até destino DFS======
            double dx = destinoX - x;
            double dy = destinoY - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);// Distância euclidiana até destino

            //======Movimento vetorial normalizado (Steering: Seek)======
            if (distancia > 0.01) {
                double dirX = dx / distancia; // Componente X do vetor unitário
                double dirY = dy / distancia; // Componente Y do vetor unitário

                // Atualiza posição aplicando vetor direção e velocidade
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
        int[][] direcoes = {{0,-1},{0,1},{-1,0},{1,0}}; // Cima, baixo, esquerda, direita

        for (int[] d : direcoes) {
            int nx = tileX + d[0];
            int ny = tileY + d[1];

            // Verifica se é posição válida e não sólida
            if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
                boolean temItem = false;

                for (Item item : mapa.itens) {
                    if (item.x == nx && item.y == ny) {
                        temItem = true;
                        break;
                    }
                }

                boolean ehSaida = (mapa.saidaX == nx && mapa.saidaY == ny);

                // Adiciona vizinho se não for item nem saída
                if (!temItem && !ehSaida) {
                    vizinhos.add(new int[]{nx, ny});
                }
            }
        }

        // Embaralha vizinhos para variar caminho
        Collections.shuffle(vizinhos);

        // Ordena vizinhos priorizando os não visitados
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

        // Verifica linha vertical
        if (tileX == jx) {
            int minY = Math.min(tileY, jy);
            int maxY = Math.max(tileY, jy);
            for (int i = minY + 1; i < maxY; i++) {
                if (mapa.tiles[tileX][i].solida) return false;
            }
            return true;
        }
        // Verifica linha horizontal
        else if (tileY == jy) {
            int minX = Math.min(tileX, jx);
            int maxX = Math.max(tileX, jx);
            for (int i = minX + 1; i < maxX; i++) {
                if (mapa.tiles[i][tileY].solida) return false; // Se houver parede entre inimigo e jogador, não pode ver
            }
            return true; // Linha horizontal livre
        }
        return false; // Jogador não está na mesma linha ou coluna
    }
    //===================================================================

    //======Gera direção diagonal aleatória======
    private int[] direcaoAleatoria() {
        // Gera um número aleatório entre 0 e 2
        int r = (int)(Math.random() * 3);

        // Retorna uma das quatro direções diagonais possíveis
        return switch (r) {
            case 0 -> new int[]{1, 1};    // Diagonal inferior direita
            case 1 -> new int[]{1, -1};   // Diagonal superior direita
            case 2 -> new int[]{-1, 1};   // Diagonal inferior esquerda
            default -> new int[]{-1, -1}; // Diagonal superior esquerda
        };
    }
    //===========================================

    //======Renderização do inimigo======
    public void render(Graphics g, int tileSize) {
        // Converte coordenadas do inimigo para pixels
        int px = (int)(x * tileSize);
        int py = (int)(y * tileSize);

        // Desenha imagem do inimigo na tela
        g.drawImage(imagem, px, py, tileSize, tileSize, null);
    }
    //===========================================
}
