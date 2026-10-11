package modelo;

public enum TipoNave {
    EXPLORADOR("Explorador", "Fácil", 2000, 20),      // Alta velocidad / Disparo lento (2.0s)
    CAZA_ESTELAR("Caza Estelar", "Normal", 1000, 15),  // Velocidad media / Disparo medio (1.0s)
    ACORAZADO("Acorazado", "Difícil", 300, 10);        // Velocidad baja / Disparo rápido (0.3s)

    private final String nombre;
    private final String dificultad;
    private final int tiempoDisparoMs;
    private final int velocidadMovimiento;

    TipoNave(String nombre, String dificultad, int tiempoDisparoMs, int velocidadMovimiento) {
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.tiempoDisparoMs = tiempoDisparoMs;
        this.velocidadMovimiento = velocidadMovimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDificultad() {
        return dificultad;
    }

    public int getTiempoDisparoMs() {
        return tiempoDisparoMs;
    }

    public int getVelocidadMovimiento() {
        return velocidadMovimiento;
    }

    @Override
    public String toString() {
        return nombre + " (" + dificultad + ")";
    }
}