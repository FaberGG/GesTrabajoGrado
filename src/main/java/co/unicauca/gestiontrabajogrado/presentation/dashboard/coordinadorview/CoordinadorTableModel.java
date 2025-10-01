package co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview;

import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * TableModel del panel del Coordinador.
 * Columnas: Proyecto | Director | Estudiantes | Modalidad | Estado | Acciones
 */
public class CoordinadorTableModel extends AbstractTableModel {

    private final List<PropuestaRow> rows = new ArrayList<>();

    private static final String[] COLS = {
            "Proyecto", "Director", "Estudiantes", "Modalidad", "Estado", "Acciones"
    };

    // -------- API usada desde CoordinadorView --------

    /** Reemplaza el contenido completo de la tabla. */
    public void setRows(List<PropuestaRow> data) {
        rows.clear();
        if (data != null) rows.addAll(data);
        fireTableDataChanged();
    }

    /** Devuelve la fila original. */
    public PropuestaRow getRow(int rowIndex) {
        return rows.get(rowIndex);
    }

    /** Actualiza SOLO el estado de una fila (creando un nuevo record). */
    public void updateEstado(int rowIndex, enumEstadoFormato nuevo) {
        PropuestaRow old = getRow(rowIndex);
        PropuestaRow upd = old.withEstado(nuevo);   // método helper en PropuestaRow
        rows.set(rowIndex, upd);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /** Útil si quieres calcular contadores por estado (p. ej. pendientes). */
    public long countBy(enumEstadoFormato estado) {
        return rows.stream().filter(r -> r.estadoFormato() == estado).count();
    }

    // -------- AbstractTableModel --------

    @Override public int getRowCount() { return rows.size(); }

    @Override public int getColumnCount() { return COLS.length; }

    @Override public String getColumnName(int column) { return COLS[column]; }

    @Override public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 4 -> enumEstadoFormato.class; // Estado (para renderers/badges)
            default -> String.class;
        };
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        PropuestaRow r = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> r.titulo();
            case 1 -> "—";               // Director (si no lo tienes en el record)
            case 2 -> "—";               // Estudiantes (idem)
            case 3 -> "Investigación";   // Modalidad (ajústalo si la tienes)
            case 4 -> r.estadoFormato(); // enumEstadoFormato (lo renderiza el badge)
            case 5 -> null;              // Acciones (la celda la maneja el renderer/botón)
            default -> "";
        };
    }

    @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Solo la columna Acciones, si usas un botón dentro de la tabla
        return columnIndex == 5;
    }
}
