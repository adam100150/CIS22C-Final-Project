/**
 * Store.java
 * @artist Nathan Brin
 * @artist Adam Ashkenazi
 * @artist Sihan Sun
 * @artist Alice Zhang
 * CIS 22C Final Project
 */

package src;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Store {
	private  final int NUM_ORDERS = 100;
	private PriorityComparator pc;
	private NameComparator nC;
	private ValueComparator vC;

	private static BST<Painting> painting_name = new BST<>();
	private static BST<Painting> painting_value = new BST<>();

	HashMap<String, Customer> customers;
	HashMap<String, Employee> employees;

	private Heap<Order> ordersStandard;
	private Heap<Order> ordersRushed;
	private Heap<Order> ordersOvernight;

	private List<Order> shippedOrders;

	public Store() {
		pc = new PriorityComparator();
		nC = new NameComparator();
		vC = new ValueComparator();
		customers = new HashMap<>();
		employees = new HashMap<>();
		ordersStandard = new Heap<>(NUM_ORDERS, pc);
		ordersRushed = new Heap<>(NUM_ORDERS, pc);
		ordersOvernight = new Heap<>(NUM_ORDERS, pc);

		shippedOrders = new List<>();

		try {
			buildPaintings();
			readCustomersFile();
			readEmployeesFile();
			buildOrders();
		} catch(FileNotFoundException e) {
			System.out.println("Could not find file.");
			e.printStackTrace();
		}
	}

	void buildPaintings() throws FileNotFoundException {
		String title, artist, description;
		double price;
		int year;

		File file = new File("src/text-files/Paintings.txt");
		Scanner input = new Scanner(file);
		while(input.hasNextLine()) {
			title = input.nextLine();
			artist = input.nextLine();
			year = input.nextInt();
			input.nextLine();
			price = input.nextFloat();
			input.nextLine();
			description = input.nextLine();
			if(input.hasNextLine()) {
				input.nextLine();
			}
			painting_name.insert(new Painting(title, artist, year, price, description), nC);
			painting_value.insert(new Painting(title, artist, year, price, description), vC);
		}
		input.close();
	}

	void readCustomersFile() throws FileNotFoundException{
		String userName, password, firstName, lastName, email, address;
		double cash;

		File file = new File("src/text-files/Customers.txt");
		Scanner input = new Scanner(file);
		while(input.hasNextLine()) {
			userName = input.nextLine();
			password = input.nextLine();
			firstName = input.next();
			lastName = input.next();
			input.nextLine();

			email = input.nextLine();
			address = input.nextLine();
			cash = input.nextDouble();


			if(input.hasNextLine()) {
				input.nextLine();
			}
			customers.putIfAbsent(userName, new Customer(userName, password, firstName, lastName, email, address, cash));
		}
		input.close();
	}

	void readEmployeesFile() throws FileNotFoundException{
		String username, password, firstName, lastName;

		File file = new File("src/text-files/Employees.txt");
		Scanner input = new Scanner(file);
		while(input.hasNextLine()) {
			username = input.nextLine();
			password = input.nextLine();
			firstName = input.nextLine();
			lastName = input.nextLine();

			if(input.hasNextLine()) {
				input.nextLine();
			}

			employees.putIfAbsent(username, new Employee(username, password, firstName, lastName));
		}
		input.close();
	}

	void buildOrders() throws FileNotFoundException {
		String user, painting, date;
		int speed;
		boolean ship;

		File file = new File("src/text-files/Orders.txt");
		Scanner input = new Scanner(file);
		while(input.hasNextLine()) {
			date = input.nextLine();
			user = input.nextLine();
			painting = input.nextLine();
			speed = input.nextInt();
			ship = input.nextBoolean();
			if(input.hasNextLine())
				input.nextLine();
			Customer tempCust = customers.get(user);
			Painting tempPaint = painting_name.search(new Painting(painting), nC);
			Order tempOrder = new Order(tempCust, date, tempPaint, speed,ship);
			tempCust.addOrder(tempOrder);

			if(ship) {
				shippedOrders.addLast(tempOrder);
			} else if(speed == Shipping.STANDARD.ordinal()) {
				ordersStandard.insert(tempOrder);
			} else if(speed == Shipping.RUSHED.ordinal()) {
				ordersRushed.insert(tempOrder);
			} else if(speed == Shipping.OVERNIGHT.ordinal()) {
				ordersOvernight.insert(tempOrder);
			}
		}
		input.close();
	}

	Painting searchPaintingName(Painting painting) {
		return painting_name.search(painting, nC);
	}

	Painting searchPaintingPrice(Painting painting) { return painting_name.search(painting, vC); }

	void printPaintingsByName() {
		painting_name.inOrderPrint();
	}

	void printPaintingsByValue() {
		painting_value.inOrderPrint();
	}

	void placeOrder(Order order) {
		Customer orderCustomer = order.getCustomer();
		if(order.getShippingSpeed() == Shipping.OVERNIGHT.ordinal()) {
			ordersOvernight.insert(order);
		} else if(order.getShippingSpeed() == Shipping.RUSHED.ordinal()) {
			ordersRushed.insert(order);
		} else if(order.getShippingSpeed() == Shipping.STANDARD.ordinal()) {
			ordersStandard.insert(order);
		}

		Painting tempPainting = order.getOrderContents();
		temp.addPainting(tempPainting);
		Double price = tempPainting.getPrice();
		temp.updateCash(-price);
		temp.addOrder(order);
			
		String fileName = "Customers.txt";
		File tempFile = new File("tempfileC.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			PrintWriter out = new PrintWriter(writer);
			String currentLine;
				while ((currentLine = reader.readLine()) != null) {
					if (currentLine.equals(temp.getUserName())) {
						out.println(currentLine);
						currentLine = reader.readLine();
						out.println(currentLine);
						currentLine = reader.readLine();
						out.println(currentLine);
						currentLine = reader.readLine();
						out.println(currentLine);
						currentLine = reader.readLine();
						out.println(currentLine);
						currentLine = reader.readLine();
						out.println(temp.getCash());
					} else {
						out.println(currentLine);
					}
				}
				reader.close();
				out.close();

				reader = new BufferedReader(new FileReader(tempFile));
				writer = new BufferedWriter(new FileWriter(fileName));
				out = new PrintWriter(writer);

				while ((currentLine = reader.readLine()) != null) {
					out.println(currentLine);
				}
				reader.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	//TODO: Add this code to addOrder


	//							fileName = "Orders.txt";
//							try {
//								BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
//								PrintWriter out = new PrintWriter(writer);
//								out.println();
//								out.println(timeStamp);
//								out.println(currentCustomer.getUserName());
//								out.println(currentCustomer.getPassword());
//								out.println(currentPainting.getTitle());
//								out.println(speedIntInput);
//								out.print(false);
//								out.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//					}

	public void shipOrder()
	{
		if(!ordersOvernight.isEmpty())
		{
			Order currentOrder = ordersOvernight.pop();
			Customer currentCust = currentOrder.getCustomer();
			currentCust.addPainting(currentOrder.getOrderContents());
			System.out.println("\nOrder Shipped: " + currentOrder);
			shippedOrders.addLast(currentOrder);
			int index = unshippedOrders.linearSearch(currentOrder);
			unshippedOrders.iteratorToIndex(index);
			unshippedOrders.removeIterator();
			currentOrder.ship();
			
			String fileName = "Orders.txt";
			File tempFile = new File("tempfile.txt");
			try {
				BufferedReader reader = new BufferedReader(new FileReader(fileName));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
				PrintWriter out = new PrintWriter(writer);
				String currentLine;

				while ((currentLine = reader.readLine()) != null) {
					if (currentLine.equals(currentOrder.getDate())) {
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
					} else {
						out.println(currentLine);
					}
				}
				reader.close();
				out.close();

				reader = new BufferedReader(new FileReader(tempFile));
				writer = new BufferedWriter(new FileWriter(fileName));
				out = new PrintWriter(writer);

				while ((currentLine = reader.readLine()) != null) {
					out.println(currentLine);
				}
				reader.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			fileName = "Orders.txt";
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
				PrintWriter out = new PrintWriter(writer);
				out.println(currentOrder.getDate());
				out.println(currentCust.getUserName());
				out.println(currentCust.getPassword());
				out.println(currentOrder.getOrderContents().getTitle());
				out.println(currentOrder.getShippingSpeed());
				out.print(currentOrder.isShipped());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(!ordersRushed.isEmpty())
		{
			Order currentOrder = ordersRushed.pop();
			Customer currentCust = currentOrder.getCustomer();
			currentCust.addPainting(currentOrder.getOrderContents());
			System.out.println("\nOrder Shipped: " + currentOrder);
			shippedOrders.addLast(currentOrder);
			int index = unshippedOrders.linearSearch(currentOrder);
			unshippedOrders.iteratorToIndex(index);
			unshippedOrders.removeIterator();
			currentOrder.ship();
			
			String fileName = "Orders.txt";
			File tempFile = new File("tempfile.txt");
			try {
				BufferedReader reader = new BufferedReader(new FileReader(fileName));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
				PrintWriter out = new PrintWriter(writer);
				String currentLine;

				while ((currentLine = reader.readLine()) != null) {
					if (currentLine.equals(currentOrder.getDate())) {
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
					} else {
						out.println(currentLine);
					}
				}
				reader.close();
				out.close();

				reader = new BufferedReader(new FileReader(tempFile));
				writer = new BufferedWriter(new FileWriter(fileName));
				out = new PrintWriter(writer);

				while ((currentLine = reader.readLine()) != null) {
					out.println(currentLine);
				}
				reader.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			fileName = "Orders.txt";
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
				PrintWriter out = new PrintWriter(writer);
				out.println(currentOrder.getDate());
				out.println(currentCust.getUserName());
				out.println(currentCust.getPassword());
				out.println(currentOrder.getOrderContents().getTitle());
				out.println(currentOrder.getShippingSpeed());
				out.print(currentOrder.isShipped());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(!ordersStandard.isEmpty())
		{
			Order currentOrder = ordersStandard.pop();
			Customer currentCust = currentOrder.getCustomer();
			currentCust.addPainting(currentOrder.getOrderContents());
			System.out.println("\nOrder Shipped: " + currentOrder);
			shippedOrders.addLast(currentOrder);
			int index = unshippedOrders.linearSearch(currentOrder);
			unshippedOrders.iteratorToIndex(index);
			unshippedOrders.removeIterator();
			currentOrder.ship();
			
			String fileName = "Orders.txt";
			File tempFile = new File("tempfile.txt");
			try {
				BufferedReader reader = new BufferedReader(new FileReader(fileName));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
				PrintWriter out = new PrintWriter(writer);
				String currentLine;

				while ((currentLine = reader.readLine()) != null) {
					if (currentLine.equals(currentOrder.getDate())) {
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
						reader.readLine();
					} else {
						out.println(currentLine);
					}
				}
				reader.close();
				out.close();

				reader = new BufferedReader(new FileReader(tempFile));
				writer = new BufferedWriter(new FileWriter(fileName));
				out = new PrintWriter(writer);

				while ((currentLine = reader.readLine()) != null) {
					out.println(currentLine);
				}
				reader.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			fileName = "Orders.txt";
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
				PrintWriter out = new PrintWriter(writer);
				out.println(currentOrder.getDate());
				out.println(currentCust.getUserName());
				out.println(currentCust.getPassword());
				out.println(currentOrder.getOrderContents().getTitle());
				out.println(currentOrder.getShippingSpeed());
				out.print(currentOrder.isShipped());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("No orders to ship.");
	}

	public void viewUnshippedOrders()
	{
		if(unshippedOrders.isEmpty())
			System.out.println("No orders to show.");
		else
		{
			ArrayList<Order> list1 = ordersOvernight.sort();
			ArrayList<Order> list2 = ordersRushed.sort();
			ArrayList<Order> list3 = ordersStandard.sort();
			list1.addAll(list2);
			list1.addAll(list3);
			System.out.println(list1);
		}
	}

	public void viewShippedOrders()
	{
		if(shippedOrders.isEmpty())
			System.out.println("No orders to show.");
		else
			shippedOrders.printNumberedList();
	}

	public void viewUnshippedOrders(Customer cust)
	{
		if(unshippedOrders.isEmpty())
		{
			System.out.println("No orders to show.");
		}
		else
		{
			List<Order> temp = new List<>(); //temp is the list of orders the Customer placed
			unshippedOrders.placeIterator();
			for(int i = 1; i <=unshippedOrders.getLength(); i++)
			{
				Order tempOrder = unshippedOrders.getIterator();
				if(tempOrder.getCustomer().equals(cust))
					temp.addLast(tempOrder);
				unshippedOrders.advanceIterator();
			}
			temp.printNumberedList();
		}
	}

	public void viewShippedOrders(Customer cust)
	{
		if(shippedOrders.isEmpty())
			System.out.println("No orders to show.");
		else
		{
			List<Order> temp = new List<>();
			shippedOrders.placeIterator();
			for(int i = 1; i <= shippedOrders.getLength(); i++)
			{
				Order tempOrder = shippedOrders.getIterator();
				if(tempOrder.getCustomer().equals(cust))
					temp.addLast(tempOrder);
				shippedOrders.advanceIterator();
			}
			if(temp.isEmpty())
				System.out.println("No shipped orders.");
			else
				temp.printNumberedList();
		}
	}
	public void addPainting(Painting painting)
	{
		painting_name.insert(painting, nC);
		painting_value.insert(painting, vC);
	}

	//TODO: Add painting to painting database

	public void removePainting(Painting painting) throws ArtGalleryException {
		System.out.println("Painting removed: " + painting);
		if (painting_name.search(painting, nC) == null) {
			throw new ArtGalleryException("Painting not found");
		}
		painting_name.remove(painting, nC);
		painting_value.remove(painting, vC);
	}

	//TODO: Remove painting from database
}