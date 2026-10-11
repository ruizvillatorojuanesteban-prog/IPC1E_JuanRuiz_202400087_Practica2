package controlador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GestorArchivos {
    private static final String RUTA_ARCHIVO = "puntajes.txt";
    private static final int MAX_REGISTROS = 100;

    // Clase auxiliar estática para almacenar un registro sin usar colecciones dinámicas
    public static class RegistroPuntaje {
        private String piloto;
        private int puntaje;
        private String nave;

        public RegistroPuntaje(String piloto, int puntaje, String nave) {
            this.piloto = piloto;
            this.puntaje = puntaje;
            this.nave = nave;
        }

        public String getPiloto() {
            return piloto;
        }

        public int getPuntaje() {
            return puntaje;
        }

        public String getNave() {
            return nave;
        }
    }

    // Guarda un nuevo puntaje al final del archivo (Append Mode)
    public static void guardarPuntaje(String piloto, int puntaje, String nave) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            bw.write(piloto + "," + puntaje + "," + nave);
            bw.newLine();
            System.out.println("Puntaje guardado exitosamente en " + RUTA_ARCHIVO);
        } catch (IOException e) {
            System.err.println("Error al guardar el puntaje: " + e.getMessage());
        }
    }

    // Lee el archivo, ordena los registros de mayor a menor puntaje (Bubble Sort) y devuelve un arreglo
    public static RegistroPuntaje[] obtenerTopPuntajes() {
        RegistroPuntaje[] registros = new RegistroPuntaje[MAX_REGISTROS];
        int contador = 0;

        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            return new RegistroPuntaje[0];
        }

        // Lectura del archivo linea por linea
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null && contador < MAX_REGISTROS) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    String[] partes = linea.split(",");
                    if (partes.length == 3) {
                        String piloto = partes[0];
                        int puntaje = Integer.parseInt(partes[1]);
                        String nave = partes[2];
                        registros[contador] = new RegistroPuntaje(piloto, puntaje, nave);
                        contador++;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer el archivo de puntajes: " + e.getMessage());
        }

        // Crear arreglo ajustado a la cantidad de registros leídos
        RegistroPuntaje[] resultado = new RegistroPuntaje[contador];
        for (int i = 0; i < contador; i++) {
            resultado[i] = registros[i];
        }

        // Algoritmo de ordenamiento por Burbuja (Descendente)
        for (int i = 0; i < contador - 1; i++) {
            for (int j = 0; j < contador - i - 1; j++) {
                if (resultado[j].getPuntaje() < resultado[j + 1].getPuntaje()) {
                    RegistroPuntaje temp = resultado[j];
                    resultado[j] = resultado[j + 1];
                    resultado[j + 1] = temp;
                }
            }
        }

        return resultado;
    }
}