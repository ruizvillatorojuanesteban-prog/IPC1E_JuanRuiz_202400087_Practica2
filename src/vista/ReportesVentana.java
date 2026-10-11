package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import controlador.GestorArchivos;
import controlador.GestorArchivos.RegistroPuntaje;

public class ReportesVentana extends JFrame {

    public ReportesVentana() {
        setTitle("Quetzal Space Defender - Top Puntajes");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Encabezados para la tabla
        String[] columnas = {"Posición", "Piloto", "Puntaje", "Nave"};
        
        // Modelo de tabla sin edición directa de celdas
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Cargar los registros ordenados desde el gestor de archivos
        RegistroPuntaje[] topPuntajes = GestorArchivos.obtenerTopPuntajes();

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

        // Botón para cerrar la ventana
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);

        add(scrollPane, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }
}