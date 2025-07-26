package de.nils.explorer.view.components;

import javax.swing.*;

public class CounterLabel extends JLabel
{
    private final String suffix;

    public CounterLabel(String suffix)
    {
        this.suffix = suffix;
        setText("");
    }

    public void setAmount(int counter)
    {
        if(counter > 0)
        {
            this.setText(counter + " " + suffix);
        }
        else
        {
            this.setText("");
        }
    }
}
