import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.math.BigDecimal;

public class GraphAxes {
    public void DrawAxes(Graphics2D graphics, BigDecimal xScreenSize, BigDecimal yScreenSize, BigDecimal panX, BigDecimal panY) {
        // Draw axes
        graphics.draw(new Line2D.Double(0.0, yScreenSize.add(panY).doubleValue(), xScreenSize.multiply(BigDecimal.valueOf(2)).doubleValue(), yScreenSize.add(panY).doubleValue()));//x
        graphics.draw(new Line2D.Double(xScreenSize.add(panX).doubleValue(), 0, xScreenSize.add(panX).doubleValue(), yScreenSize.multiply(BigDecimal.valueOf(2)).doubleValue()));//y
        //origin
        graphics.drawString("0", xScreenSize.add(panX.subtract(BigDecimal.valueOf(7))).floatValue(), yScreenSize.add(panY.add(BigDecimal.valueOf(13))).floatValue()); 
    }
}