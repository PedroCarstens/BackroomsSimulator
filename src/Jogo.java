import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Jogo extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    //======Variáveis======
    Mapa mapa;
    Jogador jogador;
    int mouseX = -1, mouseY = -1; // posição do mouse em tiles
    //====================

    //======Construtor======
    public Jogo() throws Exception {
        mapa = new Mapa();
        jogador = new Jogador();
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseListener(this);         // escuta cliques
        addMouseMotionListener(this);   // escuta movimento do mouse
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

            // verifica se é a posição do jogador
            if (jogador.x == mouseX && jogador.y == mouseY) ocupado = true;

            // verifica se é a posição de algum inimigo
            for (Enemy e : mapa.inimigos) {
                if (e.x == mouseX && e.y == mouseY) {
                    ocupado = true;
                    break;
                }
            }

            // verifica se é a posição de algum item
            for (Item i : mapa.itens) {
                if (i.x == mouseX && i.y == mouseY) {
                    ocupado = true;
                    break;
                }
            }

            // verifica se é saída
            if (tile.saida) ocupado = true;

            // só desenha se não estiver ocupado
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
            case KeyEvent.VK_W -> {
                jogador.mover(0, -1, mapa);
                System.out.println("Movendo para cima");
            }
            case KeyEvent.VK_S -> {
                jogador.mover(0, 1, mapa);
                System.out.println("Movendo para baixo");
            }
            case KeyEvent.VK_A -> {
                jogador.mover(-1, 0, mapa);
                System.out.println("Movendo para esquerda");
            }
            case KeyEvent.VK_D -> {
                jogador.mover(1, 0, mapa);
                System.out.println("Movendo para direita");
            }
            case KeyEvent.VK_BACK_SPACE -> {
                try {
                    mapa = new Mapa();
                    jogador = new Jogador();
                    System.out.println("Mapa regenerado com sucesso.");
                    requestFocusInWindow();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        repaint();
    }

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
        mouseMoved(e); // trata como movimento
    }
    //===========================

    //======Mouse clique======
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / mapa.tileSize;
        int y = e.getY() / mapa.tileSize;

        if (x >= 0 && y >= 0 && x < mapa.largura && y < mapa.altura) {
            Tile tile = mapa.tiles[x][y];

            if (SwingUtilities.isLeftMouseButton(e)) {
                //======Clique esquerdo: quebra parede======
                if (tile.solida && !tile.saida) {
                    mapa.tiles[x][y] = mapa.tileChao;
                    System.out.println("Parede quebrada em (" + x + "," + y + ")");
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                //======Clique direito: cria parede======
                if (!tile.solida && !tile.saida) {
                    mapa.tiles[x][y] = mapa.tileParede;
                    System.out.println("Parede criada em (" + x + "," + y + ")");
                }
            }


            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    //=========================
}
