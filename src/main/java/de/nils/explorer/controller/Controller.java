package de.nils.explorer.controller;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.nils.explorer.view.View;
import de.nils.explorer.view.components.tables.FileName;
import de.nils.explorer.view.components.tables.FileTableModel;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Controller
{
    private final Image folderImg;
    private final Image fileImg;

    private final View view;
    private Path currPath;
    private Path previousPath;

    // Create new files popup menu
    private final AtomicBoolean isNewPopupMenuOpen;
    private final JPopupMenu newMenu;
    private final JMenuItem folderMenuItem;
    private final JMenuItem fileMenuItem;

    // Filter popup menu
    private final AtomicBoolean isFilterMenuOpen;
    private final JPopupMenu filterMenu;
    private final JMenuItem alphabeticalAZMenuItem;
    private final JMenuItem alphabeticalZAMenuItem;
    private final JMenuItem typeMenuItem;

    // More popup menu
    private final AtomicBoolean isMoreMenuOpen;
    private final JPopupMenu moreMenu;
    private final JMenuItem selectAllMenuItem;
    private final JMenuItem selectNoneMenuItem;
    private final JMenuItem invertSelectionMenuItem;

    public Controller(View view)
    {
        Image alphabeticalAZImg;
        Image alphabeticalZAImg;
        Image typeImg;
        Image selectAllImg;
        Image selectNoneImg;
        Image invertSelectionImg;
        FlatSVGIcon svg;
        try
        {
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/folder.svg"));
            folderImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/file.svg"));
            fileImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);

            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/alphabetical-az.svg"));
            alphabeticalAZImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/alphabetical-za.svg"));
            alphabeticalZAImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/type.svg"));
            typeImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);

            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/select-all.svg"));
            selectAllImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/select-none.svg"));
            selectNoneImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/invert.svg"));
            invertSelectionImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        this.view = view;
        currPath = Paths.get(".").toAbsolutePath().normalize();

        // New files popup menu
        isNewPopupMenuOpen = new AtomicBoolean(false);
        newMenu = new JPopupMenu();

        folderMenuItem = new JMenuItem("Folder");
        fileMenuItem = new JMenuItem("File");

        folderMenuItem.setIcon(new ImageIcon(folderImg));
        fileMenuItem.setIcon(new ImageIcon(fileImg));

        newMenu.add(folderMenuItem);
        newMenu.add(fileMenuItem);

        // Filter popup menu
        isFilterMenuOpen = new AtomicBoolean(false);
        filterMenu = new JPopupMenu();

        alphabeticalAZMenuItem = new JMenuItem("Alphabetical (A-Z)");
        alphabeticalZAMenuItem = new JMenuItem("Alphabetical (Z-A)");
        typeMenuItem = new JMenuItem("Type");

        alphabeticalAZMenuItem.setIcon(new ImageIcon(alphabeticalAZImg));
        alphabeticalZAMenuItem.setIcon(new ImageIcon(alphabeticalZAImg));
        typeMenuItem.setIcon(new ImageIcon(typeImg));

        filterMenu.add(alphabeticalAZMenuItem);
        filterMenu.add(alphabeticalZAMenuItem);
        filterMenu.add(typeMenuItem);

        // More popup menu
        isMoreMenuOpen = new AtomicBoolean(false);
        moreMenu = new JPopupMenu();

        selectAllMenuItem = new JMenuItem("Select All");
        selectNoneMenuItem = new JMenuItem("Select None");
        invertSelectionMenuItem = new JMenuItem("Invert Selection");

        selectAllMenuItem.setIcon(new ImageIcon(selectAllImg));
        selectNoneMenuItem.setIcon(new ImageIcon(selectNoneImg));
        invertSelectionMenuItem.setIcon(new ImageIcon(invertSelectionImg));

        moreMenu.add(selectAllMenuItem);
        moreMenu.add(selectNoneMenuItem);
        moreMenu.add(invertSelectionMenuItem);

        listDirectoryContent();

        addDoubleClickOnTableFunctionality();
        addSelectedLabelFunctionality();
        addUpperPanelBtnFunctionality();
        addLowerPanelBtnFunctionality();
    }

    private void addDoubleClickOnTableFunctionality()
    {
        view.getTable().addMouseListener(new MouseAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getClickCount() % 2 == 0
                        && view.getTable().getSelectedRow() != -1
                        && view.getTable().rowAtPoint(e.getPoint()) != 1)
                {
                    String clickedItemName = view.getTable().getValueAt(view.getTable().getSelectedRow(), 0).toString();
                    if(Files.isDirectory(currPath.resolve(clickedItemName)))
                    {
                        currPath = currPath.resolve(clickedItemName);
                        listDirectoryContent();
                    }
                    else
                    {
                        try
                        {
                            if(!Files.isExecutable(currPath.resolve(clickedItemName)))
                            {
                                Runtime.getRuntime().exec(currPath.resolve(clickedItemName).toString());
                            }
                            else
                            {
                                Desktop.getDesktop().open(currPath.resolve(clickedItemName).toFile());
                            }
                        }
                        catch (IOException ex)
                        {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });
    }

    private void addSelectedLabelFunctionality()
    {
        view.getTable().getSelectionModel().addListSelectionListener(e ->
        {
            int selectedRowCount = view.getTable().getSelectedRows().length;

            if(selectedRowCount > 0)
            {
                view.getSelectedElementsLabel().setText(selectedRowCount + " Selected");
            }
            else
            {
                view.getSelectedElementsLabel().setText("");
            }
        });
    }

    private void addUpperPanelBtnFunctionality()
    {
        view.getBackBtn().addMouseListener(new MouseAdapter()
        {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mousePressed(MouseEvent e)
            {
                previousPath = currPath;
                currPath = currPath.getParent();
                listDirectoryContent();
            }
        });

        view.getNextBtn().addMouseListener(new MouseAdapter()
        {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mousePressed(MouseEvent e)
            {
                if(previousPath != null)
                {
                    currPath = previousPath;
                    previousPath = null;
                    listDirectoryContent();
                }
            }
        });

        view.getTopBtn().addMouseListener(new MouseAdapter()
        {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
            }
        });

        view.getRefreshBtn().addMouseListener(new MouseAdapter()
        {
            /**
             * {@inheritDoc}
             *
             * @param e
             */
            @Override
            public void mousePressed(MouseEvent e)
            {
                listDirectoryContent();
            }
        });
    }

    private void addLowerPanelBtnFunctionality()
    {
        view.getNewBtn().addActionListener(e ->
        {
            if(isNewPopupMenuOpen.get())
            {
                isNewPopupMenuOpen.set(false);
            }
            else
            {
                newMenu.show(view.getNewBtn(), 0, view.getNewBtn().getHeight());
                isNewPopupMenuOpen.set(true);
            }
        });

        folderMenuItem.addActionListener(e ->
        {
            isNewPopupMenuOpen.set(false);
            Object[] data = {new FileName(folderImg, ""), "", "Directory", "0 Bytes"};
            ((DefaultTableModel) view.getTable().getModel()).addRow(data);

            int lastRow = view.getTable().getRowCount() - 1;
            allowCellEditing(lastRow, new CellEditorListener()
            {
                @Override
                public void editingStopped(ChangeEvent e)
                {
                    CellEditor cellEditor = (CellEditor) e.getSource();

                    try
                    {
                        Files.createDirectory(currPath.resolve(cellEditor.getCellEditorValue().toString()));
                    }
                    catch (IOException ex)
                    {
                        createErrorOptionPane("Folder already exists");
                        throw new RuntimeException(ex);
                    }

                    listDirectoryContent();
                }

                @Override
                public void editingCanceled(ChangeEvent e)
                {
                    listDirectoryContent();
                }
            });
        });

        fileMenuItem.addActionListener(e ->
        {
            isNewPopupMenuOpen.set(false);
            Object[] data = {new FileName(fileImg, ""), "", "File", "0 Bytes"};
            ((DefaultTableModel) view.getTable().getModel()).addRow(data);

            int lastRow = view.getTable().getRowCount() - 1;
            allowCellEditing(lastRow, new CellEditorListener()
            {
                @Override
                public void editingStopped(ChangeEvent e)
                {
                    CellEditor cellEditor = (CellEditor) e.getSource();

                    try
                    {
                        Files.createFile(currPath.resolve(cellEditor.getCellEditorValue().toString()));
                    }
                    catch (IOException ex)
                    {
                        createErrorOptionPane("File already exists");
                        throw new RuntimeException(ex);
                    }

                    listDirectoryContent();
                }

                @Override
                public void editingCanceled(ChangeEvent e)
                {
                    listDirectoryContent();
                }
            });
        });

        view.getRenameBtn().addActionListener(e ->
        {
            int row = view.getTable().getSelectionModel().getLeadSelectionIndex();
            Object originalObject = view.getTable().getValueAt(row, 0);
            Path src = currPath.resolve(originalObject.toString());

            view.getTable().getSelectionModel().clearSelection();

            allowCellEditing(row, new CellEditorListener()
            {
                @Override
                public void editingStopped(ChangeEvent e)
                {
                    CellEditor cellEditor = (CellEditor) e.getSource();
                    Path target = currPath.resolve(cellEditor.getCellEditorValue().toString());

                    boolean success = src.toFile().renameTo(target.toFile());

                    if(!success)
                    {
                        createErrorOptionPane("File was not renamed");
                    }

                    listDirectoryContent();
                }

                @Override
                public void editingCanceled(ChangeEvent e)
                {
                    listDirectoryContent();
                }
            });
        });

        view.getTrashBtn().addActionListener(e ->
        {
            int row = view.getTable().getSelectionModel().getLeadSelectionIndex();
            Path src = currPath.resolve(view.getTable().getValueAt(row, 0).toString());

            try
            {
                if(!Files.deleteIfExists(src))
                {
                    createErrorOptionPane("File was not deleted");
                }
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }

            listDirectoryContent();
        });

        view.getFilterBtn().addActionListener(e ->
        {
            if(isFilterMenuOpen.get())
            {
                isFilterMenuOpen.set(false);
            }
            else
            {
                filterMenu.show(view.getFilterBtn(), 0, view.getFilterBtn().getHeight());
                isFilterMenuOpen.set(true);
            }
        });

        view.getMoreBtn().addActionListener(e ->
        {
            if(isMoreMenuOpen.get())
            {
                isMoreMenuOpen.set(false);
            }
            else
            {
                moreMenu.show(view.getMoreBtn(), 0, view.getMoreBtn().getHeight());
                isMoreMenuOpen.set(true);
            }
        });
    }

    private void listDirectoryContent()
    {
        // Clear table
        ((DefaultTableModel) view.getTable().getModel()).setRowCount(0);

        AtomicInteger count = new AtomicInteger();

        try(Stream<Path> files = Files.list(currPath)) {
            files.forEach(path ->
            {
                if(Files.exists(path))
                {
                    try
                    {
                        Image img;
                        String type;
                        if(Files.isDirectory(path))
                        {
                            img = folderImg;
                            type = "Directory";
                        }
                        else
                        {
                            img = fileImg;
                            type = "File";
                        }

                        String modifiedDate = LocalDateTime.ofInstant(
                                Files.getLastModifiedTime(path).toInstant(),
                                ZonedDateTime.now().getZone()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

                        String fileSize = Files.size(path) + " Bytes";

                        Object[] data = {new FileName(img, path.getFileName().toString()), modifiedDate, type, fileSize};
                        ((DefaultTableModel) view.getTable().getModel()).addRow(data);
                        count.getAndIncrement();
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // Update element count
        view.getElementsLabel().setText(count.get() + " Elements");
        // Update path label
        view.getPathLabel().setText(currPath.toAbsolutePath().normalize().toString());
        // Clear selection to prevent auto-selecting the first row
        SwingUtilities.invokeLater(() -> view.getTable().clearSelection());
    }

    private void allowCellEditing(int row, CellEditorListener cellEditorListener)
    {
        ((FileTableModel) view.getTable().getModel()).setEditableRow(row);
        view.getTable().editCellAt(row, 0);
        ((FileTableModel) view.getTable().getModel()).clearEditableRow();

        view.getTable().getCellEditor(row, 0).addCellEditorListener(cellEditorListener);
    }

    private void createErrorOptionPane(String text)
    {
        JOptionPane.showMessageDialog(view.getTable(), text, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
