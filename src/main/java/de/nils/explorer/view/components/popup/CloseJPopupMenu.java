package de.nils.explorer.view.components.popup;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CloseJPopupMenu extends JPopupMenu
{
    private boolean isMenuOpen;

    public CloseJPopupMenu(JButton btn)
    {
        this(btn, MouseEvent.BUTTON1);
    }

    public CloseJPopupMenu(JButton btn, int mouseBtn)
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

        btn.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if(e.getButton() == mouseBtn)
                {
                    if(!isMenuOpen)
                    {
                        show(btn, 0, btn.getHeight());
                    }
                }
            }
        });
    }
}
