package de.nils.explorer.controller;

import de.nils.explorer.common.Const;
import de.nils.explorer.view.GuiResources;
import de.nils.explorer.view.View;
import de.nils.explorer.view.components.popup.CloseJPopupMenu;
import de.nils.explorer.view.components.tables.FileName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Controller
{
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private final Image folderImg;
    private final Image fileImg;
    private final Image driveImg;

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
    private final JMenuItem addToSidebarMenuItem;

    public Controller(View view)
    {
        folderImg = GuiResources.loadImage(Const.FOLDER_SVG, 16, 16);
        fileImg = GuiResources.loadImage(Const.FILE_SVG, 16, 16);
        driveImg = GuiResources.loadImage(Const.DRIVE_SVG, 16, 16);

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
        addToSidebarMenuItem = new JMenuItem("Add To Sidebar");

        selectAllMenuItem.setIcon(GuiResources.loadImageIcon(Const.SELECT_ALL_SVG, 16, 16));
        selectNoneMenuItem.setIcon(GuiResources.loadImageIcon(Const.SELECT_NONE_SVG, 16, 16));
        invertSelectionMenuItem.setIcon(GuiResources.loadImageIcon(Const.INVERT_SVG, 16, 16));
        addToSidebarMenuItem.setIcon(GuiResources.loadImageIcon(Const.ADD_SVG, 16, 16));

        moreMenu.add(selectAllMenuItem);
        moreMenu.add(selectNoneMenuItem);
        moreMenu.add(invertSelectionMenuItem);
        moreMenu.addSeparator();
        moreMenu.add(addToSidebarMenuItem);

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
                        && view.getTable().rowAtPoint(e.getPoint()) != -1)
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

            view.getSelectedElementsLabel().setAmount(selectedRowCount);
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
                if(currPath.getParent() != null)
                {
                    previousPath = currPath;
                    currPath = currPath.getParent();
                    listDirectoryContent();
                }
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
                if(currPath.getRoot() != null)
                {
                    previousPath = currPath;
                    currPath = currPath.getRoot();
                    listDirectoryContent();
                }
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

        // Right click path entering
        JTextField pathTextField = new JTextField();
        pathTextField.setBorder(BorderFactory.createEmptyBorder());
        pathTextField.setOpaque(false);
        pathTextField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if(e.getKeyChar() == KeyEvent.VK_ESCAPE)
                {
                    view.getPathPanel().transferFocusBackward();
                }
                else if(e.getKeyChar() == KeyEvent.VK_ENTER)
                {
                    Path enteredPath = Paths.get(pathTextField.getText());
                    if(Files.exists(enteredPath))
                    {
                        currPath = enteredPath;
                        listDirectoryContent();
                    }
                    else
                    {
                        createErrorOptionPane("Does not exist");
                    }

                    view.getPathPanel().transferFocusBackward();
                }
                else
                {
                    view.getPathPanel().invalidate();
                    view.getPathPanel().revalidate();
                }
            }
        });

        pathTextField.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {}

            @Override
            public void focusLost(FocusEvent e)
            {
                view.getPathPanel().remove(pathTextField);
                view.getPathLabel().setVisible(true);
                view.getPathPanel().invalidate();
                view.getPathPanel().revalidate();
                view.getPathPanel().repaint();
            }
        });

        // Left click popup
        JPopupMenu pathPopup = new JPopupMenu();
        JMenuItem copyItem  = new JMenuItem("Copy");
        copyItem.addActionListener(e ->
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(currPath.toString()), null));
        pathPopup.add(copyItem);

        view.getPathPanel().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(e.getButton() == MouseEvent.BUTTON1)
                {
                    pathTextField.setText(Const.EMPTY);
                    view.getPathLabel().setVisible(false);

                    view.getPathPanel().add(pathTextField);
                    pathTextField.requestFocusInWindow();
                    view.getPathPanel().invalidate();
                    view.getPathPanel().revalidate();
                }
                else if(e.getButton() == MouseEvent.BUTTON3)
                {
                    pathPopup.show(view.getPathPanel(), e.getX(), e.getY());
                }
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
            view.getTable().allowCellEditing(lastRow, new CellEditorListener()
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

                    ((CellEditor) e.getSource()).removeCellEditorListener(this);
                    listDirectoryContent();
                }

                @Override
                public void editingCanceled(ChangeEvent e)
                {
                    ((CellEditor) e.getSource()).removeCellEditorListener(this);
                    listDirectoryContent();
                }
            });
        });

        fileMenuItem.addActionListener(e ->
        {
            Object[] data = {new FileName(fileImg, Const.EMPTY), Const.EMPTY, "File", "0 Bytes"};
            ((DefaultTableModel) view.getTable().getModel()).addRow(data);

            int lastRow = view.getTable().getRowCount() - 1;
            view.getTable().allowCellEditing(lastRow, new CellEditorListener()
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

                    ((CellEditor) e.getSource()).removeCellEditorListener(this);
                    listDirectoryContent();
                }

                @Override
                public void editingCanceled(ChangeEvent e)
                {
                    ((CellEditor) e.getSource()).removeCellEditorListener(this);
                    listDirectoryContent();
                }
            });
        });

        view.getRenameBtn().addActionListener(e ->
        {
            int row = view.getTable().getSelectionModel().getMaxSelectionIndex();

            if(row != -1)
            {
                Object originalObject = view.getTable().getValueAt(row, 0);
                Path src = currPath.resolve(originalObject.toString());

                view.getTable().getSelectionModel().clearSelection();

                view.getTable().allowCellEditing(row, new CellEditorListener()
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

                        ((CellEditor) e.getSource()).removeCellEditorListener(this);
                        listDirectoryContent();
                    }

                    @Override
                    public void editingCanceled(ChangeEvent e)
                    {
                        ((CellEditor) e.getSource()).removeCellEditorListener(this);
                        listDirectoryContent();
                    }
                });
            }
            else
            {
                createErrorOptionPane("No file selected");
            }
        });

        view.getShareBtn().addActionListener(e ->
        {
            try
            {
                // Open standard mail client
                Desktop.getDesktop().mail();
            }
            catch (IOException ex)
            {
                createErrorOptionPane("Cannot open a mail program");
            }
        });

        view.getTrashBtn().addActionListener(e ->
        {
            int row = view.getTable().getSelectionModel().getMinSelectionIndex();

            if(row != -1)
            {
                int maxRow = view.getTable().getSelectionModel().getMaxSelectionIndex();
                for(; row <= maxRow; row++)
                {
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
                }

                listDirectoryContent();
            }
            else
            {
                createErrorOptionPane("No file selected");
            }
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
            // If both paths are directories => 0 => Equal
            // Else if o1 is a directory => -1
            // Else => 1
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

        addToSidebarMenuItem.addActionListener(e ->
        {
            for(int i = 0; i < view.getSideBar().getComponentCount(); i++)
            {
                if(view.getSideBar().getComponent(i) instanceof JSeparator)
                {
                    JButton btn = view.createSidebarBtn(Const.PIN_SVG, currPath.toString());

                    Path dest = Paths.get(btn.getText());

                    JPopupMenu popup = new CloseJPopupMenu(btn, MouseEvent.BUTTON3);

                    JMenuItem remove = new JMenuItem("Remove");
                    remove.addActionListener(ae ->
                    {
                        view.getSideBar().remove(btn);
                        view.getSideBar().invalidate();
                        view.getSideBar().revalidate();
                    });

                    popup.add(remove);

                    btn.addMouseListener(new MouseAdapter()
                    {
                        @Override
                        public void mouseClicked(MouseEvent e)
                        {
                            if(SwingUtilities.isLeftMouseButton(e))
                            {
                                if(Files.exists(dest))
                                {
                                    currPath = dest;
                                    listDirectoryContent();
                                }
                                else
                                {
                                    createErrorOptionPane("Not found: " + dest);
                                }
                            }
                        }
                    });

                    view.getSideBar().add(btn, i - 1);
                    break;
                }
            }

            view.getSideBar().invalidate();
            view.getSideBar().revalidate();
        });
    }

    private void addSidebarPinsFunctionality()
    {
        for(JButton sidebarPin : view.getSidebarPins())
        {
            Path home = Paths.get(System.getProperty(Const.USER_HOME));
            Path dest = home.resolve(sidebarPin.getText());

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
                view.getSideBar().remove(sidebarPin);
            }
        }

        view.getSideBar().invalidate();
        view.getSideBar().revalidate();

        view.getThisPC().addActionListener(e ->
        {
            view.getTable().clearTable();

            for(File file : File.listRoots())
            {
                Object[] data = {new FileName(driveImg, file.toString()), "", "Drive", getSizeString(file.getTotalSpace())};
                ((DefaultTableModel) view.getTable().getModel()).addRow(data);
            }

            currPath = Paths.get("");
        });

        view.getNetwork().addActionListener(e ->
        {
            if(System.getProperty("os.name").startsWith("Windows"))
            {
                String uncPathString = JOptionPane.showInputDialog("Enter an UNC Path (\\\\Server\\Path\\to\\Drive)");

                if(uncPathString != null && uncPathString.startsWith("\\\\"))
                {
                    log.debug("");
                    Path uncPath = Paths.get(uncPathString);

                    String user = null;
                    String pass = null;
                    if(!Files.exists(uncPath))
                    {
                        user = JOptionPane.showInputDialog("Enter the user for this drive");
                        pass = JOptionPane.showInputDialog("Enter the password for this drive");
                    }

                    if(user != null && pass != null)
                    {
                        String command = String.format("net use %s /user:%s %s", uncPathString, user, pass);
                        log.debug("Running <{}>", command);

                        try
                        {
                            Runtime.getRuntime().exec(command).waitFor();
                        }
                        catch (InterruptedException | IOException ex)
                        {
                            throw new RuntimeException(ex);
                        }
                    }

                    if(Files.exists(uncPath))
                    {
                        log.debug("Connected to UNC: <{}>",  uncPath);
                        previousPath = currPath;
                        currPath = uncPath;
                        listDirectoryContent();
                    }
                    else
                    {
                        createErrorOptionPane("Not found. Try again and make sure that you have entered everything correctly");
                    }
                }
                else
                {
                    if(uncPathString != null)
                    {
                        createErrorOptionPane("Invalid UNC Path");
                    }
                }
            }
            else
            {
                createErrorOptionPane("Only supported on windows (Im sorry)");
            }
        });
    }

    private void listDirectoryContent()
    {
        log.trace("Showing contents of: <{}>",  currPath);

        view.getTable().endCellEditing();
        view.getTable().clearTable();

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
            createErrorOptionPane("Error: " + e);
        }

        // Update element count
        view.getElementsLabel().setAmount(count.get());
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
                String fileSize;
                if(Files.isDirectory(path))
                {
                    img = folderImg;
                    type = "Directory";

                    DirectorySizeFileVisitor fileVisitor = new DirectorySizeFileVisitor();
                    Files.walkFileTree(path, new HashSet<>(), 4, fileVisitor);
                    fileSize = getSizeString(fileVisitor.getSizeResult());
                }
                else
                {
                    img = fileImg;
                    type = "File";
                    fileSize = getSizeString(Files.size(path));
                }

                String modifiedDate = LocalDateTime.ofInstant(
                        Files.getLastModifiedTime(path).toInstant(),
                        ZonedDateTime.now().getZone()).format(DateTimeFormatter.ofPattern(Const.DATE_FORMAT));

                Object[] data = {new FileName(img, path.getFileName().toString()), modifiedDate, type, fileSize};
                ((DefaultTableModel) view.getTable().getModel()).addRow(data);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private void createErrorOptionPane(String text)
    {
        JOptionPane.showMessageDialog(view.getTable(), text, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getSizeString(long size)
    {
        int unitIndex = 0;
        double tempSize = size;

        for(; tempSize >= 1024 && unitIndex < Const.BINARY_PREFIX_UNITS.length - 1; unitIndex++)
        {
            tempSize /= 1024;
        }

        return Math.floor(tempSize) == tempSize ? String.format("%d %s", (long) tempSize, Const.BINARY_PREFIX_UNITS[unitIndex])
                : String.format("%.2f %s", tempSize, Const.BINARY_PREFIX_UNITS[unitIndex]);
    }
}
