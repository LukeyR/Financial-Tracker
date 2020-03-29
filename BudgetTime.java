import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class to take Budget input and store it
 * @author Sam
 */

public class BudgetTime {

	/**
	 * Set recurring budget over a time period
	 * @throws IOException
	 * 		Handled in main along with other IO exceptions to reduce error handling code
	 */
	private void addBudget() throws IOException {
		int numberOfDays = 	timeInput(); //Gets time input from user and converts to days
		if (numberOfDays == 0) {
			return; //Quit if time input failed
		}
		int budgetAmount = 0; //Variable to hold the budget before it is stored
		String input;

		System.out.println("Please enter a budget (�) for the selected time period");
		input = App.userIn.readLine();
		if (!Validation.isInteger(input)) {
			return;
		}
		budgetAmount = Integer.parseInt(input);

		RetrieveAndStore.sqlExecute("INSERT INTO tblBudget (BudgetAmount, NumberOfDays) VALUES (" + budgetAmount + ", " + numberOfDays + ")");
		//Write SQL statement here then pass to method
		System.out.println("Success a budget has been set for �" + budgetAmount + " every " + numberOfDays + " days!");
	}

	/**
	 * Print the budgets from the database to the user
	 */
	private void printBudgets() {
		ResultSet rs = RetrieveAndStore.readAllRecords("tblBudget");
		try {
			while (rs.next()) //Loop through the resultset
			{
				//Store each budget record to print
				int id = rs.getInt("BudgetID");
				int budget = rs.getInt("BudgetAmount");
				int days = rs.getInt("NumberOfDays");

				// print the results
				System.out.format("%s. Budget amount = �%s, Number of days = %s\n", id, budget, days);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Delete a stored budget
	 * @throws IOException
	 * 		Handled in main along with other IO exceptions to reduce error handling code
	 */
	private void deleteBudget() throws IOException {
		printBudgets(); //Print all budgets to the console
		System.out.println("Please enter a record number to delete");
		String input = App.userIn.readLine();
		if (!Validation.isInteger(input)) {
			return;
		}
		if (!Validation.isRangeValid(1, RetrieveAndStore.maxID("tblBudget", "BudgetID"), Integer.parseInt(input))) {
			return;
		}
		RetrieveAndStore.sqlExecute("DELETE FROM tblBudget WHERE BudgetID = '" + input + "'"); //Call method to execute deletion
		System.out.format("Record %s deleted successfully\n", input); //Tell the user record has been removed		
	}

	/**
	 * Amend a stored budget
	 * @throws IOException
	 * 		Handled in main along with other IO exceptions to reduce error handling code
	 */
	private void amendBudget() throws IOException {
		printBudgets(); //Print all budgets to the console
		System.out.println("Please enter a record number to amend");
		String input = App.userIn.readLine();
		if (!Validation.isInteger(input)) { //Recordnumber input validated
			return;
		}
		int recordNumber = Integer.parseInt(input); 
		if (!Validation.isRangeValid(1, RetrieveAndStore.maxID("tblBudget", "BudgetID"), recordNumber)) {
			return;
		}
		System.out.println("Would you like to change:\n 1.budget\n 2.timeframe?"); //Find out what the user wants to amend
		input = App.userIn.readLine();
		switch (input) {
		case "1": //Switch statement to deal with both cases of amendments
			System.out.println("Please enter an amount (�)");
			input = App.userIn.readLine();
			if (!Validation.isInteger(input)) {
				return;
			}
			int amount = Integer.parseInt(input);
			RetrieveAndStore.sqlExecute("UPDATE tblBudget SET BudgetAmount = " + amount + " WHERE BudgetID = " + recordNumber);
			break;
		case "2":
			int days = timeInput();
			RetrieveAndStore.sqlExecute("UPDATE tblBudget SET NumberOfDays = " + days + "' WHERE BudgetID = " + recordNumber);
			break;
		default:
			System.out.println("Not an option, try again"); //Filters out invalid input
		}
	}

	/**
	 * Take input of time period and convert to days
	 * @return time period for goal in days
	 * @throws IOException
	 * 		Handled in main along with other IO exceptions to reduce error handling code
	 */
	private int timeInput() throws IOException {
		int timePeriod = 0;
		int lengthTime = 0;
		String input; //To hold input from user via console

		System.out.println("Would you like to input your budget in: \n 1.Days \n 2.Calendar Months \n 3.Years");
		input = App.userIn.readLine();
		if (!Validation.isInteger(input)) {
			return 0;
		}	
		timePeriod = Integer.parseInt(input);
		if (!Validation.isRangeValid(1 , 3, timePeriod)) {
			return 0;
		}

		System.out.println("Please enter the number of days/months/years you would like this goal for");
		input = App.userIn.readLine();
		if (!Validation.isInteger(input)) {
			return 0;
		}	
		lengthTime = Integer.parseInt(input);

		//if statement to normalise input to number of days ready to be stored
		if (timePeriod == 1) {
			return lengthTime;
		} else if (timePeriod == 2) {
			lengthTime *= 28;
			return lengthTime;
		} else if (timePeriod == 3) {
			lengthTime *= 365;
			return lengthTime;
		}
		return 0;
	}

	/**
	 * Called from the main program to give user budget options
	 * Handles all IOExceptions thrown at other points in class
	 * @throws IOException 
	 */
	public void mainMenu() throws IOException {
		boolean loop = true;
		while(loop) {
			System.out.println("Would you like to:\n 1. Add a budget\n 2. Remove a budget\n 3. Amend a budget\n 4. View all budgets\n 5. Quit to main menu");
			String input = App.userIn.readLine();

			switch(input) { //Use user input to decide which action to complete
			case "1":
				addBudget();
				break;
			case "2":
				deleteBudget();
				break;
			case "3":
				amendBudget();
				break;
			case "4":
				printBudgets();
				break;
			case "5":
				loop = false;
			default: //Filter out invalid inputs
				System.out.println("Not an option, try again");
			}
		}

	}

}
