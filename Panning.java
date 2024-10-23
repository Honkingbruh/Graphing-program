import java.awt.*;
import javax.swing.*;
import java.awt.event.*;//mouse events
import java.math.BigDecimal;
public class Panning
{
   private BigDecimal panX = BigDecimal.ZERO;//used when user moves graph with mouse
   private BigDecimal panY = BigDecimal.ZERO;
   private Point lastPoint;//adds diff from last point and current point from mouse and adds to panX and panY
   public Panning(JFrame frame)
   {
      frame.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent m)
         {
            lastPoint = m.getPoint();
         }
      });
      frame.addMouseMotionListener(new MouseAdapter() 
      {//panning
            public void mouseDragged(MouseEvent m)//when mouse dragged
            {
                if (lastPoint != null)//if lastpoint is defined
                {
                    panX = panX.add(BigDecimal.valueOf(m.getX()-lastPoint.x));//add the difference
                    panY = panY.add(BigDecimal.valueOf(m.getY()-lastPoint.y));
                    frame.repaint();
                }
                lastPoint = m.getPoint();
            }
        });
   }
   public BigDecimal getPanX()
   {
      return panX;
   }
   public BigDecimal getPanY()
   {
      return panY;
   }
   public Point getLastPoint()
   {
      return lastPoint;
   }
}