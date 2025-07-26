package de.nils.explorer.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.nils.explorer.common.Const;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class GuiResources
{
    public static Image loadImage(String path, int width, int height)
    {
        try
        {
            if(path.endsWith(".svg"))
            {
                FlatSVGIcon svg = new FlatSVGIcon(GuiResources.class.getResourceAsStream(path));
                return svg.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            }
            else
            {
                return ImageIO.read(Objects.requireNonNull(GuiResources.class.getResource(Const.EXPLORER_ICON)))
                        .getScaledInstance(width, height, Image.SCALE_SMOOTH);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static ImageIcon loadImageIcon(String path, int width, int height)
    {
        return new ImageIcon(loadImage(path, width, height));
    }
}
