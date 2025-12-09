package Vista;

import Modelo.Alumno;

import javax.swing.*;
import java.awt.*;

/**
 * Es una ventana emergente (JDialog) utilizada para:
 *  - Capturar datos de un alumno nuevo
 *  - Editar los datos de un alumno existente
 * 
 * Se utiliza tanto para "Agregar" como para "Editar".
 */
public class FormularioAlumno extends JDialog {

    // Campos de texto para datos del alumno
    private JTextField txtNombre, txtMatricula, txtPromedio, txtEdad, txtCorreo, txtTelefono, txtDireccion;

    // Combos para seleccionar carrera y semestre
    private JComboBox<String> comboCarrera, comboSemestre;

    // Indica si el usuario dio clic en “Guardar”
    private boolean confirmado = false;

    /**
     * Constructor principal
     * @param parent ventana padre
     * @param titulo título del formulario
     * @param alumno datos a cargar (si es edición), si es null → formulario vacío
     * @param carreras lista dinámica de carreras obtenida del controlador
     * @param carreraSeleccionada carrera actual del alumno (si aplica)
     * @param semestreSeleccionado semestre actual del alumno (si aplica)
     */
    public FormularioAlumno(
            JFrame parent,
            String titulo,
            Alumno alumno,
            java.util.List<String> carreras,
            String carreraSeleccionada,
            String semestreSeleccionado
    ) {
        super(parent, titulo, true); // true → modal, bloquea ventana principal

        setSize(480, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel con grid para alinear los campos
        JPanel panel = new JPanel(new GridLayout(10, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Inicialización de campos
        txtNombre    = agregarCampo(panel, "Nombre:");
        txtMatricula = agregarCampo(panel, "Matrícula:");
        txtPromedio  = agregarCampo(panel, "Promedio:");
        txtEdad      = agregarCampo(panel, "Edad:");
        txtCorreo    = agregarCampo(panel, "Correo:");
        txtTelefono  = agregarCampo(panel, "Teléfono:");
        txtDireccion = agregarCampo(panel, "Dirección:");

        // Combo dinámico de carreras
        panel.add(new JLabel("Carrera:"));
        comboCarrera = new JComboBox<>(carreras.toArray(new String[0]));
        if (carreraSeleccionada != null) comboCarrera.setSelectedItem(carreraSeleccionada);
        panel.add(comboCarrera);

        // Combo estático de semestres
        panel.add(new JLabel("Semestre:"));
        comboSemestre = new JComboBox<>(new String[]{
                "Primer Semestre","Segundo Semestre","Tercer Semestre","Cuarto Semestre",
                "Quinto Semestre","Sexto Semestre","Séptimo Semestre","Octavo Semestre"
        });
        if (semestreSeleccionado != null) comboSemestre.setSelectedItem(semestreSeleccionado);
        panel.add(comboSemestre);

        // Agregar el panel principal
        add(panel, BorderLayout.CENTER);

        // Botón GUARDAR
        JButton btnGuardar = new JButton("Guardar Datos");
        btnGuardar.setBackground(new Color(46,204,113));
        btnGuardar.setForeground(Color.white);
        btnGuardar.addActionListener(e -> {
            confirmado = true;     // indica que el usuario aceptó
            setVisible(false);     // cierra el formulario
        });

        // Botón CANCELAR
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> setVisible(false));

        // Panel inferior con botones
        JPanel pbtn = new JPanel();
        pbtn.add(btnGuardar);
        pbtn.add(btnCancelar);
        add(pbtn, BorderLayout.SOUTH);

        // Si estamos editando → cargar datos del alumno
        if (alumno != null) cargarAlumno(alumno);
    }

    /**
     * Método de utilidad para crear un par "Label - TextField".
     * @param panel panel donde colocar el campo
     * @param label etiqueta visible al usuario
     * @return el JTextField creado
     */
    private JTextField agregarCampo(JPanel panel, String label) {
        JLabel l = new JLabel(label);
        JTextField t = new JTextField();
        panel.add(l);
        panel.add(t);
        return t;
    }

    /**
     * Carga los datos de un alumno existente en los controles del formulario.
     */
    private void cargarAlumno(Alumno a) {
        txtNombre.setText(a.getNombre());
        txtMatricula.setText(a.getMatricula());
        txtPromedio.setText(String.valueOf(a.getPromedio()));
        txtEdad.setText(String.valueOf(a.getEdad()));
        txtCorreo.setText(a.getCorreo());
        txtTelefono.setText(a.getTelefono());
        txtDireccion.setText(a.getDireccion());
        comboCarrera.setSelectedItem(a.getLicenciatura());
        comboSemestre.setSelectedItem(a.getSemestre());
    }

    /**
     * Indica si el usuario confirmó presionando "Guardar".
     */
    public boolean isConfirmado() {
        return confirmado;
    }

    /**
     * Construye un objeto Alumno con los datos capturados por el usuario.
     * La validación se hace después en el controlador.
     */
    public Alumno obtenerAlumno() {
        Alumno a = new Alumno();

        a.setNombre(txtNombre.getText().trim());
        a.setMatricula(txtMatricula.getText().trim());

        // Parseo seguro de valores numéricos
        try { a.setPromedio(Double.parseDouble(txtPromedio.getText().trim())); }
        catch (Exception ex) { a.setPromedio(0.0); }

        try { a.setEdad(Integer.parseInt(txtEdad.getText().trim())); }
        catch (Exception ex) { a.setEdad(0); }

        a.setCorreo(txtCorreo.getText().trim());
        a.setTelefono(txtTelefono.getText().trim());
        a.setDireccion(txtDireccion.getText().trim());

        // Valores de combo
        a.setLicenciatura(comboCarrera.getSelectedItem().toString());
        a.setSemestre(comboSemestre.getSelectedItem().toString());

        return a;
    }
}
