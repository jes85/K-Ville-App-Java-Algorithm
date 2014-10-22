
public class Person {


	private int number; //person ID
	private int numInterval;
	private int numConsecutive;
	private int availSums;
	public int[] futureAvailConsec;
	public int[] futureAvailSums;//sum of avail intervals after current interval
	public int currentAssignedSums;
	
	//public int[] surroundingAssignedConsec; 
	/*private int maxConsec=4;
	private int minConsec;*/
	
	public Person(int N, int Consec,  int A, int numIntervals){
		number = N;
		numInterval=0;
		numConsecutive = Consec;
		availSums = A;
		//previousAssignedConsec = new int[numIntervals];
		futureAvailConsec = new int[numIntervals];
		futureAvailSums = new int[numIntervals];
		currentAssignedSums = 0;
	}

	
	public int getNumber(){
		return number;

	}
	public void setNumber(int n){
		number=n;
	}
	public int getNumInterval(){
		return numInterval;
	}
	public void setNumInterval(int n){
		numInterval=n;
	}
	public int getNumConsecutive(){
		return numConsecutive;
	}
	public void setNumConsecutive(int n){
		/*if(n>=maxConsec){
			numConsecutive=0;
		}
		else{
			numConsecutive=n;
		}*/
		numConsecutive=n;
	}
	public int getAvailSums(){
		return availSums;
	}
	public void setAvailSums(int n){
		availSums=n;
	}
	public void setCurrentAssignedSums(int n)
	{
		currentAssignedSums = n;
	}
	public int getCurrentAssignedSums(){
		return currentAssignedSums;
	}
}
