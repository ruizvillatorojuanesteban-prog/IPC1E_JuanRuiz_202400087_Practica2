package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import servicio.PilotoService;

public class MenuPrincipal extends JFrame {
    private PilotoService pilotoService;

    public MenuPrincipal() {
        this.pilotoService = new PilotoService();

        // Configuración de la ventana
        setTitle("Quetzal Space Defender - Menú Principal");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);

        // Panel principal con diseño centrado
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Título
        JLabel lblTitulo = new JLabel("QUETZAL SPACE DEFENDER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitulo);

        // Botones requeridos por el enunciado
        JButton btnJugar = new JButton("Jugar");
        JButton btnCrearPiloto = new JButton("Crear Piloto");
        JButton btnTopPuntajes = new JButton("Top de Puntajes");
        JButton btnSalir = new JButton("Salir");

        panel.add(btnJugar);
        panel.add(btnCrearPiloto);
        panel.add(btnTopPuntajes);
        panel.add(btnSalir);

        add(panel);

        // Eventos de los botones
        btnCrearPiloto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarRegistroPiloto();
            }
        });

        btnJugar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pilotoService.getCantidadPilotos() == 0) {
                    JOptionPane.showMessageDialog(null, 
                        "Debe registrar al menos un piloto antes de iniciar la partida.", 
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Pantalla de Selección de Nave / Dificultad (Siguiente fase).");
                }
            }
        });

        btnTopPuntajes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, 
                    "Pantalla de Top de Puntajes (Pendiente de implementación con JFreeChart).");
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Muestra una ventana de diálogo simple para registrar un piloto.
     */
    private void mostrarRegistroPiloto() {
        String nombre = JOptionPane.showInputDialog(this, 
            "Ingrese el nombre del nuevo piloto:", 
            "Registro de Piloto", JOptionPane.QUESTION_MESSAGE);

        if (nombre != null) {
            boolean exito = pilotoService.registrarPiloto(nombre);
            if (exito) {
                JOptionPane.showMessageDialog(this, 
                    "Piloto '" + nombre.trim() + "' registrado correctamente.", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo registrar. Verifique si el nombre ya existe o si se alcanzó el límite.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Punto de entrada temporal para probar la interfaz
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}