package de.nils.explorer.view;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

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
        panel.add(new JButton("Hello world"), BorderLayout.NORTH);
        panel.add(new JButton("Hello World"), BorderLayout.NORTH);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JButton("Hello world"), BorderLayout.WEST);
        frame.add(new JButton("Hello world"), BorderLayout.SOUTH);
        frame.add(new JButton("Hello world"), BorderLayout.EAST);
        frame.add(new JButton("Hello world"), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
}
