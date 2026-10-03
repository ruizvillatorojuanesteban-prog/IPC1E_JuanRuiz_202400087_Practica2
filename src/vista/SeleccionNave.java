package vista;

import javax.swing.*;
import java.awt.*;
import modelo.Piloto;
import modelo.TipoNave;

public class SeleccionNave extends JFrame {
    private Piloto pilotoSeleccionado;

    public SeleccionNave(Piloto piloto) {
        this.pilotoSeleccionado = piloto;

        setTitle("Quetzal Space Defender - Selección de Nave");
        setSize(550, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Encabezado
        JLabel lblTitulo = new JLabel("Piloto: " + piloto.getNombre() + " - Seleccione su Nave", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Opciones de naves según el enunciado
        JPanel panelNaves = new JPanel(new GridLayout(1, 3, 15, 0));

        JButton btnExplorador = crearBotonNave(
            "Explorador",
            "<html><b>Dificultad:</b> Fácil<br><b>Velocidad:</b> Alta<br><b>Disparo:</b> Lento (2.0s)</html>"
        );

        JButton btnCaza = crearBotonNave(
            "Caza Estelar",
            "<html><b>Dificultad:</b> Normal<br><b>Velocidad:</b> Media<br><b>Disparo:</b> Medio (1.0s)</html>"
        );

        JButton btnAcorazado = crearBotonNave(
            "Acorazado",
            "<html><b>Dificultad:</b> Difícil<br><b>Velocidad:</b> Baja<br><b>Disparo:</b> Rápido (0.3s)</html>"
        );

        panelNaves.add(btnExplorador);
        panelNaves.add(btnCaza);
        panelNaves.add(btnAcorazado);

        panelPrincipal.add(panelNaves, BorderLayout.CENTER);

        // Eventos
        btnExplorador.addActionListener(e -> iniciarJuego(TipoNave.EXPLORADOR));
        btnCaza.addActionListener(e -> iniciarJuego(TipoNave.CAZA_ESTELAR));
        btnAcorazado.addActionListener(e -> iniciarJuego(TipoNave.ACORAZADO));

        add(panelPrincipal);
    }

    private JButton crearBotonNave(String titulo, String info) {
        JButton btn = new JButton("<html><center><b>" + titulo + "</b><br><br>" + info + "</center></html>");
        btn.setFocusPainted(false);
        return btn;
    }

    private void iniciarJuego(TipoNave tipoNave) {
        JOptionPane.showMessageDialog(this,
            "Partida iniciada para: " + pilotoSeleccionado.getNombre() +
            "\nDificultad: " + tipoNave.getDificultad() +
            "\nCadencia de disparo: " + tipoNave.getTiempoDisparoMs() + " ms",
            "Iniciando Batalla Space Defender", JOptionPane.INFORMATION_MESSAGE);

        this.dispose();
    }
}