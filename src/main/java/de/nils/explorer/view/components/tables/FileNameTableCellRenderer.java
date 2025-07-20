package de.nils.explorer.view.components.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class FileNameTableCellRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        FileName fileName = (FileName) value;

        label.setIcon(new ImageIcon(fileName.getImage()));
        label.setText(fileName.getFileName());
        label.setHorizontalTextPosition(SwingConstants.RIGHT);
        return label;
    }
}
