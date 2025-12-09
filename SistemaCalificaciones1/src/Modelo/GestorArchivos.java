package Modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de gestionar la lectura y escritura de archivos.
 * Proporciona métodos para:
 *  - Leer todas las líneas del archivo
 *  - Escribir (sobrescribir) líneas
 *  - Agregar una línea al final (append)
 * 
 * Esta clase es utilizada por el controlador para manejar los archivos
 * donde se guardan los alumnos.
 */
public class GestorArchivos {

    /** Archivo físico que será manipulado */
    private final File archivo;

    /**
     * Constructor que recibe la ruta del archivo a leer/escribir.
     *
     * @param ruta ruta absoluta o relativa del archivo
     */
    public GestorArchivos(String ruta) {
        this.archivo = new File(ruta);
    }

    /**
     * Lee todas las líneas del archivo.
     * Si el archivo NO existe:
     *  - Crea la carpeta padre
     *  - Crea el archivo vacío
     *  - Devuelve una lista vacía
     *
     * @return lista con todas las líneas del archivo
     * @throws IOException si ocurre un error de lectura/escritura
     */
    public List<String> leerLineas() throws IOException {

        List<String> lineas = new ArrayList<>();

        // Si el archivo no existe, se crean las carpetas necesarias
        if (!archivo.exists()) {
            File parent = archivo.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            // Crea el archivo vacío
            archivo.createNewFile();
            return lineas; // archivo vacío → lista vacía
        }

        // Lectura tradicional línea por línea
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String l;
            while ((l = br.readLine()) != null) {

                // Se evita agregar líneas completamente vacías
                if (!l.trim().isEmpty())
                    lineas.add(l);
            }
        }
        return lineas;
    }

    /**
     * Sobrescribe el archivo COMPLETAMENTE con las líneas dadas.
     * Cada elemento de la lista será una línea nueva.
     *
     * @param lineas lista de líneas a escribir en el archivo
     * @throws IOException si ocurre algún error
     */
    public void escribirLineas(List<String> lineas) throws IOException {

        // Asegura que la carpeta contenedora exista
        File parent = archivo.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        // FileWriter sin "append", por lo tanto sobrescribe
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            for (String s : lineas) {
                bw.write(s);
                bw.newLine();
            }
        }
    }

    /**
     * Agrega UNA línea al final del archivo (modo append).
     *
     * @param linea contenido a agregar en la última posición
     * @throws IOException si ocurre un error
     */
    public void appendLinea(String linea) throws IOException {

        // Crea carpetas si no existen
        File parent = archivo.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        // FileWriter con "true" → modo append
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(linea);
            bw.newLine();
        }
    }

    /**
     * Devuelve la ruta absoluta del archivo.
     *
     * @return ruta completa del archivo
     */
    public String getRuta() {
        return archivo.getAbsolutePath();
    }
}
