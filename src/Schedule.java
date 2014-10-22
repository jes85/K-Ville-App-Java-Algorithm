
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;


public class Schedule {
	
	// Basic parameters
		int numPeople;
		int numIntervals;
		int PPI; //required Persons Per Interval
	
	// Ideal Slots 
		int idealSlotsPerPersonIfAllAvailable;
		int totalSlotsRequired;
		double[] idealSlotsArray;
		//int idealSlotsPerAvailablePerson;
	
	
	// Sums Arrays
		int[] availIntervalSums;
		int[] assignIntervalSums;
		int[] availPeopleSums;
		int[] assignPeopleSums;
	
	// Matrix Schedules
		int[][] availabilitiesSchedule;
		int[][] assignmentsSchedule;
	
	// Priority Queue
		Comparator<Person> comparator = new PersonComparator();
		PriorityQueue<Person> consecutiveQueue;
		//or use hashmap with compare to?
		
		ArrayList<Person> personArrayList;
		ArrayList<Person> eachIntervalArrayList;
		
	// Scanner (test purposes)	
		Scanner input = new Scanner(System.in);
	
		
	// Printing (test purposes)
		int[] hours = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
		String[] minutes = {":00", ":15", ":30", ":45"};
		
	// Swap constants
		final int es = 1;
		int swapCount = 1;
		//int soloCount=0;
		int soloCountAvail =0;
		int soloCountAssign=0;
		int minConsec = 2;
		int swapTries = 5;
	/*
	 * Constructor
	 */
	public Schedule(AvailabilitySchedule sched){
		
		
		
		
		// Basic parameters
			numPeople = sched.numPeople;
			numIntervals = sched.numIntervals;
			PPI = (int) Math.ceil((double)numPeople/3);
			
			System.out.println("PPI: " + PPI);
			
		// Ideal Slots	
			totalSlotsRequired = PPI*numIntervals;
			idealSlotsPerPersonIfAllAvailable = totalSlotsRequired/numPeople;
			idealSlotsArray = new double[numPeople];
			
			
			System.out.println("idealSlotsPerPersonIfAllAvailable: " + idealSlotsPerPersonIfAllAvailable);
			System.out.println("totalSlotsRequired: " + totalSlotsRequired);
		
		
		
		// Sums Arrays
			availIntervalSums = new int[numIntervals];
			assignIntervalSums = new int[numIntervals];
			availPeopleSums = new int[numPeople];
			assignPeopleSums = new int[numPeople];
		
		
		// Matrix Schedules
			availabilitiesSchedule = sched.grid;
			assignmentsSchedule = new int [numPeople][numIntervals];	
		
		
		
		// Priority Queue
			consecutiveQueue = new PriorityQueue<Person>(numPeople, comparator);
			personArrayList = new ArrayList<Person>();
			eachIntervalArrayList = new ArrayList<Person>();;
			
		init();
			
		
	}
	
	
	public void init(){
		// Calculate Sums Arrays
				availIntervalSums = calculateIntervalSums(availIntervalSums, availabilitiesSchedule);
				availPeopleSums = calculatePeopleSums(availPeopleSums, availabilitiesSchedule);
				
				
				
		// Check for Error 
			if(checkForError()) return;	
				
				
		// Generate Ideal Slots Array
				generateIdealSlotsArray();
					
				//Print Array
				System.out.println("sum of idealSlotsArray: " + sumOfArray(idealSlotsArray));
				
		
		// Fill Priority Queue
			for(int p = 0;p<numPeople;p++){
				Person person = new Person(p, 0, availPeopleSums[p], numIntervals);
				calculateFutureAvailConsec(person);
				calculateFutureAvailSums(person);
				consecutiveQueue.add(person);
				personArrayList.add(person);
				
				
			}
		
		// Generate assignments
			generateSchedule();
	}
	/*
	 * Returns true if there is at least one interval with not enough people
	 */
	public boolean checkForError(){
		for(int c = 0; c<numIntervals;c++){
			int sum = availIntervalSums[c];
			if(sum<PPI){
				System.out.println("Not enough people in interval!\n");
				return true;
			}
			//assignIntervalsWithMinNumAvailable
			else if(sum==PPI){
				
			}
		}
		return false;
		
	}
	/*
	 * "main" method. Calls other methods to generate assignment schedule
	 */
	public void generateSchedule(){
		
		// Generate Assignment Schedule 
			
			assignIntervals(); 
			
			//printAssignmentSchedule();
			
		// Calculate Sums
			assignIntervalSums = calculateIntervalSums(assignIntervalSums, assignmentsSchedule);
			assignPeopleSums = calculatePeopleSums(assignPeopleSums, assignmentsSchedule);
			
			// Print Sums
			printSums();
			
		// Swap
			swapAll();
			
			// Count solos (for testing)	
				countSolos(availabilitiesSchedule, "avail");
				countSolos(assignmentsSchedule, "assign");
				//System.out.println("soloCountAssign: " +soloCountAssign);
			
		// Swap Remaining solo intervals 
		
			//(optional). Including it optimizes consecutive intervals, excluding it optimizes equality.	
			swapSolos();
			
			// Recount solos (for testing)
				soloCountAssign=0;
				countSolos(assignmentsSchedule, "assign");
			
		// Calculate sums
				assignIntervalSums = calculateIntervalSums(assignIntervalSums, assignmentsSchedule);
				assignPeopleSums = calculatePeopleSums(assignPeopleSums, assignmentsSchedule);
				
				// Print sums
				System.out.println("After swapping");
				printSums();
		
	}
	
	
	
	
	/*
	 * Assign intervals using queue. If min # available, assign all. Otherwise, assign people at front of queue
	 */
	public void assignIntervals(){
		for(int i = 0;i<numIntervals;i++){
			eachIntervalArrayList.clear();

			//assign based on queue
			int count = 0;
			for(int n = 0; n<numPeople;n++){
				Person person = consecutiveQueue.remove();
				if(count<PPI){
					if(availabilitiesSchedule[person.getNumber()][i]==1){
						assignmentsSchedule[person.getNumber()][i]=1;
						person.setCurrentAssignedSums(person.getCurrentAssignedSums()+1);
						person.setNumConsecutive(person.getNumConsecutive()+1);

						count++;
					}
					else{
						person.setNumConsecutive(0);
					}
				}
				else{
					person.setNumConsecutive(0);

				}
				if(i<numIntervals-1) person.setNumInterval(i+1);
				personArrayList.add(person.getNumber(), person);
				eachIntervalArrayList.add(person);
			}
			for(int n = 0; n<eachIntervalArrayList.size();n++){
				Person person = eachIntervalArrayList.get(n);
				consecutiveQueue.add(person);
			}
		}
	}
	
	/*
	 * Individual swap
	 * Assign p1 the interval, de-assign p2 from the interval
	 */
	public void swapSingleInterval(int p1, int p2, int i){
		assignmentsSchedule[p1][i]=1;
		assignmentsSchedule[p2][i]=0;
		assignPeopleSums[p1]++;
		assignPeopleSums[p2]--;
		swapCount++;
	}
	/*
	 * Initial swap. Loops through
	 */
	public void swapAll(){
		assignIntervalSums = calculateIntervalSums(assignIntervalSums, assignmentsSchedule);
		assignPeopleSums = calculatePeopleSums(assignPeopleSums, assignmentsSchedule);
		int count =0;
		while(count<swapTries && max(assignPeopleSums)-min(assignPeopleSums) > max(idealSlotsArray) - min(idealSlotsArray)){
			for(int p = 0; p<numPeople;p++){
				swapCount = 1;
				while(swapCount>0 && Math.abs(idealSlotsArray[p] - assignPeopleSums[p])>es){
					swapCount=0;
					
					swapHelperMethod(p);
				}
			}
			count++;
		}
	}
	
	/*
	 * checks ideal slots to see if it should try to swap
	 */
	public void swapHelperMethod(int person1){
		for(int person2=0;person2<numPeople;person2++){
			if(assignPeopleSums[person1]<idealSlotsArray[person1] && assignPeopleSums[person2]>idealSlotsArray[person2]){
				//swapFromBeginningIfPossible(person1, person2);
				//swapFromEndIfPossible(person1, person2);
				
				//old way (no minConsec)
				swapOldBeg(person1, person2);
				swapOldEnd(person1, person2);
			}
			else if(assignPeopleSums[person1]>idealSlotsArray[person1] && assignPeopleSums[person2]<idealSlotsArray[person2]){
				//swapFromBeginningIfPossible(person2, person1);
				//swapFromEndIfPossible(person2, person1);
				
				//old way (no minConsec)
				swapOldBeg(person2, person1);
				swapOldEnd(person2, person1);
			}
		}
	}
	/* 
	 * does the actual swapping while preserving consecutive stuff
	 */
	public void swapFromBeginningIfPossible(int p1, int p2){
		for(int i = 0;i<numIntervals-1;i++){
			
			if(assignPeopleSums[p1]>=idealSlotsArray[p1] || assignPeopleSums[p2]<=idealSlotsArray[p2] ){ //assignPeopleSums[p2]){
				return;
			}
			//Keep consecutive
			boolean canSwap = true;
			//if swapping is not possible for at least 2*minConsec intervals after i, set canSwap equal to false
			
			for(int n =i; n<(i+2*minConsec);n++){
				if(n >= numIntervals-1 || !(availabilitiesSchedule[p1][n]==1 && assignmentsSchedule[p1][n]==0 && assignmentsSchedule[p2][n]==1)){
					canSwap = false;
				}
			}
			if(canSwap){
		
				//if person2 is not assigned to the previous(chronologically previous) interval
				if(i==0 || assignmentsSchedule[p2][i-1]==0){
					for(int n = i;n<(i+minConsec);n++){
						swapSingleInterval(p1, p2, n);
					}
				}
			}
			
			
			
		}
	}
	
	public void swapFromEndIfPossible(int p1, int p2){
		for(int i = numIntervals-1;i>0;i--){
			
			if(assignPeopleSums[p1]>=idealSlotsArray[p1]|| assignPeopleSums[p2]<=idealSlotsArray[p2]){ //assignPeopleSums[p2]){
				return;
			}
			//Keep consecutive
			boolean canSwap = true;
			for(int n =i; n>(i-2*minConsec);n--){
				if(n <= 0 || !(availabilitiesSchedule[p1][n]==1 && assignmentsSchedule[p1][n]==0 && assignmentsSchedule[p2][n]==1)){
					canSwap = false;
				}
			}
			if(canSwap){
				
				//if person2 is not assigned to the previous(chronologically next) interval
				if(i==numIntervals-1 || assignmentsSchedule[p2][i+1]==0){
					for(int n = i;n<(i-minConsec);n--){
						swapSingleInterval(p1, p2, n);
					}
				}
			}
	
			
		}
	}
	
	
	/*
	 * swap from left end of consecutive (original)
	 * optimize equality
	 */
	public void swapOldBeg(int p1, int p2){
		for(int i = 0;i<numIntervals-1;i++){
			if(assignPeopleSums[p1]>=idealSlotsArray[p1] || assignPeopleSums[p2]<=idealSlotsArray[p2] ){ //assignPeopleSums[p2]){
				return;
			}
			//Optimize Equality

			if(i==0){ //1st interval special case

				//if person1 is free but not assigned to this interval && person2 is assigned to this interval
				if(availabilitiesSchedule[p1][i]==1 && assignmentsSchedule[p1][i]==0 && assignmentsSchedule[p2][i]==1){
					swapSingleInterval(p1, p2, i);
				}
			}
			else{
				//if person1 is free but not assigned to this interval
				if(availabilitiesSchedule[p1][i]==1 && assignmentsSchedule[p1][i]==0){
					//if person2 is assigned for this interval but not the previous(chronologically previous) interval
					if(assignmentsSchedule[p2][i]==1 && assignmentsSchedule[p2][i-1]==0){
						//switch intervals
						swapSingleInterval(p1, p2, i);

					}
				}
			}

			
		}
	}
	/*
	 * swap from right end of consecutive (original)
	 * optimize equality
	 */
	public void swapOldEnd(int p1, int p2){
		for(int i = numIntervals-1;i>0;i--){
			if(assignPeopleSums[p1]>=idealSlotsArray[p1]|| assignPeopleSums[p2]<=idealSlotsArray[p2]){ //assignPeopleSums[p2]){
				return;
			}
		
			//Optimize Equality
			
			if(i==numIntervals-1){ //1st interval special case
				
				//if person1 is free but not assigned to this interval && person2 is assigned to this interval
				if(availabilitiesSchedule[p1][i]==1 && assignmentsSchedule[p1][i]==0 && assignmentsSchedule[p2][i]==1){
					swapSingleInterval(p1, p2, i);
				}
			}
			else{
				//if person1 is free but not assigned to this interval
				if(availabilitiesSchedule[p1][i]==1 && assignmentsSchedule[p1][i]==0){
					//if person2 is assigned for this interval but not the previous(chronologically previous) interval
					if(assignmentsSchedule[p2][i]==1 && assignmentsSchedule[p2][i+1]==0){
						//switch intervals
						swapSingleInterval(p1, p2, i);
						
					}
				}
			}
			
			
		}
	}
	
	

	/*
	 * swap solos (original)
	 */
	
	public void swapRemainingSolos(){
		
	}
	/*
	 * returns the total number of solo intervals in assignmentsSchedule
	 */
	public void countSolos(int[][] schedule, String type){
		
		for(int p= 0; p<numPeople;p++){
			for(int i = 1;i<numIntervals-1;i++){
				//if person is assigned to i but not i+1 nor i-1
				if(schedule[p][i]==1 && schedule[p][i+1]==0 &&schedule[p][i-1]==0){
					if(type.equals("avail")) soloCountAvail++;
					if(type.equals("assign")) soloCountAssign++;
					
				}
			}
		}
	}
	/*
	 * Swap remaining solo intervals if possible
	 */
	public void swapSolos(){
		for(int p1 = 0; p1<numPeople;p1++){
			for(int i = 1; i<numIntervals - 2; i++) 
				if(assignmentsSchedule[p1][i] == 1 && assignmentsSchedule[p1][i+1] == 0 && assignmentsSchedule[p1][i-1] == 0){
					for(int p2=0;p2<numPeople;p2++){
						if(availabilitiesSchedule[p2][i]==1 && assignmentsSchedule[p2][i] == 0 && (assignmentsSchedule[p2][i+1] == 1 || assignmentsSchedule[p2][i-1] == 1)){
							swapSingleInterval(p2, p1, i);
							break;
						}
					}
				}

		}
	}
	

// Array Calculations
	
	
	
	/*
	 * Returns an array containing the sum of available or assigned slots (specified by typeOfSum) for each person
	 * The ith entry of the array contains the total number of slots that person i is available/assigned
	 */
	public int[]  calculatePeopleSums(int[] typeOfSum, int[][] grid){
		for(int r = 0; r<grid.length;r++){
			typeOfSum[r] = sumColumns(r, grid);
		}
		return typeOfSum;
	}
	
	public int sumColumns(int r, int[][] grid){
		int count = 0; 
		for(int c = 0; c<grid[0].length;c++){
			count+=grid[r][c];
		}
		//System.out.println(count);
		return count;
	}
	
	
	/*
	 * Returns an array containing the sum of available or assigned people (specified by typeOfSum) for each interval
	 * The ith entry of the array contains the total number of people available/assigned) for interval i 
	 */
	public int[]  calculateIntervalSums(int[] typeOfSum, int[][] grid){
		for(int c = 0; c<grid[0].length;c++){
			typeOfSum[c] = sumRows(c, grid);
		}
		return typeOfSum;
	}
	
	public int sumRows(int c, int[][]grid){
		int count = 0; 
		for(int r = 0; r<grid.length;r++){
			count+=grid[r][c];
		}
		//System.out.println(count );
		return count;
	}
	

	/*
	 * Generates array containing each person's ideal slots number
	 * i.e. the number of slots the would be assigned in an ideal scenario
	 * Stores array in idealSlotsArray
	 */
	public void generateIdealSlotsArray(){
		double idealSlotsPerAvailablePerson = idealSlotsPerPersonIfAllAvailable;
		double numPeopleLeft = numPeople;
		double numSlotsLeft = totalSlotsRequired;
		boolean changed = true;
		while(changed==true){
			changed = false;
			for(int i = 0; i<numPeople;i++){
				if(availPeopleSums[i] < idealSlotsPerAvailablePerson && idealSlotsArray[i]==0){
					idealSlotsArray[i] = availPeopleSums[i];
					numPeopleLeft--;
					numSlotsLeft-=idealSlotsArray[i];
					changed = true;

				}
			}
			if(numPeopleLeft>0){
				//System.out.println(numPeopleLeft);
				idealSlotsPerAvailablePerson = numSlotsLeft/(numPeopleLeft);
			}
		}
		calculateUpdatedIdealSlotsPerPerson(idealSlotsPerAvailablePerson);
		printArray(idealSlotsArray, "Ideal Slots Array");
	}
	/*
	 * Helper Method for generateIdealSlotsArray
	 */
	public void calculateUpdatedIdealSlotsPerPerson(double idealSlotsPerAvailablePerson){
		for(int i = 0;i<numPeople;i++){
			if(idealSlotsArray[i]==0){
				idealSlotsArray[i]=idealSlotsPerAvailablePerson;
			}
			//System.out.println(idealSlotsArray[i]);
		}
		
	}
	
	
	/*
	 * Calculates person's futureAvailConsec Array
	 * 	- each index value of futureAvailConsec represents the number of consecutive intervals 
	 * 	  that Person p is available directly after that interval (index)
	 */
	public void calculateFutureAvailConsec(Person p){
		int count = 0;
		String output = "]"; 
		for(int i = numIntervals-1; i>=0;i--){
			if(i==numIntervals-1){
				count=0;
			}
			else if(availabilitiesSchedule[p.getNumber()][i+1]==1){
				count++;
			}
			else{
				count=0;
			}
			p.futureAvailConsec[i]=count;
			output = count + " " + output;
			
		}
		output = "Person " + p.getNumber() + "'s futureAvailConsec Array: [" +output;
		
		//System.out.println(output);
	}
	
	/*
	 * Calculates person's futureAvailSums Array
	 * 	- each index value of futureAvailSums represents the number of intervals 
	 * 	  that Person p is available after that interval (index)
	 */
	public void calculateFutureAvailSums(Person p){
		int count = 0;
		String output = "]"; 
		for(int i = numIntervals-1; i>=0;i--){
			if(i==numIntervals-1){
				count=0;
			}
			else if(availabilitiesSchedule[p.getNumber()][i+1]==1){
				count++;
			}
			else{
				//don't change count
			}
			p.futureAvailConsec[i]=count;
			output = count + " " + output;
			
		}
		output = "Person " + p.getNumber() + "'s futureAvailSums Array: [" +output;
		
		//System.out.println(output);
	}
	
	
	/*
	 * Returns maximum value of an int array
	 */
	public double max(int[] array){
		double maxValue = array[0];
		for(int x= 0;x<array.length;x++){
			if(array[x]>maxValue){
				maxValue = array[x];
			}
			
		}
		return maxValue;
	}
	
	/*
	 * Returns maximum value of a double array
	 */
	public double max(double[] array){
		double maxValue = array[0];
		for(int x= 0;x<array.length;x++){
			if(array[x]>maxValue){
				maxValue = array[x];
			}
			
		}
		return maxValue;
	}
	
	/*
	 * Returns minimum value of an int array
	 */
	public double min(int[] array){
		double minValue = array[0];
		for(int x= 0;x<array.length;x++){
			if(array[x]<minValue){
				minValue = array[x];
			}
			
		}
		return minValue;
	}
	
	/*
	 * Returns minimum value of an double array
	 */
	public double min(double[] array){
		double minValue = array[0];
		for(int x= 0;x<array.length;x++){
			if(array[x]<minValue){
				minValue = array[x];
			}
			
		}
		return minValue;
	}
	/*
	 * Returns the sum of a double array
	 */
	public int sumOfArray(double[] array){
		double count=0;
		for(int i = 0;i<array.length;i++){
			count+=array[i];
		}
		return (int)count;
	}
	
// Rest is for debugging or testing only (not used in iOS app because there's a user interface)
	
	
	/*
	 * Prompts the user to enter each person's availabilities
	 */
	public void enterAvailabilitiesSchedule(){
		for(int r = 0; r<availabilitiesSchedule.length; r++){
			for(int c = 0;c<availabilitiesSchedule[0].length;c++){
				System.out.println("Enter number for Person: " + r + " Interval: " + c);
				int x = input.nextInt();
				availabilitiesSchedule[r][c]=x;
				//System.out.println(availabilitiesSchedule[r][c]);
			}
		}
		
	}
	/*
	 * prints an array
	 */
	public void printArray(int[] array, String name){
		String output = name + ": [";
		for(int i = 0; i<array.length;i++){
			output+=array[i]+ " ";
		}
		output+="]";
		System.out.println (output);
	}
	
	public void printArray(double[] array, String name){
		String output = name + ": [";
		for(int i = 0; i<array.length;i++){
			output+=array[i]+ " ";
		}
		output+="]";
		System.out.println (output);
	}
	
	/*
	 * prints the availabilities schedule (matrix)
	 */
	public void printAvailabilitiesSchedule(){
		String output = "Availabilities Schedule: \n";
		for(int r = 0; r<availabilitiesSchedule.length; r++){
			output += "\n Person: " + r;
			if(r<10) output+= " ";
			output+= " [";
			for(int c = 0;c<availabilitiesSchedule[0].length;c++){
				output+=availabilitiesSchedule[r][c];
				output+= " ";
				
			}
			output+="]";
			
		}
		System.out.println(output);
	}
	
	/*
	 * prints the assignment schedule (matrix)
	 */
	public void printAssignmentSchedule(){
		String output = "Assignment Schedule: \n";
		for(int r = 0; r<assignmentsSchedule.length; r++){
			output += "\n Person: " + r;
			if(r<10) output+= " ";
			output+= " [";
			for(int c = 0;c<assignmentsSchedule[0].length;c++){
				output+=assignmentsSchedule[r][c];
				output+= " ";
				
			}
			output+="]";
			
		}
		System.out.println(output);
	}

	public void printClockAvailabilitiesWRTIntervals(){
		String output = "Availability Schedule: \n";
		for(int c = 0;c<availabilitiesSchedule[0].length;c++){
			output += hours[c%12] + minutes[c%4];
			if(hours[c%12]<10)output+=" ";
			output+= " - " ;
			output+= hours[(c+1)%12] + minutes[(c+1)%4];
			if(hours[(c+1)%12]<10)output+=" ";
			output += " [";
			for(int r = 0; r<availabilitiesSchedule.length; r++){
				if(availabilitiesSchedule[r][c]==1){
					output+="Person: " + r + ", ";
				}
			}
			output+="] \n";
			
		}
		System.out.println(output);
	}
	public void printClockAssignmentsWRTIntervals(){
		String output = "Assignment Schedule: \n";
		for(int c = 0;c<assignmentsSchedule[0].length;c++){
			output += hours[c%12] + minutes[c%4];
			if(hours[c%12]<10)output+=" ";
			output+= " - " ;
			output+= hours[(c+1)%12] + minutes[(c+1)%4];
			if(hours[(c+1)%12]<10)output+=" ";
			output += " [";
			for(int r = 0; r<assignmentsSchedule.length; r++){
				if(assignmentsSchedule[r][c]==1){
					output+="Person: " + r + ", ";
				}
			}
			output+="] \n";
			
		}
		System.out.println(output);
	}
	public void printSums(){
		
		

		// Print Arrays
			printArray(availPeopleSums, "availPeopleSums");
			printArray(availIntervalSums, "availIntervalSums");
			printArray(assignIntervalSums, "assignIntervalSums");
			printArray(assignPeopleSums, "assignPeopleSums");
		// Print Statistics
			System.out.println("soloCountAvail: " + soloCountAvail);
			System.out.println("soloCountAssign: " +soloCountAssign);
	
	}

}

// Original algorithm, (before priority queue) first looped through each interval, assigning those intervals with the minimum number of people 
// available, as well as assigning slots to people with many consecutive intervals
		
	/*
	public void generateSchedule(){
		
		// Generate Assignment Schedule 
		 
			// Assign intervals with min available and consec intervals between 5 and 10 first
			assignIntervalsWithMinNumAvailable();
			assignConsecutiveIntervals();
			assignRestOfIntervalsUsingQueue();
			
			
		// Calculate Sums
			assignIntervalSums = calculateIntervalSums(assignIntervalSums, assignmentsSchedule);
			assignPeopleSums = calculatePeopleSums(assignPeopleSums, assignmentsSchedule);
			
			
		// Swap
			swapAll();
			
			
		// Swap Remaining solo intervals	
			//swapSolos();
			
			
		// Calculate sums
				assignIntervalSums = calculateIntervalSums(assignIntervalSums, assignmentsSchedule);
				assignPeopleSums = calculatePeopleSums(assignPeopleSums, assignmentsSchedule);
				
				//print sums
				System.out.println("After swapping");
				printSums();
		
	}
	*/
	
	/*
	 * Assign slots to people in intervals that have the minimum 
	 * number of people available to meet requirement
	 * (I might take this out. I currently just have it as part of assignIntervals
	 */
	/*
	public void assignIntervalsWithMinNumAvailable(){
		for(int i= 0;i<numIntervals;i++){
			if(availIntervalSums[i]==PPI){
				for(int p = 0;p<numPeople;p++){
					if(availabilitiesSchedule[p][i]==1){
						assignmentsSchedule[p][i]=1;
						
					}
				}
			}
		}
		
	}*/


/*
 * Assign intervals using queue. If min # available, assign all. Otherwise, assign people at front of queue
 * Don't really need to assign the min # first since the priority queue will do that anyway, so assignIntervals2()
 * get's rid of the if statement
 */
/*
public void assignIntervals2(){
	for(int i = 0;i<numIntervals;i++){
		eachIntervalArrayList.clear();
		//if min number available
		if(availIntervalSums[i]==PPI){
			for(int p = 0;p<numPeople;p++){
				if(availabilitiesSchedule[p][i]==1){
					assignmentsSchedule[p][i]=1;
					Person person = personArrayList.get(p);
					consecutiveQueue.remove(person);
					person.setNumConsecutive(person.getNumConsecutive()+1);
					person.setCurrentAssignedSums(person.getCurrentAssignedSums()+1);
					person.setNumInterval(i+1);
					personArrayList.add(p, person);
					consecutiveQueue.add(person);
					
				}
			}
		}
		
		else{//more than min number available{
			//assign based on queue
			int count = 0;
			for(int n = 0; n<numPeople;n++){
				Person person = consecutiveQueue.remove();
				
				
				if(count<PPI){
					if(availabilitiesSchedule[person.getNumber()][i]==1){
						assignmentsSchedule[person.getNumber()][i]=1;
						person.setCurrentAssignedSums(person.getCurrentAssignedSums()+1);
						person.setNumConsecutive(person.getNumConsecutive()+1);
						
						count++;
					}
					else{
						person.setNumConsecutive(0);
					}
				}
				else{
					person.setNumConsecutive(0);
					
				}
				if(i<numIntervals-1) person.setNumInterval(i+1);
				personArrayList.add(person.getNumber(), person);
				eachIntervalArrayList.add(person);
			}
			for(int n = 0; n<eachIntervalArrayList.size();n++){
				Person person = eachIntervalArrayList.get(n);
				consecutiveQueue.add(person);
			}
			
		}
		
	}
}
*/

	
	
