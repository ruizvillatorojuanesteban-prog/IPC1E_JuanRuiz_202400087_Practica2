package modelo;

public class Piloto {
    private String nombre;
    private int puntajeMaximo;
    private int partidasJugadas;

    public Piloto(String nombre) {
        this.nombre = nombre;
        this.puntajeMaximo = 0;
        this.partidasJugadas = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntajeMaximo() {
        return puntajeMaximo;
    }

    public void actualizarPuntaje(int nuevoPuntaje) {
        if (nuevoPuntaje > this.puntajeMaximo) {
            this.puntajeMaximo = nuevoPuntaje;
        }
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void incrementarPartidas() {
        this.partidasJugadas++;
    }
}