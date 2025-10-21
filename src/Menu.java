//======Classe responsável pelo menu======
import java.awt.*;

public class Menu {
    //======Renderiza o menu======
    public void render(Graphics g, int largura, int altura) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, largura, altura);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("BACKROOMS SIMULATOR", 60, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("1 - Play", 150, 200);
        g.drawString("2 - Exit", 150, 250);
    }
}
