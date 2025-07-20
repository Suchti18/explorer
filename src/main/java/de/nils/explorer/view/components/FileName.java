package de.nils.explorer.view.components;

import java.awt.*;

public class FileName
{
    private final Image image;
    private final String fileName;

    public FileName(Image image, String fileName)
    {
        this.image = image;
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public Image getImage()
    {
        return image;
    }
}
