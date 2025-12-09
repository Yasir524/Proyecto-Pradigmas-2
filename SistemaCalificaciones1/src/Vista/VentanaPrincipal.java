package Vista;

import Controlador.ControladorAlumno;
import Modelo.Alumno;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Ventana principal del sistema “Control de Calificaciones”.
 * Maneja toda la interfaz gráfica, interacción con botones,
 * y comunicación con el controlador de alumnos.
 */
public class VentanaPrincipal extends JFrame {

    // Controlador encargado de la lógica central
    private ControladorAlumno controller;

    // Componentes de UI
    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<String> comboCarrera, comboSemestre;
    private JLabel lblRuta;

    /** Constructor: crea UI inicial y carga datos */
    public VentanaPrincipal() {
        controller = new ControladorAlumno();
        initUI();                   // Construcción visual
        cargarCarrerasEnCombo();    // Llenar combo de carreras
        actualizarArchivoYCargar(); // Cargar alumnos del archivo actual
    }

    /** Construcción completa de la interfaz gráfica */
    private void initUI() {
        setTitle("Control de Calificaciones - Universidad del Istmo");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /* ---------------------- HEADER ---------------------- */
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(30,144,255));

        JLabel title = new JLabel("Sistema Control Calificaciones");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        header.add(title);
        add(header, BorderLayout.NORTH);

        /* ---------------------- PANEL IZQUIERDO ---------------------- */
        JPanel left = new JPanel(new GridBagLayout());
        left.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7,7,7,7);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        /* --- Combo de carrera --- */
        left.add(new JLabel("Carrera:"), c);
        c.gridy++;
        comboCarrera = new JComboBox<>();
        left.add(comboCarrera, c);
        c.gridy++;

        /* --- Combo de semestre --- */
        comboSemestre = new JComboBox<>(new String[]{
                "Primer Semestre","Segundo Semestre","Tercer Semestre","Cuarto Semestre",
                "Quinto Semestre","Sexto Semestre","Séptimo Semestre","Octavo Semestre"
        });
        left.add(comboSemestre, c);
        c.gridy++;

        /* --- Botones de acciones --- */
        JButton btnCargar = new JButton("Cargar Doc");
        btnCargar.addActionListener(e -> controller.cargarArchivoExterno());
        left.add(btnCargar, c); c.gridy++;

        JButton btnAgregar = new JButton("Agregar Alum");
        btnAgregar.addActionListener(e -> abrirAgregar());
        left.add(btnAgregar, c); c.gridy++;

        JButton btnEditar = new JButton("Editar Alum");
        btnEditar.addActionListener(e -> abrirEditar());
        left.add(btnEditar, c); c.gridy++;

        JButton btnEliminar = new JButton("Eliminar Alum");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        left.add(btnEliminar, c); c.gridy++;

        JButton btnBuscar = new JButton("Buscar Alum");
        btnBuscar.addActionListener(e -> buscarDialog());
        left.add(btnBuscar, c); c.gridy++;

        JButton btnImport = new JButton("Subir archivo (.txt)");
        btnImport.addActionListener(e -> importarArchivo());
        left.add(btnImport, c); c.gridy++;

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> actualizarArchivoYCargar());
        left.add(btnRefrescar, c); c.gridy++;

        /* --- Ruta del archivo actualmente cargado --- */
        lblRuta = new JLabel("Archivo: ");
        left.add(lblRuta, c);

        add(left, BorderLayout.WEST);

        /* ---------------------- TABLA CENTRAL ---------------------- */
        modelo = new DefaultTableModel(new String[]{
                "Nombre","Matrícula","Promedio","Semestre","Edad","Licenciatura",
                "Correo","Teléfono","Dirección"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(26);

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);
    }

    /** Llena el combo de carreras mediante carpetas existentes */
    private void cargarCarrerasEnCombo() {
        comboCarrera.removeAllItems();
        List<String> carreras = controller.listarCarrerasExistentes();

        // Si no hay carpetas, usar valores por defecto
        if (carreras.isEmpty()) {
            String[] def = {"Ingeniería en Sistemas","Administración","Derecho","Contaduría"};
            for (String s : def) comboCarrera.addItem(s);
        } else {
            for (String s : carreras) comboCarrera.addItem(s);
        }
    }

    /** Cambia archivo activo según selección y carga los alumnos en tabla */
    private void actualizarArchivoYCargar() {
        String carrera = (String) comboCarrera.getSelectedItem();
        String semestre = (String) comboSemestre.getSelectedItem();
        if (carrera == null || semestre == null) return;

        controller.cambiarCarreraSemestre(carrera, semestre);
        lblRuta.setText("Archivo: " + controller.getRutaActual());
        cargarTabla(controller.listar());
    }

    /** Llena la tabla con una lista proporcionada */
    private void cargarTabla(List<Alumno> lista) {
        modelo.setRowCount(0);
        for (Alumno a : lista) {
            modelo.addRow(new Object[]{
                    a.getNombre(), a.getMatricula(), a.getPromedio(), a.getSemestre(), a.getEdad(),
                    a.getLicenciatura(), a.getCorreo(), a.getTelefono(), a.getDireccion()
            });
        }
    }

    /** Abre formulario para agregar nuevo alumno */
    private void abrirAgregar() {
        List<String> carreras = controller.listarCarrerasExistentes();
        if (carreras.isEmpty())
            carreras = java.util.Arrays.asList("Ingeniería en Sistemas","Administración","Derecho","Contaduría");

        // Formulario modal
        FormularioAlumno f = new FormularioAlumno(
                this, "Agregar Alumno", null, carreras,
                (String)comboCarrera.getSelectedItem(),
                (String)comboSemestre.getSelectedItem()
        );

        f.setVisible(true);

        if (f.isConfirmado()) {
            Alumno a = f.obtenerAlumno();
            try {
                // Actualizar ruta si el usuario cambió carrera/semestre
                controller.cambiarCarreraSemestre(a.getLicenciatura(), a.getSemestre());

                controller.agregarAlumno(a);

                // Actualizar combos a la carpeta del alumno agregado
                comboCarrera.setSelectedItem(a.getLicenciatura());
                comboSemestre.setSelectedItem(a.getSemestre());

                actualizarArchivoYCargar();
                JOptionPane.showMessageDialog(this, "Alumno agregado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error guardando: " + ex.getMessage());
            }
        }
    }

    /** Abre formulario para editar un alumno seleccionado */
    private void abrirEditar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro.");
            return;
        }

        String matricula = modelo.getValueAt(fila, 1).toString();
        Alumno original = controller.buscarPorMatricula(matricula);

        if (original == null) {
            JOptionPane.showMessageDialog(this, "Registro no encontrado.");
            return;
        }

        List<String> carreras = controller.listarCarrerasExistentes();
        if (carreras.isEmpty())
            carreras = java.util.Arrays.asList("Ingeniería en Sistemas","Administración","Derecho","Contaduría");

        // Formulario con datos actuales
        FormularioAlumno f = new FormularioAlumno(
                this, "Editar Alumno",
                original,
                carreras,
                original.getLicenciatura(),
                original.getSemestre()
        );

        f.setVisible(true);

        if (f.isConfirmado()) {
            Alumno nuevos = f.obtenerAlumno();
            try {
                controller.editarAlumno(matricula, nuevos);
                actualizarArchivoYCargar();
                JOptionPane.showMessageDialog(this, "Alumno editado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error editando: " + ex.getMessage());
            }
        }
    }

    /** Elimina el alumno seleccionado en la tabla */
    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro.");
            return;
        }

        String matricula = modelo.getValueAt(fila, 1).toString();

        int r = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar alumno con matrícula " + matricula + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (r != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = controller.eliminarAlumno(matricula);
            if (ok) {
                actualizarArchivoYCargar();
                JOptionPane.showMessageDialog(this, "Eliminado.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró matrícula para eliminar.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error eliminando: " + ex.getMessage());
        }
    }

    /** Búsqueda global en todas las carpetas */
    private void buscarDialog() {
        String q = JOptionPane.showInputDialog(this, "Buscar por matrícula o nombre:");
        if (q == null || q.trim().isEmpty()) return;

        List<Alumno> encontrados = controller.buscarEnTodasLasCarpetas(q.trim());

        if (encontrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron coincidencias.");
        } else {
            cargarTabla(encontrados);
        }
    }

    /** Importa un archivo externo .txt */
    private void importarArchivo() {
        File f = SelectorArchivo.seleccionarTxt(this);
        if (f == null) return;

        try {
            int n = controller.importarArchivo(f);
            JOptionPane.showMessageDialog(this, "Importados: " + n);
            actualizarArchivoYCargar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error importando: " + ex.getMessage());
        }
    }

    /** Main: inicia la aplicación */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
