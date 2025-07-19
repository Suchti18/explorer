package de.nils.explorer.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class view
{
    public view()
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

        try(Stream<Path> files = Files.list(Paths.get("."))) {
            files.forEach(path ->
            {
                if(Files.exists(path))
                {
                    try {
                        String[] data = {path.getFileName().toString(), Files.getLastModifiedTime(path).toString(), Files.isDirectory(path) ? "Directory" : "File", String.valueOf(Files.size(path))};
                        tableModel.addRow(data);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JTable table = new JTable(tableModel);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        table.addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getClickCount() == 2 && table.getSelectedRow() != -1 && table.rowAtPoint(e.getPoint()) != 1)
                {
                    System.out.println(table.getValueAt(table.getSelectedRow(), 0));
                }
            }
        });

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
}
