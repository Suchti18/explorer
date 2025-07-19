package de.nils.explorer;

import de.nils.explorer.controller.Controller;
import de.nils.explorer.view.View;

public class Explorer
{
    public static void main(String[] args)
    {
        new Controller(new View());
    }
}
