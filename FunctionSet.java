import java.util.*;

public class FunctionSet{
	
	private ArrayList<Function> functions;
	
	public FunctionSet(){
		functions = new ArrayList<Function>(10);
	}
	
	public void clear(){
		functions.clear();
	}
	
	public boolean remove(Function f){
		int index = binarySearch(f);
		if(index == -1){
			return false;
		} else {
			functions.remove(index);
			return true;
		}
	}
	
	
	public boolean add(Function f){
		if(binarySearch(f) != -1){
			functions.set(binarySearch(f), f);
			return true;
		} else {	
			int index = 0;
			while(index<functions.size() && functions.get(index).compareTo(f)<0)index++;
			functions.add(index, f);
			return true;
		}
	}
	
	
	public boolean contains(Function f){
		if(binarySearch(f) == -1){
			return false;
		}else {
			return true;
		}
	}
	
	public boolean isEmpty(){
		if(functions.size() == 0){
			return true;
		} else {
			return false;
		}
	}
	
	public int size(){
		return functions.size();
	}
	
	private int binarySearch(Function f){
		
		int low = 0;
		int high = functions.size()-1;
		int mid = (low+high)/2;
		boolean found = false;
		
		while(!found && low <= high){
			mid = (low+high)/2;
			if(functions.get(mid).equals(f)){
				found = true;
			} else if(functions.get(mid).compareTo(f) > 0){
				high = mid-1;
			} else if(functions.get(mid).compareTo(f) < 0){
				low = mid+1;
			}		
		}
		
		if(found){
			return mid;
		}else{
			return -1;
		}
    }
    
    public Function getFunction(int i){
    	return functions.get(i);
    }
    
    public Function getFunction(String name){
    	for(int i=0; i<functions.size(); i++){
    		if(functions.get(i).getName().equals(name)){
    			return functions.get(i);
    		}
    	}
    	return null;
    }
    
    public ArrayList<Function> getFunctions(){
    	return functions;
    }
}