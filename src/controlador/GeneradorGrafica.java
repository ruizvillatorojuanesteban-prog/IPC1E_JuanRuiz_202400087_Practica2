package controlador;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GeneradorGrafica {

    public static void generarGraficaPNG(String rutaSalidaImagen) {
        int ancho = 600;
        int alto = 400;
        BufferedImage buffer = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffer.createGraphics();

        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, ancho, alto);

        // Título de la gráfica
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Reporte de Puntajes - Quetzal Space Defender", 110, 30);

        // Ejes
        g2d.drawLine(50, 350, 550, 350); // Eje X
        g2d.drawLine(50, 50, 50, 350);   // Eje Y

        // Leer puntajes del archivo plano puntajes.txt
        int[] puntajes = new int[50];
        String[] nombres = new String[50];
        int contador = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("puntajes.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null && contador < 50) {
                String[] partes = linea.split(",");
                if (partes.length >= 2) {
                    nombres[contador] = partes[0].trim();
                    puntajes[contador] = Integer.parseInt(partes[1].trim());
                    contador++;
                }
            }
        } catch (Exception e) {
            g2d.drawString("No hay suficientes datos registrados todavía.", 150, 200);
            g2d.dispose();
            try {
                ImageIO.write(buffer, "png", new File(rutaSalidaImagen));
            } catch (IOException ignored) {}
            return;
        }

        if (contador == 0) {
            g2d.drawString("Sin registros en puntajes.txt", 200, 200);
            g2d.dispose();
            try {
                ImageIO.write(buffer, "png", new File(rutaSalidaImagen));
            } catch (IOException ignored) {}
            return;
        }

        int xInicial = 80;
        int anchoBarra = 40;
        int espacio = 30;

        for (int i = 0; i < contador; i++) {
            int alturaBarra = puntajes[i] * 2;
            if (alturaBarra > 250) alturaBarra = 250;

            int yBarra = 350 - alturaBarra;

            g2d.setColor(new Color(51, 153, 255));
            g2d.fillRect(xInicial, yBarra, anchoBarra, alturaBarra);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(xInicial, yBarra, anchoBarra, alturaBarra);

            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String nombreCortado = nombres[i].length() > 8 ? nombres[i].substring(0, 7) + "." : nombres[i];
            g2d.drawString(nombreCortado, xInicial, 365);
            g2d.drawString(puntajes[i] + " pts", xInicial, yBarra - 5);

            xInicial += anchoBarra + espacio;
            if (xInicial > 500) break;
        }

        g2d.dispose();

        try {
            ImageIO.write(buffer, "png", new File(rutaSalidaImagen));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}