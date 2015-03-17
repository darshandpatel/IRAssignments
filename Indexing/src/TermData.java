import java.io.Serializable;
import java.util.ArrayList;


public class TermData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	Integer length;
	ArrayList<DBlock> dblocks;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<DBlock> getDblocks() {
		return dblocks;
	}
	public void setDblocks(ArrayList<DBlock> dblocks) {
		this.dblocks = dblocks;
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(name + ":");
		for(DBlock db : dblocks){
			str.append(":"+db.toString());
		}
		return str.toString();
	}
}
