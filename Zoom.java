import javax.swing.*;
import java.awt.event.*;//mouse events
import java.math.*;
public class Zoom
{//this alone took like two months
   private BigDecimal zoomFactor = BigDecimal.ONE;//i think this is how much it's zoomed in
   private BigDecimal zoomDynamic = BigDecimal.ONE;//i think this is related to how far apart the ticks are but i made this like a year ago so idk
   private BigDecimal exponent;
   private final BigDecimal ZERO = BigDecimal.valueOf(0);
   private final BigDecimal respacingVal = BigDecimal.valueOf(1.25);//if wheel rotates a certain amount then respace the tick marks by this much
   private final BigDecimal zoomingVal = BigDecimal.valueOf(1.1);//if wheel rotates then zoom by this factor
   public Zoom(JFrame frame)
   {
      frame.addMouseWheelListener(new MouseAdapter() 
      {//recieves mouse events
         public void mouseWheelMoved(MouseWheelEvent m) 
         {//zooming
            if(m.getWheelRotation() < 0)//zooming in
            {
               zoomFactor = zoomFactor.multiply(zoomingVal).setScale(64, RoundingMode.HALF_UP);//multiply by 1.1
            } else {//zooming out
               zoomFactor = zoomFactor.divide(zoomingVal, 64, RoundingMode.HALF_UP);///divide by 1.1
            }
            exponent = BigDecimal.valueOf(Math.floor(Math.log(zoomFactor.doubleValue()) / Math.log(2)));//log base 2 of zoomFactor rounded to nearest integer
            if(exponent.compareTo(ZERO) >= 0)//exponent is positive
            {
               zoomDynamic = BigDecimal.valueOf(2).pow(exponent.intValue()).multiply(calculateMultiplier());
            }
            if(exponent.compareTo(ZERO) < 0)//exponent is negative
            {
               zoomDynamic = BigDecimal.ONE.divide(BigDecimal.valueOf(2).pow(-1*exponent.intValue()), 128, RoundingMode.HALF_UP).multiply(calculateMultiplier());
            }
            frame.repaint();
         }
      });
   }
   public BigDecimal calculateMultiplier()//every third exponent value zoomDynamic should be multiplied by 1.25 if going up and divided if going down
   {//starting at value of 1
     if(exponent == null)
     {
         exponent = ZERO;//initial value
     }
     if(exponent.compareTo(ZERO) < 0)//zoomed out past default, exponent is negative
     {
         int multi = (exponent.intValue() - 1)/3;
         return BigDecimal.valueOf(1).divide(respacingVal.pow(-1*multi), 128, RoundingMode.HALF_UP);
     }
     else//zoomed in past default, exponent is positive
     {
         int multi = (exponent.intValue() + 1)/3;
         return respacingVal.pow(multi);
     }
   }
   public BigDecimal getZoomFactor()
   {
      return zoomFactor;
   }
   public BigDecimal getZoomDynamic()
   {
      return zoomDynamic;
   }
   public BigDecimal getExponent()
   {
      return exponent;
   }
}
//zooming in: 1, 2, 5, 10, 20, 50, 100, 200, 500
//zooming in: 0.01, 0.02, 0.05, 0.1, 0.2, 0.5, 1
//zooming out: 1, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01
//zooming out: 500, 200, 100, 50, 20, 10, 5, 2, 1
//when zooming in, exponent goes up by 1 always and zoomDynamic gets multiplied by 2, 2.5, 2, 2, 2.5, 2, 2, 2.5
//when zooming out, exponent goes down by 1 always and zoomDynamic gets multiplied by 0.5, 0.4, 0.5, 0.5, 0.4, 0.5
//what actually happens
//when zooming out, exponent goes down by 1 always and zoomDynamic gets multiplied by 2, 0.8, 0.8
//when zooming in, exponent goes up by 1 always and zoomDynamic gets multiplied by 2.5, 2, 2, 2.5