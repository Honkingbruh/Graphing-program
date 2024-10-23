import java.awt.geom.*;
import java.awt.*;
import java.math.*;

public class DrawTickMarks
{
   BigDecimal tickInterval;//space between ticks
   BigDecimal baseTickInterval = BigDecimal.valueOf(50);
   public void DrawTicks(Graphics2D graphics, BigDecimal xScreenSize, BigDecimal yScreenSize, BigDecimal zoomFactor, BigDecimal panX, BigDecimal panY, BigDecimal zoomDynamic)
   {
        tickInterval = zoomFactor.multiply(baseTickInterval).divide(zoomDynamic);//zoom*(50/zoomDynamic)
        for (BigDecimal xPos = (xScreenSize.add(panX)).remainder(tickInterval); xPos.compareTo(xScreenSize.multiply(BigDecimal.valueOf(2))) < 0; xPos = xPos.add(tickInterval)) 
        {//x position at where to put the tick marks
            graphics.draw(new Line2D.Double(xPos.doubleValue(), yScreenSize.add(panY).subtract(BigDecimal.valueOf(5)).doubleValue(), xPos.doubleValue(), yScreenSize.add(panY.add(BigDecimal.valueOf(5))).doubleValue()));
        }
        for (BigDecimal yPos = (yScreenSize.add(panY)).remainder(tickInterval); yPos.compareTo(yScreenSize.multiply(BigDecimal.valueOf(2))) < 0; yPos = yPos.add(tickInterval)) 
        {//y position at where to put the tick marks
            graphics.draw(new Line2D.Double(xScreenSize.add(panX).subtract(BigDecimal.valueOf(5)).doubleValue(), yPos.doubleValue(), xScreenSize.add(panX).add(BigDecimal.valueOf(5)).doubleValue(), yPos.doubleValue()));
        }
   }
   public BigDecimal getTickInterval()
   {
      return tickInterval;
   }
}