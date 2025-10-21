import java.awt.*;
import java.util.*;

public class Enemy {
    //======Posição do inimigo======
    public int x, y; // coordenadas do inimigo

    //======Estado atual======
    private EstadoInimigo estado = EstadoInimigo.VAGANDO; // estado inicial

    //======Parâmetros de visão======
    private int alcanceVisao = 6; // número máximo de tiles visíveis
    //===============================

    //======Cor do inimigo======
    private Color cor = Color.GRAY; // cor padrão (cinza)
    //==========================

    //======Histórico de movimento======
    private java.util.List<Point> historico = new ArrayList<>(); // memória de posições anteriores
    //==================================

    //======Construtor======
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }
    //======================

    //======Atualiza comportamento do inimigo se o jogador se mover======
    public void atualizarSeJogadorMexeu(Mapa mapa, Jogador jogador) {
        //======Verifica se jogador está visível======
        if (podeVerJogador(mapa, jogador, alcanceVisao)) {
            estado = EstadoInimigo.PERSEGUINDO;
            cor = Color.RED; // muda cor para vermelho
        } else {
            estado = EstadoInimigo.VAGANDO;
            cor = Color.GRAY; // volta para cinza
        }

        //======Debug: imprime estado atual do inimigo======
        System.out.println("Inimigo em (" + x + "," + y + ") está no estado: " + estado);

        //======Executa movimento com prioridade======
        moverComPrioridade(mapa, jogador);
    }
    //======================================================

    //======Verifica se jogador está visível com base em distância e paredes======
    private boolean podeVerJogador(Mapa mapa, Jogador jogador, int alcance) {
        int dx = jogador.x - x;
        int dy = jogador.y - y;

        //======Verifica distância Manhattan======
        if (Math.abs(dx) + Math.abs(dy) > alcance) return false;

        //======Verifica linha reta horizontal======
        if (x == jogador.x) {
            int minY = Math.min(y, jogador.y);
            int maxY = Math.max(y, jogador.y);
            for (int i = minY + 1; i < maxY; i++) {
                if (mapa.tiles[x][i].solida) return false; // parede bloqueia visão
            }
            return true;
        }

        //======Verifica linha reta vertical======
        if (y == jogador.y) {
            int minX = Math.min(x, jogador.x);
            int maxX = Math.max(x, jogador.x);
            for (int i = minX + 1; i < maxX; i++) {
                if (mapa.tiles[i][y].solida) return false; // parede bloqueia visão
            }
            return true;
        }

        return false; // não está em linha reta
    }
    //===================================================================

    //======Movimento com prioridade======
    private void moverComPrioridade(Mapa mapa, Jogador jogador) {
        //======1. Perseguir jogador======
        if (estado == EstadoInimigo.PERSEGUINDO) {
            int dx = Integer.compare(jogador.x, x);
            int dy = Integer.compare(jogador.y, y);
            int nx = x + dx;
            int ny = y + dy;

            if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
                x = nx;
                y = ny;
                historico.add(new Point(x, y));
                if (historico.size() > 10) historico.remove(0);
                return;
            }
        }

        //======2. Evitar repetir direções recentes======
        java.util.List<Point> direcoes = Arrays.asList(
                new Point(0, -1), // cima
                new Point(1, 0),  // direita
                new Point(0, 1),  // baixo
                new Point(-1, 0)  // esquerda
        );

        for (Point dir : direcoes) {
            int nx = x + dir.x;
            int ny = y + dir.y;
            Point destino = new Point(nx, ny);

            if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida && !historico.contains(destino)) {
                x = nx;
                y = ny;
                historico.add(destino);
                if (historico.size() > 10) historico.remove(0);
                return;
            }
        }

        //======3. Movimento aleatório válido======
        Collections.shuffle(direcoes); // embaralha direções

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

    //======Renderiza inimigo com cor baseada no estado======
    public void render(Graphics g, int tileSize) {
        g.setColor(cor); // define cor atual
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize); // desenha inimigo
    }
    //=======================================================
}
