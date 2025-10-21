//======Importação de bibliotecas======
import javax.sound.sampled.*;
import java.io.File;
//=====================================

//======Classe responsável por reproduzir sons======
public class Som implements Runnable {
    //======Caminho do arquivo de som======
    private String caminho; // caminho do arquivo de áudio
    //=====================================

    //======Construtor======
    public Som(String caminho) {
        this.caminho = caminho; // define o caminho do som
    }
    //======================

    //======Executa som em loop======
    public void run() {
        try {
            File arquivo = new File(caminho); // cria referência ao arquivo
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(arquivo); // abre fluxo de áudio
            Clip clip = AudioSystem.getClip(); // cria objeto de reprodução
            clip.open(audioStream); // carrega áudio no clip
            clip.loop(Clip.LOOP_CONTINUOUSLY); // toca em loop contínuo
            clip.start(); // inicia reprodução
        } catch (Exception e) {
            e.printStackTrace(); // mostra erro no console
        }
    }
    //==================================
}
