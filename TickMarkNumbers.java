import java.text.DecimalFormat;
import java.awt.Graphics2D;
import java.math.BigDecimal;

public class TickMarkNumbers
{
   //constants to not be annoying
   private static final BigDecimal FIFTY = BigDecimal.valueOf(50);
   private static final BigDecimal TWO = BigDecimal.valueOf(2);
   private static final BigDecimal TEN = BigDecimal.valueOf(10);
   
   public void drawTickNumbers(Graphics2D graphics, BigDecimal xScreenSize, BigDecimal yScreenSize, BigDecimal zoomFactor, BigDecimal panX, BigDecimal panY, BigDecimal tickInterval, BigDecimal zoomDynamic)
   {
        //scientific and standard notation
        DecimalFormat fourDecimalPlaces = new DecimalFormat("#.####");
        DecimalFormat sciFormat = new DecimalFormat("#.####E0");
        
        //drawing tick numbers along x axis
        //xPos = (xScreenSize + panX) % tickInterval; stop when xPos gets to 2*xScreenSize; increase by tickInterval
        for (BigDecimal xPos = (xScreenSize.add(panX)).remainder(tickInterval); xPos.compareTo(TWO.multiply(xScreenSize)) < 0; xPos = xPos.add(tickInterval)) 
        {
            //if (xPos - panX - xScreenSize)/(50*zoomFactor) is 0 (xPos adjusting for panning minus xScreenSize)
            if(fourDecimalPlaces.format((xPos.subtract(panX.add(xScreenSize))).divide(FIFTY.multiply(zoomFactor))).equals("0")||fourDecimalPlaces.format((xPos.subtract(panX.add(xScreenSize))).divide(FIFTY.multiply(zoomFactor))).equals("-0"))
            {
               xPos = xPos.add(tickInterval);
            }
            String text = fourDecimalPlaces.format((xPos.subtract(panX.add(xScreenSize))).divide(FIFTY.multiply(zoomFactor)));
            if(text.length() <  9 && !text.equals("0") && !text.equals("-0"))
            {
               graphics.drawString(text, xPos.subtract(TEN).intValue(), yScreenSize.subtract(TEN).add(panY).intValue());
            }
            else
            {
               String temp = sciFormat.format((xPos.subtract(panX.add(xScreenSize))).divide(FIFTY.multiply(zoomFactor)));
               graphics.drawString(temp.replace("E", "*10^"), xPos.subtract(TEN).intValue(), yScreenSize.subtract(TEN).add(panY).intValue());
            }
        }
        
        //drawing tick numbers along y axis
        for (BigDecimal yPos = (yScreenSize.add(panY)).remainder(tickInterval); yPos.compareTo(yScreenSize.multiply(TWO)) < 0; yPos = yPos.add(tickInterval)) 
        {
            if(yScreenSize.subtract(BigDecimal.valueOf(1)).compareTo(yPos) < 0 && yScreenSize.add(BigDecimal.valueOf(1)).compareTo(yPos) > 0)
            {
               yPos = yPos.add(tickInterval);
            }
            String text = fourDecimalPlaces.format((panY.add(yScreenSize.subtract(yPos))).divide(FIFTY.multiply(zoomFactor)));
            if(text.length() <  9 && !text.equals("0") && !text.equals("-0"))
            {
               graphics.drawString(text, xScreenSize.add(TEN).add(panX).intValue(), yPos.add(BigDecimal.valueOf(5)).intValue());
            }
            else
            {
               String temp = sciFormat.format((panY.add(yScreenSize.subtract(yPos))).divide(FIFTY.multiply(zoomFactor)));
               if(!temp.equals("0E0"))
               {
                  graphics.drawString(temp.replace("E", "*10^"), xScreenSize.add(TEN).add(panX).intValue(), yPos.add(BigDecimal.valueOf(5)).intValue());
               }
            }      
        }   
   }
}