package de.nils.explorer;

import javax.swing.*;
import java.nio.file.Paths;

public class explorer
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Hello world");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Paths.get(".", "src", "main", "resources", "icons", "256-explorer.png").toString()).getImage());

        JLabel label = new JLabel("Hello world");
        frame.add(label);

        frame.pack();
        frame.setVisible(true);
    }
}
