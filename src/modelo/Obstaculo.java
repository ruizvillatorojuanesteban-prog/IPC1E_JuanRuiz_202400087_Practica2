package modelo;

import java.util.Random;

public class Obstaculo implements Runnable {
    private int x;
    private int y;
    private int velocidad;
    private int tamano = 25;
    private boolean activo;

    public Obstaculo(int xInicio, int altoPantalla) {
        Random rand = new Random();
        this.x = xInicio;
        // Genera una posición Y aleatoria dentro del área visible del juego
        this.y = rand.nextInt(altoPantalla - 80) + 10;
        // Velocidad aleatoria entre 3 y 8 píxeles por ciclo
        this.velocidad = rand.nextInt(6) + 3;
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo && x + tamano > 0) {
            x -= velocidad;

            try {
                // Tasa de movimiento del obstáculo (30 ms)
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                activo = false;
            }
        }
        activo = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTamano() {
        return tamano;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}