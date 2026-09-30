package servicio;

import modelo.Piloto;

public class PilotoService {
    private static final int MAX_PILOTOS = 50; // Capacidad máxima con arreglo estático
    private Piloto[] pilotos;
    private int cantidadPilotos;

    public PilotoService() {
        this.pilotos = new Piloto[MAX_PILOTOS];
        this.cantidadPilotos = 0;
    }

    /**
     * Registra un nuevo piloto si no existe y si hay espacio en el arreglo estático.
     */
    public boolean registrarPiloto(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        String nombreLimpio = nombre.trim();

        // Validar si ya existe
        if (buscarPiloto(nombreLimpio) != null) {
            return false;
        }

        // Validar espacio en arreglo estático
        if (cantidadPilotos >= MAX_PILOTOS) {
            return false;
        }

        pilotos[cantidadPilotos] = new Piloto(nombreLimpio);
        cantidadPilotos++;
        return true;
    }

    /**
     * Busca un piloto por su nombre.
     */
    public Piloto buscarPiloto(String nombre) {
        if (nombre == null) return null;

        for (int i = 0; i < cantidadPilotos; i++) {
            if (pilotos[i].getNombre().equalsIgnoreCase(nombre.trim())) {
                return pilotos[i];
            }
        }
        return null;
    }

    /**
     * Devuelve una copia ajustada con los pilotos registrados hasta el momento.
     */
    public Piloto[] getPilotos() {
        Piloto[] copia = new Piloto[cantidadPilotos];
        for (int i = 0; i < cantidadPilotos; i++) {
            copia[i] = pilotos[i];
        }
        return copia;
    }

    public int getCantidadPilotos() {
        return cantidadPilotos;
    }
}