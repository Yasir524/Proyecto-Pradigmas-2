package Vista;

import javax.swing.*;
import java.io.File;

/**
 * Clase utilitaria que encapsula la lógica para abrir un JFileChooser
 * y seleccionar exclusivamente archivos .txt, usados para importar
 * registros de alumnos.
 *
 * Se utiliza desde la vista/controlador para evitar repetir código.
 */
public class SelectorArchivo {

    /**
     * Abre una ventana de selección de archivo .txt y regresa el archivo elegido.
     *
     * @param parent ventana padre para anclar el JFileChooser
     * @return File seleccionado o null si el usuario cancela o el archivo no es válido
     */
    public static File seleccionarTxt(JFrame parent) {

        // Crea el explorador de archivos
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo .txt de alumnos");

        // Muestra el diálogo y obtiene la acción del usuario
        int r = fc.showOpenDialog(parent);

        // Si el usuario presionó "Aceptar"
        if (r == JFileChooser.APPROVE_OPTION) {

            // Obtiene el archivo seleccionado
            File f = fc.getSelectedFile();

            // Validación: solo permitir .txt
            if (!f.getName().toLowerCase().endsWith(".txt")) {
                JOptionPane.showMessageDialog(parent, "El archivo debe ser .txt");
                return null;
            }

            return f; // archivo válido
        }

        // Si el usuario canceló, se retorna null
        return null;
    }
}
