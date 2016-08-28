import java.awt.*;
import java.util.ArrayList;
import java.util.*;

public class Function implements Comparable
{
    String functionIdentity; // for example "f(x) = x^2"
    String functionName;
    Color functionColor;
    static FunctionPanel functionPanel = null;

    public Function(String f)
    {
        functionIdentity = clearSpaces(f).substring(clearSpaces(f).indexOf("=")+1, clearSpaces(f).length());
        functionName = clearSpaces(f).substring(0, clearSpaces(f).indexOf("(x)"));
    }

    public Function(String f, FunctionPanel fun)
    {
        functionIdentity = clearSpaces(f).substring(clearSpaces(f).indexOf("=")+1, clearSpaces(f).length());
        functionName = clearSpaces(f).substring(0, clearSpaces(f).indexOf("(x)"));
        functionPanel = fun;
    }

    public void setColor(Color c)
    {
        functionColor = c;
    }

    public double[] generateOutput(double[] in)
    {
        double[] output = new double[in.length];
        for(int i=0; i<in.length; i++)
        {
            output[i] = evalExp(in[i], functionIdentity);
        }
        return output;
    }

    public double evalExp(double input, String exp) throws RuntimeException
    {
    	//System.out.println(exp);
    	///Determines if the expression is concealed completely by parentheses
        if(exp.charAt(0) == '(' && exp.charAt(exp.length()-1) == ')'){
            int pLevel = 1;
            boolean entire = true;
            for(int i=1; i<exp.length()-1; i++){
                if(exp.charAt(i) == '(')
                    pLevel++;
                if(exp.charAt(i) == ')')
                    pLevel--;
                if(pLevel == 0)
                    entire = false;
            }
            if(entire == true){
                return evalExp(input, exp.substring(1, exp.length()-1));
            }
        }
        ////////////////////////////////////////////////////////////////////////
        //determines if expression is a simple mathematical constant. will need to be
        //expanded to incluse all user variables
        Set<String> keys = GraphingData.getVariableNames();
        {
            Iterator i = keys.iterator();
            while(i.hasNext()){
                if(exp.equals(i.next())){
                    return GraphingData.variableValue(exp);
                }
            }
        }
        if(exp.equals("x") || exp.equals("X")){
            return input;
        }
        else if(exp.toUpperCase().equals("PI")){
            return Math.PI;
        }
        else if(exp.toUpperCase().equals("E")){
            return Math.E;
        }
        else{
            try{
                double number = Double.parseDouble(exp);
                return number;
            }
            catch(Exception e){}
        }
        ////////////////////////////////////////////////////////////////////////
		//this calculates addition and subtraction
        int pLevel = 0;
        for(int i=exp.length()-1; i>=0; i--){
            switch (exp.charAt(i)){
                case '(':
                    pLevel --;
                    break;
                case ')':
                    pLevel++;
                    break;
                case '+':
                    if(pLevel == 0){
                        return evalExp(input, exp.substring(0, i)) + evalExp(input, exp.substring(i+1, exp.length()));
                    }
                case '-':
                    if(pLevel == 0){
                    	if(i==0){
                    		return - evalExp(input, exp.substring(i+1, exp.length()));
                    	}
                    	else{
                            return evalExp(input, exp.substring(0, i)) - evalExp(input, exp.substring(i+1, exp.length()));
                    	}
                    }
            }
        }
		////////////////////////////////////////////////////////////////////////
		// extensive, higher priority operations (see embedded documentation)
        pLevel = 0;
        for(int i=exp.length()-1; i>=0; i--){
            switch (exp.charAt(i)){
                case '(':
                    pLevel --;
                    boolean exponential = false;
                    //this conditional evaluates labeled parantheses
                    if(pLevel == 0 && i!= 0 && exp.charAt(i-1) != '*' && exp.charAt(i-1) != '/' && exp.charAt(i-1)!='^'
                    			   && !(exp.indexOf(")", i) != exp.length()-1 && exp.charAt(exp.indexOf(")", i)+1) == '^')){
                    	//General, imbedded functions
                        if(!(i<4) && exp.substring(i-4,i).equals("asin") && exp.charAt(exp.length()-1)==')'){
                            if(i==4){
                                return Math.asin(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-5) != '^'){
                                return evalExp(input, exp.substring(0, i-4))*Math.asin(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<4) && exp.substring(i-4,i).equals("acos") && exp.charAt(exp.length()-1)==')'){
                            if(i==4){
                                return Math.acos(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-5) != '^'){
                                return evalExp(input, exp.substring(0, i-4))*Math.acos(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<4) && exp.substring(i-4,i).equals("atan") && exp.charAt(exp.length()-1)==')'){
                            if(i==4){
                                return Math.atan(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-5) != '^'){
                                return evalExp(input, exp.substring(0, i-4))*Math.atan(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<3) && exp.substring(i-3,i).equals("sin") && exp.charAt(exp.length()-1)==')'){
                            if(i==3){
                                return Math.sin(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-4) != '^'){
                                return evalExp(input, exp.substring(0, i-3))*Math.sin(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<3) && exp.substring(i-3,i).equals("cos") && exp.charAt(exp.length()-1)==')'){
                            if(i==3){
                                return Math.cos(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-4) != '^'){
                                return evalExp(input, exp.substring(0, i-3))*Math.cos(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<3) && exp.substring(i-3,i).equals("tan") && exp.charAt(exp.length()-1)==')'){
                            if(i==3){
                                return Math.tan(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-4) != '^'){
                                return evalExp(input, exp.substring(0, i-3))*Math.tan(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<3) && exp.substring(i-3,i).equals("abs") && exp.charAt(exp.length()-1)==')'){
                            if(i==3){
                                return Math.abs(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-4) != '^'){
                                return evalExp(input, exp.substring(0, i-3))*Math.abs(evalExp(input, exp.substring(i+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<3) && exp.substring(i-3,i).equals("min") && exp.charAt(exp.length()-1)==')'){
                            if(i==3){
                                return Math.min(evalExp(input, exp.substring(i+1, exp.lastIndexOf(","))), evalExp(input, exp.substring(exp.lastIndexOf(",")+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-4) != '^'){
                                return evalExp(input, exp.substring(0, i-3))*Math.min(evalExp(input, exp.substring(i+1, exp.lastIndexOf(","))), evalExp(input, exp.substring(exp.lastIndexOf(",")+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<3) && exp.substring(i-3,i).equals("max") && exp.charAt(exp.length()-1)==')'){
                            if(i==3){
                                return Math.max(evalExp(input, exp.substring(i+1, exp.lastIndexOf(","))), evalExp(input, exp.substring(exp.lastIndexOf(",")+1, exp.length()-1)));
                            }
                            else if(exp.charAt(i-4) != '^'){
                                return evalExp(input, exp.substring(0, i-3))*Math.max(evalExp(input, exp.substring(i+1, exp.lastIndexOf(","))), evalExp(input, exp.substring(exp.lastIndexOf(",")+1, exp.length()-1)));
                            } else {
                            	exponential = true;
                            }
                        }
                        if(!(i<8) && exp.substring(i-8,i).equals("localMin") && exp.charAt(exp.length()-1)==')'){
                            int level = 1;
                            int index;
                            for(index=i; level!=0; index++){
                            	if(exp.charAt(index+1)==')') level --;
                            	if(exp.charAt(index+1)=='(') level ++;
                            }
                            String parameters = exp.substring(i+1, index);
                            StringTokenizer t = new StringTokenizer(parameters, ",");
                            Function f = new Function("new(x)="+t.nextToken());
                            double lb = evalExp(input, t.nextToken());
                            double rb = evalExp(input, t.nextToken());
                            int precision = t.hasMoreTokens() ? Integer.parseInt(t.nextToken()) : 30;

                            if(i==8){
                                return evalExp(f.localMinXVal(lb,rb,precision), f.toString());
                            }
                            else if(exp.charAt(i-9) != '^'){
                                return evalExp(input, exp.substring(0, i-8))*evalExp(f.localMinXVal(lb,rb,precision), f.toString());
                            } else {
                                exponential = true;
                            }
                        }
                        if(!(i<8) && exp.substring(i-8,i).equals("localMax") && exp.charAt(exp.length()-1)==')'){
                            int level = 1;
                            int index;
                            for(index=i; level!=0; index++){
                            	if(exp.charAt(index+1)==')') level --;
                            	if(exp.charAt(index+1)=='(') level ++;
                            }
                            String parameters = exp.substring(i+1, index);
                            StringTokenizer t = new StringTokenizer(parameters, ",");
                            Function f = new Function("new(x)="+t.nextToken());
                            double lb = evalExp(input, t.nextToken());
                            double rb = evalExp(input, t.nextToken());
                            int precision = t.hasMoreTokens() ? Integer.parseInt(t.nextToken()) : 30;

                            if(i==8){
                                return evalExp(f.localMaxXVal(lb,rb,precision), f.toString());
                            }
                            else if(exp.charAt(i-9) != '^'){
                                return evalExp(input, exp.substring(0, i-8))*evalExp(f.localMaxXVal(lb,rb,precision), f.toString());
                            } else {
                                exponential = true;
                            }
                        }
                        if(!(i<12) && exp.substring(i-12,i).equals("intersection") && exp.charAt(exp.length()-1)==')'){
                            int level = 1;
                            int index;
                            for(index=i; level!=0; index++){
                            	if(exp.charAt(index+1)==')') level --;
                            	if(exp.charAt(index+1)=='(') level ++;
                            }
                            String parameters = exp.substring(i+1, index);
                            StringTokenizer t = new StringTokenizer(parameters, ",");
                            Function f1 = new Function("new(x)="+t.nextToken());
                            Function f2 = new Function("new(x)="+t.nextToken());
                            double lb = evalExp(input, t.nextToken());
                            double rb = evalExp(input, t.nextToken());

                            if(i==12){
                                return Function.calcInterception(f1, f2, lb, rb);
                            }
                            else if(exp.charAt(i-13) != '^'){
                                return evalExp(input, exp.substring(0, i-12))*Function.calcInterception(f1, f2, lb, rb);
                            } else {
                                exponential = true;
                            }
                        }
                        if(!(i<11) && exp.substring(i-11,i).equals("curveLength") && exp.charAt(exp.length()-1)==')'){
                            int level = 1;
                            int index;
                            for(index=i; level!=0; index++){
                            	if(exp.charAt(index+1)==')') level --;
                            	if(exp.charAt(index+1)=='(') level ++;
                            }
                            String parameters = exp.substring(i+1, index);
                            System.out.println(parameters);
                            StringTokenizer t = new StringTokenizer(parameters, ",");
                            Function f = new Function("new(x)="+t.nextToken());
                            double lb = evalExp(input, t.nextToken());
                            double rb = evalExp(input, t.nextToken());
                            int precision = t.hasMoreTokens() ? Integer.parseInt(t.nextToken()) : 30;

                            if(i==11){
                                return Function.calcCurveLength(f, lb, rb, precision);
                            }
                            else if(exp.charAt(i-12) != '^'){
                                return evalExp(input, exp.substring(0, i-11))*Function.calcCurveLength(f, lb, rb, precision);
                            } else {
                                exponential = true;
                            }
                        }
                        if(!(i<8) && exp.substring(i-8,i).equals("integral") && exp.charAt(exp.length()-1)==')'){
                            int level = 1;
                            int index;
                            for(index=i; level!=0; index++){
                            	if(exp.charAt(index+1)==')') level --;
                            	if(exp.charAt(index+1)=='(') level ++;
                            }
                            String parameters = exp.substring(i+1, index);
                            StringTokenizer t = new StringTokenizer(parameters, ",");
                            Function f = new Function("new(x)="+t.nextToken());
                            double lb = evalExp(input, t.nextToken());
                            double rb = evalExp(input, t.nextToken());
                            int precision = t.hasMoreTokens() ? Integer.parseInt(t.nextToken()) : 30;

                            if(i==8){
                                return Function.calcAreaUnderCurve(f, lb, rb, precision);
                            }
                            else if(exp.charAt(i-9) != '^'){
                                return evalExp(input, exp.substring(0, i-8))*Function.calcAreaUnderCurve(f, lb, rb, precision);
                            } else {
                                exponential = true;
                            }
                        }
                        ////////////////////////////////////////////////////////
                        //this conditional evaluates references to user defined methods
                        boolean hold = false;
                        if(i!=0 && ((exp.charAt(i-1) >=65 && exp.charAt(i-1) <= 90) || (exp.charAt(i-1) >= 97 && exp.charAt(i-1) <= 122))){
                        	String embeddedFunction = "";
                        	int j = i-1;
                        	while(j>=0 && ((exp.charAt(j) >=65 && exp.charAt(j) <= 90) || (exp.charAt(j) >= 97 && exp.charAt(j) <= 122))){
                        		embeddedFunction = exp.charAt(j)+embeddedFunction;
                        		j--;
                        	}
                        	Function eFunction = GraphingData.getFunction(embeddedFunction);
                        	if(eFunction != null){
                        		if(i == embeddedFunction.length()){
                        			return evalExp(evalExp(input, exp.substring(i+1, exp.length()-1)), eFunction.toString());
                        		}
                                else if(exp.charAt(i-embeddedFunction.length()-1) != '*' && exp.charAt(i-embeddedFunction.length()-1) != '/' && exp.charAt(i-embeddedFunction.length()-1) != '^'){
                        			return evalExp(input, exp.substring(0, i-embeddedFunction.length()))*evalExp(evalExp(input, exp.substring(i+1, exp.length()-1)), eFunction.toString());
                        		} else {hold = true;}
                        	}
                        }

                        if(!exponential && !hold)
                        	return evalExp(input, exp.substring(0, i))*evalExp(input, exp.substring(i, exp.length()));
                    }
                    break;
                case ')':
                    pLevel++;
                    if(pLevel == 0 && i != exp.length()-1 && exp.charAt(i+1) != '*' && exp.charAt(i+1) != '/' && exp.charAt(i+1) != '^'){
                        return evalExp(input, exp.substring(0, i+1))*evalExp(input, exp.substring(i+1, exp.length()));
                    }
                    break;
                case '*':
                    if(pLevel == 0){
                        return evalExp(input, exp.substring(0, i)) * evalExp(input, exp.substring(i+1, exp.length()));
                    }
                case '/':
                    if(pLevel == 0){
                        try{
                            return evalExp(input, exp.substring(0, i)) / evalExp(input, exp.substring(i+1, exp.length()));
                        }catch(ArithmeticException e){
                            return Double.NaN;
                        }
                    }
                case 'x':
                    if(pLevel==0 && i!=0 && exp.charAt(i-1)!='*' && exp.charAt(i-1)!='/' && exp.charAt(i-1)!='^'){
                        return evalExp(input, exp.substring(0, i))*evalExp(input, exp.substring(i, exp.length()));

                    }
                    if(pLevel==0 && i!=exp.length()-1 && exp.charAt(i+1)!='*' && exp.charAt(i+1)!='/' && exp.charAt(i+1)!='^'){
                        return evalExp(input, exp.substring(0, i+1))*evalExp(input, exp.substring(i+1, exp.length()));
                    }
            }
        }
		int level = 0;
        for(int i=exp.length()-1; i>=0; i--){
        	if(exp.charAt(i) == '(')level++;
        	if(exp.charAt(i) == ')')level--;
            if(exp.charAt(i) == '^' && level ==0)
                return Math.pow(evalExp(input, exp.substring(0, i)), evalExp(input, exp.substring(i+1, exp.length())));
        }

        System.out.println("there were errors calculating " + exp);
        throw new RuntimeException();
    }

    public double localMinXVal(double leftBound, double rightBound, int precision)
    {
        double interval = (rightBound-leftBound)/precision;
        double minX = leftBound;
        double minY = evalExp(leftBound, functionIdentity);
        double v;
        for(double xVal=leftBound; rightBound>leftBound ? xVal<rightBound : xVal>rightBound ; xVal+=interval){
            if((v=evalExp(xVal, functionIdentity))<minY){
                minX=xVal;
                minY=v;
            }
        }
        return minX;
    }

    public double localMaxXVal(double leftBound, double rightBound, int precision)
    {
        double interval = (rightBound-leftBound)/precision;
        double maxX = leftBound;
        double maxY = evalExp(leftBound, functionIdentity);
        double v;
        for(double xVal=leftBound; rightBound>leftBound ? xVal<rightBound : xVal>rightBound ; xVal+=interval){
            if((v=evalExp(xVal, functionIdentity))>maxY){
                maxX=xVal;
                maxY=v;
            }
        }
        return maxX;
    }

    public static double calcInterception(Function f1, Function f2, double leftBound, double rightBound)
    {
        double interval = (rightBound-leftBound)/100.0;
        double minDiffVal = leftBound;
        for(double i = leftBound; i<=rightBound; i+=interval)
        {
            if(Math.abs(f1.evalExp(i,f1.toString())-f2.evalExp(i, f2.toString()))<Math.abs(f1.evalExp(minDiffVal,f1.toString())-f2.evalExp(minDiffVal, f2.toString())))
            {
                minDiffVal = i;
            }
            if(Math.abs(f1.evalExp(minDiffVal,f1.toString())-f2.evalExp(minDiffVal, f2.toString()))<0.0000000001)
            {
                return minDiffVal;
            }
        }
        return calcInterception(f1, f2, minDiffVal-interval, minDiffVal+interval);
    }

    public static double calcCurveLength(Function f, double leftBound, double rightBound, int precision)
    {
        double interval = (rightBound-leftBound)/precision;

        double curveLength=0.0;

        for(double i=leftBound; rightBound>leftBound ? i<rightBound : i>rightBound ; i+=interval)
        {
            curveLength += Math.sqrt(Math.pow(interval, 2)+Math.pow(f.evalExp(i, f.toString())-f.evalExp(i+interval, f.toString()), 2));
        }

        curveLength = Math.round(curveLength*10000)/10000.0;

        return curveLength;
    }

    public static double calcAreaUnderCurve(Function f, double leftBound, double rightBound, int precision)
    {
        double interval = (rightBound-leftBound)/precision;
        double area = 0.0;
        for(double i=leftBound; rightBound>leftBound ? i<rightBound : i>rightBound ; i+=interval)
        {
        	area += interval*(f.evalExp(i, f.toString())+f.evalExp(i+interval, f.toString()))/2.0;
        }
        return area;
    }

    public static double calcAreaBetweenFunctions(double lb, double rb, ArrayList<Function> mf, ArrayList<Function> lf)
    {
    	double interval = (rb-lb)/(5000*Math.sqrt(rb-lb));
    	double area = 0.0;
    	for(double i = lb; i<rb; i+=interval)
    	{
    		Function leastMax = mf.get(0);
    		Function greatestMin = lf.get(0);
    		for(int j=0; j<mf.size(); j++)
    		{
    			if(mf.get(j).evalExp(i, mf.get(j).toString())<leastMax.evalExp(i, leastMax.toString()))
    			{
    				leastMax = mf.get(j);
    			}
    		}
    		for(int j=0; j<lf.size(); j++)
    		{
    			if(lf.get(j).evalExp(i, lf.get(j).toString())>greatestMin.evalExp(i, greatestMin.toString()))
    			{
    				greatestMin = lf.get(j);
    			}
    		}

    		if(leastMax.evalExp(i, leastMax.toString())>greatestMin.evalExp(i, greatestMin.toString()))
    		{
    			area += interval*(leastMax.evalExp(i, leastMax.toString())-greatestMin.evalExp(i, greatestMin.toString()));
    		}
    	}

    	return area;
    }

    public String clearSpaces(String s)
    {
        String ret = "";
        for(int i=0; i<s.length(); i++)
        {
            if(s.charAt(i)!= ' ')
                ret += s.charAt(i);
        }

        return ret;
    }

    public Color getFunctionColor() {return functionColor;}

    public String toString()
    {
        return functionIdentity;
    }

    public boolean equals(Function f){
    	return functionName.equals(f.getName());
    }

    public String getName(){
    	return functionName;
    }

    public int compareTo(Object f){
    	return functionName.compareTo(((Function)f).getName());
    }
}