import java.math.BigDecimal;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.math.RoundingMode;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.awt.Shape;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class Equation
{//used bigdecimal because arbitrary precision
   //private HashMap<String, BigDecimal> variables;
   final int maxPoints = 1000;//how many points to evaluate function at (you draw lines between the points)
   final int complexRes = 100;//uses this for the same thing but evaluates it in complexRes^2 rectangles
   //so like if this was 10 then it would evaluate 100 points at like -4 -4i, -4 -3i, -4 -2i and fill rectangles from like -4 - 4i to -3 -3i or something
   private String userEquation;//equation that the user inputs
   //curve
   private final ArrayList<BigDecimal> yCoordArray = new ArrayList<>();//y coordinate array (should definitely make this into a hashmap)
   private final ArrayList<String> degrees = new ArrayList<>();//what power the variable is raised to except idk how it would work for multiple variables
   //line and curve
   private final ArrayList<String> coefficients = new ArrayList<>();//coefficients of each term
   private final ArrayList<String> terms = new ArrayList<>();//said terms
   private final ArrayList<String> sides = new ArrayList<>();//sides of the equation (as in y = and 4)
   //Point
   private BigDecimal userPointX;//if user wants to draw a point by doing (x, y) then this would store x position
   private BigDecimal userPointY;//would store y position
   private final BigDecimal pointSize = BigDecimal.valueOf(10);//how large the point should be
   //importing stuff
   private BigDecimal xScreenSize;//gets the screen size width from initialization of equation, i think it's in pixels but idk
   private BigDecimal yScreenSize;//screen size height
   private BigDecimal panX;//how much the user has panned (dragged across the screen to transform the origin to a different coordinate) in the x direction
   private BigDecimal panY;//y direction same thing
   private BigDecimal tickInterval;//how far apart the ticks are
   private BigDecimal zoomDynamic;//something with how much the user zoomed idk i don't remember
   public static final BigDecimal e = BigDecimal.valueOf(2.71828182845904523536028747135266249775724709369995957496696762772407663035354759457);
   public static final BigDecimal pi = BigDecimal.valueOf(3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986);
   private static final double DEGREES_TO_RADIANS = 0.017453292519943295;
   private static final double RADIANS_TO_DEGREES = 57.29577951308232;
   //arc functions don't work for now
   private static final String[] trigFunctions = {"arccos(", "arcsin(", "arctan(", "arcsec(", "arccsc(", "arccot(","cos(", "sin(", "tan(", "sec(", "csc(", "cot("};
   public Equation(String initEquation)
   {
      //variables = variablesInit;
      userEquation = initEquation.replaceAll(" ", "");//REMOVES SPACES
      if(userEquation.isEmpty())//THROWS OUT EMPTY EQUATIONS
      {
         System.out.println("Empty equation!");
      }
      else if(userEquation.contains(","))//IS A POINT
      {
         String[] temp = userEquation.split(",");
         userPointX = BigDecimal.valueOf(Double.parseDouble(temp[0].replaceAll("[^\\d.]", "")));//gets rid of any non number/decimal values
         userPointY = BigDecimal.valueOf(Double.parseDouble(temp[1].replaceAll("[^\\d.]", "")));
      }
      else if(userEquation.contains("f(z)"))//COMPLEX EQUATION
      {
         sides.add(userEquation.split("=")[1]);//only adds the thing after = because thing before is just f(z)
         splitAndProcessTerms(sides.getFirst(), "z");
         System.out.println("terms: " + terms);
      }
      else if(userEquation.contains("=") && (userEquation.contains("y") || userEquation.split("=")[0].matches(".*[a-zA-Z]'*\\(x\\).*")))//y = or a-zA-Z(x) as in f(x)
      {
         sides.add(userEquation.split("=")[0]);
         sides.add(userEquation.split("=")[1]);
         System.out.println("1: " + sides.get(0) + " 2: " + sides.get(1));
         splitAndProcessTerms(sides.get(1), "x");
         System.out.println("terms: " + terms);
      }
      else//only equation in form of x^2 instead of y = x^2
      {
         sides.add(userEquation);
         System.out.println("sides0: " + sides.getFirst());
         splitAndProcessTerms(sides.getFirst(), "x");
         System.out.println("terms2: " + terms);
      }
   }
   public void drawUserEquation(Graphics2D graphics, BigDecimal initXScreenSize, BigDecimal initYScreenSize, BigDecimal initPanX, BigDecimal initPanY, BigDecimal initTickInterval, BigDecimal initZoomDynamic)
   {
      userEquation = replaceSpecialConstants(userEquation);//gets rid of like e and pi and replaces them with their actual values (and variables soon i hope)
      xScreenSize = initXScreenSize;
      yScreenSize = initYScreenSize;
      panX = initPanX;
      panY = initPanY;
      tickInterval = initTickInterval;
      zoomDynamic = initZoomDynamic;
      if(userEquation.contains(","))
      {
         drawUserPoint(graphics);
      }
      else if (userEquation.contains("z"))
      {
         drawUserComplex(graphics);
      }
      else
      {
         drawUserCurve(graphics);
      }
   }
   public void drawUserPoint(Graphics2D graphics)
   {
      BigDecimal newX = coordsToScreenX(userPointX);//to draw you need to convert the coordinate to the actual screen pixel value thing
      BigDecimal newY = coordsToScreenY(userPointY);
      Shape Circle = new Ellipse2D.Double(newX.doubleValue(), newY.doubleValue(), pointSize.doubleValue(), pointSize.doubleValue());
      graphics.fill(Circle);
   }
   public void drawUserCurve(Graphics2D graphics)
   {
      //iterates through screen
      //starts at 0 because i is in the pixel format so 0 in the pixel format is whatever is at the left of the screen and the max is at the right
      //also xScreenSize is the distance from the middle fo the screen to one of the edges so the full screen is 2 times that for some reason
      for(BigDecimal i = BigDecimal.valueOf(0); i.compareTo(BigDecimal.valueOf(2).multiply(xScreenSize)) < 0; i = i.add(BigDecimal.valueOf(2).multiply(xScreenSize).divide(BigDecimal.valueOf(maxPoints), 256, RoundingMode.HALF_UP)))
      {
         //total for specific x value
         BigDecimal total = BigDecimal.valueOf(0);
         //evaluates each term
         total = computeExpressionAtPoint(screenXToCoords(i), total);
         if(total != null)
         {
            yCoordArray.add(coordsToScreenY(total));
            if (yCoordArray.size() >= 2)
            {
               BigDecimal x1 = i.subtract(BigDecimal.valueOf(2).multiply(xScreenSize).divide(BigDecimal.valueOf(maxPoints), 256, RoundingMode.HALF_UP));//i-(2*xScreenSize/maxPoints)
               BigDecimal y1 = yCoordArray.get(yCoordArray.size() - 2);
               BigDecimal y2 = yCoordArray.getLast();
               graphics.draw(new Line2D.Double(x1.doubleValue(), y1.doubleValue(), i.doubleValue(), y2.doubleValue()));//draws lines between the points
            }
         }
         else
         {
            //System.out.println(screenXToCoords(i) + " returned null");
         }
      }
   }//for an actual good example to check if this is working go to samuelj.li/complex-function-plotter/#z and type in the equation
   public void drawUserComplex(Graphics2D graphics)
   {
      for(BigDecimal i = BigDecimal.valueOf(0); i.compareTo(BigDecimal.valueOf(2).multiply(xScreenSize)) < 0; i = i.add(BigDecimal.valueOf(2).multiply(xScreenSize).divide(BigDecimal.valueOf(complexRes), 256, RoundingMode.HALF_UP)))
      {//iterates through Re(z) complexRes times
         for(BigDecimal j = BigDecimal.valueOf(0); j.compareTo(BigDecimal.valueOf(2).multiply(yScreenSize)) < 0; j = j.add(BigDecimal.valueOf(2).multiply(yScreenSize).divide(BigDecimal.valueOf(complexRes), 256, RoundingMode.HALF_UP)))
         {//iterates through Im(z) complexRes times
            double tempi = Double.parseDouble(String.valueOf(screenXToCoords(i)));//temporary real coordinate
            double tempj = Double.parseDouble(String.valueOf(screenYToCoords(j)));//temporary complex coordinate
            BigDecimal totalRe = BigDecimal.valueOf(0);
            BigDecimal totalIm = BigDecimal.valueOf(0);
            for(int k = 0; k < terms.size(); k++)//iterates through terms
            {
               if(terms.get(k).contains("z"))
               {
                  if(terms.get(k).contains("i"))//iz or z^i or i^z whatever
                  {
                     if(getCoefficient(terms.get(k), "z").contains("i"))
                     {
                        totalRe = totalRe.add(BigDecimal.valueOf(-1*computeDivisor(getCoefficient(coefficients.get(k), "i")).doubleValue()).multiply(BigDecimal.valueOf(tempj)));
                        totalIm = totalIm.add(BigDecimal.valueOf(computeDivisor(getCoefficient(coefficients.get(k), "i")).doubleValue()).multiply(BigDecimal.valueOf(tempi)));
                     }
                  }
                  else
                  {
                     if(!degrees.get(k).equals("1"))
                     {//right now you have some term maybe 2z^2 or 2(a+bi)^2 and removing coefficient you have r^2(cos(theta)+isin(theta)) because of euler's identity thing
                        double deg = computeDivisor(degrees.get(k)).doubleValue();
                        double arg = Math.atan2(tempj, tempi);//argument of input (weird math way of saying the angle relative to the horizontal going counterclockwise)
                        if(arg < 0)
                        {
                           arg = arg + 2*Math.PI;
                        }
                        BigDecimal r;//modulus (also weird way of saying how big the output of the function is at that point
                        //so likw if it returned 1 + 0i the modulus would be 1 and if it was like 0 + 2i it would be 2 and if it was 3 + 5i it would be sqrt(3^2 + 5^2)
                        if(tempi == 0.0 && Math.cos(arg) < 0.0000000000001 && Math.cos(arg) > -0.0000000000001)//it sucks but it works
                        {
                           r = BigDecimal.valueOf(tempj);
                        }
                        else
                        {
                           r = BigDecimal.valueOf(tempi).divide(BigDecimal.valueOf(Math.cos(arg)), 256, RoundingMode.HALF_UP);
                        }
                        r = r.abs();
                        BigDecimal newR = BigDecimal.valueOf(Math.pow(r.doubleValue(), computeDivisor(degrees.get(k)).doubleValue()));
                        totalRe = totalRe.add(newR.multiply(computeDivisor(coefficients.get(k))).multiply(BigDecimal.valueOf(Math.cos(deg * arg))));
                        totalIm = totalIm.add(newR.multiply(computeDivisor(coefficients.get(k))).multiply(BigDecimal.valueOf(Math.sin(deg * arg))));
                     }
                     else
                     {//evaluates the real and imaginary parts separately
                        BigDecimal tempRe = replaceVarCoef(coefficients.get(k), BigDecimal.valueOf(tempi), "z");
                        BigDecimal tempIm = replaceVarCoef(coefficients.get(k), BigDecimal.valueOf(tempj), "z");
                        if(tempRe != null && tempIm != null)
                        {
                           totalRe = totalRe.add(computeDivisor(tempRe.toString()).multiply(BigDecimal.valueOf(tempi)));
                           totalIm = totalIm.add(computeDivisor(tempIm.toString()).multiply(BigDecimal.valueOf(tempj)));
                        }
                        else
                        {
                           String tempFunction = getCoefficient(getNumerator(coefficients.get(k)), "z") + "/" + getCoefficient(getDenominator(coefficients.get(k)), "z");
                           BigDecimal tempResult = computeDivisor(tempFunction);
                           totalRe = totalRe.add(tempResult);
                           totalIm = totalIm.add(tempResult);
                        }
                     }
                  }
               }
               else if(terms.get(k).contains("i"))//term is something like i or 2i
               {
                  totalIm = totalIm.add(BigDecimal.valueOf(Double.parseDouble(getCoefficient(coefficients.get(k), "i"))));
               }
               else//real valued number
               {
                  totalRe = totalRe.add(BigDecimal.valueOf(Double.parseDouble(coefficients.get(k))));
               }
            }
            float argument;//i think these are floats because the hsltorgb converter only takes floats
            float modulus;
            if(totalRe != null && totalIm != null)
            {//atan2 just gets the angle between the two things
               argument = (float) Math.atan2(Double.parseDouble(String.valueOf(totalIm)), Double.parseDouble(String.valueOf(totalRe)));
               modulus = (float)Math.sqrt(Math.pow(Double.parseDouble(String.valueOf(totalRe)), 2) + Math.pow(Double.parseDouble(String.valueOf(totalIm)), 2));
               modulus = (float)Math.pow((1f-Math.exp(-0.02f*modulus)), 0.3);//thing that spaces out the colors nicely (arbitrary values, just makes it pretty)
               System.out.println("argument: " + argument + " modulus: " + modulus);
            }
            else
            {
               System.out.println("aodijs: ");
               argument = 360f;
               modulus = 0f;
            }
            argument = (argument*180)/(float)Math.PI;
            if(argument < 0f)
            {
               argument = argument + 360f;
            }
            graphics.setColor(hslToRGB(argument, modulus, modulus));//gets the color of the rectangle
            graphics.fillRect((int)Math.ceil(Double.parseDouble(String.valueOf(i))), (int)Math.ceil(Double.parseDouble(String.valueOf(j))), (int)Math.ceil(2*Double.parseDouble(String.valueOf(xScreenSize))/complexRes), (int)Math.ceil(2*Double.parseDouble(String.valueOf(yScreenSize))/complexRes));
         }
      }
   }
   public void splitAndProcessTerms(String polynomial, String variable)
   {
      String[] termArrayTemp = polynomial.split("(?<![\\^(])(?=[+-])");//splits when + or - not directly after ^ or not directly after ( (took intense googling)
      ArrayList<String> termArray = new ArrayList<>(Arrays.asList(termArrayTemp));
      for(int startPar = 0; startPar < termArray.size() - 1; startPar++)//awful thing that joins things back together
      {//honestly don't know what this does but seems importatnt
         if(termArray.get(startPar).contains("(") && !termArray.get(startPar).contains(")"))
         {
            int endPar = startPar;
            while(!termArray.get(endPar).contains(")"))
            {
               endPar++;
            }
            String combinedParentheses = "";
            for(int currIndex = startPar; currIndex != endPar + 1; currIndex++)
            {
               combinedParentheses += termArray.get(currIndex);
            }
            int originalLength = termArray.size();
            termArray.set(startPar, combinedParentheses);
            while(termArray.size() != originalLength - (endPar - startPar))
            {
               termArray.remove(startPar + 1);
            }
         }
      }
      for(String i: termArray)
      {
         System.out.print(i + " ");
      }
      System.out.println();
      //String[] parenthesesArray = polynomial.split("\\(([^()*])\\)");
      for(String i: termArray)
      {
         i = replaceSpecialConstants(i);
         if(isTrigFunction(i))
         {
            i = evaluateTrigFunction(i);
         }
         if(i.contains("^") && !i.contains(variable))
         {//raises constant to constant
            i = String.valueOf(Math.pow(Double.parseDouble(i.split("\\^")[0]), Double.parseDouble(i.split("\\^")[1])));
         }
         if(i.contains("*"))
         {
            i = String.valueOf(BigDecimal.valueOf(Double.parseDouble(i.split("\\*")[0])).multiply(BigDecimal.valueOf(Double.parseDouble(i.split("\\*")[1]))));
         }
         if(!i.isEmpty())
         {
            terms.add(i);
            if(i.contains("y"))
            {
               coefficients.add(getCoefficient(i, "y"));
               degrees.add(String.valueOf(getDegree(i, "y")));
            }
            else
            {
               coefficients.add(getCoefficient(i, variable));
               degrees.add(getDegree(i, variable));
            }
         }
      }
   }
   public String getCoefficient(String term, String variable)
   {//gets the coefficient of term (like getCoefficient("2x", "x") would return 2)
      term = term.replaceAll("\\+","");
      //term = term.replaceAll("\\^", "");
      String divisor = "1";//default
      if(term.contains("/"))
      {
         divisor = term.substring(term.indexOf("/") + 1);
      }
      if(term.isEmpty())
      {
         return "1";
      }
      if(term.equals("-"))
      {
         return "-1";
      }
      if(isTrigFunction(term))
      {
         for(String i: trigFunctions)
         {
            if(term.contains(i))
            {
               return getCoefficient(term.substring(0, term.indexOf(i)), variable) + "/" + divisor;
            }
         }
         return null;
      }
      if(!term.contains(variable))//constant
      {
         if(!term.contains("^"))
         {
            return term;
         }
         if(term.contains("[-+*]"))
         {
            return getCoefficient(term.split("[-+*]")[0], variable) + "/" + divisor;//splits at +, -, *, or /
         }
         if(!divisor.equals("1"))
         {
            return "1" + "/" + divisor;
         }
         return "1";
      }
      if(term.contains("-" + variable))//-x
      {
         if(divisor.equals("1"))
         {
            return "-1";
         }
         return "-1" + "/" + divisor;
      }
      if(term.contains("\\(.*\\)") && term.indexOf("(") != term.indexOf("^") + 1)
      {
         String temp = term.substring(term.indexOf("(")+1, term.indexOf(variable));//works on something like cos(24x) and gets 24
         return getCoefficient(temp, variable) + "/" + divisor;
      }
      if(term.charAt(0) == '+' || term.charAt(0) == variable.charAt(0))
      {
         return "1" + "/" + divisor;
      }
      return term.substring(0,term.indexOf(variable)) + "/" + divisor;
   }
   public BigDecimal computeDivisor(String function)
   {
      if(function == null)
      {
         return null;
      }
      if(function.contains("/"))
      {
         String numerator = function.substring(0, function.indexOf("/"));
         String denominator = function.substring(function.indexOf("/") + 1);
         if(denominator.matches("0E-?\\d*"))
         {
            return null;
         }
         try
         {
            return BigDecimal.valueOf(Double.parseDouble(numerator)).divide(computeDivisor(denominator), 256, RoundingMode.HALF_UP);
         }
         catch(ArithmeticException a)
         {
            System.out.println("caught " + a);
            if(numerator.equals(denominator))
            {
               return BigDecimal.ONE;
            }
            return null;
         }
      }
      return BigDecimal.valueOf(Double.parseDouble(function));
   }
   public String getDegree(String term, String variable)
   {
      if(!term.contains("^") && term.contains(variable))
      {
         return "1";
      }
      if(!term.contains("^") && !term.contains(variable))
      {
         return "0";
      }
      int carrot = term.indexOf("^");
      int startPar = term.indexOf("(", carrot);
      int endPar = term.indexOf(")", carrot);
      if(startPar != -1 && endPar != -1)
      {
         return getInsideParenth(term);
      }
      return term.substring(term.indexOf("^")+1);
   }
   public boolean isTrigFunction(String function)
   {
      return(function.matches(".*(cos\\(|sin\\(|tan\\(|sec\\(|csc\\(|cot\\(|arccos\\(|arcsin\\(|arctan\\(|arcsec\\(|arccsc\\(|arccot\\().*"));
   }
   public String evaluateTrigFunction(String function)
   {
      function = function.replace(")", "");//gets rid of closing parenthesis
      if(function.contains("x"))
      {
         return function;
      }
      function = function.replaceAll("\\)","");//arc functions don't work
      if(function.contains("arccos("))
      {
         return String.valueOf(Math.acos(Double.parseDouble(function.substring(function.indexOf("arccos(")+7))));
      }
      if(function.contains("arcsin("))
      {
         return String.valueOf(Math.asin(Double.parseDouble(function.substring(function.indexOf("arcsin(")+7))));
      }
      if(function.contains("arctan("))
      {
         return String.valueOf(Math.atan(Double.parseDouble(function.substring(function.indexOf("arctan(")+7))));
      }
      if(function.contains("arcsec("))
      {
         return String.valueOf(Math.acos(0.5*Double.parseDouble(function.substring(function.indexOf("arcsec(")+7))));
      }
      if(function.contains("arccsc("))
      {
         return String.valueOf(Math.asin(0.5*Double.parseDouble(function.substring(function.indexOf("arccsc(")+7))));
      }
      if(function.contains("arccot("))
      {
         return String.valueOf(Math.atan(0.5 * Double.parseDouble(function.substring(function.indexOf("arccot(") + 7))));
      }
      if(function.contains("cos("))
      {
         return String.valueOf(Math.cos(Double.parseDouble(function.substring(function.indexOf("cos(")+4))));
      }
      if(function.contains("sin("))
      {
         return String.valueOf(Math.sin(Double.parseDouble(function.substring(function.indexOf("sin(")+4))));
      }
      if(function.contains("tan("))
      {
         return String.valueOf(Math.tan(Double.parseDouble(function.substring(function.indexOf("tan(")+4))));
      }
      if(function.contains("sec("))
      {
         return String.valueOf(1/Math.cos(Double.parseDouble(function.substring(function.indexOf("sec(")+4))));
      }
      if(function.contains("csc("))
      {
         return String.valueOf(1/Math.sin(Double.parseDouble(function.substring(function.indexOf("csc(")+4))));
      }//cot(x)
      return String.valueOf(1/Math.tan(Double.parseDouble(function.substring(function.indexOf("cot(")+4))));
   }
   public String replaceSpecialConstants(String function)
   {
      /*Iterator<String> varNames = variables.keySet().iterator();
      Iterator<BigDecimal> varValues = variables.values().iterator();
      while(varNames.hasNext())
      {
         String tempName = varNames.next();
         BigDecimal tempVal = varValues.next();
         if(function.contains(tempName)) {
            if(function.indexOf(tempName) != 0 && function.length() != 1) {
               function = tempVal.multiply(BigDecimal.valueOf(Double.parseDouble(getCoefficient(function, tempName)))).toString();
            }
         }
      }*/
      if(function.indexOf("e") != -1 && (function.indexOf("e") == 0 || (function.length()-function.indexOf("e")>=2 && !function.substring(function.indexOf("e")-1, function.indexOf("e")+2).equals("sec"))))
      {
         function = function.replaceAll("e", String.valueOf(e));//replaces e and pi with their values
      }
      function = function.replaceAll("pi", String.valueOf(pi));

      return function;
   }
   public BigDecimal replaceVarCoef(String function, BigDecimal xVal, String variable)
   {//so that if it's 2x and plugging in 5 for x it would return 10 instead of 25
      BigDecimal replacingVal;
      //tempCoef = String.valueOf(computeDivisor(replaceVarCoef(tempCoef, xVal)));
      String numerator = function.replaceAll("/.*", "/");
      String denominator = "1";
      if(function.contains("/"))
      {
         numerator = function.substring(0, function.indexOf("/"));
         denominator = function.substring(function.indexOf("/") + 1);
      }
      if(isTrigFunction(function))
      {
         String start = function.substring(0, function.indexOf("(") + 1);
         String insidePar;
         if(function.indexOf(")") != -1)
         {
            insidePar = function.substring(function.indexOf("(") + 1, function.indexOf(")"));
         }
         else
         {
            insidePar = function.substring(function.indexOf("(") + 1);
         }
         try
         {
            return BigDecimal.valueOf(Double.parseDouble(evaluateTrigFunction(start + replaceVarCoef(insidePar, xVal, variable))));
         }
         catch(NumberFormatException e)
         {
            return null;
         }
      }
      BigDecimal numResult = computeDivisor(getCoefficient(numerator, variable));
      BigDecimal denomResult = computeDivisor(getCoefficient(denominator, variable));
      if(numerator.contains(variable))
      {
         numResult = numResult.multiply(xVal);
      }
      if(denominator.contains(variable))
      {
         denomResult = denomResult.multiply(xVal);
      }
      return computeDivisor(numResult + "/" + denomResult);
   }
   public String getInsideParenth(String function)
   {
      return function.substring(function.indexOf("(") + 1, function.indexOf(")"));
   }
   public boolean isInsideParenth(String function, String subFunction)
   {
      return function.substring(function.indexOf("(") + 1, function.indexOf(")")).contains(subFunction);
   }
   public String getNumerator(String function)
   {
      return function.substring(0, function.indexOf("/"));
   }
   public String getDenominator(String function)
   {
      return function.substring(function.indexOf("/") + 1);
   }
   public BigDecimal computeExpression(String function)
   {
      //System.out.println("func: " + function);
      String[] processedDegree = function.split("((?=[\\+-]))");
      BigDecimal totalAdd = BigDecimal.ZERO;
      for(String m: processedDegree)
      {
         if(!m.equals("0E")) {
            totalAdd = totalAdd.add(BigDecimal.valueOf(Double.parseDouble(m)));
         }
      }
      return totalAdd;
   }
   //converts the graphical x coordinates to pixel x coordinates
   public BigDecimal coordsToScreenX(BigDecimal initX)
   {//y = x*d*b + c + a (inverse): y = (x - c - a)/(d*b)
      //adjusting based on zoom
      initX = initX.multiply(zoomDynamic);//f(initX) = initX*zoomDynamic // y = x*d
      //adjusting based on pan
      initX = initX.add(panX.divide(tickInterval, 256, RoundingMode.HALF_UP));//f(initX) = initX + (panX/tickInterval) // y = x + (c/b)
      //adjusting based on screen size
      initX = initX.multiply(tickInterval).add(xScreenSize);//f(initX) = initX*tickInterval + xScreenSize // y = x*b + a
      return initX;
   }
   //converts the graphical y coordinates to pixel y coordinates
   public BigDecimal coordsToScreenY(BigDecimal initY)
   {//y = a - x*d*b + c inverse: y = (a - x + c)/(d*b)
      //adjusting based on zoom
      initY = initY.multiply(zoomDynamic);//f(initY) = initY*zoomDynamic // y = x*d
      //adjusting based on pan
      initY = initY.subtract(panY.divide(tickInterval, 256, RoundingMode.HALF_UP));//f(initY) = initY - (panY/tickInterval) // y = x - (c/b)
      //adjusting based on screen size
      initY = yScreenSize.subtract(initY.multiply(tickInterval));//f(initY) = yScreenSize - (initY*tickInterval) //y = a - x*b
      return initY;
   }//a = ScreenSize, b = tickInterval, c = pan, d = zoomDynamic*/
   public BigDecimal screenXToCoords(BigDecimal initX)//converts pixel x coordinates to graphical x coordinates
   {//y = (x - a - c)/(d*b)
      //adjusting based on screen size
      initX = (initX.subtract(xScreenSize)).divide(tickInterval, 256, RoundingMode.HALF_UP);//f(initX) = (initX - xScreenSize)/tickInterval // y = (x-a)/b
      //adjusting based on pan
      initX = initX.subtract(panX.divide(tickInterval, 256, RoundingMode.HALF_UP));//f(initX) = initX - (panX/tickInterval) // y = x - (c/b)
      //adjusting based on zoom
      initX = initX.divide(zoomDynamic, 256, RoundingMode.HALF_UP);//f(initX) = initX/zoomDynamic // y = x/d
      return initX;
   }
   public BigDecimal screenYToCoords(BigDecimal initY)//converts pixel y coordinates to graphical y coordinates
   {//y = (a - x + c)/(d*b)
      //adjusting based on screen size
      initY = (yScreenSize.subtract(initY).divide(tickInterval, 256, RoundingMode.HALF_UP));// f(initY) = (yScreenSize-initY)/b //y = (a-x)/b
      //adjusting based on pan
      initY = initY.add((panY.divide(tickInterval, 256, RoundingMode.HALF_UP)));//f(initY) = initY + (panY/tickInterval) // y = x + (c/b)
      //adjusting based on zoom
      initY = initY.divide(zoomDynamic, 256, RoundingMode.HALF_UP);//f(initY) = initY/zoomDynamic //y = x/d
      return initY;
   }//most complex graphing platforms use hsl (hue, saturation, lightness) instead of rgb (red, green, blue) but like everything uses rgb so had to dig through wikipedia for the formula
   public static Color hslToRGB(float h, float s, float l)
    {//h is [0,360) (deg), s is [0, 1] l is [0, 1] 
      float C = (1-Math.abs(2*l - 1f))*s;
      float H_ = h/60;
      float X = C*(1-Math.abs(H_%2f - 1));
      float m = l - (C/2f);
      float R1;
      float G1;
      float B1;
      
      if(0f <= H_ && H_ < 1f){R1 = C; G1 = X; B1 = 0f;}
      else if(1f <= H_ && H_ < 2f){R1 = X; G1 = C; B1 = 0f;}
      else if(2f <= H_ && H_ < 3f){R1 = 0f; G1 = C; B1 = X;}
      else if(3f <= H_ && H_ < 4f){R1 = 0f; G1 = X; B1 = C;}
      else if(4f <= H_ && H_ < 5f){R1 = X; G1 = 0f; B1 = C;}
      else{R1 = C; G1 = 0f; B1 = X;}//(5f <= H_ && H_ < 6f)
      return new Color(R1 + m, G1 + m, B1 + m);
   }
   public BigDecimal getDerivativeAtPoint(String function, BigDecimal xVal, String variable, int numDerivatives)
   {//gets the value of the nth derivative of a function at a point
      BigDecimal h = BigDecimal.valueOf(0.00001);
      if(numDerivatives == 1)
      {
         BigDecimal pointA = computeTermAtPoint(function, xVal, BigDecimal.valueOf(0), getCoefficient(function, "x"), getDegree(function, "x"));
         BigDecimal pointB = computeTermAtPoint(function, xVal.add(h), BigDecimal.valueOf(0), getCoefficient(function, "x"), getDegree(function, "x"));
         //System.out.println("derivative shit: function: " + function + " pointA: " + pointA.stripTrailingZeros() + " pointB: " + pointB.stripTrailingZeros() + " xVal: " + xVal.stripTrailingZeros() + " result: " + (pointB.subtract(pointA)).divide(h, 256, RoundingMode.HALF_UP).stripTrailingZeros());
         if (pointA != null && pointB != null) {
            return (pointB.subtract(pointA)).divide(h, 256, RoundingMode.DOWN);
         }
         return null;
      }
      else
      {
         return (getDerivativeAtPoint(function, xVal.add(h), variable, numDerivatives-1).subtract(getDerivativeAtPoint(function, xVal, variable, numDerivatives-1))).divide(h, 256, RoundingMode.DOWN);
      }
   }
   public BigDecimal computeExpressionAtPoint(BigDecimal xVal, BigDecimal total)
   {//idk if this is even used
      if(!userEquation.contains("'"))
      {
         for (int k = 0; k < terms.size(); k++)
         {
            System.out.println("adk: " + xVal);
            BigDecimal totalTemp = computeTermAtPoint(terms.get(k), xVal, total, coefficients.get(k), degrees.get(k));
            if(totalTemp != null)
            {
               total = total.add(totalTemp);
            }
            else
            {
               total = null;
            }
         }
         return total;
      }
      int derNum = 0;
      for(int i = userEquation.indexOf("'"); userEquation.substring(i, i+1).equals("'"); i++)
      {
         derNum++;
      }
      for (int k = 0; k < terms.size(); k++)
      {
         BigDecimal totalTemp = getDerivativeAtPoint(terms.get(k), xVal, "x", derNum);
         if(totalTemp != null)
         {
            total = total.add(getDerivativeAtPoint(terms.get(k), xVal, "x", derNum));
         }
         else
         {
            total = null;
         }
      }
      return total;
   }
   public BigDecimal computeTermAtPoint(String term, BigDecimal xVal, BigDecimal total, String coefficient, String degree)
   {
      //applies exponent and converts to coords
      double exponentResult;
      if (isTrigFunction(term))
      {
         BigDecimal multiplyCoef;
         if (coefficient.contains("/"))
         {
            String coefThing = getCoefficient(term.substring(term.indexOf("(") + 1, term.indexOf("x") + 1), "x");
            multiplyCoef = computeDivisor(coefThing).multiply(xVal);
            //multiplyCoef = BigDecimal.valueOf(Double.parseDouble(coefThing.substring(0,coefThing.indexOf("/")))/Double.parseDouble(coefThing.substring(coefThing.indexOf("/") + 1))).multiply(screenXToCoords(i));
         }
         else
         {
            multiplyCoef = BigDecimal.valueOf(Double.parseDouble(getCoefficient(term.substring(term.indexOf("(") + 1, term.indexOf("x") + 1), "x"))).multiply(xVal);
         }
         exponentResult = Double.parseDouble(evaluateTrigFunction(term.substring(0, term.indexOf("(") + 1) + multiplyCoef + ")"));
      }
      else
      {
         if (!degree.contains("x"))
         {
            BigDecimal totalAdd = computeExpression(degree);
            exponentResult = Math.pow(xVal.doubleValue(), totalAdd.doubleValue());
         }
         else
         {
            String corrected = replaceVarCoef(degree, xVal, "x").toString();
            //exponentResult = Math.pow(Double.parseDouble(terms.get(k).split("\\^")[0])/Double.parseDouble(String.valueOf(coefficients.get(k))), Double.parseDouble(corrected));
            exponentResult = Math.pow(xVal.doubleValue(), computeExpression(corrected).doubleValue());
         }
      }
      if (Double.isNaN(exponentResult) || Double.isInfinite(exponentResult))
      {
         return null;
      }
      if (!sides.get(0).contains("y"))
      {
         if (coefficient.contains("/"))
         {
            String termThing = term.replaceAll("\\^" + "[0-9|.-]*", "");
            termThing = termThing.replace(")", "");
            BigDecimal totalTemp;
            totalTemp = replaceVarCoef(termThing, BigDecimal.valueOf(exponentResult), "x");
            if (totalTemp == null)
            {
               return null;
            }
            total = total.add(totalTemp);
         }
         else
         {
            total = total.add(BigDecimal.valueOf(Double.parseDouble(replaceSpecialConstants(coefficient)) * exponentResult));
         }
      }
      else
      {
         BigDecimal yCoef = computeDivisor(getCoefficient(sides.get(0), "y"));
         if (coefficient.contains("/"))
         {
            String termThing = term.replaceAll("\\^" + "[0-9|.-]*", "");
            termThing = termThing.replace(")", "");
            BigDecimal result = replaceVarCoef(termThing, BigDecimal.valueOf(exponentResult), "x");
            total = total.add(result.divide(yCoef, 256, RoundingMode.HALF_UP));
         }
         else
         {
            total = total.add(BigDecimal.valueOf(Double.parseDouble(coefficient) * exponentResult).divide(yCoef, 256, RoundingMode.HALF_UP));
         }
      }
      return total;
   }
   public String getUserEquation()
   {
      return userEquation;
   }
}//731 lines 4/20/24 664 lines 4/23/24