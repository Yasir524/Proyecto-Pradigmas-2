package Modelo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de la gestión de datos de Alumno a nivel de archivo.
 * 
 * Funciona como un "DAO" (Data Access Object),objeto de acceso a datos. ofreciendo operaciones:
 *  - Listar alumnos
 *  - Agregar alumnos
 *  - Editar alumnos
 *  - Eliminar alumnos
 *  - Verificar duplicados
 *  - Importar alumnos desde archivos externos
 *
 * Internamente utiliza GestorArchivos para trabajar con los archivos físicos.
 */
public class AlumnoDAO {

    /** Ruta actual del archivo donde se almacenan los alumnos */
    private String ruta;

    /** Objeto que realiza la lectura y escritura del archivo */
    private GestorArchivos gestor;

    /**
     * Constructor que recibe la ruta donde se guardarán los alumnos.
     * Inmediatamente inicializa el gestor de archivos.
     */
    public AlumnoDAO(String ruta) {
        cambiarRuta(ruta);
    }

    /**
     * Permite cambiar dinámicamente el archivo donde se guarda la información.
     * Muy útil cuando el usuario cambia de carrera o semestre.
     *
     * @param nuevaRuta ruta del archivo alumnos.txt actual
     */
    public void cambiarRuta(String nuevaRuta) {
        this.ruta = nuevaRuta;
        this.gestor = new GestorArchivos(nuevaRuta);
    }

    /**
     * Lee todos los alumnos desde el archivo correspondiente.
     *
     * @return lista de alumnos convertidos desde sus líneas
     */
    public List<Alumno> listar() {
        try {
            // Obtiene líneas crudas desde el archivo
            List<String> lines = gestor.leerLineas();
            List<Alumno> res = new ArrayList<>();

            // Convierte cada línea en un objeto Alumno
            for (String l : lines) 
                res.add(Alumno.fromLinea(l));

            return res;
        } catch (IOException e) {
            e.printStackTrace();
            // Si hay error, devolver lista vacía evita que la app se caiga
            return new ArrayList<>();
        }
    }

    /**
     * Verifica si una matrícula ya existe en el archivo actual.
     * Sirve para evitar duplicados al agregar o importar.
     */
    public boolean existeMatriculaEnArchivoActual(String matricula) {
        if (matricula == null) return false;

        matricula = matricula.trim();

        // Recorre todos los alumnos y compara matrícula
        for (Alumno a : listar()) {
            if (matricula.equalsIgnoreCase(a.getMatricula())) 
                return true;
        }
        return false;
    }

    /**
     * Agrega un alumno al final del archivo (modo append).
     *
     * @param a alumno a agregar
     */
    public void agregar(Alumno a) throws IOException {
        gestor.appendLinea(a.toLinea());
    }

    /**
     * Sobrescribe TODO el archivo con los alumnos dados.
     * Se usa en eliminar y editar.
     */
    public void guardarTodo(List<Alumno> lista) throws IOException {
        List<String> lines = new ArrayList<>();

        // Convierte cada alumno a formato línea
        for (Alumno a : lista) 
            lines.add(a.toLinea());

        gestor.escribirLineas(lines);
    }

    /**
     * Elimina del archivo el alumno cuya matrícula coincida.
     */
    public void eliminarPorMatricula(String matricula) throws IOException {
        List<Alumno> lista = listar();

        // removeIf elimina todos los que cumplan la condición
        lista.removeIf(x -> x.getMatricula().equalsIgnoreCase(matricula));

        guardarTodo(lista);
    }

    /**
     * Edita un alumno dentro del archivo.
     *
     * @param matriculaOriginal matrícula a buscar
     * @param nuevos datos actualizados del alumno
     * @return true si se encontró y reemplazó, false si no existía
     */
    public boolean editar(String matriculaOriginal, Alumno nuevos) throws IOException {
        List<Alumno> lista = listar();

        for (int i = 0; i < lista.size(); i++) {

            // Compara matrícula actual con la matrícula buscada
            if (lista.get(i).getMatricula().equalsIgnoreCase(matriculaOriginal)) {

                // Reemplaza los datos del alumno
                lista.set(i, nuevos);

                // Guarda en archivo
                guardarTodo(lista);
                return true;
            }
        }
        return false;
    }

    /**
     * Importa alumnos desde un archivo externo.
     * 
     * Formato esperado: CSV separado por comas,
     * con 9 campos: nombre, matricula, promedio, semestre,
     * edad, carrera, correo, telefono, direccion.
     *
     * Si la matrícula ya existe en el archivo actual, se omite ese registro.
     *
     * @param archivo archivo externo (.txt o .csv)
     * @return cantidad de alumnos agregados
     */
    public int importarDesdeArchivo(File archivo) throws IOException {

        int contador = 0;

        java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.FileReader(archivo)
        );

        String linea;

        while ((linea = br.readLine()) != null) {

            // Cada línea se divide por comas
            String[] partes = linea.split(",");

            // Si no tiene los 9 campos requeridos, se descarta
            if (partes.length < 9) continue;

            // Construye un nuevo alumno
            Alumno a = new Alumno(
                    partes[0],        // nombre
                    partes[1],        // matricula
                    Double.parseDouble(partes[2]),
                    partes[3],        // semestre
                    Integer.parseInt(partes[4]),
                    partes[5],        // carrera
                    partes[6],        // correo
                    partes[7],        // telefono
                    partes[8]         // direccion
            );

            // Evita duplicados según matrícula
            if (!existeMatriculaEnArchivoActual(a.getMatricula())) {
                agregar(a);
                contador++;
            }
        }

        br.close();
        return contador;
    }

    /** @return ruta del archivo actual donde se guarda la información */
    public String getRuta() { return ruta; }
}
