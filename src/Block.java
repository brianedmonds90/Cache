import java.util.ArrayList;
/**
 * @author Brian Edmonds
 *
 */
public class Block {
	boolean dirty;
	boolean valid;
	int offset, tag,index;
	//LinkedList 
	public Block(int offset, int tag){
		this.offset=offset;
		this.tag=tag;
	}
	
}
