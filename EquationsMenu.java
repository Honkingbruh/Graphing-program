import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
//this is the red and blue thing that accepts equations at the left side of the screen
public class EquationsMenu extends JMenuBar {
   private static final int width = 500;
   private static final int height = 700;
   private static final int frameHeight = 50;//i think this is how tall the thing that holds the equations should be (individual text box)
   private ArrayList<Equation> userInputs = new ArrayList<>();
   private boolean b = false;

   private BaseGraph baseGraph;
   private JFrame frame;

   public EquationsMenu(BaseGraph baseGraphInit, JFrame initFrame) {
      baseGraph = baseGraphInit;
      frame = initFrame;
      addToMainFrame();
   }

   public void addToMainFrame() {
      if (!b) {//only adds to main frame once
         b = true;
         frame.setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         
         JPanel panel = new JPanel();// place where + button goes
         panel.setBackground(Color.red);
         panel.setVisible(true);
         
         gbc.gridx = 0;//got all these from stackoverflow no clue what they do they just took forever to put in
         gbc.gridy = 0;
         gbc.weightx = 0.3;
         gbc.weighty = 0.05;
         gbc.gridwidth = 1;
         gbc.gridheight = 1;
         gbc.anchor = GridBagConstraints.NORTHWEST;
         gbc.fill = GridBagConstraints.BOTH;

         JPanel eqPanel = new JPanel();
         eqPanel.setBackground(Color.blue);
         eqPanel.setVisible(true);
         eqPanel.setSize(233, 500);
         eqPanel.setPreferredSize(new Dimension(233, 500));
         
         JPanel tempPanel = new JPanel();
         tempPanel.setBackground(Color.green);
         tempPanel.setVisible(true);
         
         frame.add(panel, gbc);
         gbc.gridy = 1;
         gbc.weighty = 0.95;
         frame.add(eqPanel, gbc);
         gbc.gridy = 0;
         gbc.gridx = 1;
         gbc.weightx = 0.7;
         gbc.weighty = 1;
         gbc.gridheight = 2;
         frame.add(baseGraph, gbc);

         JButton button = new JButton("+");//plus button to add new equation
         panel.add(button);
          /*
          JScrollPane scrollPane = new JScrollPane(eqPanel,
          JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
          */
          GridBagConstraints gbc1 = new GridBagConstraints();
          gbc1.fill = GridBagConstraints.NONE;
          gbc1.weightx = 1;
          gbc1.weighty = 1;
          gbc1.gridheight = 1;
          gbc1.gridwidth = 1;
          gbc1.gridx = 0;
          gbc1.gridy = 0;
          button.addActionListener(new ActionListener()
          {
            @Override
            public void actionPerformed(ActionEvent e)
            {
               JTextField newTextField = new JTextField();
               newTextField.setSize(200, 50);
               newTextField.setPreferredSize(new Dimension(200, 30));
               newTextField.setFont(new Font("Arial", Font.PLAIN, 17));
               gbc1.gridx = 0;
               eqPanel.add(newTextField, gbc1);
               JButton newCloseButton = new JButton("x");
               newCloseButton.setFont(new Font("Arial", Font.PLAIN, 15));
               newCloseButton.setMargin(new Insets(0,0,0,0));
               newCloseButton.setForeground(Color.BLACK);
               newCloseButton.setSize(25,25);
               newCloseButton.setPreferredSize(new Dimension(25, 25));
               gbc1.gridx = 1;
               eqPanel.add(newCloseButton, gbc1);
               frame.revalidate();
               newTextField.addActionListener(new ActionListener()
               {//text field changed
                
                  @Override
                  public void actionPerformed(ActionEvent k)
                  {
                     ArrayList<Component> temp = new ArrayList<>();
                     temp = new ArrayList<>(Arrays.asList(eqPanel.getComponents()));
                     for(int i = 0; i < eqPanel.getComponents().length; i++)
                     {  
                        if(temp.get(i) instanceof JTextField && userInputs.size() < i + 1)
                        {
                           userInputs.add(new Equation(((JTextField)temp.get(i)).getText()));
                        }
                        else if(temp.get(i) instanceof JTextField)
                        {
                           userInputs.set(i, new Equation(((JTextField)temp.get(i)).getText()));
                        }
                     }
                     frame.repaint();
                  }
               });
               newCloseButton.addActionListener(new ActionListener()
               {//x button pressed 
                  @Override
                  public void actionPerformed(ActionEvent z)
                  {
                     eqPanel.remove(newTextField);
                     eqPanel.remove(newCloseButton);
                     frame.repaint();
                  }
               });

            }
          });
          /*userInputs.add(newTextField.getText());
          baseGraph.addEquation(new Equation(newTextField.getText()));
          newTextField.setMaximumSize(new Dimension(width + 75, 50)); // Set maximum
          height
          eqPanel.add(newTextField);
          frame.revalidate();*.
          /*
          JButton infoButton = new JButton("O");
          infoButton.addActionListener(new ActionListener()
          {
          
          @Override
          public void actionPerformed(ActionEvent m)
          {
          System.out.println("go fuck yourself");
          }
          });
          }
          });
          
          }
          });
          */
         // frame.setLayout(new GridBagLayout());
         // GridBagConstraints gbc = new GridBagConstraints();
         // frame.add(scrollPane);
         //int frameWidth = panelSize.width; // Use the width of the red panel
         //int frameHeight = panelSize.height + eqPanelSize.height;
         //frame.setSize(frameWidth, frameHeight);
         //frame.setVisible(true);
         frame.pack();
         frame.setVisible(true);
      }
   }
   public ArrayList<Equation> getUserInputs()
   {
      return userInputs;
   }
}