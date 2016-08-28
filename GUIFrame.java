import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class GUIFrame extends JFrame
{
    FunctionPanel fPanel;
    TextInputController tPanel;
    JTextArea textArea;

    public GUIFrame(String title)
    {
        super(title);
        
        setLayout(new GridBagLayout());
        
        fPanel = new FunctionPanel();
        tPanel = new TextInputController(fPanel);
        
        
        JPanel south = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        south.add(tPanel, c);
        
        //Add Components to this panel.
        c = new GridBagConstraints(); 
        c.gridwidth = GridBagConstraints.REMAINDER; 
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;  
        c.weighty = 0.5;
        add(fPanel, c);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.5;   
        add(south, c);

        
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        pack();
        GraphingData.setFunctionPanel(fPanel);
    }
}