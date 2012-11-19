
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

public class TestCache {
	Cache tester=new Cache();
	
	@Test
	public void testnumBlockOffsetBits(){
		tester.blockSize=16;
		assertEquals("Offset",4,tester.numBlockOffsetBits());
	}
	@Test
	public void testNumLines(){
		tester.totalDataStorage=(int) (Math.pow(2,10)*Math.pow(2,6));
		tester.associativity=4;
		tester.blockSize=16;
		assertEquals("Number of Lines",10,tester.numLines());
	}
	@Test
	public void testNumTagBits(){
		tester.numOffsetBits=4;
		tester.numIndexBits=10;
		assertEquals("Number Index Bits",18,tester.numTagBits());
	}
	@Test
	public void testToBinary(){
		Cache cache=new Cache();
		assertEquals("00010000000000101001111000010010",cache.hexToBin("10029e12"));
	}
	@Test
	public void testComputeBlockOffset(){
		Cache cache=new Cache();
		cache.numOffsetBits=4;
		assertEquals("0010",cache.computeBlockOffset(cache.hexToBin("10029e12")));
	}
	@Test
	public void testComputeIndexOffset(){
		Cache cache=new Cache();
		cache.numOffsetBits=2;
		cache.numIndexBits=6;
		assertEquals("000100",cache.computeIndexofAddress(cache.hexToBin("10029e12")));
	}
	@Test
	public void testTagIndexing(){
		Cache cache= new Cache();
		cache.numTagBits=10;
		assertEquals("0001000000",cache.computeTagOfAddress(cache.hexToBin("10029e12")));
	}
	@Test
	public void testHit(){
		Cache cache= Cache.init();
		assertEquals(false,cache.hit("10029e12"));
		Block b= new Block("00010000000000101001111");
		cache.storage[0][1]= b;
		assertEquals(true,cache.hit("10029e12"));
	}
	@Test
	public void testBinaryToDecimal(){
		assertEquals(16394,Cache.binaryToDecimal("000100000000001010"));
		assertEquals(14,Cache.binaryToDecimal("01110"));
	}
	@Test
	public void testCacheSize(){
		Cache cache=new Cache();
		cache.numIndexBits=10;
		cache.associativity=4;
		cache.storage = new Block [(int) Math.pow(2,cache.numIndexBits)][cache.associativity];
		assertEquals(cache.storage.length,1024);
		assertEquals(cache.storage[0].length,4);
	}
	@Test
	public void testWrite(){
		Cache cache= Cache.init();
		cache.write("10029e12");
		assertEquals(true,cache.hit("10029e12"));
		//cache.storage = new Block [(int) Math.pow(2,cache.numIndexBits)][cache.associativity];
	}
	@Test
	public void testAccess(){
		Cache cache= Cache.init();
		//cache.access('r', "10029e12");
		assertEquals(Cache.numReads,1);
		assertEquals(cache.storage[0][0].tag,00000000000010000000000);
		
		
	}
	public void testLRUVictim(){
		/*Cache cache= Cache.init();
		cache.write("10029e12");
		cache.write(null);
		cache.write(address);
		cache.write(address);
		cache.write(address);
		assertEquals();*/
	}
}

