import java.awt.*;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.HashMap;

public class BaseGraph extends JComponent
{
    private JFrame frame = new JFrame("Graphing");//what the graph is displayed on
    JTextArea statsPage = new JTextArea(5, 15);//don't think i use this but it took forever to do and i don't want to delete it
    private GraphAxes thing1 = new GraphAxes();
    private Zoom thing2 = new Zoom(frame);
    private Panning thing3 = new Panning(frame);
    private DrawTickMarks thing4 = new DrawTickMarks();
    private UpdateStatsPage thing5 = new UpdateStatsPage();
    private TickMarkNumbers thing6 = new TickMarkNumbers();
    //private HashMap<String, BigDecimal> variables = new HashMap<>();
    long runTime = 0;
    int numRan = 0;
    //private EquationsMenu equationsMenu;
    private EquationsMenu equationsMenu;
    
    private ArrayList<Equation> equations = new ArrayList<>();
    
    public void addEquation(Equation equation)//stores points
    {
         equations.add(equation);
    }

    public void paintComponent(Graphics g)//this actually draws it
    {
       long startTime = System.currentTimeMillis();
       super.paintComponent(g);
       Graphics2D graphics = (Graphics2D) g;

       BigDecimal xScreenSize = BigDecimal.valueOf(this.getWidth()/2);//width of each quadrant
       BigDecimal yScreenSize = BigDecimal.valueOf(this.getHeight()/2);//height of each quadrant

       graphics.setStroke(new BasicStroke(2));//width of lines
       graphics.setColor(Color.black);
         
       thing1.DrawAxes(graphics, xScreenSize, yScreenSize, thing3.getPanX(), thing3.getPanY());//draws the x and  y axes
       thing4.DrawTicks(graphics, xScreenSize, yScreenSize, thing2.getZoomFactor(), thing3.getPanX(), thing3.getPanY(), thing2.getZoomDynamic());//draws the tick marks
       thing5.UpdateStats(statsPage, xScreenSize, yScreenSize, thing2.getZoomFactor(), thing3.getPanX(), thing3.getPanY(), thing3.getLastPoint(), thing2.getZoomDynamic(), thing4.getTickInterval(), thing3.getLastPoint(), thing2.getExponent(), thing2.calculateMultiplier(), runTime);
       thing6.drawTickNumbers(graphics, xScreenSize, yScreenSize, thing2.getZoomFactor(), thing3.getPanX(), thing3.getPanY(), thing4.getTickInterval(), thing2.getZoomDynamic());//draws the numbers on the tick marks
       equationsMenu.addToMainFrame();
       
       graphics.setColor(Color.red);//sets colors of the equations to red
       
       equations = equationsMenu.getUserInputs();
       
       for(Equation equation: equations)
       {
           //try {
               equation.drawUserEquation(graphics, xScreenSize, yScreenSize, thing3.getPanX(), thing3.getPanY(), thing4.getTickInterval(), thing2.getZoomDynamic());
           /*}
           catch(NumberFormatException e)
           {
               String eq = equation.getUserEquation();
               if(!eq.contains("="))
               {
                   equation.drawUserEquation(graphics, xScreenSize, yScreenSize, thing3.getPanX(), thing3.getPanY(), thing4.getTickInterval(), thing2.getZoomDynamic());
               }
               else if (!variables.containsKey(eq.substring(0, eq.indexOf("="))))
               {
                   variables.put(eq.substring(0, eq.indexOf("=")), BigDecimal.valueOf(Double.parseDouble(eq.substring(eq.indexOf("=") + 1))));
               }
           }*/
       }
      long endTime = System.currentTimeMillis();
      runTime += endTime - startTime;
      numRan++;
   }

   
    public void display() {
        frame.setLayout(new GridBagLayout());//idk what this does i got it from stackoverflow
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setPreferredSize(new Dimension(700, 500));
        
        GridBagConstraints gbc = new GridBagConstraints();//this was absolute hell to set up
        gbc.anchor = GridBagConstraints.NORTHEAST;
        JButton radDegButton = new JButton("Rad");//don't think this works
        boolean rad = true;
       radDegButton.addActionListener(new ActionListener()
       {
            @Override
            public void actionPerformed(ActionEvent f)
            {
               System.out.println("ajodiwjoia");
            }
        });

        
        /*GridBagConstraints gbcStatsPage = new GridBagConstraints(); //this either
        gbcStatsPage.anchor = GridBagConstraints.NORTHEAST;//where component should be placed if doesn't fill entire cell
        gbcStatsPage.gridwidth = GridBagConstraints.REMAINDER;//how many cells in grid component will span, will span remaining columns in row
        gbcStatsPage.fill = GridBagConstraints.NONE; //doesn't let it expand
        frame.add(statsPage, gbcStatsPage); //adds statspage to frame
        gbcStatsPage.gridx = 1;//specifies column where component should be placed default 0 (left)         
        gbcStatsPage.gridy = 0;//specifies row default 0 (top)
        gbcStatsPage.weightx = 0.7;//how much it should grow horizontally                       
        gbcStatsPage.weighty = 1.0;                          
        gbcStatsPage.fill = GridBagConstraints.BOTH; //how component should be resized if doesn't fit cell     
        frame.add(this, gbcStatsPage);
        
        GridBagConstraints gbcEquationHolder = new GridBagConstraints();
        gbcEquationHolder.anchor = GridBagConstraints.NORTHWEST;
        gbcEquationHolder.fill = GridBagConstraints.NONE;
        gbcEquationHolder.gridy = 0;
        gbcEquationHolder.gridx = 0;
        gbcEquationHolder.gridwidth = this.getWidth()/2;
        gbcEquationHolder.gridheight = this.getHeight()*2;
        gbcEquationHolder.weighty = 0.0;
        gbcEquationHolder.weightx = 1.0;
        */
        equationsMenu = new EquationsMenu(this, frame);
        //statsPage.setEditable(false);
        //equationsMenu.setVisible(true);
        frame.setVisible(true);
    }
    /*public HashMap<String, BigDecimal> getVariables()
    {
        return variables;
    }*/
}