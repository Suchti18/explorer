package de.nils.explorer.controller;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.nils.explorer.view.View;
import de.nils.explorer.view.components.tables.FileName;

import javax.swing.*;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Controller
{
    private final Image folderImg;
    private final Image fileImg;

    private final View view;
    private Path currPath;

    public Controller(View view)
    {
        FlatSVGIcon svg;
        try
        {
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/folder.svg"));
            folderImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            svg = new FlatSVGIcon(getClass().getResourceAsStream("/images/file.svg"));
            fileImg = svg.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        this.view = view;
        currPath = Paths.get(".").toAbsolutePath().normalize();

        listDirectoryContent();

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
                currPath = currPath.getParent();
                listDirectoryContent();
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

        view.getNewBtn().addActionListener(e ->
        {
            Component b = (Component) e.getSource();

            Point p=b.getLocationOnScreen();

            JPopupMenu menu = new JPopupMenu();

            JMenuItem m1 = new JMenuItem("Folder");
            JMenuItem m2 = new JMenuItem("File");

            m1.setIcon(new ImageIcon(folderImg));
            m2.setIcon(new ImageIcon(fileImg));

            menu.add(m1);
            menu.add(m2);

            menu.show(view.getNewBtn(),0,0);

            menu.setLocation(p.x,p.y+b.getHeight());
        });
    }

    public void listDirectoryContent()
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
    }
}
