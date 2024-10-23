import java.awt.*;   
import javax.swing.*;
import java.math.*;
import java.text.*;
public class UpdateStatsPage
{
   static DecimalFormat sciFormat = new DecimalFormat("#.####E0");
   public static void UpdateStats(JTextArea statsPage, BigDecimal xScreenSize, BigDecimal yScreenSize, BigDecimal zoomFactor, BigDecimal panX, BigDecimal panY, Point lastPoint, BigDecimal zoomDynamic, BigDecimal tickInterval, Point mouseCoords, BigDecimal exponent, BigDecimal multi, long runTime)
   {
      statsPage.setText("panX: " + panX + "\npanY: " + panY + "\nxScreenSize: " + xScreenSize + "\nyScreenSize: " + yScreenSize + "\nzoomFactor: " + formatThing(zoomFactor) + "\nlastPoint: " + lastPoint + "\nzoomDynamic: " + formatThing(zoomDynamic) + "\ntickInterval: " + formatThing(tickInterval) + "\nmouseCoords: " + mouseCoords + "\nexponent: " + exponent + "\nmulti: " + multi + "\nrunTime: " + runTime + " ms");
   }
   public static String formatThing(BigDecimal thing)
   {
      if(thing == null)
      {
         return null;
      }
      if(thing.abs().compareTo(BigDecimal.valueOf(1000000)) > 0)//abs value is more than a million
      {
         thing = thing.setScale(5, RoundingMode.HALF_UP);//takes only first 5 nonzero decimal digits and rounds up
         String tempThing = sciFormat.format(thing).replaceAll("0*$", "");//formats in sci notation
         return tempThing.replace("E", "*10^");
      }
      else if(thing.scale() == 0)//less than a million and no decimal
      {
         return String.valueOf(thing).replaceAll("0*$", "");
      }
      else//less than million decimal
      {
         if(thing.abs().compareTo(BigDecimal.valueOf(0.001)) < 0)//less than 0.001
         {
            thing = thing.setScale(5, RoundingMode.HALF_UP);
            String tempThing = sciFormat.format(thing).replaceAll("0*$", "");
            return tempThing.replace("E", "*10^");
         }
         return String.valueOf(thing.setScale(5, RoundingMode.HALF_UP)).replaceAll("0*$", "");
      }
   }
   //priority
   //if greater than 1 million (abs value) DONE
   //take first 5 nonzero digits
   //scientific format them so #.####*10^ and cut off trailing zeroes
   //if less than 1 million (abs value) NOT DONE
   //if no decimal places DONE
   //set regular
   //if decimal places NOT DONE
   //take first 5 digits and scientific format them
   //if big number like 91028309218309 then sci format like 9.1028*10^13
   //if big number w decimal like 109238213.9128312398 then sci format like 1.0924*10^8
   //if small number like 0.00002389123839 then format like 2.3891*10^-5
   //if number like 0.000392832139 then format like 0.000392832
   //if small number like 1.00009237 then format like 1
   //if number like 129384 don't scientific notation it until it's greater than 1 million
}