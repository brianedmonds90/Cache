
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

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
	public void testAccessOnNull() throws FileNotFoundException{
		Cache cache= Cache.init();
		cache.access('r', "10029e12");
		cache.access('w', "16fff6b8");
		cache.access('r', "10028e12");
		assertEquals(Cache.numReads,2);
		assertEquals(Cache.numWrites,1);
		assertEquals(cache.storage[0][0].tag,"00010000000000101001111");
		assertEquals(cache.storage[2][0].tag,"00010110111111111111011");
		assertEquals(cache.storage[0][1].tag,"00010000000000101000111");
	}
	@Test 
	public void testLRUVictim() throws FileNotFoundException{
		Cache cache=Cache.init();
		cache.associativity=4;
		cache.lruArray[0].add(0);
		cache.lruArray[0].add(1);
		cache.lruArray[0].add(0);
		cache.lruArray[0].add(3);
		cache.lruArray[0].add(2);
		cache.lruArray[0].add(1);
		assertEquals(0,cache.lruVictim(0));
	}
	@Test
	public void testLRU1(){
		
	}
	@Test 
	public void testLoadBlock() throws FileNotFoundException{
		Cache cache= Cache.init();
		String address="10029e12";
		
		
	}
}

