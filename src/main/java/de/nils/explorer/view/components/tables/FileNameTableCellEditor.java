package de.nils.explorer.view.components.tables;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class FileNameTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
    private final JPanel panel;
    private final JLabel iconLabel;
    private final JTextField textField;

    private FileName currentFileName;

    public FileNameTableCellEditor()
    {
        panel = new JPanel(new BorderLayout());
        textField = new JTextField();
        iconLabel = new JLabel();

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
    }

    /**
     * Sets an initial <code>value</code> for the editor.  This will cause
     * the editor to <code>stopEditing</code> and lose any partially
     * edited value if the editor is editing when this method is called. <p>
     * <p>
     * Returns the component that should be added to the client's
     * <code>Component</code> hierarchy.  Once installed in the client's
     * hierarchy this component will then be able to draw and receive
     * user input.
     *
     * @param table      the <code>JTable</code> that is asking the
     *                   editor to edit; can be <code>null</code>
     * @param value      the value of the cell to be edited; it is
     *                   up to the specific editor to interpret
     *                   and draw the value.  For example, if value is
     *                   the string "true", it could be rendered as a
     *                   string or it could be rendered as a check
     *                   box that is checked.  <code>null</code>
     *                   is a valid value
     * @param isSelected true if the cell is to be rendered with
     *                   highlighting
     * @param row        the row of the cell being edited
     * @param column     the column of the cell being edited
     * @return the component for editing
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        currentFileName = (FileName) value;

        textField.setText(currentFileName.getFileName());
        iconLabel.setIcon(new ImageIcon(currentFileName.getImage()));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 5));

        SwingUtilities.invokeLater(textField::requestFocusInWindow);

        return panel;
    }

    /**
     * Returns the value contained in the editor.
     *
     * @return the value contained in the editor
     */
    @Override
    public Object getCellEditorValue()
    {
        return new FileName(currentFileName.getImage(), textField.getText());
    }
}
