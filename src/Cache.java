import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
public class Cache {
	static int numReads=0;
	static int numWrites=0;
	static int numReadMisses=0;
	static int numWriteMisses=0;
	static int numWriteBacks=0;
	static int numAccesses=0;
	//Stack lruStack;
	Stack [] lruArray;
	int totalDataStorage, blockSize,associativity,prefetcherSize,numOffsetBits,numIndexBits,numTagBits;
	File file;
	Block [][]storage;
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
		//lruStack= new Stack();
		
	}
	void access(char rw, String address, Scanner scan){
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
		//System.out.println("Block Tag: "+blockTag);
		Boolean hit=false;
		for(int i=0;i<this.associativity;i++){
			if(this.storage[index][i]!=null){
				if(this.storage[index][i].tag.equals(blockTag)&&this.storage[index][i].valid){
					i=1000;
					hit=true;
					//Set dirty bit here if write/read
				}
			}
			else{
				this.storage[index][i]=new Block(blockTag);
				int k=0;
				/*while(k<this.prefetcherSize){
					scan.nextLine();
				}*/
				//Bring blocks in from memory
			}
		}
		if(!hit){
			if(rw =='r')numReadMisses++;
			else if(rw=='w')numWriteMisses++;
			//int victim=this.lru();
			//this.storage[index][victim].tag=blockTag;
		//if Write Back set dirty bit to true
			//this.storage[index][victim].dirty=true;
		//Call LRU*/
		}
	}
	int numBlockOffsetBits(){
		int blockOffset= (int) (Math.log(blockSize)/Math.log(2));
		return blockOffset;
	}
	int numLines(){//Returns the number of lines of a 
		int numLines= this.totalDataStorage/(this.blockSize*this.associativity);
		int bits=(int) (Math.log(numLines)/Math.log(2));
		//System.out.println("Bits: "+bits);
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
	Boolean hit(String address){//Given an address returns true or false if the
		//location is a hit
		String binaryAddress= hexToBin(address);
		int index = Cache.binaryToDecimal(this.computeIndexofAddress(binaryAddress));
		String blockTag = this.computeTagOfAddress(binaryAddress);
		String blockOffset = this.computeBlockOffset(binaryAddress);
		for(int j=0;j<Math.pow(2,this.numIndexBits);j++){
			for(int i=0;i<this.associativity;i++){
				if(this.storage[j][i]!=null){
					if(this.storage[j][i].tag.equals(blockTag)&&this.storage[j][i].valid){
						return true;
					}
				}
			}
		}
		return false;
	}
	static int binaryToDecimal(String binaryString){
		
		return Integer.parseInt(binaryString,2);
	}
	void write(String address){
		String binaryAddress= hexToBin(address);
		int index = Cache.binaryToDecimal(this.computeIndexofAddress(binaryAddress));
		String blockTag = this.computeTagOfAddress(binaryAddress);
		Boolean loadSuccessfull=false;
		for(int i=0;i<this.associativity;i++){
			if(this.storage[index][i]==null){
				this.storage[index][i]=new Block(blockTag);
				i=1000;
			}
		}
		if(loadSuccessfull){
			return;
		}
		int victim=this.lru();
		this.storage[index][victim].tag=blockTag;
		//if Write Back set dirty bit to true
		this.storage[index][victim].dirty=true;
		//Call LRU
	}
	int lru(){
		//return this.lruStack.lastElement();
		return 0;
	}
	public static void main(String[]args) throws FileNotFoundException{
	Cache cache= init();
	/*
	System.out.println("Enter Path of Input File: ");
	Scanner scan = new Scanner(System.in);
	cache.file= new File(scan.nextLine());
	System.out.println("Enter Total Data Storage in KB: ");
	cache.totalDataStorage= scan.nextInt();
	System.out.println("Enter the block size: ");
	cache.blockSize=scan.nextInt();
	System.out.println("Enter the set associativity: ");
	cache.associativity=scan.nextInt();
	System.out.println("Enter the prefetcher size: ");
	cache.prefetcherSize=scan.nextInt();
	 */
		Scanner scanner=new Scanner(cache.file);
		String line;
		while(scanner.hasNext()){//Loop through trace files
			line=scanner.nextLine();
			cache.access(line.charAt(0),line.substring(2),scanner);//parse trace file for read/write and address
		}	
		System.out.println("Number of Accesses: "+numAccesses);
		System.out.println("Number of Reads= "+numReads);
		System.out.println("Number of Writes= "+numWrites);
		System.out.println("Number of Read Misses= "+numReadMisses);
		System.out.println("Number of Write Misses= "+numWriteMisses);
		//number of write backs measured in bytes
		//total number of bytes transferred to/from memory.
		//total number of blocks pre-fetced
		int totalMisses=numReadMisses+numWriteMisses;
		System.out.println("Total Number of Misses: "+totalMisses);
		double cacheMissRate=(double)numReadMisses/(double)numReads;
		System.out.println("Cache miss rate: "+cacheMissRate);
		//total number of bits of cache storage, including all data storage, tag storage, valid and dirty bits.
		//Need EMAT also
	}
	static Cache init(){//Init method for quick testing
		Cache cache= new Cache();
		cache.file=new File("/home/brian/Desktop/projEC/traces/bzip2_trace.txt");
		cache.totalDataStorage=1024;
		cache.blockSize=64;
		cache.associativity=2;
		cache.prefetcherSize=2;
		cache.numOffsetBits=cache.numBlockOffsetBits();
		//System.out.println("Num Offset Bits: "+cache.numOffsetBits);
		cache.numIndexBits=cache.numLines();
		//System.out.println("Num Index Bits: "+cache.numIndexBits);
		cache.numTagBits=cache.numTagBits();
		//System.out.println("Num Tag Bits: "+cache.numTagBits);
		cache.storage = new Block [(int) Math.pow(2,cache.numIndexBits)][cache.associativity];
		cache.lruArray=new Stack [(int) Math.pow(2,cache.numIndexBits)];
		for(int i=0;i<cache.lruArray.length;i++){
			cache.lruArray[i]=new Stack();
		}
		return cache;
	}
}//End of main
