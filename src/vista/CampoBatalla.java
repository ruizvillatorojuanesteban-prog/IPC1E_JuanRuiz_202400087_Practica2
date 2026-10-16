package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import controlador.GestorArchivos;
import controlador.GeneradorReporteHTML;
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
    private JLabel lblEstadoEfecto;

    // Control de juego e hilos
    private boolean enEjecucion;
    private Thread hiloPrincipal;
    private int posicionXNave = 50;
    private int posicionYNave = 180;
    private int puntos = 0;

    // Control de bloqueo / ralentización por asteroide gris (2 segundos)
    private boolean naveBloqueada = false;
    private long tiempoBloqueoFin = 0;

    // Disparos rápidos
    private static final int MAX_DISPAROS = 100; 
    private Disparo[] disparos = new Disparo[MAX_DISPAROS];
    private long ultimoDisparoMs = 0;
    private static final long CADENCIA_DISPARO_MS = 150;

    // Obstáculos, elementos del campo y tipos aleatorios
    private static final int MAX_OBSTACULOS = 20;
    private Obstaculo[] obstaculos = new Obstaculo[MAX_OBSTACULOS];
    private int[] tiposObstaculos = new int[MAX_OBSTACULOS]; // 0: Rojo, 1: Verde, 2: Morado, 3: Gris
    private long ultimoObstaculoMs = 0;
    private static final long INTERVALO_GENERACION_OBSTACULO_MS = 1300; 

    public CampoBatalla(Piloto piloto, TipoNave tipoNave) {
        this.piloto = piloto;
        this.tipoNave = tipoNave;
        this.enEjecucion = true;

        setTitle("Quetzal Space Defender - Campo de Batalla");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Superior para HUD
        JPanel panelHUD = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        
        lblPuntaje = new JLabel("Piloto: " + this.piloto.getNombre() + " | Puntos: " + puntos);
        lblEstadoEfecto = new JLabel("");
        lblEstadoEfecto.setForeground(Color.RED);
        
        panelHUD.add(lblPuntaje);
        panelHUD.add(lblEstadoEfecto);

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

        setFocusable(true);
        requestFocusInWindow();

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

                    // Tipo aleatorio: 0 = Enemigo Rojo, 1 = Cápsula Verde, 2 = Núcleo Morado, 3 = Asteroide Gris
                    tiposObstaculos[i] = (int) (Math.random() * 4);

                    Thread hiloObstaculo = new Thread(nuevoObstaculo);
                    hiloObstaculo.start();

                    ultimoObstaculoMs = tiempoActual;
                    break;
                }
            }
        }
    }

    private void verificarColisiones() {
        long tiempoActual = System.currentTimeMillis();

        // Control de expiración del bloqueo/ralentización por asteroide gris (2 segundos)
        if (naveBloqueada && tiempoActual >= tiempoBloqueoFin) {
            naveBloqueada = false;
            lblEstadoEfecto.setText("");
        }

        Rectangle recNave = new Rectangle(posicionXNave, posicionYNave, 30, 20);

        for (int i = 0; i < MAX_OBSTACULOS; i++) {
            if (obstaculos[i] != null && obstaculos[i].isActivo()) {
                Rectangle recObstaculo = new Rectangle(
                    obstaculos[i].getX(), 
                    obstaculos[i].getY(), 
                    obstaculos[i].getTamano(), 
                    obstaculos[i].getTamano()
                );

                // 1. Disparo vs Elemento/Obstáculo
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

                // 2. Nave vs Elementos del Campo
                if (obstaculos[i] != null && obstaculos[i].isActivo() && recNave.intersects(recObstaculo)) {
                    int tipo = tiposObstaculos[i];

                    if (tipo == 0) {
                        // Enemigo (Rojo): Game Over inmediato
                        obstaculos[i].setActivo(false);
                        enEjecucion = false;
                        SwingUtilities.invokeLater(this::finalizarJuego);
                        break;
                    } 
                    else if (tipo == 1) {
                        // Cápsula de Suministro (Verde): Suma 10 puntos
                        puntos += 10;
                        lblPuntaje.setText("Piloto: " + this.piloto.getNombre() + " | Puntos: " + puntos);
                        obstaculos[i].setActivo(false);
                    } 
                    else if (tipo == 2) {
                        // Núcleo de Energía (Morado): +150 puntos y destruye todos los visibles
                        puntos += 150;
                        lblPuntaje.setText("Piloto: " + this.piloto.getNombre() + " | Puntos: " + puntos);
                        
                        for (int k = 0; k < MAX_OBSTACULOS; k++) {
                            if (obstaculos[k] != null) {
                                obstaculos[k].setActivo(false);
                            }
                        }
                    } 
                    else {
                        // Asteroide (Bolita Gris): Ralentiza / bloquea la nave por 2 segundos
                        naveBloqueada = true;
                        tiempoBloqueoFin = tiempoActual + 2000; // 2 segundos exactos
                        lblEstadoEfecto.setText(" ¡ASTEROIDE: NAVE BLOQUEADA (2s)! ");
                        obstaculos[i].setActivo(false);
                    }
                }
            }
        }
    }

    private void finalizarJuego() {
        GestorArchivos.guardarPuntaje(piloto.getNombre(), puntos, tipoNave.name());

        // Actualiza y genera la gráfica y el reporte HTML automáticamente con los nuevos datos
        GeneradorReporteHTML.exportarReporteHTML();

        JOptionPane.showMessageDialog(this, 
            "¡GAME OVER!\nPiloto: " + piloto.getNombre() + "\nPuntaje final: " + puntos + "\n\n¡Puntaje registrado y reporte actualizado exitosamente!", 
            "Fin de la partida", 
            JOptionPane.INFORMATION_MESSAGE);
        
        this.dispose();
    }

    private void dibujarJuego(Graphics g) {
        // La nave cambia a color naranja mientras está ralentizada/bloqueada
        g.setColor(naveBloqueada ? Color.ORANGE : Color.CYAN);
        g.fillRect(posicionXNave, posicionYNave, 30, 20);

        g.setColor(Color.WHITE);
        g.drawString(">", posicionXNave + 32, posicionYNave + 15);

        // Dibujar Proyectiles
        g.setColor(Color.YELLOW);
        for (int i = 0; i < MAX_DISPAROS; i++) {
            if (disparos[i] != null && disparos[i].isActivo()) {
                g.fillRect(disparos[i].getX(), disparos[i].getY(), 10, 4);
            } else if (disparos[i] != null && !disparos[i].isActivo()) {
                disparos[i] = null;
            }
        }

        // Dibujar elementos activos
        for (int i = 0; i < MAX_OBSTACULOS; i++) {
            if (obstaculos[i] != null && obstaculos[i].isActivo()) {
                int tipo = tiposObstaculos[i];
                int x = obstaculos[i].getX();
                int y = obstaculos[i].getY();
                int tam = obstaculos[i].getTamano();

                if (tipo == 0) {
                    g.setColor(Color.RED); // Enemigo Rojo
                    g.fillRect(x, y, tam, tam);
                } else if (tipo == 1) {
                    g.setColor(Color.GREEN); // Cápsula Verde (+10 pts)
                    g.fillRect(x, y, tam, tam);
                } else if (tipo == 2) {
                    g.setColor(Color.MAGENTA); // Núcleo Morado (+150 pts)
                    g.fillRect(x, y, tam, tam);
                } else {
                    g.setColor(Color.GRAY); // Asteroide Gris (Bolita que ralentiza 2s)
                    g.fillOval(x, y, tam, tam);
                }
            } else if (obstaculos[i] != null && !obstaculos[i].isActivo()) {
                obstaculos[i] = null;
            }
        }
    }

    private void manejarTeclas(KeyEvent e) {
        if (naveBloqueada) return; // Si fue alcanzada por un asteroide, no responde al movimiento

        int key = e.getKeyCode();
        int maxX = panelJuego.getWidth() > 0 ? panelJuego.getWidth() - 40 : 760;
        int maxY = panelJuego.getHeight() > 0 ? panelJuego.getHeight() - 30 : 320;
        
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            if (posicionYNave > 10) posicionYNave -= 15;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            if (posicionYNave < maxY) posicionYNave += 15;
        }
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            if (posicionXNave > 10) posicionXNave -= 15;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            if (posicionXNave < maxX) posicionXNave += 15;
        }
        if (key == KeyEvent.VK_SPACE) {
            disparar();
        }
    }

    private void disparar() {
        if (naveBloqueada) return; // Si fue alcanzada por un asteroide, tampoco puede disparar

        long tiempoActual = System.currentTimeMillis();

        if (tiempoActual - ultimoDisparoMs >= CADENCIA_DISPARO_MS) {
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