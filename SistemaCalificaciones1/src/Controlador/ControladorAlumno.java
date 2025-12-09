package Controlador;

import Modelo.Alumno;
import Modelo.AlumnoDAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Modelo.GestorArchivos;
/*es la clase que permite que la interfaz del usuario 
 * pueda crear, modificar, eliminar, cargar y mostrar alumnos usando los métodos del DAO.
 */
public class ControladorAlumno {

    // Carpeta raíz donde se almacenan las carreras y sus archivos
    private static final String RAIZ = "Alumnos";

    // DAO para manejar lectura y escritura del archivo actual
    private AlumnoDAO dao;

    // Ruta del archivo .txt que se está usando actualmente
    private String rutaActual;

    // Carrera y semestre seleccionados actualmente
    private String carreraActual;
    private String semestreActual;

    // No usado en este código, pero es una referencia para posible controlador padre
    private ControladorAlumno controlador;

    /**
     * Constructor.
     * Asigna una ruta por defecto para evitar errores iniciales.
     * En la interfaz se puede cambiar después.
     */
    public ControladorAlumno() {
        // Establece carpeta y archivo inicial
        cambiarCarreraSemestre("Ingeniería en Sistemas", "Quinto Semestre");
    }

    /**
     * Cambia el archivo de trabajo dependiendo de la carrera y semestre seleccionados.
     * También garantiza que existan las carpetas y el archivo .txt.
     */
    public void cambiarCarreraSemestre(String carrera, String semestre) {
        this.carreraActual = carrera;
        this.semestreActual = semestre;

        // Crea carpeta raíz "Alumnos" si no existe
        File base = new File(RAIZ);
        if (!base.exists()) base.mkdirs();

        // Crea carpeta de la carrera (por ejemplo: Alumnos/Ingeniería en Sistemas)
        File carpeta = new File(RAIZ + File.separator + carrera);
        if (!carpeta.exists()) carpeta.mkdirs();

        // Construye la ruta del archivo del semestre
        String archivo = carpeta.getAbsolutePath() + File.separator + semestre + ".txt";
        File f = new File(archivo);

        // Garantiza existencia del archivo
        try {
            if (!f.exists()) f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.rutaActual = archivo;

        // Crea DAO asociado a este archivo
        this.dao = new AlumnoDAO(archivo);
    }

    /** Obtiene la lista completa de alumnos del archivo actual */
    public List<Alumno> listar() { 
        return dao.listar(); 
    }

    public String getRutaActual() { 
        return rutaActual; 
    }
    /**
     * Realiza todas las validaciones de negocio antes de guardar o editar un alumno.
     */
    public List<String> validarAlumno(Alumno a) {
        List<String> errores = new ArrayList<>();

        if (a.getNombre() == null || a.getNombre().trim().isEmpty())
            errores.add("Nombre obligatorio.");

        if (a.getMatricula() == null || a.getMatricula().trim().isEmpty())
            errores.add("Matrícula obligatoria.");

        // Matrícula debe contener solo letras o números
        if (a.getMatricula() != null && !a.getMatricula().trim().matches("^[A-Za-z0-9]+$"))
            errores.add("Matrícula debe ser alfanumérica (sin espacios).");

        // Promedio permitido
        if (a.getPromedio() < 0 || a.getPromedio() > 10)
            errores.add("Promedio debe estar entre 0 y 10.");

        // Edad realista
        if (a.getEdad() < 15 || a.getEdad() > 120)
            errores.add("Edad fuera de rango (15-120).");

        // Validación de correo básica
        if (a.getCorreo() != null && !a.getCorreo().trim().isEmpty() &&
            !a.getCorreo().trim().matches(".+@.+\\..+"))
            errores.add("Correo con formato inválido.");
        return errores;
    }
    /**
     * Agrega un alumno al archivo actual después de validar datos.
     */
    public boolean agregarAlumno(Alumno a) throws IOException {
        List<String> errores = validarAlumno(a);

        if (!errores.isEmpty())
            throw new IllegalArgumentException(String.join(" ", errores));

        // Evita que dos estudiantes tengan la misma matrícula
        if (dao.existeMatriculaEnArchivoActual(a.getMatricula())) {
            throw new IllegalStateException("La matrícula ya existe en este archivo.");
        }

        dao.agregar(a);
        return true;
    }

    /**
     * Edita un alumno. Si se cambió carrera o semestre:
     *   → elimina del archivo actual
     *   → mueve al archivo destino
     */
    public boolean editarAlumno(String matriculaOriginal, Alumno nuevos) throws IOException {

        List<String> errores = validarAlumno(nuevos);
        if (!errores.isEmpty())
            throw new IllegalArgumentException(String.join(" ", errores));

        // Si la carrera y semestre no cambian → modificar en el mismo archivo
        if (nuevos.getLicenciatura().equals(carreraActual) &&
            nuevos.getSemestre().equals(semestreActual)) {

            return dao.editar(matriculaOriginal, nuevos);
        } 
        
        // Si cambian, mover registro
        else {
            dao.eliminarPorMatricula(matriculaOriginal);

            // Construir destino
            File carpetaDestino = new File(RAIZ + File.separator + nuevos.getLicenciatura());
            if (!carpetaDestino.exists()) carpetaDestino.mkdirs();

            String rutaDestino = carpetaDestino.getAbsolutePath() + File.separator + nuevos.getSemestre() + ".txt";
            AlumnoDAO daoDestino = new AlumnoDAO(rutaDestino);

            // Evitar duplicación en archivo destino
            if (daoDestino.existeMatriculaEnArchivoActual(nuevos.getMatricula())) {
                throw new IllegalStateException("Ya existe la matrícula en el archivo destino.");
            }

            daoDestino.agregar(nuevos);

            // Si se movió al archivo actual, recargar DAO
            if (rutaActual.equals(rutaDestino))
                dao = new AlumnoDAO(rutaActual);

            return true;
        }
    }

    /** Elimina alumno únicamente si existe en este archivo */
    public boolean eliminarAlumno(String matricula) throws IOException {
        if (!dao.existeMatriculaEnArchivoActual(matricula))
            return false;

        dao.eliminarPorMatricula(matricula);
        return true;
    }

    /** Busca alumno por matrícula dentro del archivo actual */
    public Alumno buscarPorMatricula(String matricula) {
        for (Alumno a : dao.listar())
            if (a.getMatricula().equalsIgnoreCase(matricula))
                return a;

        return null;
    }

    /** Búsqueda por nombre dentro del archivo actual */
    public List<Alumno> buscarPorNombre(String frag) {
        List<Alumno> res = new ArrayList<>();
        for (Alumno a : dao.listar()) {
            if (a.getNombre().toLowerCase().contains(frag.toLowerCase()))
                res.add(a);
        }
        return res;
    }

    /** Importa desde un archivo externo seleccionado por JFileChooser */
    public int importarArchivo(File archivo) throws IOException {

        int contador = 0;

        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivo));
        String linea;

        while ((linea = br.readLine()) != null) {

            String[] partes = linea.split(",");

            if (partes.length < 9) continue; // no válido

            Alumno a = new Alumno(
                    partes[0], // nombre
                    partes[1], // matricula
                    Double.parseDouble(partes[2]), // promedio
                    partes[3], // semestre
                    Integer.parseInt(partes[4]), // edad
                    partes[5], // carrera
                    partes[6], // correo
                    partes[7], // teléfono
                    partes[8]  // dirección
            );

            // 1. Cambiar automáticamente a la carrera/semestre del alumno leído
            cambiarCarreraSemestre(a.getLicenciatura(), a.getSemestre());

            // 2. Verificar duplicados
            if (!dao.existeMatriculaEnArchivoActual(a.getMatricula())) {
                dao.agregar(a);
                contador++;
            }
        }

        br.close();
        return contador;
    }


    /**
     * Retorna la lista de carpetas dentro de "Alumnos".
     * Sirve para llenar el combo de carreras dinámicamente.
     */
    public List<String> listarCarrerasExistentes() {
        List<String> res = new ArrayList<>();

        File base = new File(RAIZ);
        if (!base.exists()) return res;

        File[] dirs = base.listFiles(File::isDirectory);
        if (dirs == null) return res;

        for (File d : dirs)
            res.add(d.getName());

        return res;
    }

    /**
     * Permite elegir un archivo desde Windows y cargarlo como importación de alumnos.
     */
    public void cargarArchivoExterno() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo de alumnos");

        int resultado = chooser.showOpenDialog(null);
        if (resultado != javax.swing.JFileChooser.APPROVE_OPTION)
            return;

        File archivo = chooser.getSelectedFile();

        if (!archivo.exists()) {
            System.out.println("Archivo no encontrado.");
            return;
        }
        try {
            int cantidad = dao.importarDesdeArchivo(archivo);
            System.out.println("Se importaron: " + cantidad + " registros.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Búsqueda global en TODAS las carreras y TODOS los semestres.
     * Permite encontrar alumnos incluso si no se seleccionó su carrera actual.
     */
    public List<Alumno> buscarEnTodasLasCarpetas(String texto) {
        List<Alumno> resultados = new ArrayList<>();

        // Directorio raíz absoluto del proyecto
        File base = new File(System.getProperty("user.dir"), "Alumnos");
        if (!base.exists() || !base.isDirectory()) {
            System.out.println("buscarEnTodasLasCarpetas: base no encontrada -> " + base.getAbsolutePath());
            return resultados;
        }
        // Cola para recorrido en anchura (BFS)
        Queue<File> queue = new LinkedList<>();
        queue.add(base);
        while (!queue.isEmpty()) {
            File dir = queue.poll();
            File[] files = dir.listFiles();
            if (files == null) continue;

            for (File f : files) {

                if (f.isDirectory()) {
                    // Agregar carpeta a la cola
                    queue.add(f);
                } 
                else if (f.isFile() && f.getName().toLowerCase().endsWith(".txt")) {

                    // Leer archivo encontrado
                    List<Alumno> lista = leerArchivo(f);

                    for (Alumno a : lista) {
                        if (a == null) continue;

                        String matricula = a.getMatricula() != null ? a.getMatricula().trim() : "";
                        String nombre = a.getNombre() != null ? a.getNombre().trim().toLowerCase() : "";
                        String qLower = texto.toLowerCase();

                        // 1) Coincidencia exacta de matrícula
                        if (!matricula.isEmpty() && matricula.equalsIgnoreCase(texto)) {
                            resultados.add(a);
                        }
                        // 2) Coincidencia por nombre parcial
                        else if (!nombre.isEmpty() && nombre.contains(qLower)) {
                            resultados.add(a);
                        }
                    }
                }
            }
        }
        return resultados;}
    /**
     * Lee un archivo .txt y convierte cada línea en un objeto Alumno.
     * Campos separados por "|".
     */
    public List<Alumno> leerArchivo(File archivo) {
        List<Alumno> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {

                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split("\\|");
                if (p.length >= 2) { // Al menos nombre y matrícula

                    Alumno a = new Alumno(
                        p[0].trim(),                                       // nombre
                        p[1].trim(),                                       // matrícula
                        p.length > 2 && !p[2].isEmpty() ? Double.parseDouble(p[2].trim()) : 0.0,
                        p.length > 3 ? p[3].trim() : "",
                        p.length > 4 && !p[4].isEmpty() ? Integer.parseInt(p[4].trim()) : 0,
                        p.length > 5 ? p[5].trim() : "",
                        p.length > 6 ? p[6].trim() : "",
                        p.length > 7 ? p[7].trim() : "",
                        p.length > 8 ? p[8].trim() : ""
                    );

                    lista.add(a);
                }
            }} catch (Exception e) {
            e.printStackTrace();
        } return lista; }
}
