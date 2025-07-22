package de.nils.explorer.view.components.tables;

import javax.swing.table.DefaultTableModel;

public class FileTableModel extends DefaultTableModel
{
    int editableRow = -1;

    public FileTableModel(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return row == editableRow && column == 0;
    }

    public void setEditableRow(int row)
    {
        editableRow = row;
    }

    public void clearEditableRow()
    {
        editableRow = -1;
    }
}
