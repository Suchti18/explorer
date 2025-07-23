package de.nils.explorer.controller;

import de.nils.explorer.common.Const;
import de.nils.explorer.view.GuiResources;
import de.nils.explorer.view.View;
import de.nils.explorer.view.components.popup.CloseJPopupMenu;
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
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Controller
{
    private final Image folderImg;
    private final Image fileImg;

    private final View view;
    private Path currPath;
    private Path previousPath;

    private Comparator<Path> filter;

    // Create new files popup menu
    private final JMenuItem folderMenuItem;
    private final JMenuItem fileMenuItem;

    // Filter popup menu
    private final JMenuItem alphabeticalAZMenuItem;
    private final JMenuItem alphabeticalZAMenuItem;
    private final JMenuItem typeMenuItem;

    // More popup menu
    private final JMenuItem selectAllMenuItem;
    private final JMenuItem selectNoneMenuItem;
    private final JMenuItem invertSelectionMenuItem;

    public Controller(View view)
    {

        folderImg = GuiResources.loadImage(Const.FOLDER_SVG, 16, 16);
        fileImg = GuiResources.loadImage(Const.FILE_SVG, 16, 16);

        this.view = view;
        currPath = Paths.get(".").toAbsolutePath().normalize();

        // New files popup menu
        JPopupMenu newMenu = new CloseJPopupMenu(view.getNewBtn());

        folderMenuItem = new JMenuItem("Folder");
        fileMenuItem = new JMenuItem("File");

        folderMenuItem.setIcon(new ImageIcon(folderImg));
        fileMenuItem.setIcon(new ImageIcon(fileImg));

        newMenu.add(folderMenuItem);
        newMenu.add(fileMenuItem);

        // Filter popup menu
        JPopupMenu filterMenu = new CloseJPopupMenu(view.getFilterBtn());

        alphabeticalAZMenuItem = new JMenuItem("Alphabetical (A-Z)");
        alphabeticalZAMenuItem = new JMenuItem("Alphabetical (Z-A)");
        typeMenuItem = new JMenuItem("Type");

        alphabeticalAZMenuItem.setIcon(GuiResources.loadImageIcon(Const.ALPHABETICAL_AZ_SVG, 16, 16));
        alphabeticalZAMenuItem.setIcon(GuiResources.loadImageIcon(Const.ALPHABETICAL_ZA_SVG, 16, 16));
        typeMenuItem.setIcon(GuiResources.loadImageIcon(Const.TYPE_SVG, 16, 16));

        filterMenu.add(alphabeticalAZMenuItem);
        filterMenu.add(alphabeticalZAMenuItem);
        filterMenu.add(typeMenuItem);

        // More popup menu
        JPopupMenu moreMenu = new CloseJPopupMenu(view.getMoreBtn());

        selectAllMenuItem = new JMenuItem("Select All");
        selectNoneMenuItem = new JMenuItem("Select None");
        invertSelectionMenuItem = new JMenuItem("Invert Selection");

        selectAllMenuItem.setIcon(GuiResources.loadImageIcon(Const.SELECT_ALL_SVG, 16, 16));
        selectNoneMenuItem.setIcon(GuiResources.loadImageIcon(Const.SELECT_NONE_SVG, 16, 16));
        invertSelectionMenuItem.setIcon(GuiResources.loadImageIcon(Const.INVERT_SVG, 16, 16));

        moreMenu.add(selectAllMenuItem);
        moreMenu.add(selectNoneMenuItem);
        moreMenu.add(invertSelectionMenuItem);

        listDirectoryContent();

        addDoubleClickOnTableFunctionality();
        addSelectedLabelFunctionality();
        addUpperPanelBtnFunctionality();
        addLowerPanelBtnFunctionality();
        addSidebarPinsFunctionality();
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
            public void mousePressed(MouseEvent e)
            {
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
        folderMenuItem.addActionListener(e ->
        {
            Object[] data = {new FileName(folderImg, Const.EMPTY), Const.EMPTY, "Directory", "0 Bytes"};
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
            Object[] data = {new FileName(fileImg, Const.EMPTY), Const.EMPTY, "File", "0 Bytes"};
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

        view.getShareBtn().addActionListener(e ->
        {

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

        alphabeticalAZMenuItem.addActionListener(e ->
        {
            filter = null;
            listDirectoryContent();
        });

        alphabeticalZAMenuItem.addActionListener(e ->
        {
            filter = Comparator.comparing(Path::getFileName).reversed();
            listDirectoryContent();
        });

        typeMenuItem.addActionListener(e ->
        {
            filter = (o1, o2) -> Files.isDirectory(o1) && Files.isDirectory(o2) ? 0 : Files.isDirectory(o1) ? -1 : 1;
            listDirectoryContent();
        });

        selectAllMenuItem.addActionListener(e -> view.getTable().selectAll());

        selectNoneMenuItem.addActionListener(e -> view.getTable().clearSelection());

        invertSelectionMenuItem.addActionListener(e ->
        {
            int[] gotSelected = view.getTable().getSelectedRows();
            view.getTable().selectAll();

            for(int i : gotSelected)
            {
                view.getTable().removeRowSelectionInterval(i, i);
            }
        });
    }

    private void addSidebarPinsFunctionality()
    {
        for(JButton sidebarPin : view.getSidebarPins())
        {
            Path userhome = Paths.get(System.getProperty("user.home"));
            Path dest = userhome.resolve(sidebarPin.getText());

            if(Files.exists(dest))
            {
                sidebarPin.addActionListener(e ->
                {
                    if (Files.exists(dest))
                    {
                        currPath = dest;
                        listDirectoryContent();
                    }
                    else
                    {
                        createErrorOptionPane("Not found: " + dest);
                    }
                });
            }
            else
            {
                sidebarPin.getParent().remove(sidebarPin);
            }
        }
    }

    private void listDirectoryContent()
    {
        // Clear table
        ((DefaultTableModel) view.getTable().getModel()).setRowCount(0);

        AtomicInteger count = new AtomicInteger();

        try(Stream<Path> files = Files.list(currPath))
        {
            if(filter != null)
            {
                files.sorted(filter).forEach(path ->
                {
                    addSingleRow(path);
                    count.getAndIncrement();
                });
            }
            else
            {
                files.forEach(path ->
                {
                    addSingleRow(path);
                    count.getAndIncrement();
                });
            }
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

    private void addSingleRow(Path path)
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
                        ZonedDateTime.now().getZone()).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT));

                String fileSize = Files.size(path) + " Bytes";

                Object[] data = {new FileName(img, path.getFileName().toString()), modifiedDate, type, fileSize};
                ((DefaultTableModel) view.getTable().getModel()).addRow(data);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
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
