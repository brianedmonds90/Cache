import java.util.ArrayList;
/**
 * @author Brian Edmonds
 *
 */
public class Block {
	boolean dirty;
	boolean valid;
	int offset, tag,index;
	ArrayList<Block> blocks;
	public Block(){
		blocks=new ArrayList<Block>();
		
	}
	
}
