package de.nils.explorer.controller;

import de.nils.explorer.view.View;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Controller
{
    private final View view;
    private Path currPath;

    public Controller(View view)
    {
        this.view = view;
        currPath = Paths.get(".");

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
                                System.out.println(currPath.resolve(clickedItemName).toFile());
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

    public void listDirectoryContent()
    {
        // Clear table
        ((DefaultTableModel) view.getTable().getModel()).setRowCount(0);

        try(Stream<Path> files = Files.list(currPath)) {
            files.forEach(path ->
            {
                if(Files.exists(path))
                {
                    try {
                        String[] data = {path.getFileName().toString(), Files.getLastModifiedTime(path).toString(), Files.isDirectory(path) ? "Directory" : "File", String.valueOf(Files.size(path)) + " Bytes"};
                        ((DefaultTableModel) view.getTable().getModel()).addRow(data);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
