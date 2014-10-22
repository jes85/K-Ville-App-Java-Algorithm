import java.util.Comparator;


public class PersonComparator implements Comparator<Person>{
	private int maxConsec=10; // maximum number of consecutive time intervals allowed to be assigned to a group member
	private int minConsec;
	@Override
	public int compare(Person person1, Person person2){
		/*rank by
			1. Most consecutive previous intervals assigned (if less than 10)
			2. Most consecutive future intervals available
			3. Least current assigned intervals
			4. Least future available intervals 
			5. Least total available intervals
			
			maybe have something where if they're available for less than ideal, they are assigned all of their available intervals
			*/
		
		int consecDiff = person2.getNumConsecutive() - person1.getNumConsecutive();
		if(person1.getNumConsecutive()>=maxConsec && person2.getNumConsecutive()<maxConsec || person1.getNumConsecutive()<maxConsec && person2.getNumConsecutive()>=maxConsec){
			return -1*consecDiff; //if one of them is greater than the maxConsec, the lower one has higher priority
		}
		if(consecDiff==0){
			int futureAvailConsecDiff = person2.futureAvailConsec[person2.getNumInterval()] - person1.futureAvailConsec[person1.getNumInterval()];
			if(futureAvailConsecDiff==0){
				int currentAssignedSumDiff = person1.getCurrentAssignedSums() - person2.getCurrentAssignedSums();
				if(currentAssignedSumDiff == 0){

					//compare future available sums (less is higher priority)
					int futureAvailSumsDiff = person1.futureAvailSums[person1.getNumInterval()] - person2.futureAvailSums[person2.getNumInterval()];
					if(futureAvailSumsDiff==0){

						int availSumDiff = person1.getAvailSums() - person2.getAvailSums();
						if(availSumDiff==0){


						}
						return availSumDiff;
					}

					return futureAvailSumsDiff;
				}
				return currentAssignedSumDiff;
			}
			return futureAvailConsecDiff;
		}
		return consecDiff;

	}
	
	


}
