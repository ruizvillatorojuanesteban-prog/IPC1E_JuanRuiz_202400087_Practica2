package modelo;

public class Disparo implements Runnable {
    private int x;
    private int y;
    private int velocidad = 10;
    private boolean activo;
    private int limiteX;

    public Disparo(int xInicio, int yInicio, int limiteX) {
        this.x = xInicio;
        this.y = yInicio;
        this.limiteX = limiteX;
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo && x < limiteX) {
            x += velocidad;

            try {
                // Velocidad de avance del proyectil (30 ms)
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                activo = false;
            }
        }
        // Al salir de la pantalla o ser desactivado, se marca inactivo
        activo = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}