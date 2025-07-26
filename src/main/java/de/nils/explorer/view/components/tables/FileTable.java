package de.nils.explorer.view.components.tables;

import de.nils.explorer.common.Const;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class FileTable extends JTable
{
    public FileTable()
    {
        super(new FileTableModel(Const.COLUMN_NAMES, 0));

        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        getColumnModel().getColumn(0).setCellEditor(new FileNameTableCellEditor());
        getColumnModel().getColumn(0).setCellRenderer(new FileNameTableCellRenderer());
        getTableHeader().setOpaque(false);
        getTableHeader().setBackground(Color.white);
        getTableHeader().setForeground(Color.black);
    }

    public void clearTable()
    {
        ((DefaultTableModel) getModel()).setRowCount(0);
    }

    public void allowCellEditing(int row, CellEditorListener cellEditorListener)
    {
        ((FileTableModel) getModel()).setEditableRow(row);
        editCellAt(row, 0);
        ((FileTableModel) getModel()).clearEditableRow();

        getCellEditor(row, 0).addCellEditorListener(cellEditorListener);
    }

    public void endCellEditing()
    {
        if(isEditing())
        {
            TableCellEditor editor = getCellEditor();
            if(!editor.stopCellEditing())
            {
                editor.cancelCellEditing();
            }
        }
    }
}
