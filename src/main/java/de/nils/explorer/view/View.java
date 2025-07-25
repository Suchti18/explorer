package de.nils.explorer.view;

import de.nils.explorer.common.Const;
import de.nils.explorer.view.components.roundedPanel;
import de.nils.explorer.view.components.scrollpane.ScrollPaneWin11;
import de.nils.explorer.view.components.tables.FileNameTableCellEditor;
import de.nils.explorer.view.components.tables.FileNameTableCellRenderer;
import de.nils.explorer.view.components.listeners.ListDragListener;
import de.nils.explorer.view.components.tables.FileTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class View
{
    private static final Logger log = LoggerFactory.getLogger(View.class);

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
    private final JButton renameBtn;
    private final JButton shareBtn;
    private final JButton trashBtn;
    private final JButton filterBtn;
    private final JButton moreBtn;

    // Sidebar
    private final JPanel sideBar;
    private final List<JButton> sidebarPins;
    private final JButton thisPC;
    private final JButton network;

    public View()
    {
        sidebarPins = new ArrayList<>();
        JFrame frame = new JFrame(Const.WINDOW_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try
        {
            frame.setIconImage(
                    new ImageIcon(
                            Objects.requireNonNull(
                                    getClass().getResource(Const.EXPLORER_ICON)).toURI().toURL()).getImage());
        }
        catch (URISyntaxException | MalformedURLException e)
        {
            throw new RuntimeException(e);
        }

        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel upperPanel = new JPanel();
        upperPanel.setBackground(Color.white);
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.LINE_AXIS));
        upperPanel.setBorder(new EmptyBorder(5, 8, 5, 5));

        backBtn = createMenuBtn(Const.LEFT_ARROW_SVG, "Back");
        nextBtn = createMenuBtn(Const.RIGHT_ARROW_SVG, "Previous");
        topBtn = createMenuBtn(Const.TOP_ARROW_SVG, "Go to the top");
        refreshBtn = createMenuBtn(Const.REFRESH_SVG, "Refresh");

        pathLabel = new JLabel();
        JPanel pathPanel = new roundedPanel();
        pathPanel.setBackground(Color.white);

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
        lowerPanel.setBackground(Color.white);
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
        lowerPanel.setBorder(new EmptyBorder(5, 8, 5, 5));

        newBtn = createMenuBtn(Const.NEW_SVG, "Create new Files/Directories");
        newBtn.setText(Const.NEW_TEXT);
        newBtn.setPreferredSize(new Dimension(82 + 5, 24 + 5));
        newBtn.setMinimumSize(new Dimension(82 + 5, 24 + 5));
        renameBtn = createMenuBtn(Const.RENAME_SVG, "Rename a selected file");
        shareBtn = createMenuBtn(Const.SHARE_SVG, "Share a selected file (Just opens your mail client)");
        trashBtn = createMenuBtn(Const.TRASH_SVG, "Delete selected files");
        filterBtn = createMenuBtn(Const.FILTER_SVG, "Change the filter");
        moreBtn = createMenuBtn(Const.MORE_SVG, "Show more options");

        lowerPanel.add(newBtn);
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createSeparator(JSeparator.VERTICAL, 1, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(renameBtn);
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(shareBtn);
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(trashBtn);
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createSeparator(JSeparator.VERTICAL, 1, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(filterBtn);
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(createSeparator(JSeparator.VERTICAL, 1, 24));
        lowerPanel.add(Box.createHorizontalStrut(10));
        lowerPanel.add(moreBtn);
        lowerPanel.add(Box.createHorizontalGlue());

        mainPanel.add(upperPanel);
        mainPanel.add(createSeparator(JSeparator.HORIZONTAL, 0, 0));
        mainPanel.add(lowerPanel);
        mainPanel.add(createSeparator(JSeparator.HORIZONTAL, 0, 0));

        frame.add(mainPanel, BorderLayout.NORTH);

        JPanel sideNavBar = new JPanel();
        sideNavBar.setLayout(new BoxLayout(sideNavBar, BoxLayout.LINE_AXIS));

        sideBar =  new JPanel();
        sideBar.setBackground(Color.white);
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.PAGE_AXIS));
        sideBar.setBorder(new EmptyBorder(5, 5, 0, 5));

        sidebarPins.add(createSidebarBtn(Const.DESKTOP_SVG, "Desktop"));
        sidebarPins.add(createSidebarBtn(Const.DOWNLOAD_SVG, "Downloads"));
        sidebarPins.add(createSidebarBtn(Const.DOCUMENT_SVG, "Documents"));
        sidebarPins.add(createSidebarBtn(Const.PICTURE_SVG, "Pictures"));
        sidebarPins.add(createSidebarBtn(Const.MUSIC_SVG, "Music"));
        sidebarPins.add(createSidebarBtn(Const.VIDEO_SVG, "Videos"));

        thisPC = createSidebarBtn(Const.COMPUTER_SVG, "This PC");
        network = createSidebarBtn(Const.NETWORK_SVG, "Network");

        for(JButton sidebarBtn : sidebarPins)
        {
            sideBar.add(sidebarBtn);
        }

        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(createSeparator(JSeparator.HORIZONTAL, Integer.MAX_VALUE, 1));
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(thisPC);
        sideBar.add(network);
        sideBar.add(Box.createVerticalGlue());

        JScrollPane list = new ScrollPaneWin11();
        list.setViewportView(sideBar);
        list.getVerticalScrollBar().setUnitIncrement(16);
        list.setPreferredSize(new Dimension(175, 100));
        list.setBorder(BorderFactory.createEmptyBorder());

        JSeparator sideBarSeparator = createSeparator(JSeparator.VERTICAL, 0, 0);

        ListDragListener listDragListener = new ListDragListener(list, frame, sideBarSeparator);
        sideBarSeparator.addMouseListener(listDragListener);
        sideBarSeparator.addMouseMotionListener(listDragListener);

        sideNavBar.add(list);
        sideNavBar.add(sideBarSeparator);

        frame.add(sideNavBar, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.white);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));

        elementsLabel = new JLabel(Const.EMPTY);
        elementsLabel.setBorder(new EmptyBorder(2, 5, 2, 0));

        selectedElementsLabel = new JLabel(Const.EMPTY);
        selectedElementsLabel.setBorder(new EmptyBorder(2, 0, 2, 0));

        infoPanel.add(elementsLabel);
        infoPanel.add(Box.createHorizontalStrut(5));
        infoPanel.add(createSeparator(JSeparator.VERTICAL, 1, 12));
        infoPanel.add(Box.createHorizontalStrut(5));
        infoPanel.add(selectedElementsLabel);

        frame.add(infoPanel, BorderLayout.SOUTH);

        DefaultTableModel tableModel = new FileTableModel(Const.COLUMN_NAMES, 0);

        table = new JTable(tableModel);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.getColumnModel().getColumn(0).setCellEditor(new FileNameTableCellEditor());
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

        log.debug("View loaded");
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

    public JButton getRenameBtn()
    {
        return renameBtn;
    }

    public JButton getShareBtn()
    {
        return shareBtn;
    }

    public JButton getTrashBtn()
    {
        return trashBtn;
    }

    public JButton getFilterBtn()
    {
        return filterBtn;
    }

    public JButton getMoreBtn()
    {
        return moreBtn;
    }

    public JPanel getSideBar()
    {
        return sideBar;
    }

    public List<JButton> getSidebarPins()
    {
        return sidebarPins;
    }

    public JButton getThisPC()
    {
        return thisPC;
    }

    public JButton getNetwork()
    {
        return network;
    }

    private JButton createMenuBtn(String resourceName, String toolTipText)
    {
        JButton btn = new JButton();
        btn.setIcon(GuiResources.loadImageIcon(resourceName, 24, 24));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBackground(Color.lightGray);
        btn.setToolTipText(toolTipText);
        btn.setPreferredSize(new Dimension(24 + 5, 24 + 5));
        btn.setMinimumSize(new Dimension(24 + 5, 24 + 5));
        btn.addMouseListener(createNewHoverEffectMouseAdapter(btn));

        return btn;
    }

    public JButton createSidebarBtn(String resourceName, String text)
    {
        JButton btn = new JButton(text);
        btn.setIcon(GuiResources.loadImageIcon(resourceName, 16, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setBackground(Color.lightGray);
        btn.addMouseListener(createNewHoverEffectMouseAdapter(btn));

        return btn;
    }

    private MouseAdapter createNewHoverEffectMouseAdapter(JButton btn)
    {
        return new MouseAdapter()
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
        };
    }

    private JSeparator createSeparator(int orientation, int width, int height)
    {
        JSeparator separator = new JSeparator(orientation);

        if(width > 0 && height > 0)
        {
            separator.setMaximumSize(new Dimension(width, height));
        }

        separator.setForeground(Color.black);

        return separator;
    }
}
