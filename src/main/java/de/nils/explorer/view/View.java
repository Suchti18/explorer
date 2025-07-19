package de.nils.explorer.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Paths;

public class View
{
    private final JTable table;
    private final JLabel elementsLabel;

    public View()
    {
        JFrame frame = new JFrame("Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Paths.get(".", "src", "main", "resources", "icons", "256-explorer.png").toString()).getImage());
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.LINE_AXIS));

        upperPanel.add(new JButton("Back"));
        upperPanel.add(Box.createHorizontalStrut(5));
        upperPanel.add(new JButton("Root"));
        upperPanel.add(Box.createHorizontalGlue());

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));

        lowerPanel.add(new JButton("Test1"));
        lowerPanel.add(new JButton("Test2"));
        lowerPanel.add(Box.createHorizontalGlue());

        mainPanel.add(upperPanel);
        mainPanel.add(lowerPanel);

        frame.add(mainPanel, BorderLayout.NORTH);

        JMenu list = new JMenu("List");
        list.add("Test1");
        list.add("Test2");

        frame.add(list, BorderLayout.WEST);

        elementsLabel = new JLabel("{} Elements");
        elementsLabel.setBorder(new EmptyBorder(2, 5, 2, 0));
        frame.add(elementsLabel, BorderLayout.SOUTH);

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

    public JLabel getElementsLabel()
    {
        return elementsLabel;
    }
}
