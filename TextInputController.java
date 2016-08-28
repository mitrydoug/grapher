import java.util.*;
import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;

public class TextInputController extends JPanel implements Runnable
{
    Scanner scan;
    FunctionPanel functionPanel;
    JTextArea textArea;
    Function test;

    double storedCalculation;

    public TextInputController(FunctionPanel fP)
    {
    	super(new GridBagLayout());

    	textArea = new JTextArea(10, 40);
    	textArea.setEditable(true);
    	JScrollPane scrollPane = new JScrollPane(textArea);
    	test = new Function("test(x)=0");

    	printToTextArea("Mitchell's Graphing Utility \nSystem now reading commands\nEnter LIST COMMANDS to see a list of known commands\n");

    	//Add Components to this panel.
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridwidth = GridBagConstraints.REMAINDER;
    	c.fill = GridBagConstraints.BOTH;
    	c.weightx = 1.0;
    	c.weighty = 1.0;
    	add(scrollPane, c);

        functionPanel = fP;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void executeCommand(String command)
    {
		boolean unknownCommand = true;
        functionPanel.revertGraph();

        try {
            if(getWord(command, 1).toUpperCase().equals("QUIT"))
            {
                System.exit(0);
                unknownCommand = false;
            }
            if(getWord(command, 1).toUpperCase().equals("ADD"))
            {
                if(getWord(command, 2).toUpperCase().equals("TANGENTLINE") && getWord(command, 3).toUpperCase().equals("FUNCTION"))
                {
                	Function f = GraphingData.getFunction(getWord(command, 4));
                	functionPanel.generateTangentLine(f, Double.parseDouble(getWord(command, 6)));
                	unknownCommand = false;
                }
            }

            if(command.contains("(x)=")){
            	Function newFunction = new Function(getWord(command, 1), functionPanel);
                unknownCommand = false;
                try{
                	if(!getWord(command, 2).equals(""))
                    {
                        Color color = getColorName(getWord(command, 2).toUpperCase());
                        newFunction.setColor(color);
                    }
                    else
                    {
                        newFunction.setColor(Color.red);
                    }
                    newFunction.evalExp(0, newFunction.toString());
                    GraphingData.addFunction(newFunction);
                } catch (RuntimeException e){

                }
            }else if(command.contains("=")){
                String var = command.substring(0, command.indexOf("="));
                String eq  = command.substring(command.indexOf("=")+1, command.length());
                double val;

                try {
                    val = Double.parseDouble(eq);
                    GraphingData.setVariable(var, val);
                } catch (Exception e){
                    try{
                       val = test.evalExp(0, eq);
                       GraphingData.setVariable(var, val);
                    } catch (Exception g){
                        printToTextArea("Invalid assignment");
                    }
                }
                unknownCommand = false;
            }

            if(getWord(command, 1).toUpperCase().equals("CLEAR"))
            {
                if(getWord(command, 2).toUpperCase().equals("FUNCTIONS"))
                {
                    GraphingData.clearFunctions();
                    functionPanel.refresh();
                    unknownCommand = false;
                }
            }

            if(getWord(command, 1).toUpperCase().equals("LIST"))
            {
                if(getWord(command, 2).toUpperCase().equals("FUNCTIONS"))
                {
                    ArrayList<Function> f = GraphingData.getFunctions();
                    String list = "Functions:";
                    for(int i=0; i<f.size(); i++)
                    {
                        list += "\n" +  f.get(i).getName() + "(x)=" + f.get(i);
                    }
                    list += "\n";
                    printToTextArea(list);
                    unknownCommand = false;
                }

                if(getWord(command, 2).toUpperCase().equals("VARIABLES")){
                    Set<String> keys = GraphingData.getVariableNames();
                    Iterator i = keys.iterator();
                    String list = "\nVariables:";
                    while(i.hasNext()){
                        String s = (String)i.next();
                        list+= "\n" + s + "=" + GraphingData.variableValue(s);
                    }
                printToTextArea(list+"\n");
                unknownCommand = false;
                }

                if(getWord(command, 2).toUpperCase().equals("COMMANDS"))
                {
                    printToTextArea("FUNCTION HANDLING\n" +
                                       "    add Function: (enter function with no spaces) (<optional>function color)\n" +
                                       "    clear functions\n" +
                                       "    list functions\n" +
                                       "    remove function (function index)\n" +
                                       "SETTING VALUES\n" +
                                       "    set xScale (new val)\n" +
                                       "    set yScale (new val)\n" +
                                       "    set xMin (new val)\n" +
                                       "    set xMax (new val)\n" +
                                       "    set yMin (new val)\n" +
                                       "    set yMax (new val)\n" +
                                       "    set background (new color)\n" +
                                       "    set grid (new color)\n" +
                                       "    set axes (new color)\n" +
                                       "    show grid\n" +
                                       "    remove grid\n" +
                                       "    (available colors: red, blue, green, yellow, black, white, orange)\n" +
                                       "CALCULATING ON FUNCTIONS\n" +
                                       "    local minimum function (function index) leftBound (value) rightBound (value)\n" +
                                       "    local maximum function (function index) leftBound (value) rightBound (value)\n" +
                                       "    intersection function (function1 #) function (function2 #) lB (val) rB (val)\n" +
                                       "    curve length function (function index) leftBound (value) rightBound (value)\n" +
                                       "    area under function (function index) leftBound (value) rightBound (value)\n" +
                                       "	add tangentLine function (function index) at (x value of tangent Line)");


                    unknownCommand = false;
                }
            }

            if(getWord(command, 1).toUpperCase().equals("SHOW"))
            {
                if(getWord(command, 2).toUpperCase().equals("GRID"))
                {
                    functionPanel.setGrid(true);
                    unknownCommand = false;
                }
            }

            if(getWord(command, 1).toUpperCase().equals("SET"))
            {
                if(getWord(command, 2).toUpperCase().equals("XMIN"))
                {
                    functionPanel.setXMin(test.evalExp(0, getWord(command, 3)));
                    unknownCommand = false;
                }
                if(getWord(command, 2).toUpperCase().equals("XMAX"))
                {
                    functionPanel.setXMax(test.evalExp(0, getWord(command, 3)));
                    unknownCommand = false;
                }
                if(getWord(command, 2).toUpperCase().equals("YMIN"))
                {
                    functionPanel.setYMin(test.evalExp(0, getWord(command, 3)));
                    unknownCommand = false;
                }
                if(getWord(command, 2).toUpperCase().equals("YMAX"))
                {
                    functionPanel.setYMax(test.evalExp(0, getWord(command, 3)));
                    unknownCommand = false;
                }
                if(getWord(command, 2).toUpperCase().equals("XSCALE"))
                {
                   functionPanel.setXScale(test.evalExp(0, getWord(command, 3)));
                   unknownCommand = false;
                }
                if(getWord(command, 2).toUpperCase().equals("YSCALE"))
                {
                   functionPanel.setYScale(test.evalExp(0, getWord(command, 3)));
                   unknownCommand = false;
                }
                if(getWord(command, 2).toUpperCase().equals("BACKGROUND"))
                {
                   if(!getWord(command, 3).equals(""))
                   {
                       Color color = getColorName(getWord(command, 3).toUpperCase());
                       functionPanel.backgroundColor = color;
                       functionPanel.refresh();
                       unknownCommand = false;
                   }
                }
                if(getWord(command, 2).toUpperCase().equals("GRID"))
                {
                   if(!getWord(command, 3).equals(""))
                   {
                       Color color = getColorName(getWord(command, 3).toUpperCase());
                       functionPanel.gridColor = color;
                       functionPanel.refresh();
                       unknownCommand = false;
                   }
                }
                if(getWord(command, 2).toUpperCase().equals("AXES"))
                {
                   if(!getWord(command, 3).equals(""))
                   {
                       Color color = getColorName(getWord(command, 3).toUpperCase());
                       functionPanel.axesColor = color;
                       functionPanel.refresh();
                       unknownCommand = false;
                   }
                }
            }

            if(getWord(command, 1).toUpperCase().equals("REMOVE"))
            {
                if(getWord(command, 2).toUpperCase().equals("FUNCTION"))
                {
                    GraphingData.removeFunction(GraphingData.getFunction(getWord(command, 3)));
                    unknownCommand = false;
                    functionPanel.refresh();

                }

                if(getWord(command, 2).toUpperCase().equals("GRID"))
                {
                    functionPanel.setGrid(false);
                    unknownCommand = false;
                }
            }

            if(getWord(command, 1).toUpperCase().equals("MOUSETRACK"))
            {
                if(getWord(command, 2).toUpperCase().equals("ON"))
                {
                    functionPanel.setMouseTrack(true);
                    unknownCommand = false;
                }

                if(getWord(command, 2).toUpperCase().equals("OFF"))
                {
                    functionPanel.setMouseTrack(false);
                    unknownCommand = false;
                }
            }

            if(getWord(command, 1).toUpperCase().equals("TRACK"))
            {
            	if(getWord(command, 2).toUpperCase().equals("FUNCTION"))
            	{
            		Function f = GraphingData.getFunction(getWord(command, 3));
            		functionPanel.setTrackFunction(f);
            		unknownCommand = false;
            	}
            	if(getWord(command, 2).toUpperCase().equals("OFF"))
            	{
            		functionPanel.setTrackFunction(null);
            		unknownCommand = false;
            	}
            }

            if(getWord(command, 1).toUpperCase().equals("LOCAL"))
            {
                if(getWord(command, 2).toUpperCase().equals("MINIMUM"))
                {
                    Function f = GraphingData.getFunction(getWord(command, 4));

                    double minX = Math.round(f.localMinXVal(Double.parseDouble(getWord(command, 6)), Double.parseDouble(getWord(command, 8)), 1000)*100000000.0)/100000000.0;
                    double minY = f.evalExp(minX, f.toString());


                    Color c[] ={functionPanel.backgroundColor, functionPanel.gridColor, f.getFunctionColor(), functionPanel.axesColor};
                    KeyPoint[] k = {new KeyPoint(minX, minY, Color.black)};


                    functionPanel.addKeyPoints(k);
                    functionPanel.addDeadZones(Double.parseDouble(getWord(command, 6)), Double.parseDouble(getWord(command, 8)));
                    printToTextArea("Local Minimum: x= " + minX + " y= " + minY);
                    GraphingData.setVariable("minX", minX);
                    GraphingData.setVariable("minY", minY);
                    unknownCommand = false;
                }

                if(getWord(command, 2).toUpperCase().equals("MAXIMUM"))
                {
                    Function f = GraphingData.getFunction(getWord(command, 4));

                    double maxX = Math.round(f.localMaxXVal(Double.parseDouble(getWord(command, 6)), Double.parseDouble(getWord(command, 8)), 1000)*100000000.0)/100000000.0;
                    double maxY = f.evalExp(maxX, f.toString());

                    KeyPoint[] k = {new KeyPoint(maxX, maxY, Color.black)};

                    functionPanel.addKeyPoints(k);
                    functionPanel.addDeadZones(Double.parseDouble(getWord(command, 6)), Double.parseDouble(getWord(command, 8)));
                    printToTextArea("Local Maximum: x= " + maxX + " y= " + maxY);
                    GraphingData.setVariable("maxX", maxX);
                    GraphingData.setVariable("maxY", maxY);
                    unknownCommand = false;
                }
            }

            if(getWord(command, 1).toUpperCase().equals("INTERSECTION"))
            {
                Function f1=null, f2=null;

                double leftBound= 0.0, rightBound=0.0;

                f1 = GraphingData.getFunction(getWord(command, 3));
                f1 = GraphingData.getFunction(getWord(command, 5));
                leftBound = Double.parseDouble(getWord(command, 7));
                rightBound = Double.parseDouble(getWord(command, 9));

                double interX = Math.round(Function.calcInterception(f1, f2, leftBound, rightBound)*100000000.0)/100000000.0;
                double interY = f1.evalExp(interX, f1.toString());

                KeyPoint[] k = {new KeyPoint(interX, interY, Color.black)};

                functionPanel.addKeyPoints(k);
                functionPanel.addDeadZones(leftBound, rightBound);
                printToTextArea("Interception: x= " + interX + " y= " + interY);
                GraphingData.setVariable("intersectionX", interX);
                GraphingData.setVariable("intersectionY", interY);
                unknownCommand = false;
            }

            if(getWord(command, 1).toUpperCase().equals("CURVE") && getWord(command, 2).toUpperCase().equals("LENGTH")
               && getWord(command, 3).toUpperCase().equals("FUNCTION"))
            {
                Function f = GraphingData.getFunction(getWord(command, 4));
                double leftBound = Double.parseDouble(getWord(command, 6)), rightBound = Double.parseDouble(getWord(command, 8));

                double curveL = Function.calcCurveLength(f, leftBound, rightBound, 1000);

                KeyPoint[] k = {new KeyPoint(leftBound, f.evalExp(leftBound, f.toString()), Color.black),
                                new KeyPoint(rightBound, f.evalExp(rightBound, f.toString()), Color.black)};

                functionPanel.addKeyPoints(k);
                functionPanel.addDeadZones(leftBound, rightBound);

                printToTextArea("Length of Segment:" + curveL);
                GraphingData.setVariable("curveLength", curveL);
                unknownCommand = false;
            }

            if(getWord(command, 1).toUpperCase().equals("AREA") && getWord(command, 2).toUpperCase().equals("UNDER") && getWord(command, 3).toUpperCase().equals("FUNCTION"))
            {
                Function f = GraphingData.getFunction(getWord(command, 4));

                double leftBound = Double.parseDouble(getWord(command, 6)), rightBound = Double.parseDouble(getWord(command, 8));

                double AreaUnderCurve = Function.calcAreaUnderCurve(f, leftBound, rightBound, 1000);

                DecimalFormat d = new DecimalFormat("0.00000");

                printToTextArea("Area Under Function:" + d.format(AreaUnderCurve));
                GraphingData.setVariable("area", AreaUnderCurve);
                functionPanel.setAreaUnderCurve(f, leftBound, rightBound);
                unknownCommand = false;
            }

            if(getWord(command, 1).toUpperCase().equals("AREA") && getWord(command, 2).toUpperCase().equals("BETWEEN") && getWord(command, 3).toUpperCase().equals("FUNCTIONS"))
            {
            	ArrayList<Function> mf = new ArrayList<Function>(3);
            	ArrayList<Function> lf = new ArrayList<Function>(3);

            	double leftBound = Integer.parseInt(getWord(command, 5));
            	double rightBound = Integer.parseInt(getWord(command, 7));

            	String function = "";
            	Scanner scan = new Scanner(System.in);

            	printToTextArea("Enter maximum functions:");
            	do
            	{
            		printToTextArea("-->");
            		function = scan.nextLine();
            		try
            		{
            			mf.add(GraphingData.getFunction(function));
            		}
            		catch(Exception e)
            		{
            		}

            	}while(!function.equals("done"));

            	printToTextArea("Enter minimum functions:");
            	do
            	{
                    printToTextArea("-->");
            		function = scan.nextLine();
            		try
            		{
            			lf.add(GraphingData.getFunction(function));
            		}
            		catch(Exception e)
            		{
            		}

            	}while(!function.equals("done"));

            	double area = Function.calcAreaBetweenFunctions(leftBound, rightBound, mf, lf);
            	DecimalFormat d = new DecimalFormat("0.00000000");
            	printToTextArea("Area between functions: " + area);
                GraphingData.setVariable("area", area);
            	unknownCommand = false;
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
        if(unknownCommand)
        {
            try{
                printToTextArea(""+test.evalExp(0, command));
            } catch (Exception e){
                printToTextArea("Unknown Command");
            }
        }
    }

    public String getWord(String s, int index) // string manipulation method
    {
        s = s.trim();

        String word ="";
        int spaceCount = 0;

        for(int i=0; i<s.length(); i++)
        {
            if(s.charAt(i) == ' ')
            {
                spaceCount++;
            }
            else if(spaceCount == index-1)
            {
                word += s.charAt(i);
            }
        }
        return word;
    }

    public Color getColorName(String color)
    {
        if(color.equals("RED"))
            return Color.red;
        else if(color.equals("BLUE"))
            return Color.blue;
        else if(color.equals("YELLOW"))
            return Color.yellow;
        else if(color.equals("GREEN"))
            return Color.green;
        else if(color.equals("BLACK"))
            return Color.black;
        else if(color.equals("ORANGE"))
            return Color.orange;
        else if(color.equals("WHITE"))
            return Color.white;
        else
           return Color.red;
    }

    public void run(){
    	boolean writingNewCommand = false;
    	int lastLength = textArea.getText().length();
    	while(true){
    		String text=textArea.getText();
    		if(text.charAt(text.length()-1) != '\n'){
    			writingNewCommand = true;
    		}
    		if(writingNewCommand == true && text.length()>lastLength && text.charAt(text.length()-1) == '\n'){
    		    lastLength = text.length();
    			executeCommand(getLastLine(text));
    			writingNewCommand = false;
    		}
    	}
    }

    public String getLastLine(String text){
    	 return text.substring(text.lastIndexOf('\n', text.length()-2)+1, text.length()-1);
    }

    public void printToTextArea(String s){
    	textArea.append(s+"\n");
    	textArea.setCaretPosition(textArea.getText().length());
    }
}