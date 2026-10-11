package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import controlador.GestorArchivos;
import modelo.Disparo;
import modelo.Obstaculo;
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
    private int puntos = 0;
    private int vidas = 3; // Control de vida del jugador

    // Arreglo estático para proyectiles y control de cadencia
    private static final int MAX_DISPAROS = 50;
    private Disparo[] disparos = new Disparo[MAX_DISPAROS];
    private long ultimoDisparoMs = 0;

    // Arreglo estático para obstáculos y generación periódica
    private static final int MAX_OBSTACULOS = 20;
    private Obstaculo[] obstaculos = new Obstaculo[MAX_OBSTACULOS];
    private long ultimoObstaculoMs = 0;
    private static final long INTERVALO_GENERACION_OBSTACULO_MS = 1500; // 1.5 segundos

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
        
        lblPuntaje = new JLabel("Piloto: " + this.piloto.getNombre() + " | Puntos: " + puntos);
        lblVida = new JLabel("Nave: " + this.tipoNave.name() + " | Vidas: " + vidas);
        
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

    private void generarObstaculos() {
        long tiempoActual = System.currentTimeMillis();

        if (tiempoActual - ultimoObstaculoMs >= INTERVALO_GENERACION_OBSTACULO_MS) {
            for (int i = 0; i < MAX_OBSTACULOS; i++) {
                if (obstaculos[i] == null || !obstaculos[i].isActivo()) {
                    int xInicial = panelJuego.getWidth() > 0 ? panelJuego.getWidth() : 780;
                    int altoPanel = panelJuego.getHeight() > 0 ? panelJuego.getHeight() : 350;

                    Obstaculo nuevoObstaculo = new Obstaculo(xInicial, altoPanel);
                    obstaculos[i] = nuevoObstaculo;

                    Thread hiloObstaculo = new Thread(nuevoObstaculo);
                    hiloObstaculo.start();

                    ultimoObstaculoMs = tiempoActual;
                    break;
                }
            }
        }
    }

    private void verificarColisiones() {
        Rectangle recNave = new Rectangle(posicionXNave, posicionYNave, 30, 20);

        for (int i = 0; i < MAX_OBSTACULOS; i++) {
            if (obstaculos[i] != null && obstaculos[i].isActivo()) {
                Rectangle recObstaculo = new Rectangle(
                    obstaculos[i].getX(), 
                    obstaculos[i].getY(), 
                    obstaculos[i].getTamano(), 
                    obstaculos[i].getTamano()
                );

                // 1. Disparo vs Obstáculo
                for (int j = 0; j < MAX_DISPAROS; j++) {
                    if (disparos[j] != null && disparos[j].isActivo()) {
                        Rectangle recDisparo = new Rectangle(disparos[j].getX(), disparos[j].getY(), 10, 4);

                        if (recDisparo.intersects(recObstaculo)) {
                            disparos[j].setActivo(false);
                            obstaculos[i].setActivo(false);
                            puntos += 10;
                            lblPuntaje.setText("Piloto: " + this.piloto.getNombre() + " | Puntos: " + puntos);
                            break;
                        }
                    }
                }

                // 2. Nave vs Obstáculo
                if (obstaculos[i] != null && obstaculos[i].isActivo() && recNave.intersects(recObstaculo)) {
                    obstaculos[i].setActivo(false);
                    vidas--;
                    lblVida.setText("Nave: " + this.tipoNave.name() + " | Vidas: " + vidas);

                    if (vidas <= 0) {
                        enEjecucion = false; // Detiene el Game Loop
                        SwingUtilities.invokeLater(this::finalizarJuego);
                    }
                }
            }
        }
    }

    private void finalizarJuego() {
        // Guardar el puntaje al perder todas las vidas
        GestorArchivos.guardarPuntaje(piloto.getNombre(), puntos, tipoNave.name());

        JOptionPane.showMessageDialog(this, 
            "¡GAME OVER!\nPiloto: " + piloto.getNombre() + "\nPuntaje final: " + puntos + "\n\n¡Puntaje registrado exitosamente!", 
            "Fin de la partida", 
            JOptionPane.INFORMATION_MESSAGE);
        
        this.dispose(); // Cierra la pantalla de batalla
    }

    private void dibujarJuego(Graphics g) {
        // Dibujar Nave Jugador
        g.setColor(Color.CYAN);
        g.fillRect(posicionXNave, posicionYNave, 30, 20);

        g.setColor(Color.WHITE);
        g.drawString(">", posicionXNave + 32, posicionYNave + 15);

        // Dibujar Proyectiles Activos
        g.setColor(Color.YELLOW);
        for (int i = 0; i < MAX_DISPAROS; i++) {
            if (disparos[i] != null && disparos[i].isActivo()) {
                g.fillRect(disparos[i].getX(), disparos[i].getY(), 10, 4);
            } else if (disparos[i] != null && !disparos[i].isActivo()) {
                disparos[i] = null;
            }
        }

        // Dibujar Obstáculos Activos
        g.setColor(Color.RED);
        for (int i = 0; i < MAX_OBSTACULOS; i++) {
            if (obstaculos[i] != null && obstaculos[i].isActivo()) {
                g.fillRect(obstaculos[i].getX(), obstaculos[i].getY(), obstaculos[i].getTamano(), obstaculos[i].getTamano());
            } else if (obstaculos[i] != null && !obstaculos[i].isActivo()) {
                obstaculos[i] = null;
            }
        }
    }

    private void manejarTeclas(KeyEvent e) {
        int key = e.getKeyCode();
        
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

        if (tiempoActual - ultimoDisparoMs >= tipoNave.getTiempoDisparoMs()) {
            for (int i = 0; i < MAX_DISPAROS; i++) {
                if (disparos[i] == null || !disparos[i].isActivo()) {
                    Disparo nuevoDisparo = new Disparo(posicionXNave + 35, posicionYNave + 8, panelJuego.getWidth());
                    disparos[i] = nuevoDisparo;

                    Thread hiloDisparo = new Thread(nuevoDisparo);
                    hiloDisparo.start();

                    ultimoDisparoMs = tiempoActual;
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        while (enEjecucion) {
            generarObstaculos();
            verificarColisiones();
            panelJuego.repaint();

            try {
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