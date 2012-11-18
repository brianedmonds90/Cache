import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
public class Cache {
	static int numReads=0;
	static int numWrites=0;
	static int numReadMisses=0;
	static int numWriteMisses=0;
	static int numWriteBacks=0;
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
		
	}
	void access(char rw, String address){

		if(rw =='r'){
			numReads++;
		}
		else if(rw=='w'){
			numWrites++;
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
		String index = this.computeIndexofAddress(binaryAddress);
		String tag = this.computeTagOfAddress(binaryAddress);
		String offset = this.computeBlockOffset(binaryAddress);
		
		return false;
	}
	public static void main(String[]args) throws FileNotFoundException{
		//ArrayList mainArray=commandLineParams();
		Cache cache= new Cache();
		//Remove the following code block when done testing
		cache.file=new File("/home/brian/Desktop/projEC/traces/bzip2_trace.txt");
		cache.totalDataStorage=1000;
		cache.blockSize=250;
		cache.associativity=2;
		cache.prefetcherSize=1;
		cache.numOffsetBits=cache.numBlockOffsetBits();
		cache.numIndexBits=cache.numLines();
		cache.numTagBits=cache.numTagBits();
		cache.storage = new Block [(int) Math.pow(2,cache.numIndexBits)][cache.associativity];
	//End of code block
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
			cache.access(line.charAt(0),line.substring(2));//parse trace file for read/write and address
		}	
		System.out.println("Number of Reads= "+numReads);
		System.out.println("Number of Writes= "+numWrites);
	}
}//End of main
