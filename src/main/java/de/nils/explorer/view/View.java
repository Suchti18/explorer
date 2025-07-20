package de.nils.explorer.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.nils.explorer.view.components.FileNameTableCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Objects;

public class View
{
    private final JTable table;
    private final JLabel elementsLabel;
    private final JLabel pathLabel;

    private final JButton backBtn;
    private final JButton nextBtn;
    private final JButton topBtn;
    private final JButton refreshBtn;

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
        upperPanel.setBorder(new EmptyBorder(5, 8, 5, 5));

        backBtn = createMenuBtn("/images/left.svg", 24, 24);
        nextBtn = createMenuBtn("/images/right.svg", 24, 24);
        topBtn = createMenuBtn("/images/top.svg", 24, 24);
        refreshBtn = createMenuBtn("/images/refresh.svg", 24, 24);

        pathLabel = new JLabel();

        upperPanel.add(backBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
        upperPanel.add(nextBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
        upperPanel.add(topBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
        upperPanel.add(refreshBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
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

    public JButton getBackBtn()
    {
        return backBtn;
    }

    public JButton getNextBtn()
    {
        return nextBtn;
    }

    public JButton getTopBtn()
    {
        return topBtn;
    }

    public JButton getRefreshBtn()
    {
        return refreshBtn;
    }

    private JButton createMenuBtn(String resourceName, int width, int height)
    {
        try
        {
            FlatSVGIcon svg = new FlatSVGIcon(getClass().getResourceAsStream(resourceName));
            JButton btn = new JButton();
            btn.setIcon(new ImageIcon(svg.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);
            btn.setBackground(Color.lightGray);
            btn.setPreferredSize(new Dimension(width + 5, height + 5));
            btn.addMouseListener(new MouseAdapter() {
                /**
                 * {@inheritDoc}
                 *
                 * @param e
                 */
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setOpaque(true);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @param e
                 */
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setOpaque(false);
                }
            });

            return btn;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
