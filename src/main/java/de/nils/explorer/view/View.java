package de.nils.explorer.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.nils.explorer.view.components.scrollpane.ScrollPaneWin11;
import de.nils.explorer.view.components.tables.FileNameTableCellRenderer;
import de.nils.explorer.view.components.listeners.ListDragListener;

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
    private final JLabel selectedElementsLabel;

    // Upper panel
    private final JLabel pathLabel;
    private final JButton backBtn;
    private final JButton nextBtn;
    private final JButton topBtn;
    private final JButton refreshBtn;

    // Lower panel
    private final JButton newBtn;

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
        JPanel pathPanel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                Dimension arcs = new Dimension(10,10);
                int width = getWidth();
                int height = getHeight();
                Graphics2D graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                graphics.setColor(Color.lightGray);
                graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
                graphics.setColor(getBackground());
                graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
            }
        };

        pathPanel.add(pathLabel);

        JScrollPane pathScrollPane = new ScrollPaneWin11();
        pathScrollPane.setBorder(BorderFactory.createEmptyBorder());
        pathScrollPane.setViewportView(pathPanel);

        upperPanel.add(backBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
        upperPanel.add(nextBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
        upperPanel.add(topBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
        upperPanel.add(refreshBtn);
        upperPanel.add(Box.createHorizontalStrut(10));
        upperPanel.add(pathScrollPane);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
        lowerPanel.setBorder(new EmptyBorder(5, 8, 5, 5));

        newBtn = createMenuBtn("/images/new.svg", 24, 24);
        newBtn.setText("New");
        newBtn.setPreferredSize(new Dimension(82 + 5, 24 + 5));
        newBtn.setMinimumSize(new Dimension(82 + 5, 24 + 5));

        lowerPanel.add(newBtn);
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createSeparator(JSeparator.VERTICAL, Color.black, 1, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createMenuBtn("/images/rename.svg", 24, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createMenuBtn("/images/share.svg", 24, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createMenuBtn("/images/trash.svg", 24, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createSeparator(JSeparator.VERTICAL, Color.black, 1, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createMenuBtn("/images/filter.svg", 24, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createSeparator(JSeparator.VERTICAL, Color.black, 1, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createMenuBtn("/images/more.svg", 24, 24));
        lowerPanel.add(Box.createHorizontalGlue());

        mainPanel.add(upperPanel);
        mainPanel.add(createSeparator(JSeparator.HORIZONTAL, Color.black, 0, 0));
        mainPanel.add(lowerPanel);
        mainPanel.add(createSeparator(JSeparator.HORIZONTAL, Color.black, 0, 0));

        frame.add(mainPanel, BorderLayout.NORTH);

        JPanel sideNavBar = new JPanel();
        sideNavBar.setLayout(new BoxLayout(sideNavBar, BoxLayout.LINE_AXIS));

        JPanel sideBar =  new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.PAGE_AXIS));
        sideBar.setBorder(new EmptyBorder(0, 5, 0, 5));

        sideBar.add(createSidebarBtn("/images/desktop.svg", "Desktop"));
        sideBar.add(createSidebarBtn("/images/download.svg", "Downloads"));
        sideBar.add(createSidebarBtn("/images/document.svg", "Documents"));
        sideBar.add(createSidebarBtn("/images/picture.svg", "Pictures"));
        sideBar.add(createSidebarBtn("/images/music.svg", "Music"));
        sideBar.add(createSidebarBtn("/images/video.svg", "Videos"));
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(createSeparator(JSeparator.HORIZONTAL, Color.black, Integer.MAX_VALUE, 1));
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(createSidebarBtn("/images/computer.svg", "This PC"));
        sideBar.add(createSidebarBtn("/images/network.svg", "Network"));
        sideBar.add(Box.createVerticalGlue());

        JScrollPane list = new ScrollPaneWin11();
        list.setViewportView(sideBar);
        list.getVerticalScrollBar().setUnitIncrement(16);
        list.setPreferredSize(new Dimension(175, 100));
        list.setBorder(BorderFactory.createEmptyBorder());

        JSeparator sideBarSeparator = createSeparator(JSeparator.VERTICAL, Color.black, 0, 0);

        ListDragListener listDragListener = new ListDragListener(list, frame, sideBarSeparator);
        sideBarSeparator.addMouseListener(listDragListener);
        sideBarSeparator.addMouseMotionListener(listDragListener);

        sideNavBar.add(list);
        sideNavBar.add(sideBarSeparator);

        frame.add(sideNavBar, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));

        elementsLabel = new JLabel("");
        elementsLabel.setBorder(new EmptyBorder(2, 5, 2, 0));

        selectedElementsLabel = new JLabel("");
        selectedElementsLabel.setBorder(new EmptyBorder(2, 0, 2, 0));

        infoPanel.add(elementsLabel);
        infoPanel.add(Box.createHorizontalStrut(5));
        infoPanel.add(createSeparator(JSeparator.VERTICAL, Color.black, 1, 12));
        infoPanel.add(Box.createHorizontalStrut(5));
        infoPanel.add(selectedElementsLabel);

        frame.add(infoPanel, BorderLayout.SOUTH);

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
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(Color.white);
        table.getTableHeader().setForeground(Color.black);

        JScrollPane tableScrollPane = new ScrollPaneWin11();
        tableScrollPane.setViewportView(table);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        frame.add(tableScrollPane, BorderLayout.CENTER);

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

    public JLabel getSelectedElementsLabel()
    {
        return selectedElementsLabel;
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

    public JButton getNewBtn()
    {
        return newBtn;
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
            btn.setMinimumSize(new Dimension(width + 5, height + 5));
            btn.addMouseListener(new MouseAdapter()
            {
                /**
                 * {@inheritDoc}
                 *
                 * @param e
                 */
                @Override
                public void mouseEntered(MouseEvent e)
                {
                    btn.setOpaque(true);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @param e
                 */
                @Override
                public void mouseExited(MouseEvent e)
                {
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

    private JButton createSidebarBtn(String resourceName, String text)
    {
        try
        {
            FlatSVGIcon svg = new FlatSVGIcon(getClass().getResourceAsStream(resourceName));
            JButton btn = new JButton(text);
            btn.setIcon(new ImageIcon(svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(false);
            btn.setBackground(Color.lightGray);
            btn.addMouseListener(new MouseAdapter()
            {
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

    private JSeparator createSeparator(int orientation, Color color, int width, int height)
    {
        JSeparator separator = new JSeparator(orientation);

        if(width > 0 && height > 0)
        {
            separator.setMaximumSize(new Dimension(width, height));
        }

        separator.setForeground(color);

        return separator;
    }
}
