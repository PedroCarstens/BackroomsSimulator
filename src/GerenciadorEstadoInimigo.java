//======Classe que gerencia os estados do inimigo======
public class GerenciadorEstadoInimigo {
    private EstadoInimigo estadoAtual = EstadoInimigo.VAGANDO;

    //======Getter======
    public EstadoInimigo getEstado() {
        return estadoAtual;
    }

    //======Setter======
    public void setEstado(EstadoInimigo novoEstado) {
        this.estadoAtual = novoEstado;
    }

    //======Verifica se está em um estado específico======
    public boolean estaEm(EstadoInimigo estado) {
        return this.estadoAtual == estado;
    }
}
