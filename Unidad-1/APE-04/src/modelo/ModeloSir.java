package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModeloSir {
    private final int poblacion;
    private final int infectadosIniciales;
    private final double beta;
    private final double gamma;
    private final int tiempoMaximo;
    private final long semilla;
    private final List<Individuo> individuos = new ArrayList<>();

    public ModeloSir(int poblacion, int infectadosIniciales, double beta,
                     double gamma, int tiempoMaximo, long semilla) {
        if (poblacion < 2 || infectadosIniciales < 0 || infectadosIniciales > poblacion
                || beta < 0 || beta > 1 || gamma < 0 || gamma > 1
                || tiempoMaximo < 0) {
            throw new IllegalArgumentException("Parametros del modelo no validos");
        }

        this.poblacion = poblacion;
        this.infectadosIniciales = infectadosIniciales;
        this.beta = beta;
        this.gamma = gamma;
        this.tiempoMaximo = tiempoMaximo;
        this.semilla = semilla;
    }

    public List<RegistroSir> simular() {
        Random aleatorio = new Random(semilla);
        List<RegistroSir> resultados = new ArrayList<>();

        // Crear la poblacion: los primeros 5 infectados y los demas susceptibles.
        individuos.clear();
        for (int i = 0; i < poblacion; i++) {
            EstadoSalud estado = i < infectadosIniciales
                    ? EstadoSalud.INFECTADO : EstadoSalud.SUSCEPTIBLE;
            individuos.add(new Individuo(estado));
        }

        // Registrar el estado inicial, tiempo 0.
        registrarEstado(0, resultados);

        for (int tiempo = 1; tiempo <= tiempoMaximo; tiempo++) {
            // 1. Identificar estados actuales y preparar su copia.
            EstadoSalud[] actuales = new EstadoSalud[poblacion];
            EstadoSalud[] siguientes = new EstadoSalud[poblacion];
            for (int i = 0; i < poblacion; i++) {
                actuales[i] = individuos.get(i).getEstado();
                siguientes[i] = actuales[i];
            }

            // 2. Generar contagios: cada infectado contacta a una persona al azar.
            for (int i = 0; i < poblacion; i++) {
                if (actuales[i] == EstadoSalud.INFECTADO) {
                    int contacto = aleatorio.nextInt(poblacion - 1);
                    if (contacto >= i) {
                        contacto++;
                    }

                    if (actuales[contacto] == EstadoSalud.SUSCEPTIBLE
                            && aleatorio.nextDouble() < beta) {
                        siguientes[contacto] = EstadoSalud.INFECTADO;
                    }
                }
            }

            // 3. Determinar recuperaciones: solo de infectados anteriores.
            for (int i = 0; i < poblacion; i++) {
                if (actuales[i] == EstadoSalud.INFECTADO
                        && aleatorio.nextDouble() < gamma) {
                    siguientes[i] = EstadoSalud.RECUPERADO;
                }
            }

            // 4. Actualizar todos los individuos al mismo tiempo.
            for (int i = 0; i < poblacion; i++) {
                individuos.get(i).setEstado(siguientes[i]);
            }

            // 5. Guardar S, I, R del tiempo actual.
            registrarEstado(tiempo, resultados);
        }

        // 6. Terminar al alcanzar el tiempo maximo.
        return resultados;
    }

    private void registrarEstado(int tiempo, List<RegistroSir> resultados) {
        int susceptibles = 0;
        int infectados = 0;
        int recuperados = 0;

        for (Individuo individuo : individuos) {
            switch (individuo.getEstado()) {
                case SUSCEPTIBLE:
                    susceptibles++;
                    break;
                case INFECTADO:
                    infectados++;
                    break;
                case RECUPERADO:
                    recuperados++;
                    break;
            }
        }

        if (susceptibles + infectados + recuperados != poblacion) {
            throw new IllegalStateException("La poblacion total ha cambiado");
        }

        resultados.add(new RegistroSir(tiempo, susceptibles, infectados, recuperados));
    }
}
