import java.util.*;

public class GraphingData{
	private static FunctionSet functions;
	private static FunctionPanel functionPanel;
	private static Hashtable<String, Double> variables;

	static{
		functions = new FunctionSet();
		functionPanel = null;
		variables = new Hashtable<String, Double>();
	}

    //function stuff
	public static void setFunctionPanel(FunctionPanel p){
		functionPanel = p;
	}

	public static boolean addFunction(Function f){
		boolean valid =  functions.add(f);
		functionPanel.refresh();
		return valid;
	}

	public static boolean removeFunction(Function f){
		return functions.remove(f);
	}

	public static int functionSize(){
		return functions.size();
	}

	public static Function getFunction(int i){
		return functions.getFunction(i);
	}

	public static Function getFunction(String str){
		return functions.getFunction(str);
	}

	public static void clearFunctions(){
		functions.clear();
	}

	public static ArrayList<Function> getFunctions(){
		return functions.getFunctions();
	}

    //variables stuff
    public static void setVariable(String name, double value){
        variables.put(name, value);
    }

    public static double variableValue(String name){
        return variables.get(name);
    }

    public static Set<String> getVariableNames(){
        return variables.keySet();
    }
}