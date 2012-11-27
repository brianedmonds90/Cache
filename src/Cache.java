import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
public class Cache {
	static int numReads=0;
	static int numWrites=0;
	static int numReadMisses=0;
	static int numWriteMisses=0;
	static int numWriteBacks=0;
	static int numAccesses=0;
	static int prefetcherLines=0;
	
	//Stack lruStack;
	LinkedList [] lruArray;
	int totalDataStorage, blockSize,associativity,prefetcherSize,numOffsetBits,numIndexBits,numTagBits;
	File file;
	Block [][]storage;
	Scanner globalScanner;
	private Object prefetcher;
	public Cache(){
		totalDataStorage=0;
		blockSize=0;
		associativity=0;
		prefetcherSize=0;
		numOffsetBits=0;
		numIndexBits=0;
		numTagBits=0;
		file=null;
		storage=null;
		lruArray=null;
		Scanner globalScanner=null;		
	}
	/*
	 * Method that take in an address and computes a hit or miss 
	 * On miss it will call loadMemory() function to 
	 * deal with such errors
	 */
	void access(char rw, String address){
		numAccesses++;
		if(rw =='r'){
			numReads++;
		}
		else if(rw=='w'){
			numWrites++;
		}
		String binaryAddress= hexToBin(address);
		int index = Cache.binaryToDecimal(this.computeIndexofAddress(binaryAddress));
		String blockTag = this.computeTagOfAddress(binaryAddress);
		Boolean hit=false;
		for(int i=0;i<this.associativity;i++){
			if(this.storage[index][i]!=null){
				if(this.storage[index][i].tag.equals(blockTag)&&this.storage[index][i].valid){
					hit=true;
					this.lruAdding(blockTag,index);//Add the tag of the most recently used
					//item to the LRU list;
				if(rw=='w')
					this.storage[index][i].dirty=true;//Set dirty bit if write access
					i=1000;
				}
			}
			else{//Miss and empty space for current address
				this.storage[index][i]=new Block(blockTag);
				this.lruAdding(blockTag,index);//Mark the LRU list with the most recently
				//used item
				if(rw =='r')numReadMisses++;
				else if(rw=='w'){
					numWriteMisses++;
					numWriteBacks++;
					this.storage[index][i].dirty=true;
				}
				if(rw!='x')//This check is used to avoid an endless loop of memory accesses
					this.loadMemory(address,0);//Bring blocks in from memory
				i=1000;
				return;
			}
		}
		if(!hit){//Access was not a hit and a entry in the cache needs to be evicted
			if(rw =='r')numReadMisses++;
			else if(rw=='w'){
				numWriteMisses++;
				numWriteBacks++;
			}
			int victim=this.lruVictim(index);
			this.storage[index][victim]=new Block(blockTag);
			if(rw=='w')
				this.storage[index][victim].dirty=true;
			this.lruAdding(blockTag,index);//[index].add(blockTag);
			if(rw!='x')
				this.loadMemory(address,0);
		}
	}
	public void loadMemory(String address,int counter) {//Method is called to access memory on a miss 
		//computes the address of the next block to fetch into the cache		
		double add=binaryToDecimal(hexToBin(address));
		double x= add/(double)blockSize+this.blockSize;
		String addAccess=Integer.toHexString((int) x);
		access('x',addAccess);
		if(counter<this.prefetcherSize){
			prefetcherLines++;
			loadMemory(addAccess,++counter);//Recursive call used to load as many prefetched blocks
			//as necessary 
		}
	}
	int numBlockOffsetBits(){
		int blockOffset= (int) (Math.log(blockSize)/Math.log(2));
		return blockOffset;
	}
	int numLines(){//Returns the number of lines of the cache
		int numLines= this.totalDataStorage/(this.blockSize*this.associativity);
		int bits=(int) (Math.log(numLines)/Math.log(2));
		return bits;
	}
	int numTagBits(){//Returns the number of tag bits of a particular cache
		return 32-this.numIndexBits-this.numOffsetBits;
	}
	String computeBlockOffset(String address){//Given a binary string address returns the bits used to
		//index into a cache's offset
		return address.substring(32-this.numOffsetBits);	
	}
	static String hexToBin(String s) {//Converts a hex number to binary string
		String temp=new BigInteger(s,16).toString(2);
		while(temp.length()<32){
			temp="0"+temp;
		}
		return temp;
	}
	String computeIndexofAddress(String address){//Given a binary string address, returns the 
		//index bits
		String temp=address.substring(32-this.numIndexBits-this.numOffsetBits);
		return temp.substring(0,temp.length()-this.numOffsetBits);
	}
	String computeTagOfAddress(String address){//Given a binary string address, returns
		//the Tag bits
		return address.substring(0,this.numTagBits);
	}
	static int binaryToDecimal(String binaryString){
		
		return Integer.parseInt(binaryString,2);
	}
	//This method return the associativity index to evict given an index of the cache
	int lruVictim(int index){
		//return this.lruStack.lastElement();
		String temp=(String) this.lruArray[index].peek();
		for (int i = 0; i < this.associativity; i++) {
			if(storage[index][i].tag.equals(temp)){
				return i;
			}
		}
		return -1;
	}
	//This method is used for lruBookKeeping
	//@params blockTag: the tag of the block to be added to the cache
	//@params index: the index of the line of cache to add to
	void lruAdding(String blockTag,int index){
		if(lruArray[index].contains(blockTag)){
			lruArray[index].remove(blockTag);
			lruArray[index].add(blockTag);
			return;
		}
		if(lruArray[index].size()<this.associativity){
			lruArray[index].add(blockTag);
		}
		else{
			lruArray[index].pop();
			lruArray[index].add(blockTag);
		}
		
		
	}
	public static void main(String[]args) throws FileNotFoundException{
	//	Cache cache= init();
		Cache cache= new Cache();
		cache.file=new File(args[0]);
		cache.globalScanner = new Scanner(cache.file);
		cache.totalDataStorage= Integer.parseInt(args[1]);
		cache.blockSize=Integer.parseInt(args[2]);
		cache.associativity=Integer.parseInt(args[3]);
		cache.prefetcherSize=Integer.parseInt(args[4]);
		cache.numOffsetBits=cache.numBlockOffsetBits();
		cache.numIndexBits=cache.numLines();
		cache.numTagBits=cache.numTagBits();
		cache.storage = new Block [(int) Math.pow(2,cache.numIndexBits)][cache.associativity];
		cache.lruArray=new LinkedList [(int) Math.pow(2,cache.numIndexBits)];
		for(int i=0;i<cache.lruArray.length;i++){
			cache.lruArray[i]=new LinkedList();
		}
		//Scanner scanner=new Scanner(cache.file);
		String line;
		while(cache.globalScanner.hasNext()){//Loop through trace files
			line=cache.globalScanner.nextLine();
			cache.access(line.charAt(0),line.substring(2));//parse trace file for read/write and address
		}	
		System.out.println("Number of Accesses: "+numAccesses);
		System.out.println("Number of Reads= "+numReads);
		System.out.println("Number of Writes= "+numWrites);
		System.out.println("Number of Read Misses= "+numReadMisses);
		System.out.println("Number of Write Misses= "+numWriteMisses);
		System.out.println("Number of Write Backs= "+numWriteBacks);
		int totalMisses=numReadMisses+numWriteMisses;
		System.out.println("Total Number of Misses: "+totalMisses);
		double cacheMissRate=(double)numReadMisses/(double)numReads;
		System.out.println("Cache miss rate: "+cacheMissRate);
		System.out.println("Total Number of blocks prefetched: "+prefetcherLines);
		double Tc=2.0+(.2*cache.totalDataStorage);
		double Tm= 50;
		double EMAT= Tc + cacheMissRate*Tm;
		System.out.println("EMAT: "+EMAT+" ns");
		//total number of bits of cache storage, including all data storage, tag storage, valid and dirty bits.
		//Need EMAT also
	}
	static Cache init() throws FileNotFoundException{//Init method for quick testing
		Cache cache= new Cache();
		cache.file=new File("/home/brian/Desktop/projEC/traces/bzip2_trace.txt");
		cache.globalScanner = new Scanner(cache.file);
		cache.totalDataStorage=1024;
		cache.blockSize=64;
		cache.associativity=2;
		cache.prefetcherSize=2;
		cache.numOffsetBits=cache.numBlockOffsetBits();
		System.out.println("Num Offset Bits: "+cache.numOffsetBits);
		cache.numIndexBits=cache.numLines();
		System.out.println("Num Index Bits: "+cache.numIndexBits);
		cache.numTagBits=cache.numTagBits();
		System.out.println("Num Tag Bits: "+cache.numTagBits);
		cache.storage = new Block [(int) Math.pow(2,cache.numIndexBits)][cache.associativity];
		cache.lruArray=new LinkedList [(int) Math.pow(2,cache.numIndexBits)];
		for(int i=0;i<cache.lruArray.length;i++){
			cache.lruArray[i]=new LinkedList();
		}
		return cache;
	}
}//End of main
