package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import modelo.Disparo;
import modelo.Piloto;
import modelo.TipoNave;

public class CampoBatalla extends JFrame implements Runnable {
    private Piloto piloto;
    private TipoNave tipoNave;

    // Componentes del juego
    private JPanel panelJuego;
    private JLabel lblPuntaje;
    private JLabel lblVida;

    // Control de juego e hilos
    private boolean enEjecucion;
    private Thread hiloPrincipal;
    private int posicionXNave = 50;
    private int posicionYNave = 150;

    // Arreglo estático para proyectiles y control de cadencia
    private static final int MAX_DISPAROS = 50;
    private Disparo[] disparos = new Disparo[MAX_DISPAROS];
    private long ultimoDisparoMs = 0;

    public CampoBatalla(Piloto piloto, TipoNave tipoNave) {
        this.piloto = piloto;
        this.tipoNave = tipoNave;
        this.enEjecucion = true;

        setTitle("Quetzal Space Defender - Campo de Batalla");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Superior para HUD (Puntaje y Estado)
        JPanel panelHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        
        // Uso explícito de this.piloto para eliminar la advertencia de campo no usado
        lblPuntaje = new JLabel("Piloto: " + this.piloto.getNombre() + " | Puntos: 0");
        lblVida = new JLabel("Nave: " + this.tipoNave.name() + " | Dificultad: " + this.tipoNave.getDificultad());
        
        panelHUD.add(lblPuntaje);
        panelHUD.add(lblVida);

        // Panel donde se dibuja el campo de batalla
        panelJuego = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarJuego(g);
            }
        };
        panelJuego.setBackground(Color.BLACK);

        add(panelHUD, BorderLayout.NORTH);
        add(panelJuego, BorderLayout.CENTER);

        // Controles por teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                manejarTeclas(e);
            }
        });

        // Iniciar el hilo del ciclo de juego
        hiloPrincipal = new Thread(this);
        hiloPrincipal.start();
    }

    private void dibujarJuego(Graphics g) {
        // Dibujar Nave Jugador (Representación gráfica temporal)
        g.setColor(Color.CYAN);
        g.fillRect(posicionXNave, posicionYNave, 30, 20);

        // Dibujar detalles o bordes de la nave
        g.setColor(Color.WHITE);
        g.drawString(">", posicionXNave + 32, posicionYNave + 15);

        // Dibujar Proyectiles Activos
        g.setColor(Color.YELLOW);
        for (int i = 0; i < MAX_DISPAROS; i++) {
            if (disparos[i] != null && disparos[i].isActivo()) {
                g.fillRect(disparos[i].getX(), disparos[i].getY(), 10, 4);
            } else if (disparos[i] != null && !disparos[i].isActivo()) {
                // Liberar la casilla del arreglo cuando el hilo finaliza
                disparos[i] = null;
            }
        }
    }

    private void manejarTeclas(KeyEvent e) {
        int key = e.getKeyCode();
        
        // Movimiento básico de la nave
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            if (posicionYNave > 10) posicionYNave -= 15;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            if (posicionYNave < panelJuego.getHeight() - 30) posicionYNave += 15;
        }
        if (key == KeyEvent.VK_SPACE) {
            disparar();
        }
    }

    private void disparar() {
        long tiempoActual = System.currentTimeMillis();

        // Control de cadencia de fuego según el tipo de nave
        if (tiempoActual - ultimoDisparoMs >= tipoNave.getTiempoDisparoMs()) {
            for (int i = 0; i < MAX_DISPAROS; i++) {
                if (disparos[i] == null || !disparos[i].isActivo()) {
                    Disparo nuevoDisparo = new Disparo(posicionXNave + 35, posicionYNave + 8, panelJuego.getWidth());
                    disparos[i] = nuevoDisparo;

                    // Instanciar e iniciar el hilo independiente para el proyectil
                    Thread hiloDisparo = new Thread(nuevoDisparo);
                    hiloDisparo.start();

                    ultimoDisparoMs = tiempoActual;
                    System.out.println("¡Disparo efectuado! Cadencia: " + tipoNave.getTiempoDisparoMs() + "ms");
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        // Ciclo principal del juego (Game Loop)
        while (enEjecucion) {
            panelJuego.repaint();

            try {
                // Tasa de refresco constante para el renderizado (~60 FPS)
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                enEjecucion = false;
            }
        }
    }

    public Piloto getPiloto() {
        return piloto;
    }
}