import javax.sound.sampled.*;
import java.io.File;

public class Som implements Runnable {
    //======Caminho do arquivo de som======
    private String caminho;

    //======Construtor======
    public Som(String caminho) {
        this.caminho = caminho;
    }

    //======Executa som em loop======
    public void run() {
        try {
            File arquivo = new File(caminho);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivo);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // toca em loop
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
