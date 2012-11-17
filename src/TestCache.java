
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
}

