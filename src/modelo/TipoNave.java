package modelo;

public enum TipoNave {
    EXPLORADOR("Fácil", 2000),      // Recarga disparo: 2.0 seg
    CAZA_ESTELAR("Normal", 1000),   // Recarga disparo: 1.0 seg
    ACORAZADO("Difícil", 300);       // Recarga disparo: 0.3 seg

    private final String dificultad;
    private final int tiempoDisparoMs;

    TipoNave(String dificultad, int tiempoDisparoMs) {
        this.dificultad = dificultad;
        this.tiempoDisparoMs = tiempoDisparoMs;
    }

    public String getDificultad() {
        return dificultad;
    }

    public int getTiempoDisparoMs() {
        return tiempoDisparoMs;
    }
}