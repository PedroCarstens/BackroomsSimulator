//======Importação de bibliotecas======
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//=====================================

//======Classe principal do painel do jogo======
public class Jogo extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    //======Variáveis======
    private Mapa mapa;                // referência ao mapa
    private Jogador jogador;          // referência ao jogador
    private Menu menu;                // referência ao menu
    private GerenciadorEstadosMenu estados; // gerenciador de estados do menu
    int mouseX = -1, mouseY = -1;     // posição do mouse em tiles
    boolean mouseEsquerdoPressionado = false; // controle de clique contínuo
    //====================

    //======Construtor======
    public Jogo() throws Exception {
        mapa = new Mapa();         // cria novo mapa procedural
        jogador = new Jogador();   // cria novo jogador
        menu = new Menu();         // cria menu
        estados = new GerenciadorEstadosMenu(); // cria gerenciador de estados

        setFocusable(true);        // permite foco no painel
        requestFocusInWindow();    // requisita foco
        addKeyListener(this);      // adiciona listener de teclado
        addMouseListener(this);    // adiciona listener de mouse
        addMouseMotionListener(this); // adiciona listener de movimento do mouse
    }
    //======================

    //======Getters para Main acessar======
    public int getLarguraMapa() {
        return mapa.largura;
    }

    public int getAlturaMapa() {
        return mapa.altura;
    }

    public int getTileSize() {
        return mapa.tileSize;
    }
    //=====================================

    //======Renderização do jogo======
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (estados.estaEm(EstadoJogo.MENU)) {
            menu.render(g, getWidth(), getHeight()); // desenha menu
        } else if (estados.estaEm(EstadoJogo.JOGANDO)) {
            mapa.render(g);                          // desenha mapa
            jogador.render(g, mapa.tileSize);        // desenha jogador
        }
    }
    //========================

    //======Controle de teclado======
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        if (estados.estaEm(EstadoJogo.MENU)) {
            switch (tecla) {
                case KeyEvent.VK_1 -> { // Play
                    try {
                        mapa = new Mapa();       // reinicia mapa
                        jogador = new Jogador(); // reinicia jogador
                        estados.setEstado(EstadoJogo.JOGANDO);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    repaint();
                }
                case KeyEvent.VK_2 -> System.exit(0); // Exit
            }
        } else if (estados.estaEm(EstadoJogo.JOGANDO)) {
            switch (tecla) {
                case KeyEvent.VK_W -> jogador.mover(0, -1, mapa); // cima
                case KeyEvent.VK_S -> jogador.mover(0, 1, mapa);  // baixo
                case KeyEvent.VK_A -> jogador.mover(-1, 0, mapa); // esquerda
                case KeyEvent.VK_D -> jogador.mover(1, 0, mapa);  // direita
                case KeyEvent.VK_ESCAPE -> estados.setEstado(EstadoJogo.MENU); // volta ao menu
                case KeyEvent.VK_BACK_SPACE -> { //======Regera o mapa======
                    try {
                        mapa = new Mapa();       // cria novo mapa procedural
                        jogador = new Jogador(); // reseta jogador
                        System.out.println("Novo mapa gerado!"); // debug
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    repaint();
                }
            }

            //======Verifica colisão com inimigos======
            for (Enemy inimigo : mapa.inimigos) {
                if (inimigo.x == jogador.x && inimigo.y == jogador.y) {
                    estados.setEstado(EstadoJogo.MENU); // volta ao menu
                    return;
                }
            }

            //======Verifica se jogador chegou na saída======
            if (mapa.saidaX == jogador.x && mapa.saidaY == jogador.y) {
                estados.setEstado(EstadoJogo.MENU); // volta ao menu
                return;
            }

            repaint();
        }
    }
    //==================

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    //==================

    //======Movimento do mouse======
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX() / mapa.tileSize;
        mouseY = e.getY() / mapa.tileSize;
        repaint();
    }

    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
        if (mouseEsquerdoPressionado) {
            aplicarEdicao(e);
        }
    }
    //===========================

    //======Clique do mouse======
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseEsquerdoPressionado = true;
            aplicarEdicao(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseEsquerdoPressionado = false;
        }
    }

    public void mouseClicked(MouseEvent e) {} // não usado
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    //=========================

    //======Aplica edição de terreno com o mouse======
    private void aplicarEdicao(MouseEvent e) {
        if (!estados.estaEm(EstadoJogo.JOGANDO)) return; // só edita no jogo

        int x = e.getX() / mapa.tileSize;
        int y = e.getY() / mapa.tileSize;

        if (x >= 0 && y >= 0 && x < mapa.largura && y < mapa.altura) {
            Tile tile = mapa.tiles[x][y];
            boolean ocupado = false;

            if (jogador.x == x && jogador.y == y) ocupado = true;

            for (Enemy enemy : mapa.inimigos) {
                if (enemy.x == x && enemy.y == y) {
                    ocupado = true;
                    break;
                }
            }

            for (Item item : mapa.itens) {
                if (item.x == x && item.y == y) {
                    ocupado = true;
                    break;
                }
            }

            if (tile.saida) ocupado = true;

            if (!ocupado) {
                if (tile.solida) {
                    mapa.tiles[x][y] = mapa.tileChao;
                } else {
                    mapa.tiles[x][y] = mapa.tileParede;
                }
                repaint();
            }
        }
    }
    //====================================
}
