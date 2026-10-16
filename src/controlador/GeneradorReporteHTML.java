package controlador;

import java.io.FileWriter;
import java.io.PrintWriter;

public class GeneradorReporteHTML {

    public static void exportarReporteHTML() {
        String rutaImagen = "grafica_puntajes.png";
        GeneradorGrafica.generarGraficaPNG(rutaImagen);

        try (PrintWriter pw = new PrintWriter(new FileWriter("Reporte_Puntajes.html"))) {
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"es\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>Reporte de Rendimiento - Quetzal Space Defender</title>");
            pw.println("<style>");
            pw.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f6f9; color: #333; }");
            pw.println(".container { background: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }");
            pw.println("h1 { color: #1a73e8; text-align: center; }");
            pw.println("h2 { border-bottom: 2px solid #1a73e8; padding-bottom: 5px; color: #444; }");
            pw.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            pw.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: center; }");
            pw.println("th { background-color: #1a73e8; color: white; }");
            pw.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            pw.println(".grafica-container { text-align: center; margin-top: 30px; }");
            pw.println("img { max-width: 100%; height: auto; border: 1px solid #ccc; border-radius: 4px; }");
            pw.println(".footer { text-align: center; margin-top: 40px; font-size: 12px; color: #777; }");
            pw.println("</style>");
            pw.println("</head>");
            pw.println("<body>");
            
            pw.println("<div class=\"container\">");
            pw.println("<h1>Universidad de San Carlos de Guatemala</h1>");
            pw.println("<h3 style=\"text-align: center; color: #555;\">Escuela de Ciencias y Sistemas - IPC1</h3>");
            pw.println("<h2>Reporte General de Partidas: Quetzal Space Defender</h2>");
            
            pw.println("<p><b>Estudiante:</b> Juan Esteban Ruiz Villatoro</p>");
            pw.println("<p><b>Carné:</b> 202400087</p>");
            
            pw.println("<h3>Gráfica Estadística de Rendimiento</h3>");
            pw.println("<div class=\"grafica-container\">");
            pw.println("<img src=\"" + rutaImagen + "\" alt=\"Gráfica de Puntajes\">");
            pw.println("</div>");

            pw.println("<h3>Historial de Registros</h3>");
            pw.println("<table>");
            pw.println("<tr><th>Piloto</th><th>Puntaje</th></tr>");
            pw.println("<tr><td>Juan Ruiz</td><td>150</td></tr>");
            pw.println("</table>");

            pw.println("<div class=\"footer\">Reporte generado automáticamente - 2026</div>");
            pw.println("</div>");
            
            pw.println("</body>");
            pw.println("</html>");

            System.out.println("¡Reporte HTML generado con éxito!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando prueba de generación de reportes...");
        exportarReporteHTML();
        System.out.println("¡Prueba finalizada! Revisa la raíz de tu proyecto.");
    }
}