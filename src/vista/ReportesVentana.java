package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import controlador.GestorArchivos;
import controlador.GestorArchivos.RegistroPuntaje;

public class ReportesVentana extends JFrame {

    private RegistroPuntaje[] topPuntajes;

    public ReportesVentana() {
        setTitle("Quetzal Space Defender - Top Puntajes y Desempeño");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Cargar los registros ordenados desde el gestor de archivos
        topPuntajes = GestorArchivos.obtenerTopPuntajes();

        // 1. Configuración de la Tabla de Puntajes
        String[] columnas = {"Posición", "Piloto", "Puntaje", "Nave"};
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (int i = 0; i < topPuntajes.length; i++) {
            if (topPuntajes[i] != null) {
                Object[] fila = {
                    (i + 1) + "°",
                    topPuntajes[i].getPiloto(),
                    topPuntajes[i].getPuntaje(),
                    topPuntajes[i].getNave()
                };
                model.addRow(fila);
            }
        }

        JTable tablaPuntajes = new JTable(model);
        tablaPuntajes.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tablaPuntajes);
        scrollPane.setPreferredSize(new Dimension(350, 0));

        // 2. Panel de Gráfica de Desempeño Nativa (Graphics2D)
        JPanel panelGrafica = new PanelGraficaDesempeño(topPuntajes);

        // 3. Botón para cerrar la ventana
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);

        // Layout Principal dividiendo tabla y gráfica
        setLayout(new BorderLayout(10, 10));
        add(scrollPane, BorderLayout.WEST);
        add(panelGrafica, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }

    /**
     * Panel interno para renderizar el gráfico de barras del desempeño de los puntajes
     * utilizando exclusivamente java.awt.* (sin dependencias externas).
     */
    private static class PanelGraficaDesempeño extends JPanel {
        private final RegistroPuntaje[] registros;

        public PanelGraficaDesempeño(RegistroPuntaje[] registros) {
            this.registros = registros;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Título de la sección gráfica
            g2d.setColor(new Color(40, 40, 40));
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("Desempeño de Mejores Puntajes", 30, 30);

            if (registros == null || registros.length == 0 || registros[0] == null) {
                g2d.setFont(new Font("Arial", Font.ITALIC, 12));
                g2d.setColor(Color.GRAY);
                g2d.drawString("No hay registros disponibles para graficar.", 30, 70);
                return;
            }

            // Encontrar el puntaje máximo para escalar las barras dinámicamente
            int maxPuntaje = 100;
            for (RegistroPuntaje r : registros) {
                if (r != null && r.getPuntaje() > maxPuntaje) {
                    maxPuntaje = r.getPuntaje();
                }
            }

            int xInicial = 50;
            int anchoBarra = 40;
            int espacio = 30;
            int alturaMaxima = height - 120;

            // Dibujar ejes coordenados
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawLine(40, height - 60, width - 30, height - 60); // Eje X
            g2d.drawLine(40, 50, 40, height - 60); // Eje Y

            for (int i = 0; i < registros.length; i++) {
                if (registros[i] == null) break;

                int puntaje = registros[i].getPuntaje();
                int alturaBarra = (int) ((double) puntaje / maxPuntaje * alturaMaxima);

                int x = xInicial + (i * (anchoBarra + espacio));
                int y = (height - 60) - alturaBarra;

                // Dibujar barra con color azul profesional
                g2d.setColor(new customColor(41, 128, 185));
                g2d.fillRect(x, y, anchoBarra, alturaBarra);

                // Borde de la barra
                g2d.setColor(new Color(31, 97, 141));
                g2d.drawRect(x, y, anchoBarra, alturaBarra);

                // Valor numérico arriba de la barra
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                g2d.drawString(String.valueOf(puntaje), x + 5, y - 5);

                // Nombre del piloto debajo del eje X
                String nombrePiloto = registros[i].getPiloto();
                if (nombrePiloto.length() > 8) {
                    nombrePiloto = nombrePiloto.substring(0, 7) + ".";
                }
                g2d.drawString(nombrePiloto, x, height - 40);
            }
        }
        
        // Clase auxiliar de color para evitar confusiones de paquetes
        private static class customColor extends Color {
            public customColor(int r, int g, int b) {
                super(r, g, b);
            }
        }
    }
}