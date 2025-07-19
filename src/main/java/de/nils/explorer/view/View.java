package de.nils.explorer.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Paths;

public class View
{
    private final JTable table;

    public View()
    {
        JFrame frame = new JFrame("Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Paths.get(".", "src", "main", "resources", "icons", "256-explorer.png").toString()).getImage());
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JButton("Hello world"));
        panel.add(new JButton("Hello World"));

        frame.add(panel, BorderLayout.NORTH);

        JMenu list = new JMenu("List");
        list.add("Test1");
        list.add("Test2");

        frame.add(list, BorderLayout.WEST);
        frame.add(new JButton("Hello world"), BorderLayout.SOUTH);

        String[] columnNames = { "Name", "Date modified", "Type", "Size"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public JTable getTable()
    {
        return table;
    }
}
