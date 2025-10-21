import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Jogo extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    //======Variáveis======
    Mapa mapa;
    Jogador jogador;
    int mouseX = -1, mouseY = -1; // posição do mouse em tiles
    boolean mouseEsquerdoPressionado = false; // controle de clique contínuo
    //====================

    //======Construtor======
    public Jogo() throws Exception {
        mapa = new Mapa();
        jogador = new Jogador();
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    //======================

    //======Renderização======
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        mapa.render(g);
        jogador.render(g, mapa.tileSize);

        //======Destacar tile sob o mouse======
        if (mouseX >= 0 && mouseY >= 0 && mouseX < mapa.largura && mouseY < mapa.altura) {
            Tile tile = mapa.tiles[mouseX][mouseY];
            boolean ocupado = false;

            if (jogador.x == mouseX && jogador.y == mouseY) ocupado = true;

            for (Enemy e : mapa.inimigos) {
                if (e.x == mouseX && e.y == mouseY) {
                    ocupado = true;
                    break;
                }
            }

            for (Item i : mapa.itens) {
                if (i.x == mouseX && i.y == mouseY) {
                    ocupado = true;
                    break;
                }
            }

            if (tile.saida) ocupado = true;

            if (!ocupado) {
                if (tile.solida) {
                    g.setColor(new Color(255, 255, 255, 100)); // branco translúcido para parede
                } else {
                    g.setColor(new Color(0, 0, 0, 100)); // preto translúcido para chão
                }
                g.fillRect(mouseX * mapa.tileSize, mouseY * mapa.tileSize, mapa.tileSize, mapa.tileSize);
            }
        }
    }
    //========================

    //======Teclas======
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        switch (tecla) {
            case KeyEvent.VK_W -> jogador.mover(0, -1, mapa); // cima
            case KeyEvent.VK_S -> jogador.mover(0, 1, mapa);  // baixo
            case KeyEvent.VK_A -> jogador.mover(-1, 0, mapa); // esquerda
            case KeyEvent.VK_D -> jogador.mover(1, 0, mapa);  // direita
            case KeyEvent.VK_BACK_SPACE -> {
                try {
                    mapa = new Mapa(); // reinicia mapa
                    jogador = new Jogador(); // reinicia jogador
                    requestFocusInWindow(); // foca na janela
                } catch (Exception ex) {
                    ex.printStackTrace(); // mostra erro
                }
            }
        }

        //======Atualiza inimigos com base no movimento do jogador======
        for (Enemy inimigo : mapa.inimigos) {
            inimigo.atualizarSeJogadorMexeu(mapa, jogador); // aplica lógica de IA
        }
        //==============================================================

        repaint(); // redesenha tela
    }
    //==================
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    //==================

    //======Mouse movimento======
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

    //======Mouse clique======
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

    //======Aplica edição de terreno======
    private void aplicarEdicao(MouseEvent e) {
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
