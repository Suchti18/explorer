package de.nils.explorer.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.nils.explorer.view.components.FileNameTableCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Objects;

public class View
{
    private final JTable table;
    private final JLabel elementsLabel;
    private final JLabel pathLabel;

    public View()
    {
        JFrame frame = new JFrame("Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try
        {
            frame.setIconImage(
                    new ImageIcon(
                            Objects.requireNonNull(
                                    getClass().getResource("/icons/256-explorer.png")).toURI().toURL()).getImage());
        }
        catch (URISyntaxException | MalformedURLException e)
        {
            throw new RuntimeException(e);
        }

        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.LINE_AXIS));

        JButton backBtn = new JButton();
        JButton nextBtn = new JButton();
        JButton topBtn = new JButton();
        JButton refreshBtn = new JButton();
        try
        {
            FlatSVGIcon svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/left.svg"));
            backBtn.setIcon(new ImageIcon(svg.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));

            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/right.svg"));
            nextBtn.setIcon(new ImageIcon(svg.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));

            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/top.svg"));
            topBtn.setIcon(new ImageIcon(svg.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));

            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/refresh.svg"));
            refreshBtn.setIcon(new ImageIcon(svg.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        pathLabel = new JLabel();

        upperPanel.add(backBtn);
        upperPanel.add(Box.createHorizontalStrut(5));
        upperPanel.add(nextBtn);
        upperPanel.add(Box.createHorizontalStrut(5));
        upperPanel.add(topBtn);
        upperPanel.add(Box.createHorizontalStrut(5));
        upperPanel.add(refreshBtn);
        upperPanel.add(Box.createHorizontalStrut(5));
        upperPanel.add(pathLabel);
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
        table.getColumnModel().getColumn(0).setCellRenderer(new FileNameTableCellRenderer());

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
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

    public JLabel getPathLabel()
    {
        return pathLabel;
    }
}
