import java.awt.*;
import java.util.Iterator;

public class Jogador {
    //======Posição do jogador======
    public int x = 1, y = 1; // posição inicial
    //==============================

    //======Construtor======
    public Jogador() {
        // posição inicial já definida
    }
    //======================

    //======Movimento do jogador======
    public void mover(int dx, int dy, Mapa mapa) {
        int nx = x + dx;
        int ny = y + dy;

        //======Verifica se destino é válido e não sólido======
        if (mapa.valido(nx, ny) && !mapa.tiles[nx][ny].solida) {
            x = nx;
            y = ny;

            //======Verifica se há item na nova posição======
            Iterator<Item> iterador = mapa.itens.iterator();
            while (iterador.hasNext()) {
                Item item = iterador.next();
                if (item.x == x && item.y == y) {
                    iterador.remove(); // remove item coletado
                    break;
                }
            }
            //==============================================

            //======Verifica se chegou na saída======
            if (mapa.saidaX == x && mapa.saidaY == y) {
                System.out.println("Você escapou das Backrooms!");
                System.exit(0); // encerra o programa
            }
            //=======================================
        }
    }
    //===============================

    //======Renderiza jogador======
    public void render(Graphics g, int tileSize) {
        g.setColor(Color.BLUE); // cor do jogador
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize); // desenha jogador
    }
    //============================
}
