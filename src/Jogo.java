//======Importação de bibliotecas======
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//=====================================

//======Classe principal do painel do jogo======
public class Jogo extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    //======Variáveis======
    Mapa mapa;                // referência ao mapa
    Jogador jogador;          // referência ao jogador
    int mouseX = -1, mouseY = -1; // posição do mouse em tiles
    boolean mouseEsquerdoPressionado = false; // controle de clique contínuo
    //====================

    //======Construtor======
    public Jogo() throws Exception {
        mapa = new Mapa();         // cria novo mapa procedural
        jogador = new Jogador();   // cria novo jogador
        setFocusable(true);        // permite foco no painel
        requestFocusInWindow();    // requisita foco
        addKeyListener(this);      // adiciona listener de teclado
        addMouseListener(this);    // adiciona listener de mouse
        addMouseMotionListener(this); // adiciona listener de movimento do mouse
    }
    //======================

    //======Renderização do jogo======
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        mapa.render(g);                     // desenha mapa
        jogador.render(g, mapa.tileSize);   // desenha jogador

        //======Destacar tile sob o mouse======
        if (mouseX >= 0 && mouseY >= 0 && mouseX < mapa.largura && mouseY < mapa.altura) {
            Tile tile = mapa.tiles[mouseX][mouseY];
            boolean ocupado = false;

            // verifica ocupação por jogador
            if (jogador.x == mouseX && jogador.y == mouseY) ocupado = true;

            // verifica ocupação por inimigos
            for (Enemy e : mapa.inimigos) {
                if (e.x == mouseX && e.y == mouseY) {
                    ocupado = true;
                    break;
                }
            }

            // verifica ocupação por itens
            for (Item i : mapa.itens) {
                if (i.x == mouseX && i.y == mouseY) {
                    ocupado = true;
                    break;
                }
            }

            // verifica se é saída
            if (tile.saida) ocupado = true;

            // aplica destaque se não estiver ocupado
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

    //======Controle de teclado======
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        switch (tecla) {
            case KeyEvent.VK_W -> jogador.mover(0, -1, mapa); // cima
            case KeyEvent.VK_S -> jogador.mover(0, 1, mapa);  // baixo
            case KeyEvent.VK_A -> jogador.mover(-1, 0, mapa); // esquerda
            case KeyEvent.VK_D -> jogador.mover(1, 0, mapa);  // direita
            case KeyEvent.VK_BACK_SPACE -> {
                try {
                    mapa = new Mapa();       // reinicia mapa
                    jogador = new Jogador(); // reinicia jogador
                    requestFocusInWindow();  // foca na janela
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        //======Atualiza inimigos com base no movimento do jogador======
        for (Enemy inimigo : mapa.inimigos) {
            inimigo.atualizarSeJogadorMexeu(mapa, jogador);
        }

        //======Verifica colisão com inimigos======
        for (Enemy inimigo : mapa.inimigos) {
            if (inimigo.x == jogador.x && inimigo.y == jogador.y) {
                try {
                    mapa = new Mapa();       // reinicia mapa
                    jogador = new Jogador(); // reinicia jogador
                    requestFocusInWindow();  // foca na janela
                    return;                  // encerra execução atual
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        repaint(); // redesenha tela
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
        int x = e.getX() / mapa.tileSize;
        int y = e.getY() / mapa.tileSize;

        if (x >= 0 && y >= 0 && x < mapa.largura && y < mapa.altura) {
            Tile tile = mapa.tiles[x][y];
            boolean ocupado = false;

            // verifica ocupação por jogador
            if (jogador.x == x && jogador.y == y) ocupado = true;

            // verifica ocupação por inimigos
            for (Enemy enemy : mapa.inimigos) {
                if (enemy.x == x && enemy.y == y) {
                    ocupado = true;
                    break;
                }
            }

            // verifica ocupação por itens
            for (Item item : mapa.itens) {
                if (item.x == x && item.y == y) {
                    ocupado = true;
                    break;
                }
            }

            // verifica se é saída
            if (tile.saida) ocupado = true;

            // alterna entre chão e parede se não estiver ocupado
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
