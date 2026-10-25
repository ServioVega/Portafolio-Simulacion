package modelo;

public class Individuo {
    private EstadoSalud estado;

    public Individuo(EstadoSalud estado) {
        this.estado = estado;
    }

    public EstadoSalud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSalud estado) {
        this.estado = estado;
    }
}
