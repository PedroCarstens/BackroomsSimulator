//======Importação de bibliotecas======
import java.awt.*;
import java.util.*;
//=====================================

//======Classe que representa um inimigo======
public class Enemy {
    //======Posição do inimigo======
    public int x, y;
    //==============================

    //======Gerenciador de estados======
    private GerenciadorEstadoInimigo estados = new GerenciadorEstadoInimigo();
    //==================================

    //======Parâmetros de visão======
    private int alcanceVisao = 6;
    //===============================

    //======Cor do inimigo======
    private Color cor = Color.GRAY;
    //==========================

    //======Histórico de movimento======
    private java.util.List<Point> historico = new ArrayList<>();
    //==================================

    //======Construtor======
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }
    //======================

    //======Atualiza comportamento do inimigo======
    public void atualizarSeJogadorMexeu(Mapa mapa, Jogador jogador) {
        //======Verifica se jogador está visível======
        if (podeVerJogador(mapa, jogador, alcanceVisao)) {
            estados.setEstado(EstadoInimigo.PERSEGUINDO);
            cor = Color.RED;
        } else {
            estados.setEstado(EstadoInimigo.VAGANDO);
            cor = Color.GRAY;
        }

        //======Executa ação com base no estado======
        if (estados.estaEm(EstadoInimigo.PERSEGUINDO)) {
            perseguirJogador(mapa, jogador);
        } else if (estados.estaEm(EstadoInimigo.VAGANDO)) {
            vagarAleatoriamente(mapa);
        }
    }
    //====================================================

    //======Verifica se jogador está visível======
    private boolean podeVerJogador(Mapa mapa, Jogador jogador, int alcance) {

        int dx = jogador.x - x;
        int dy = jogador.y - y;

        if (Math.abs(dx) + Math.abs(dy) > alcance) return false;

        if (x == jogador.x) {
            int minY = Math.min(y, jogador.y);
            int maxY = Math.max(y, jogador.y);
            for (int i = minY + 1; i < maxY; i++) {
                if (mapa.tiles[x][i].solida) return false;
            }
            return true;
        }

        if (y == jogador.y) {
            int minX = Math.min(x, jogador.x);
            int maxX = Math.max(x, jogador.x);
            for (int i = minX + 1; i < maxX; i++) {
                if (mapa.tiles[i][y].solida) return false;
            }
            return true;
        }

        return false;
    }
    //====================================================

    //======Movimento de perseguição======
    private void perseguirJogador(Mapa mapa, Jogador jogador) {
        System.out.println("I see you");
        int dx = Integer.compare(jogador.x, x);
        int dy = Integer.compare(jogador.y, y);
        int nx = x + dx;
        int ny = y + dy;

        if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
            x = nx;
            y = ny;
            historico.add(new Point(x, y));
            if (historico.size() > 10) historico.remove(0);
        }
    }
    //====================================

    //======Movimento aleatório======
    private void vagarAleatoriamente(Mapa mapa) {
        java.util.List<Point> direcoes = Arrays.asList(
                new Point(0, -1),
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0)
        );
        Collections.shuffle(direcoes);

        for (Point dir : direcoes) {
            int nx = x + dir.x;
            int ny = y + dir.y;
            if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
                x = nx;
                y = ny;
                historico.add(new Point(x, y));
                if (historico.size() > 10) historico.remove(0);
                return;
            }
        }
    }
    //====================================

    //======Renderiza inimigo======
    public void render(Graphics g, int tileSize) {
        g.setColor(cor);
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
    }
    //=============================
}
