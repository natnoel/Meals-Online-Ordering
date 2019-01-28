/*
 * Name: Tan Shi Terng Leon
 * Student No.: 4000602
 * */
import java.util.*;
import java.text.DecimalFormat;
import java.io.*;

public class Assignment1 {
	
	//Available main courses
	final static String[] MAINCOURSE = {"Fish and Chips", "Sirloin Steak", "Lamb Chop", "Grilled Fish", "Seafood Pasta",
		"Carbonara", "Pepperoni Pizza", "Hawaiian Pizza", "Curry Chicken", "Fried Rice"};
	
	//Available side-dishes
	final static String[] SIDEDISHES = {"Soup", "Orange Juice", "Coffee"};
	
	//Available delivery times
	final static String[] DELIVERYTIME = {"11am-1pm", "5pm-7pm", "7pm-8pm"};
	
	//To collect or deliver
	enum Status {COLLECT, DELIVER};
	static Status status;
	
	static Scanner scanner = new Scanner (System.in);						//To get input from user
	static Scanner fileScanner;												//To read from file
	static FileWriter outfile;												//To write to file
	static int option = 0;													//For integer value options
	static char choice = '\0';												//For character options
	static String inputStr = null, username, passwordAttempt, custRecord;
	static ArrayList<SetMeal> orderList = new ArrayList<SetMeal>();			//Stores order to be made
	static ArrayList<SetMeal> checkOrderList = new ArrayList<SetMeal>();	//Stores order user wants to track
	static int noOfReg, noOfLarge, noOfSide, side, loopCount;
	static DecimalFormat dollarCent = new DecimalFormat("0.00");			//Format for money (dollars and cents)
	static DecimalFormat orderFormat = new DecimalFormat("000000000");		//Format for order ID
	static boolean valid;
	static String name, password, address, contact;
	static int date, month, year;
	static int orderID = 1;
	static String deliveryTime, collectionTime, checkOrderID, mainCourse, sideDish, mealSize;
	static String deliveryDate, deliveryStat;
	
	public static void main(String[] args) {
		
		generateOrderID();
		
		do {
			//Main menu
			getMainMenuOption();
		
			scanner.nextLine();	//Clears the input buffer
			
			System.out.println();
		
		switch (option) {
		
		case 1:		//Place Order/Change Order
			do {
				displaySetMealsMenu();
				
				getSetMealsMenuOption();
				
				switch (choice) {
				case 'a':		//Select a main course and the number of regular and large meals
							
							orderList.clear();	//Resets the order
							
							//Enter the Main Course
							getMainCourseOption();
							
							//Enter number of regular size meal
							getRegular();
								
							//Enter number of large size meal
							getLarge();
							
							//Adds regular meals into order
							for (int i = 0; i < noOfReg; i++) {
								orderList.add(new SetMeal (MAINCOURSE[option - 1], "Regular"));
							}
							
							//Adds large meals into order
							for (int i = 0; i < noOfLarge; i++) {
								orderList.add(new SetMeal (MAINCOURSE[option -1], "Large"));
							}
							
							//Displaying Main Courses ordered
							System.out.println("\nYou have ordered: ");
							for (int i = 0; i < orderList.size(); i++)
							{
								System.out.println("Meal " + (i + 1) + ": " + orderList.get(i).getMainCourse() +
										", " + orderList.get(i).getMealSize());
							}
							
							System.out.println("\nPress enter to continue...");
							scanner.nextLine();
							
							break;
							
				case 'b':	//Gets the side-dishes
					
					if (orderList.isEmpty())	//If no main course selected yet
						System.out.println("Please enter your main course first!\n" +
								"Press Enter to continue...");
					else {
						//Resets all the side-dishes
						for (SetMeal meal : orderList) {
							meal.clearSideDishes();
						}
						
						//Enters side-dishes
						getSideDishes();
						
						//Displays side-dishes ordered
						for (int i = 0; i < orderList.size(); i++) {	//For each meal
							System.out.println("\nSide-dishes for Meal " + (i + 1) + " - " + orderList.get(i).getMainCourse() +
									", " + orderList.get(i).getMealSize() + ":");
							
							//Displays side-dishes for this meal
							for (int j = 0; j < orderList.get(i).numOfSideDishes(); j++) {
								System.out.println("\tSide-dish " + (j + 1) + ": " + orderList.get(i).getSideDish(j));
							}
						}
						System.out.println();
					}
					
					break;
							
				default:	System.out.println("Going back to Main Menu...\n");
				}
				
				if (choice != 'c' && choice != 'C')
					scanner.nextLine();
				
			} while (choice != 'c' && choice != 'C');
			
			break;
				
		case 2:		//Check Out
			
			if (orderList.isEmpty())	//If no order placed
				System.out.println("You have yet to place an order!");
			else {
				displayOrder(orderList);
				displaySummary(orderList);
				
				System.out.print("To change your order, select Option 1 on the Main Page\n" +
						"To proceed with order, select Option 3 or 4 on the Main Page\n\n");
			}
			
			break;
			
		case 3:		//Existing Customer
			
			try {
				if (orderList.isEmpty())	//If no order has been placed
					throw new Exception("Please place an order first!");
				
				//Reading from "customerinfo.txt"
				fileScanner = new Scanner(new File("customerinfo.txt"));
				
				//Enter username
				System.out.print("Username: ");
				username = scanner.nextLine().trim();
				
				//If username does not exist
				if (!custRecordExist(username))
					throw new Exception("No such user!");
				
				//If username exists, extract the customer info
				String[] custInfo = custRecord.split(";");
				name = custInfo[0];
				password = custInfo[2];
				address = custInfo[3];
				contact = custInfo[4];
				
				//Enter password
				enterPassword();
				
				//Display greeting message
				greetingMessage();
				
				//Determines whether user will collect or ask for delivery
				getCollectOrDeliverOption();
				
				scanner.nextLine();	//Flushes input buffer
				
				System.out.println();
				
				switch (option) {
				case 1:
					getCollectionDetails();
					break;
					
				case 2:
					getDeliveryDetails();
					break;
				}
				
				recordOrder();
				recordOrderMeals();
				orderID++;		//Sets (increments) order ID for the next order
				orderList.clear();	//Clears order stored
			}
			catch (FileNotFoundException e) {	//If file not found
				System.out.println("Error! " + e.getMessage());
			}
			catch (Exception e) {	//Any other exceptions thrown
				System.out.println("Error! " + e.getMessage());
			}
			
			break;
				
		case 4:		//New Customer
			
			if (orderList.isEmpty())	//If no order placed
				System.out.println("Please place an order first!");
			else {
				registerNewCustomer();
				getCollectOrDeliverOption();
				
				scanner.nextLine();	//Flushes input buffer
				
				System.out.println();
				
				switch (option) {
				case 1:
					getCollectionDetails();
					break;
					
				case 2:
					getDeliveryDetails();
					break;
				}
				recordOrder();
				recordOrderMeals();
				orderID++;	//Sets (increments) order ID for the next order
				orderList.clear();	//Clears order stored
			}
			break;
			
		case 5:		//Track order
			
			checkOrderList.clear();		//Clears the current list
			noOfReg = noOfLarge = 0;	//Initializes variables
			
			getOrderID();
			
			if (findOrderID(checkOrderID)) {	//If order exist
				
				readMeals();
				displayOrderStatus();
				
				if (!checkOrderList.isEmpty()) {
					displayOrder(checkOrderList);
					displaySummary(checkOrderList);
				}
			}
			else	//If order does not exist
				System.out.println("Error: Order ID not found!");
			break;
				
		default:
		}
			System.out.println();
			
			//Resets option
			if (option != 6)
				option = 0;
			
		} while (option != 6);
		
		System.out.println("Thank You. See you again!");
		scanner.close();
	}
	
	//Generates a starting orderID
	static void generateOrderID() {
		try {
			fileScanner = new Scanner(new File("orderData.txt"));
			int max = 0;
			int currID;
			
			//Gets the largest order ID stored in records
			while (fileScanner.hasNextLine()) {
				inputStr = fileScanner.nextLine();
				
				String[] orderInfo = inputStr.split(";");
				currID = Integer.parseInt(orderInfo[0]);
				
				if (currID > max) {
					max = currID;
				}
			}
			
			//Sets the current orderID to +1 the larger orderID found
			orderID = max + 1;
		}
		catch (FileNotFoundException e) {
		}
	}
	
	//Gets option from main menu
	static void getMainMenuOption() {
		do {
			displayMainMenu();
			
			try {
				if (scanner.hasNextInt())		//Input ok
					option = scanner.nextInt();
				else							//Input value is not an integer
					throw new Exception ("Invalid option! Please enter an integer");
					
				if (option < 1 || option > 6)	//Out of range
					throw new Exception("Invalid option! " +
							"Please enter an option between 1 and 6 inclusive!");
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				scanner.nextLine();
			}
			
		} while (option < 1 || option > 6);
	}
	
	//Check if order exists in records
	//Stores extracts all information of the record and returns true if found
	//Returns false if not found
	static boolean findOrderID(String orderID) {
		boolean found = false;
		
		try {
			fileScanner = new Scanner(new File("orderData.txt"));
			
			while (fileScanner.hasNextLine()) {
				inputStr = fileScanner.nextLine();
				
				String[] orderInfo = inputStr.split(";");
				
				if (orderInfo[0].equals(orderID)) {	//If order ID is found
					
					username = orderInfo[1];		//Stores username
					deliveryDate = orderInfo[2];	//Stores delivery/collection date
					deliveryTime = orderInfo[3];	//Stores delivery/collection time
					deliveryStat = orderInfo[4];	//Stores deliver/collection status
					
					found = true;
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		return found;
	}
	
	//Displays the status of order
	static void displayOrderStatus() {
		System.out.print("Username: " + username + "\nDelivery Date: " + deliveryDate + 
				"\nDelivery Time: " + deliveryTime + "\nStatus: " + deliveryStat + "\n\n");
	}
	
	//Gets the Order ID
	static void getOrderID() {
		do {
			System.out.print("Please enter your order ID: ");
			checkOrderID = scanner.nextLine().trim();
			
		} while (!validOrderID(checkOrderID));
	}
	
	//Checks for valid Order ID
	static boolean validOrderID(String orderID) {
		boolean valid = true;
		
		try {
			option = Integer.parseInt(orderID);	//Throws NumberFormatException if not an integer
			
			if (orderID.length() != 9) {		//If it doesn't contain 9 digits
				System.out.println("Invalid Order ID! Please enter a 9 digit number!");
				valid = false;
			}
		}
		catch (NumberFormatException e) {
			System.out.println("Invalid Order ID! Please enter a numerical value!");
			valid = false;
		}
		
		return valid;
	}
	
	//Gets Set Meals Menu option
	static void getSetMealsMenuOption() {
		do {
			try {
				System.out.print("Please enter your option (a, b or c): ");
				
				if (scanner.hasNextLine())
					inputStr = scanner.nextLine().trim();

				if (inputStr.length() > 1)			//More than one character
					throw new Exception("Enter only one character!");
				else if (inputStr.length() == 0)	//Nothing entered
					throw new Exception("Please enter a character!");
				
				choice = inputStr.charAt(0);
				
				if (choice != 'a' && choice != 'b' && choice != 'c')	//If not 'a', 'b' or 'c'
					throw new Exception("Please enter only 'a', 'b' or 'c'!");
			}
			catch (Exception e) {
				System.out.println("Invalid entry! " + e.getMessage());
				choice = 'z';
			}
		} while (choice != 'a' && choice != 'b' && choice != 'c');
		
	}
	
	//Gets option for Main Course
	static void getMainCourseOption() {
		do {
			try {
				System.out.print("Enter Set Meal options (Enter '1' to '10): ");
				
				if (scanner.hasNextInt())
					option = scanner.nextInt();
				else							//Not an integer
					throw new Exception("Please enter an integer value!");
				
				if (option < 1 || option > 10)	//Out of range
					throw new Exception("Please enter a value between '1' to '10' inclusive!");
			}
			catch (Exception e) {
				System.out.println("Invalid option! " + e.getMessage());
				option = -1;
				scanner.nextLine();
			}
		} while (option == -1);
	}
	
	//Gets the number of regular meals
	static void getRegular() {
		loopCount = 0;
		do {
			try {
				System.out.print("Number of regular size meal (maximum 10): ");
				
				if (scanner.hasNextInt())
					noOfReg = scanner.nextInt();
				else								//Non integer value detected
					throw new Exception("Please enter an integer value!");
				
				if (noOfReg < 0 || noOfReg > 10)	//Out of range
					throw new Exception("Please enter a value between '0' to '10' inclusive!");
			}
			catch (Exception e) {
				System.out.println("Invalid option! " + e.getMessage());
				noOfReg = -1;
				
				scanner.nextLine();
				
				//Ensures input buffer is cleared
				if (loopCount == 0 && e.getMessage().equals("Please enter an integer value!"))
					scanner.nextLine();
			}
			loopCount++;
		} while (noOfReg == -1);
	}

	//Gets number of large meals
	static void getLarge() {
		loopCount = 0;
		do {
			try {
				System.out.print("Number of large size meal (maximum 10): ");
				
				if (scanner.hasNextInt())
					noOfLarge = scanner.nextInt();
				else									//If not an integer
					throw new Exception("Please enter an integer value!");
				
				if (noOfLarge < 0 || noOfLarge > 10)	//If out of range
					throw new Exception("Please enter a value between '0' to '10'!");
			}
			catch (Exception e) {
				System.out.println("Invalid option! " + e.getMessage());
				noOfLarge = -1;
				
				//Ensures input buffer is cleared
				if (loopCount == 0 && e.getMessage().equals("Please enter an integer value!"))
					scanner.nextLine();
				scanner.nextLine();
			}
			loopCount++;
		} while (noOfLarge == -1);
	}
	
	//Gets the side-dishes for each meal
	static void getSideDishes() {
		
		for (int i = 0; i < orderList.size(); i++) {
			
			do {
				System.out.print("Enter number of side-dishes for ");
				if (i < noOfReg)
					System.out.print("Regular Meal " + (i + 1) + ": ");
				else
					System.out.print("Large Meal " + (i - noOfReg + 1) + ": ");
				
				try {
					if (!scanner.hasNextInt())		//If not an integer
						throw new Exception("Please enter an integer value!");
					option = scanner.nextInt();
					
					if (option < 0 || option > 3)	//If out of range
						throw new Exception("Please enter a value between 0 to 3 inclusive!");
				}
				catch (Exception e) {
					System.out.println("Invalid option! " + e.getMessage());
					option = -1;
					
					scanner.nextLine();
				}
				
			} while (option == -1);
			
			//For the number of side-dishes a meal
			for (int j = 0; j < option; j++) {
				
				//Adds a side-dish
				loopCount = 0;
				do {
					System.out.print("\tEnter side-dish " + (j + 1) + ": ");
					try {
						if (!scanner.hasNextInt())	//If not an integer
							throw new Exception("Please enter an integer!");
						side = scanner.nextInt();
						
						if (side < 1 || side > 3)	//If out of range
							throw new Exception("Please enter a value between 1 and 3 inclusive!");
					}
					catch (Exception e) {
						System.out.println("Invalid option! " + e.getMessage());
						side = -1;
						
						//Ensures input buffer is cleared
						if (loopCount == 0 && e.getMessage().equals("Please enter an integer!"))
							scanner.nextLine();
						scanner.nextLine();
					}
					loopCount++;
				} while (side == -1);
				
				//Adds side-dish to the orderList
				orderList.get(i).addSideDish(SIDEDISHES[side - 1]);
			}
			
			//Clears the input buffer
			if (i < (orderList.size() - 1))
				scanner.nextLine();
		}
	}
	
	//Choosing to collect or ask for delivery
	static void getCollectOrDeliverOption() {
		do {
			System.out.print("Please state self-collect or use delivery service " +
				"(option 1 for self-collect and option 2 for delivery service): ");
			try {
				if (!scanner.hasNextInt())
					throw new Exception("Please enter an integer");
				option = scanner.nextInt();
				
				if (option < 1 || option > 2)
					throw new Exception("Please enter a 1 or 2 only!");
			}
			catch (Exception e) {
				System.out.println("Invalid option! " + e.getMessage());
				option = -1;
				
				scanner.nextLine();
			}
		} while (option == -1);
	}
	
	//Displays the order
	static void displayOrder(ArrayList<SetMeal> orderList) {
		System.out.print(
				"Your Order\n" +
				"==========\n" + 
				orderList.get(0).getMainCourse() + ": " + noOfReg + " Regular at $10.00 each and " + 
				noOfLarge + " Large at $13.00 each\n");
		
		//Prints each order
		for (int i = 0; i < orderList.size(); i++) {
			System.out.print(orderList.get(i).getMainCourse());
			
			if (i < noOfReg)
				System.out.print(" Regular " + (i + 1));
			else
				System.out.print(" Large " + (i + 1 - noOfReg));
			
			System.out.print(": " + orderList.get(i).displaySideDishes() + "\n");
		}
	}
	
	//Display the order summary
	static void displaySummary(ArrayList<SetMeal> orderList) {
		
		double totalPrice = calculateTotalPrice(orderList);	//Computes total price
		
		System.out.print(
				"\nTotal number of set meals: " + orderList.size() + "\n" +
				"Total price is: $" + dollarCent.format(totalPrice) + "\n");
		
		if (totalPrice < 50)
			System.out.println("As the total price of your order is less than $50.00, " +
					"you will need to pay an additional $8.00 for delivery or " +
					"you can opt to pick up the set meals at the shop.");
	}
	
	//Computes total price for the order
	static double calculateTotalPrice(ArrayList<SetMeal> orderList) {
		double totalPrice = 0;
		
		for (SetMeal meal : orderList) {	//For each meal in the order
			//Sums up the cost of all the meals
			totalPrice += meal.computeCost();
		}
		
		return totalPrice;
	}
	
	//Allows user to enter password and system validates password
	static void enterPassword() throws Exception {
		loopCount = 0;
		do {
			System.out.print("Password: ");
			passwordAttempt = scanner.nextLine().trim();
			
			//If password don't match, prints number of tries left
			if (!password.equals(passwordAttempt))
				System.out.println("Invalid password! Number of tries left: " + (3 - loopCount - 1));
			
			loopCount++;
			
		} while (!password.equals(passwordAttempt) && loopCount < 3);
		
		if (!password.equals(passwordAttempt) && loopCount == 3)	//If out of tries
			throw new Exception("Out of tries!");
	}
	
	//Checks if a username exists in the customer records
	//(used for existing customers logging into the system)
	static boolean custRecordExist(String uname) {
		
		while (fileScanner.hasNextLine()) {
			inputStr = fileScanner.nextLine();
			
			String custInfo[] = inputStr.split(";");
			
			if (custInfo[1].equals(uname)){
				custRecord = inputStr;
				return true;
			}
		}
		
		return false;
	}
	
	//Writes order details into file
	static void recordOrder() {
		try {
			outfile = new FileWriter(new File("orderData.txt"), true);
			
			outfile.write(orderFormat.format(orderID) + ";" + username + ";" + 
					date + "-" + month + "-" + year + ";");
			
			if (status == Status.COLLECT)
				outfile.append(collectionTime + ";" + "pending for collection\n");
			else
				outfile.append(deliveryTime + ";" + "pending for delivery\n");
			
			outfile.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Error! " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("Error! " + e.getMessage());
		}
	}
	
	//Writes order meal details into file
	static void recordOrderMeals() {
		try {
			outfile = new FileWriter(new File("orderMeals.txt"), true);
			
			for (SetMeal meal : orderList) {
				outfile.write(orderFormat.format(orderID) + ";" + meal.getMainCourse() + ";" + meal.getMealSize() + 
						";" + meal.numOfSideDishes());
				for (int j = 0; j < meal.numOfSideDishes(); j++) {
					outfile.write(";" + meal.getSideDish(j));
				}
				outfile.write("\n");
			}
			
			outfile.close();
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	//Read meal details from file matching the order ID
	static void readMeals() {
		try {
		fileScanner = new Scanner(new File("orderMeals.txt"));
		
		while (fileScanner.hasNextLine()) {
			inputStr = fileScanner.nextLine();
			String[] mealInfo = inputStr.split(";");
			
			//If order ID found
			if (mealInfo[0].equals(checkOrderID)) {
				mainCourse = mealInfo[1];					//Stores main course
				mealSize = mealInfo[2];						//Stores meal size
				noOfSide = Integer.parseInt(mealInfo[3]);	//Stores number of side-dishes
				
				SetMeal meal = new SetMeal(mainCourse, mealSize);	//Creates new SetMeal object
				
				for (int i = 0; i < noOfSide; i++) {	//Adds side-dishes to the SetMeal object
					meal.addSideDish(mealInfo[4 + i]);
				}
				checkOrderList.add(meal);				//Adds the SetMeal object to the list
				
				if (mealSize.equals("Large"))			//Counts number of large meals
					noOfLarge++;
				else if (mealSize.equals("Regular"))	//Counts number of regular meals
					noOfReg++;
			}
		}
		fileScanner.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	//Gets date and time for collection
	static void getCollectionDetails() {
		status = Status.COLLECT;
		do {
			//Enters date
			do {
				System.out.print("Please enter the collection date (dd-mm-yyyy)\n" +
						"Collection Date: ");
				inputStr = scanner.nextLine();
				
				String[] dateInfo = inputStr.split("-");
				
				try {
					date = Integer.parseInt(dateInfo[0]);
					month = Integer.parseInt(dateInfo[1]);
					year = Integer.parseInt(dateInfo[2]);
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
					date = month = year = -1;
				}
				
			} while (!validDate(date, month, year));
			
			System.out.print(
					"\nSelect a collection time (option 1, 2 and 3):\n" +
					"1) 11am - 1pm\n" +
					"2) 5pm - 7pm\n" +
					"3) 7pm - 8pm\n\n");
			
			//Enters collection time
			do {
				System.out.print("Please enter Collection Time: ");
				try {
					if (!scanner.hasNextInt())
						throw new Exception("Please enter an integer!");
					
					option = scanner.nextInt();
					
					if (option < 1 || option > 3)
						throw new Exception("Please enter a value between 1 and 3 inclusive!");
				}
				catch (Exception e) {
					System.out.println("Invalid option! " + e.getMessage());
					option = -1;
					
					scanner.nextLine();
				}
			} while (option == -1);
			
			collectionTime = DELIVERYTIME[option - 1];
			
			System.out.print("\nYour collection details:\n" +
					"collection Date: " + date + "-" + month + "-" + year + "\n" +
					"Collection Time: ");
			
			switch (option) {
			case 1:	System.out.print("11am - 1pm");
					break;
			case 2:	System.out.print("5pm - 7pm");
					break;
			default:System.out.print("7pm - 8pm");
			}
			
			System.out.print("\n\nPlease select one of the following options to proceed\n" +
					"1) Confirm\n" +
					"2) Change of collection details\n\n");
			
			//Confirm or change collection details
			loopCount = 0;
			do {
				System.out.print("Your choice: ");
				try {
					if (!scanner.hasNextInt())
						throw new Exception("Please enter an integer!");
					
					option = scanner.nextInt();
					
					if (option < 1 || option > 2)
						throw new Exception("Please enter a value between 1 and 2 inclusive!");
				}
				catch (Exception e) {
					System.out.println("Invalid option! " + e.getMessage());
					option = -1;
					
					scanner.nextLine();
					if (loopCount == 0 && e.getMessage().equals("Please enter an integer!"))
						scanner.nextLine();
				}
				loopCount++;
			} while (option == -1);
			
			if (option == 2) {
				System.out.println("Changing collection details...\n");
				scanner.nextLine();
			}
		} while (option == 2);
		
		System.out.print("Order confirmed. Your Order ID is: " + orderFormat.format(orderID) + "\n" +
				"newID: " + orderFormat.format(orderID) + "\n");
	}
	
	//Gets date and time for delivery
	static void getDeliveryDetails() {
		
		status = Status.DELIVER;
		
		//Enter date
		do {
			do {
				System.out.print("Please enter the delivery date (dd-mm-yyyy)\n" +
						"Delivery Date: ");
				inputStr = scanner.nextLine();
				
				String[] dateInfo = inputStr.split("-");
				date = Integer.parseInt(dateInfo[0]);
				month = Integer.parseInt(dateInfo[1]);
				year = Integer.parseInt(dateInfo[2]);
				
			} while (!validDate(date, month, year));
			
			System.out.print(
					"\nSelect a delivery time (option 1, 2 and 3):\n" +
					"1) 11am - 1pm\n" +
					"2) 5pm - 7pm\n" +
					"3) 7pm - 8pm\n\n");
			
			//Enter delivery time
			do {
				System.out.print("Please enter Delivery Time: ");
				try {
					if (!scanner.hasNextInt())
						throw new Exception("Please enter an integer!");
					
					option = scanner.nextInt();
					
					if (option < 1 || option > 3)
						throw new Exception("Please enter a value between 1 and 3 inclusive!");
				}
				catch (Exception e) {
					System.out.println("Invalid option! " + e.getMessage());
					option = -1;
					
					scanner.nextLine();
				}
			} while (option == -1);
			
			deliveryTime = DELIVERYTIME[option - 1];
			
			System.out.print("\nYour delivery details:\n" +
					"Delivery Date: " + date + "-" + month + "-" + year + "\n" +
					"Delivery Time: ");
			
			switch (option) {
			case 1:	System.out.print("11am - 1pm");
					break;
			case 2:	System.out.print("5pm - 7pm");
					break;
			default:System.out.print("7pm - 8pm");
			}
			
			System.out.print("\n\nPlease select one of the following options to proceed\n" +
					"1) Confirm\n" +
					"2) Change of delivery details\n\n");
			
			//Confirm or change delivery details
			loopCount = 0;
			do {
				System.out.print("Your choice: ");
				try {
					if (!scanner.hasNextInt())
						throw new Exception("Please enter an integer!");
					
					option = scanner.nextInt();
					
					if (option < 1 || option > 2)
						throw new Exception("Please enter a value between 1 and 2 inclusive!");
				}
				catch (Exception e) {
					System.out.println("Invalid option! " + e.getMessage());
					option = -1;
					
					scanner.nextLine();
					if (loopCount == 0 && e.getMessage().equals("Please enter an integer!"))
						scanner.nextLine();
				}
				loopCount++;
			} while (option == -1);
			
			if (option == 2) {
				System.out.println("Changing delivery details...\n");
				scanner.nextLine();
			}
		} while (option == 2);
		
		System.out.print("Order confirmed. Your Order ID is: " + orderFormat.format(orderID) + "\n" +
				"newID: " + orderFormat.format(orderID) + "\n");
	}
	
	//Gets and records details for new customer
	static void registerNewCustomer() {
		System.out.print(
				"Welcome to Online Set Meals! Please enter your particulars.\n" +
				"=============================================================\n" +
				"Name: ");
		
		//Enter name
		name = scanner.nextLine().trim();
		
		//Enter username
		do {
			System.out.print("Username: ");
			username = scanner.nextLine().trim();
		} while (userExist(username));
		
		//Enter password
		System.out.print("Password: ");
		password = scanner.nextLine().trim();
		
		//Enter address
		System.out.print("Address: ");
		address = scanner.nextLine().trim();
		
		//Enter contact number
		do {
			option = 1;
			System.out.print("Contact Number: ");
			contact = scanner.nextLine();
			try {
				option = Integer.parseInt(contact);
				
				if (contact.length() != 9)
					throw new Exception("Invalid entry! Please enter a 9 digit number!");
			}
			catch (NumberFormatException e) {
				System.out.println("Invalid entry! Please enter a numerical value!");
				option = -1;
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
				option = -1;
			}
		} while (option == -1);
		
		addNewCustomer();	//Adds the new record into "customerinfo.txt"
		
		System.out.println("\nThank you for registering. Your account has been created.\n");
		
	}
	
	//Records details for new customer
	static void addNewCustomer() {
		try{
			outfile = new FileWriter(new File("customerinfo.txt"), true);
			outfile.write(name + ";" + username + ";" + password + ";" + address + ";" + contact + "\n");
			outfile.close();
		}
		catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	//Checks if username exists in customer records
	//(used for creating a new customer)
	static boolean userExist(String username) {
		try {
			fileScanner = new Scanner(new File("customerinfo.txt"));
			
			while (fileScanner.hasNextLine()) {
				inputStr = fileScanner.nextLine();
				String[] custInfo = inputStr.split(";");
				
				//If record found
				if (custInfo[1].equals(username)) {
					System.out.println("Invalid username! User already exist!");
					fileScanner.close();
					return true;
				}
			}
			
			fileScanner.close();
			return false;
		}
		catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	//Checks if date is valid
	static boolean validDate (int day, int month, int year) {
		
		int currYear = Calendar.getInstance().get(Calendar.YEAR);	//Gets current year in integer
		int currMonth = Calendar.getInstance().get(Calendar.MONTH);
		int currDay = Calendar.getInstance().get(Calendar.DATE);	//Gets current date in integer

		currMonth++;	//Current month in integer
		
		boolean isValid = true;
		
		if (day < 1 || day > 31)	//Checks for out of range day
		{
			System.out.println("Invalid day entered! Please re-enter");
			isValid = false;
		}
		else if (month < 1 || month > 12)	//Checks for out of range month
		{
			System.out.println("Invalid month entered! Please re-enter");
			isValid = false;
		}
		else if ((month == 4 || month == 6|| month == 9|| month == 11)
				 && (day == 31))	//Checks for months that have more than 30 days
		{
			System.out.println("This day do not exist in the month! Please re-enter");
			isValid = false;
		}
		else
		{
			int febMax, yrsFromALeapYr;
			
			yrsFromALeapYr = 2008 - year;
			
			if (yrsFromALeapYr % 4 != 0)
				febMax = 28;
			else
				febMax = 29;
			
			if (month == 2 && day > febMax)	//Checks for possible leap years
			{
				System.out.println("This day do not exist! Please re-enter");
				isValid = false;
			}
			else
			{	
				if	((year < currYear) || 
					(year == currYear && month < currMonth) ||
					(year == currYear && month == currMonth && day < currDay))	//Checks if date is in the past
				{
					System.out.println("Date is in the past! Please re-enter");
					isValid = false;
				}
			}
		}
		
		return isValid;
	}
	
	static void displayMainMenu() {
		System.out.print(
				"==============================================\n" +
				"Meals Online Ordering System - Main Page\n" +
				"==============================================\n" +
				"1) Place Order/Change Order\n" +
				"2) Check Out\n" +
				"3) Existing Customer\n" +
				"4) New Customer\n" +
				"5) Check Order Status\n" +
				"6) Exit\n\n" +
				"Please enter your option: ");
	}
	
	static void displaySetMealsMenu() {
		System.out.print(
				"Set Meals Menu\n" +
				"==============\n\n" +
				"Options:\n" +
				"a) Main Course (Regular-$10.00, Large-$13.00)\n" +
				"============================================================\n");
		
		for (int i = 0; i < MAINCOURSE.length; i++) {
			System.out.println(i+1 + ") " + MAINCOURSE[i]);
		}

		System.out.print("\nb) Side-dishes (1 side - $1.50, 2 sides - $2.50, 3 sides - $3.50)\n" +
				"=================================================================\n" +
				"1) Soup\n" +
				"2) Orange Juice\n" +
				"3) Coffee\n\n" +
				"c) Exit from Set Meals Menu\n" +
				"===========================\n\n" +
				"Note:\n" +
				"Enter option 'a' to select a set meal and then select the desired quantity for a particular size\n" +
				"Enter option 'b' to select the side dishes for the set meals\n" +
				"Enter option 'c' to exit from Set Meals Menu\n"
				);
	}
	
	static void greetingMessage() {
		System.out.print(
				"===============================\n" +
				"Welcome back " + name + "!\n" +
				"Your information is as follows:\n" +
				"Address: " + address + "\n" +
				"Contact Number: " + contact + "\n\n"
				);
	}

}


class SetMeal {
	static final double[] MAINMEALCOST = {10, 13};				//Cost for regular and large meals respectively
	static final double[] SIDEDISHESCOST = {1.50, 2.50, 3.50};	//Cost for 1, 2 and 3 side dishes respectively
	
	private String mainCourse;										//The main course
	private ArrayList<String> sideDishes = new ArrayList<String>();	//List of side-dishes
	private String mealSize;										//Regular or Large

	//Empty constructor
	SetMeal () {
	}
	
	//Constructs with main course
	SetMeal (String mainCourse) {
		this.mainCourse = mainCourse;
	}
	
	//Constructs with main course and meal size
	SetMeal (String mainCourse, String mealSize) {
		this (mainCourse);
		this.mealSize = mealSize;
	}

	public String getMainCourse() {
		return mainCourse;
	}

	public void setMainCourse(String mainCourse) {
		this.mainCourse = mainCourse;
	}
	
	//Adds a side-dish to the meal
	public void addSideDish (String sideDish) {
		sideDishes.add(sideDish);
	}
	
	//Removes all side-dishes of the meal
	public void clearSideDishes () {
		sideDishes.clear();
	}
	
	//Returns a particular side-dish
	public String getSideDish (int idx) {
		return sideDishes.get(idx);
	}
	
	public String getMealSize() {
		return mealSize;
	}
	
	public void setMealSize(String mealSize) {
		this.mealSize = mealSize;
	}
	
	//Sets meal size to Regular
	public void setReg() {
		mealSize = "Regular";
	}
	
	//Sets meal size to Large
	public void setLarge() {
		mealSize = "Large";
	}
	
	//Calculates and returns total cost of the set meal (main course + side-dishes)
	public double computeCost () {
		double finalCost = 0;
		
		if (mealSize.equals("Regular"))
			finalCost += MAINMEALCOST[0];
		else
			finalCost += MAINMEALCOST[1];
		
		if (!sideDishes.isEmpty())
			finalCost += SIDEDISHESCOST[sideDishes.size() - 1];
		
		return finalCost;
	}
	
	//Gets the number of side-dishes ordered with the meal
	public int numOfSideDishes() {
		return sideDishes.size();
	}
	
	//Displays the side-dishes and their total cost
	public String displaySideDishes() {
		int numOfSD = sideDishes.size();
		
		String s = numOfSD + " side dish";
			
		switch (numOfSD) {
			case 1:		s += " ($1.50)";
						break;
					
			case 2: 	s += "es ($2.50)";
						break;
			
			case 3:		s += "es ($3.50)";
						break;
						
			default:	
		}
	
		for (int i = 0; i < numOfSD; i++) {
			s += ", Side-dish " + (i + 1) + " - " + sideDishes.get(i);
		}
			
		return s;
	}
}
