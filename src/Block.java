import java.util.ArrayList;
/**
 * @author Brian Edmonds
 *
 */
public class Block {
	boolean dirty;
	boolean valid;
	public String offset, tag;//index;
	//LinkedList 
	public Block(String tag){
		this.tag=tag;
		valid=true;
		dirty=false;
	}
	public String toString(){
		return "Offset: "+offset+"tag: "+tag;
		
	}
	Boolean equals(Block b){
		if(this.tag==b.tag)return true;
		return false;
	}
}
