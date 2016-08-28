import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.text.DecimalFormat;

public class FunctionPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener
{
    double xMin,
           xMax,
           yMin,
           yMax,
           xScale,
           yScale,
           MouseClickedX,
           MouseClickedY,
           cMinX,
           cMinY,
           cMaxX,
           cMaxY,
           lessThanXDeadZone,
           greaterThanXDeadZone,
           mouseTrackX,
           mouseTrackY,
           functionTrackX,
           functionTrackY;

    boolean showGrid,
            dragDown,
            compressDown,
            deadZoneHighlight,
            keyPointHighlight,
            areaUnderCurveHighlight,
            mouseTrack,
            showMouseTrack,
            trackingFunction;

    private ArrayList<KeyPoint> keyPoints;
    
    private ArrayList<Double[]> storedVals;

    public Color backgroundColor,
                  gridColor,
                  axesColor;

    private Function AUCFunction;
    private double AUCLeftBound,
                   AUCRightBound;
                   
    private Function trackFunction;               

    public FunctionPanel()
    {
        xMin = -10;
        xMax = 10;
        yMin = -10;
        yMax = 10;
        xScale = 1;
        yScale = 1;
        mouseTrackX = 0;
        mouseTrackY = 0;
        functionTrackX=0;
        functionTrackY=0;
        trackingFunction = false;
        showGrid = true;
        keyPointHighlight = false;
        areaUnderCurveHighlight = false;
        deadZoneHighlight = false;
        mouseTrack = false;
        showMouseTrack = false;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        keyPoints = new ArrayList<KeyPoint>(5);
        backgroundColor = Color.white;
        gridColor = Color.lightGray;
        axesColor = Color.black;
        setPreferredSize(new Dimension(300, 300));
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        double helperLine;
        double[] domain, range;

        g.setColor(backgroundColor);
        g.fillRect(0,0,getWidth(), getHeight());

        if(deadZoneHighlight)
        {
            highlightDeadZone(g);
        }

        if(areaUnderCurveHighlight)
        {
            paintAreaUnderCurve(g);
        }

        if(xMax>0 && xMin<0)
        {
            if(showGrid)
            {
                g.setColor(gridColor);
                for(helperLine = 0.0; helperLine>xMin; helperLine-=xScale); helperLine+=xScale;
                for(; helperLine<xMax; helperLine+=xScale)
                {
                    int hLevel = (int)Math.round(((helperLine-xMin)/(double)(xMax-xMin))*getWidth());
                    g.drawLine(hLevel, 0, hLevel, getHeight());
                }
            }
            int oLevel = (int)Math.round(((0-xMin)/(double)(xMax-xMin))*getWidth());
            g.setColor(axesColor);
            g.drawLine(oLevel, 0, oLevel, getHeight());
        }
        if(yMax>0 && yMin<0)
        {
            if(showGrid)
            {
                g.setColor(gridColor);
                for(helperLine = 0.0; helperLine>yMin; helperLine-=yScale); helperLine+= yScale;
                for(;helperLine<yMax; helperLine+=yScale)
                {
                    int hLevel = (int)Math.round(((helperLine-yMin)/(double)(yMax-yMin))*getHeight());
                    g.drawLine(0, getHeight()-hLevel, getWidth(), getHeight()-hLevel);
                }
            }
            int oLevel = (int)Math.round(((0-yMin)/(double)(yMax-yMin))*getHeight());
            g.setColor(axesColor);
            g.drawLine(0, getHeight()-oLevel, getWidth(), getHeight()-oLevel);
        }
        if(showGrid)
        {
            if(xMin>0)
            {
                g.setColor(gridColor);
                for(helperLine = 0.0; helperLine<xMin; helperLine+=xScale);
                for(;helperLine<xMax;helperLine+= xScale)
                {
                    int hLevel = (int)Math.round(((helperLine-xMin)/(double)(xMax-xMin))*getWidth());
                    g.drawLine(hLevel, 0, hLevel, getHeight());
                }
            }
            if(xMax<0)
            {
                g.setColor(gridColor);
                for(helperLine=0.0; helperLine>xMax; helperLine-= xScale);
                for(;helperLine>xMin; helperLine-=xScale)
                {
                    int hLevel = (int)Math.round(((helperLine-xMin)/(double)(xMax-xMin))*getWidth());
                    g.drawLine(hLevel, 0, hLevel, getHeight());
                }
            }
            if(yMin>0)
            {
                g.setColor(gridColor);
                for(helperLine=0.0; helperLine>yMin; helperLine+=yScale);
                for(;helperLine<yMax; helperLine+=yScale)
                {
                    int hLevel = (int)Math.round(((helperLine-yMin)/(double)(yMax-yMin))*getHeight());
                    g.drawLine(0, getHeight()-hLevel, getWidth(), getHeight()-hLevel);
                }
            }
            if(yMax<0)
            {
                g.setColor(gridColor);
                for(helperLine=0.0; helperLine<yMax; helperLine-=yScale);
                for(;helperLine>yMin; helperLine-=yScale)
                {
                    int hLevel = (int)Math.round(((helperLine-yMin)/(double)(yMax-yMin))*getHeight());
                    g.drawLine(0, getHeight()-hLevel, getWidth(), getHeight()-hLevel);
                }
            }
        }

        domain = new double[getWidth()];
        for(int i=0; i<getWidth(); i++)
        {
            domain[i] = xMin + ((xMax-xMin)/(double)getWidth())*i;
        }

        Function currentFunction;
        for(int i=0; i<GraphingData.functionSize(); i++)
        {
            currentFunction = GraphingData.getFunction(i);
            range = currentFunction.generateOutput(domain);
            g.setColor(currentFunction.getFunctionColor());
            int currentHeight = getHeight();
            for(int j=0; j<getWidth()-1; j++)
            {
                if((range[j+1]>yMin && range[j+1]<yMax) || (range[j]>yMin && range[j]<yMax))
                    g.drawLine(j, (int)Math.round(currentHeight-((range[j]-yMin)/(yMax-yMin))*getHeight()), j+1, (int)Math.round(currentHeight-((range[j+1]-yMin)/(yMax-yMin))*getHeight()));
            }
        }

        if(keyPointHighlight)
        {
            paintKeyPoints(g);
        }
        
        if(mouseTrack && showMouseTrack)
        {
            DecimalFormat d = new DecimalFormat("0.0000");
            String xString = "X: " + d.format(mouseTrackX);
            String yString = "Y: " + d.format(mouseTrackY);
            g.setColor(Color.white);
            g.fillRect(getWidth()-160, getHeight()-20, 160, 20);
            g.setColor(Color.black);
            g.drawRect(getWidth()-160, getHeight()-20, 160, 20);
            g.drawString(xString, getWidth()-150, getHeight()-5);
            g.drawString(yString, getWidth()-70, getHeight()-5);
        }
        
        if(trackFunction != null && trackingFunction)
        {
        	DecimalFormat d = new DecimalFormat("0.0000");
            String xString = "X: " + d.format(functionTrackX);
            String yString = "Y: " + d.format(functionTrackY);
            g.setColor(Color.white);
            g.fillRect((int)Math.round((functionTrackX-xMin)/(double)(xMax-xMin)*getWidth())+5, getHeight()-((int)Math.round((functionTrackY-yMin)/(double)(yMax-yMin)*getHeight()))+5, 160, 20);
            g.setColor(Color.black);
            g.drawRect((int)Math.round((functionTrackX-xMin)/(double)(xMax-xMin)*getWidth())+5, getHeight()-((int)Math.round((functionTrackY-yMin)/(double)(yMax-yMin)*getHeight()))+5, 160, 20);
            g.drawString(xString, (int)Math.round((functionTrackX-xMin)/(double)(xMax-xMin)*getWidth())+15, getHeight()-((int)Math.round((functionTrackY-yMin)/(double)(yMax-yMin)*getHeight()))+20);
            g.drawString(yString, (int)Math.round((functionTrackX-xMin)/(double)(xMax-xMin)*getWidth())+85, getHeight()-((int)Math.round((functionTrackY-yMin)/(double)(yMax-yMin)*getHeight()))+20);
        }
    }

    public void paintKeyPoints(Graphics g)
    {
        for(int i=0; i<keyPoints.size(); i++)
        {
            KeyPoint currentPoint = keyPoints.get(i);
            if(currentPoint.xPos>xMin && currentPoint.xPos<xMax && currentPoint.yPos>yMin && currentPoint.yPos<yMax)
            {
                int drawX = (int)Math.round(((currentPoint.xPos-xMin)/(xMax-xMin))*getWidth());
                int drawY = getHeight()-(int)Math.round(((currentPoint.yPos-yMin)/(yMax-yMin))*getHeight());
                g.setColor(currentPoint.pointColor);
                g.fillOval(drawX-4, drawY-4, 8, 8);
            }

        }
    }

    public void highlightDeadZone(Graphics g)
    {
        g.setColor(gridColor);
        if(lessThanXDeadZone >xMin)
        {
            int xWall = (int)Math.round(((lessThanXDeadZone-xMin)/(xMax-xMin))*getWidth());
            g.fillRect(0,0, xWall, getHeight());
        }
        if(greaterThanXDeadZone<xMax)
        {
            int xWall = (int)Math.round(((greaterThanXDeadZone-xMin)/(xMax-xMin))*getWidth());
            g.fillRect(xWall, 0, getWidth()-xWall, getHeight());
        }
    }

    public void refresh()
    {
        repaint();
    }

    public void addKeyPoints(KeyPoint[] k)
    {
        keyPointHighlight = true;
        for(int i=0; i<k.length; i++)
        {
            keyPoints.add(k[i]);
        }
        refresh();
    }

    public void addDeadZones(double lowerZone, double higherZone)
    {
        lessThanXDeadZone = lowerZone;
        greaterThanXDeadZone = higherZone;
        deadZoneHighlight = true;
        refresh();
    }

    public void setAreaUnderCurve(Function f, double lb, double rb)
    {
        AUCLeftBound = lb;
        AUCRightBound = rb;
        AUCFunction = f;
        areaUnderCurveHighlight = true;
        refresh();
    }

    public void clearKeyPoints()
    {
        keyPoints = new ArrayList<KeyPoint>(5);
    }
    
    public void generateTangentLine(Function f, double xVal)
    {
    	double yVal = f.evalExp(xVal, f.toString());
    	double slope = (f.evalExp(xVal+0.0000000001, f.toString())-f.evalExp(xVal-0.0000000001, f.toString()))/(0.0000000002);
    	DecimalFormat d = new DecimalFormat("0.00000000");
    	
    	String newFunction = "f(x)=" + d.format(slope) + "(x-(" + d.format(xVal) + "))+(" + d.format(yVal)+")";
    	GraphingData.addFunction(new Function(newFunction));  
    }

    public void setXMin(double v)
    {
        if(v<xMax)
            xMin = v;
        refresh();
    }
    public void setXMax(double v)
    {
        if(v>xMin)
            xMax = v;
        refresh();
    }
    public void setYMin(double v)
    {
        if(v<yMax)
            yMin = v;
        refresh();
    }
    public void setYMax(double v)
    {
        if(v>yMin)
            yMax = v;
        refresh();
    }
    public void setXScale(double v)
    {
        xScale = v;
        refresh();
    }
    public void setYScale(double v)
    {
        yScale = v;
        refresh();
    }

    public void setGrid(boolean b)
    {
        showGrid = b;
        refresh();
    }

    public void setMouseTrack(boolean m)
    {
        mouseTrack = m;
    }

    public void paintAreaUnderCurve(Graphics g)
    {
        g.setColor(Color.black);
        int leftColumn = (int)Math.round((AUCLeftBound-xMin)/(xMax-xMin)*getWidth());
        int rightColumn = (int)Math.round((AUCRightBound-xMin)/(xMax-xMin)*getWidth());
        if(rightColumn>0 && leftColumn<getWidth())
        {
            int zeroRow = (int)Math.round(getHeight()-((0-yMin)/(yMax-yMin))*getHeight());
            for(int i=leftColumn; i<=rightColumn; i++)
            {
                g.drawLine(i, (int)Math.round(getHeight()-(AUCFunction.evalExp(xMin+(i/(double)getWidth())*(xMax-xMin), AUCFunction.toString())-yMin)/(yMax-yMin)*getHeight()), i, zeroRow);
            }
        }
    }

    public void revertGraph()
    {
        keyPointHighlight = false;
        areaUnderCurveHighlight = false;
        deadZoneHighlight = false;
        clearKeyPoints();
        refresh();
    }
    
    public void setTrackFunction(Function f)
    {
    	if(f == null)
    	{
    		clearKeyPoints();
    	}
    	trackFunction = f;
    }

    public void mouseClicked(MouseEvent m){}
    public void mousePressed(MouseEvent m)
    {
        if(m.getButton() == 1)
        {
            dragDown = true;
            MouseClickedX = xMin+(m.getX()/(double)getWidth())*(xMax-xMin);
            MouseClickedY = yMin+((getHeight()-m.getY())/(double)getHeight())*(yMax-yMin);
        }
        else if(m.getButton() == 3)
        {
            compressDown = true;
            MouseClickedX = m.getX();
            MouseClickedY = m.getY(); //<- not conventional use of variables but oh well
            cMinX = xMin;
            cMaxX = xMax;
            cMinY = yMin;
            cMaxY = yMax;
        }
    }
    public void mouseReleased(MouseEvent m)
    {
        if(m.getButton() == 1)
        {
            dragDown = false;
        }
        else if(m.getButton() == 3)
        {
            compressDown = false;
        }
    }
    public void mouseDragged(MouseEvent m)
    {
        if(dragDown)
        {

            double shiftX = (xMin+(m.getX()/(double)getWidth())*(xMax-xMin))-MouseClickedX;
            double shiftY = (yMin+((getHeight()-m.getY())/(double)getHeight())*(yMax-yMin))-MouseClickedY;
            xMin-= shiftX;
            xMax-= shiftX;
            yMin-= shiftY;
            yMax-= shiftY;
            refresh();
            mouseMoved(m);
        }
        else if(compressDown)
        {
            double xCompress = MouseClickedX-m.getX();
            double yCompress = MouseClickedY-m.getY();
            double xScale = 1 + (xCompress/getWidth());
            double yScale = 1 - (yCompress/getHeight());
            double midX = (cMinX+cMaxX)/2.0;
            double midY = (cMinY+cMaxY)/2.0;
            xMin = midX - xScale*(midX-cMinX);
            xMax = midX + xScale*(cMaxX-midX);
            yMin = midY - yScale*(midY-cMinY);
            yMax = midY + yScale*(cMaxY-midY);
            refresh();
            mouseMoved(m);
        }
    }
    public void mouseMoved(MouseEvent m)
    {
        if(mouseTrack && showMouseTrack)
        {
            mouseTrackX = xMin + (m.getX()/(double)getWidth())*(xMax-xMin);
            mouseTrackY = yMin + ((getHeight()-m.getY())/(double)getHeight())*(yMax-yMin);
            refresh();
        }
        if(trackFunction != null)
        {
        	clearKeyPoints();
        	functionTrackX = xMin + (m.getX()/(double)getWidth())*(xMax-xMin);
        	functionTrackY = trackFunction.evalExp(functionTrackX, trackFunction.toString());
        	
        	KeyPoint[] k = {new KeyPoint(functionTrackX, functionTrackY, Color.black)};
        	addKeyPoints(k);
        }
    }

    public void mouseEntered(MouseEvent m)
    {
        if(mouseTrack)
        {
            showMouseTrack = true;
            refresh();
        }
        if(trackFunction!=null)
        {
        	trackingFunction = true;
        }
    }
    public void mouseExited(MouseEvent m)
    {
        showMouseTrack = false;
        trackingFunction = false;
    }

    public void mouseWheelMoved(MouseWheelEvent m)
    {
        MouseClickedX = xMin+(m.getX()/(double)getWidth())*(xMax-xMin);
        MouseClickedY = yMin+((getHeight()-m.getY())/(double)getHeight())*(yMax-yMin);
        double scale;
        if(m.getWheelRotation()>0)
            scale = 1.05;
        else
            scale = 0.95;

        xMin = MouseClickedX-scale*(MouseClickedX-xMin);
        xMax = MouseClickedX+scale*(xMax-MouseClickedX);
        yMin = MouseClickedY-scale*(MouseClickedY-yMin);
        yMax = MouseClickedY+scale*(yMax-MouseClickedY);

        refresh();
    }

    public Color getApropriateColor(Color[] in)
    {
        Color[] c = {Color.black, Color.red, Color.blue, Color.white, Color.green, Color.orange, Color.yellow, Color.lightGray};
        Random r = new Random(123);
        boolean unique = true;
        Color randomColor = null;
        do
        {
            for(int i=0; i<c.length; i++)
            {
                randomColor = c[i];
                unique = true;
                for(int j=0; j<in.length; j++)
                {
                    if(in[j].equals(randomColor))
                    {
                        unique = false;
                    }
                }
            }
        }
        while(unique == false);

        return randomColor;
    }
}