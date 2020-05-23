package Lab4DataBase;

import java.util.*;

public class Good {
	private Scanner scanner;
	private HashMap<String, Runnable> map;

	public Good() {
		this.scanner = new Scanner(System.in);
		this.scanner.useLocale(Locale.US);

		this.map = new HashMap<>();
		this.map.put("/add", this::Add);
		this.map.put("/delete", this::Delete);
		this.map.put("/show_all", this::ShowAll);
		this.map.put("/price", this::GetPrice);
		this.map.put("/change_price", this::ChangePrice);
		this.map.put("/filter_by_price", this::FilterByPrice);
	}

	public void process() {
		DBConnection.instance();
		System.out.println("Enter command::");
		while (scanner.hasNext()) {
			goodCommand();
		}
	}

	private void Add() {
		String name;
		double cost;
		try {
			name = Name();
			cost = Cost();
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
			scanner.nextLine();
			return;
		}
		scanner.nextLine();

		DBConnection.instance().addProduct(name, cost);
	}

	private void Delete() {
		String name;
		try {
			name = Name();
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
			scanner.nextLine();
			return;
		}
		scanner.nextLine();

		DBConnection.instance().deleteProduct(name);
	}

	private void ShowAll() {
		if (scanner.nextLine().length() > 0) {
			System.out.println("Command /show_all do not have contains arguments");
		}

		DBConnection.instance().showProducts();
	}

	private void GetPrice() {
		String name;
		try {
			name = Name();
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
			scanner.nextLine();
			return;
		}
		scanner.nextLine();

		DBConnection.instance().getPriceByTitle(name);
	}

	private void ChangePrice() {
		String name;
		double cost;
		try {
			name = Name();
			cost = Cost();
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
			scanner.nextLine();
			return;
		}
		scanner.nextLine();
		DBConnection.instance().changePrice(name, cost);
	}

	private void FilterByPrice() {
		double leftBorder;
		double rightBorder;
		try {
			leftBorder = Cost();
			rightBorder = Cost();
		} catch (NoSuchElementException e) {
			System.out.println(e.getMessage());
			scanner.nextLine();
			return;
		}
		scanner.nextLine();

		DBConnection.instance().showProductsInPriceRange(leftBorder, rightBorder);
	}

	private void goodCommand() {
		String command = scanner.next();
		if (map.containsKey(command)) {
			map.get(command).run();
		} else {
			System.out.println("There is no commands. Try another: ");
			scanner.nextLine();
		}
	}

	private String Name() throws NoSuchElementException {
		String name;
		if (scanner.hasNext()) {
			name = scanner.next();
		} else {
			throw new NoSuchElementException("You must input name with this command!");
		}
		return name;
	}

	private double Cost() throws NoSuchElementException {
		double cost;
		if (scanner.hasNextDouble()) {
			cost = scanner.nextDouble();
		} else {
			throw new NoSuchElementException("You must input cost with this command!");
		}
		return cost;
	}
}
