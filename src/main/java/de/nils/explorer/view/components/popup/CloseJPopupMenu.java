package de.nils.explorer.view.components.popup;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class CloseJPopupMenu extends JPopupMenu
{
    private boolean isMenuOpen;

    public CloseJPopupMenu(JButton btn)
    {
        addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                isMenuOpen = true;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
                isMenuOpen = false;
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {
                isMenuOpen = false;
            }
        });

        btn.addActionListener(e ->
        {
            if(!isMenuOpen)
            {
                show(btn, 0, btn.getHeight());
            }
        });
    }
}
