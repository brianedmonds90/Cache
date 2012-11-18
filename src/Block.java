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
	public Block(String offset, String tag){
		this.offset=offset;
		this.tag=tag;
	}
	public String toString(){
		return "Offset: "+offset+"tag: "+tag;
		
	}
}
