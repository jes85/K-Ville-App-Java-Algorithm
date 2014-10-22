
public class RunSchedule {

	public static void main(String[] args) {
		AvailabilitySchedule sched = new AvailabilitySchedule(); //generate random schedule (easier for testing)
		Schedule tent = new Schedule(sched); // algorithm
		
		//tent.enterAvailabilitiesSchedule(); // to enter schedule manually
		
		// For more debugging
		
		//tent.printArray(tent.assignPeopleSums, "assignPeopleSums");
		//tent.printAvailabilitiesSchedule();
		//tent.printAssignmentSchedule();
		//tent.printClockAssignmentsWRTIntervals();
		//tent.printClockAvailabilitiesWRTIntervals();
		
	
	}

}
