package Modelo;

/**
 * Clase que representa a un alumno dentro del sistema.
 * Contiene información personal, académica y de contacto.
 * Esta clase es utilizada para manejar los registros que
 * se guardan y leen desde los archivos de texto.
 */
public class Alumno {

    /** Nombre completo del alumno */
    private String nombre;

    /** Matrícula única que identifica al alumno */
    private String matricula;

    /** Promedio general del alumno (0.0 - 10.0) */
    private double promedio;

    /** Semestre actual que cursa el alumno */
    private String semestre;

    /** Edad del alumno */
    private int edad;

    /** Carrera o licenciatura que estudia */
    private String licenciatura;

    /** Correo electrónico del alumno */
    private String correo;

    /** Número telefónico del alumno */
    private String telefono;

    /** Dirección del alumno (ciudad, colonia, etc.) */
    private String direccion;

    /**
     * Constructor vacío.
     * Se utiliza cuando se necesita crear un objeto Alumno
     * e ir llenando los datos manualmente.
     */
    public Alumno() {}

    /**
     * Constructor completo para inicializar todos los datos del alumno.
     */
    public Alumno(String nombre, String matricula, double promedio, String semestre,
                  int edad, String licenciatura, String correo, String telefono, String direccion) {

        this.nombre = nombre;
        this.matricula = matricula;
        this.promedio = promedio;
        this.semestre = semestre;
        this.edad = edad;
        this.licenciatura = licenciatura;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // -------------------------------
    //        GETTERS Y SETTERS
    // -------------------------------
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getLicenciatura() { return licenciatura; }
    public void setLicenciatura(String licenciatura) { this.licenciatura = licenciatura; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     * Convierte los datos del alumno en una línea lista para guardarse en archivo.
     * El formato es separado por barras verticales (|).
     * Este método asegura que ningún campo sea null y elimina saltos de línea.
     *
     * @return línea lista para escribir en el archivo
     */
    public String toLinea() {
        return safe(nombre) + "|" + safe(matricula) + "|" + promedio + "|" + safe(semestre) + "|" +
                edad + "|" + safe(licenciatura) + "|" + safe(correo) + "|" + safe(telefono) + "|" + safe(direccion);
    }

    /**
     * Crea un objeto Alumno a partir de una línea leída del archivo.
     * Soporta líneas incompletas y evita que falle por índices fuera de rango.
     *
     * @param linea línea del archivo separados por |
     * @return objeto Alumno con los datos cargados
     */
    public static Alumno fromLinea(String linea) {

        // Divide respetando campos vacíos con -1
        String[] p = linea.split("\\|", -1);
        Alumno a = new Alumno();

        // Carga segura de cada posición, si falta algo se usa un valor por defecto
        a.nombre = p.length > 0 ? p[0] : "";
        a.matricula = p.length > 1 ? p[1] : "";
        a.promedio = p.length > 2 && !p[2].isEmpty() ? parseDoubleSafe(p[2]) : 0.0;
        a.semestre = p.length > 3 ? p[3] : "";
        a.edad = p.length > 4 && !p[4].isEmpty() ? parseIntSafe(p[4]) : 0;
        a.licenciatura = p.length > 5 ? p[5] : "";
        a.correo = p.length > 6 ? p[6] : "";
        a.telefono = p.length > 7 ? p[7] : "";
        a.direccion = p.length > 8 ? p[8] : "";

        return a;
    }

    /**
     * Convierte un String a double sin lanzar excepción.
     * Si hay error, devuelve 0.0
     */
    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception ex) { return 0.0; }
    }

    /**
     * Convierte un String a entero sin lanzar excepción.
     * Si hay error, devuelve 0.
     */
    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception ex) { return 0; }
    }

    /**
     * Evita valores null y elimina saltos de línea para que no corrompan el archivo.
     */
    private static String safe(String s) {
        return s == null ? "" : s.replace("\n"," ").replace("\r"," ");
    }

    /**
     * Representación del alumno cuando se imprime en consola o en comboBox.
     */
    @Override
    public String toString() {
        return matricula + " - " + nombre;
    }
}
