package de.nils.explorer.view.components.listeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListDragListener extends MouseAdapter
{
    private final JScrollPane list;
    private final JFrame frame;
    private final JComponent trigger;
    private Point initialMousePosition = null;
    private int initialWidth;
    private final JComponent glassPane;

    public ListDragListener(JScrollPane list, JFrame frame, JComponent trigger)
    {
        this.list = list;
        this.frame = frame;
        this.trigger = trigger;
        this.glassPane = (JComponent) frame.getGlassPane();
        glassPane.setOpaque(false);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(initialMousePosition != null)
        {
            Point currentScreenPos = e.getLocationOnScreen();
            int deltaX = currentScreenPos.x - initialMousePosition.x;
            int newWidth = Math.max(50, initialWidth + deltaX);

            if(newWidth > frame.getWidth() - 50)
            {
                return;
            }

            list.setPreferredSize(new Dimension(newWidth, frame.getHeight()));
            list.setMinimumSize(new Dimension(50, frame.getHeight()));

            list.invalidate();
            frame.revalidate();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(initialMousePosition != null)
        {
            initialMousePosition = null;

            glassPane.setVisible(false);
            glassPane.setCursor(Cursor.getDefaultCursor());
            glassPane.removeMouseMotionListener(this);
            glassPane.removeMouseListener(this);
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        initialMousePosition = e.getLocationOnScreen();
        initialWidth = list.getWidth();

        glassPane.setVisible(true);
        glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        glassPane.addMouseMotionListener(this);
        glassPane.addMouseListener(this);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if(!glassPane.isVisible())
        {
            if(e.getComponent() == trigger)
            {
                trigger.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            }
            else
            {
                trigger.setCursor(Cursor.getDefaultCursor());
            }
        }
    }
}