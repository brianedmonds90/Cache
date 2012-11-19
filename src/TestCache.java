
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
	public void testHit() throws FileNotFoundException{
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
	public void testWrite() throws FileNotFoundException{
		Cache cache= Cache.init();
		cache.write("10029e12");
		assertEquals(true,cache.hit("10029e12"));
		//cache.storage = new Block [(int) Math.pow(2,cache.numIndexBits)][cache.associativity];
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
	public void testPrefetcherLoading() throws FileNotFoundException{//Tests loading blocks into cache w/o checking
		//for matches
		Cache mycache= Cache.init();
		String line=mycache.globalScanner.nextLine();
		mycache.access(line.charAt(0),line.substring(2));
		mycache.loadMemory();
		assertEquals(mycache.storage[0][0].tag,"00010110111111111110111");
		assertEquals(mycache.storage[2][0].tag,"00010110111111111111011");
		assertEquals(mycache.storage[4][0].tag,"00010110111111111111010");
	}
	@Test
	public void testLRUBookKeeping() throws FileNotFoundException{
		Cache cache= Cache.init();
		String line=cache.globalScanner.nextLine();
		cache.access(line.charAt(0),line.substring(2));
		assertEquals(cache.lruArray[3].lastElement(),0);
	}
	@Test
	public void testLRUVictimSelection() throws FileNotFoundException{
		Cache cache= Cache.init();
		cache.access('r', "10029e12");
		cache.access('r', "10029e12");
		//cache.access('r', "10029e12");
		//cache.access('r', "10029e12");
		assertEquals(0,cache.lruVictim(0));
	}
}

