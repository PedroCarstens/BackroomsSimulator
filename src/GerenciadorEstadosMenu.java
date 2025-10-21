//======Classe que gerencia os estados do MENU do jogo======
public class GerenciadorEstadosMenu {
    //======Estado atual======
    private EstadoJogo estadoAtual = EstadoJogo.MENU; // começa no menu
    //========================

    //======Getter======
    public EstadoJogo getEstado() {
        return estadoAtual;
    }
    //==================

    //======Setter======
    public void setEstado(EstadoJogo novoEstado) {
        this.estadoAtual = novoEstado;
    }
    //==================

    //======Verifica se está em um estado específico======
    public boolean estaEm(EstadoJogo estado) {
        return this.estadoAtual == estado;
    }
    //====================================================
}
