package Lab4DataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import Lab4DataBase.*;
import javax.sound.sampled.LineListener;

public class DBConnection {
	static public DBConnection instance() {
		while (dataBase == null) {
			String username = "root";
			String password = "06081998";
			try {
				dataBase = new DBConnection(username, password);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return dataBase;
	}

	private static DBConnection dataBase = null;
	private static String name = "product2";
	private static int prodid = 1;
	private Connection connection;

	private DBConnection(String username, String password) throws SQLException {
		String url = "jdbc:mysql://localhost:3306/productmanagement?useSSL=false";
		this.connection = DriverManager.getConnection(url, username, password);
		createProductsTable(10);
	}

	private void createProductsTable(int N) {
		String dropQuery = "SHOW TABLES LIKE '" + name + "'";
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(dropQuery);
			if (resultSet != null) {
				if (resultSet.next()) {
					clearTable();
				} else {
					String createQuery = "CREATE TABLE " + name + "(" + "id INT NOT NULL AUTO_INCREMENT, "
							+ "prodid INT NOT NULL, " + "title VARCHAR(90) NOT NULL UNIQUE  , "
							+ "cost DOUBLE NOT NULL," + "PRIMARY KEY (id)" + ");";
					executeUpdate(createQuery);
				}
			}
			statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		String title = "Good";
		Random random = new Random();
		for (int i = 1; i <= N; ++i) {
			int titleNumber = random.nextInt(100);
			double cost = random.nextInt(100);
			addProduct(title + titleNumber, cost);
		}
	}

	private void executeUpdate(String query) {
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);
			statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	private void clearTable() {
		String clearQuery = "TRUNCATE TABLE " + name;
		executeUpdate(clearQuery);
	}

	public void addProduct(String title, double cost) {
		String addQuery = "INSERT INTO " + name + " (prodid, title, cost) VALUES (" + prodid + ", '" + title + "', "
				+ cost + ')';
		executeUpdate(addQuery);
		++prodid;
	}

	public void deleteProduct(String title) {
		String deleteQuery = "DELETE FROM " + name + " WHERE title = '" + title + "'";
		executeUpdate(deleteQuery);
	}

	private void show(ResultSet resultSet) throws SQLException {
		if (resultSet != null) {
			System.out.println("prodid title cost");
			try{
				while (resultSet.next()) {
				int prodid = resultSet.getInt("prodid");
				String title = resultSet.getString("title");
				double cost = resultSet.getDouble("cost");
				System.out.println(prodid + " " + title + " " + cost);
			}
			resultSet.close();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
        }
		}
			else {
			System.out.println("There are no records!");
		}

	}

	public void showProducts() {
		String showQuery = "SELECT * FROM " + name;
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(showQuery);
			show(resultSet);
			statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void getPriceByTitle(String title) {
		String priceQuery = "SELECT cost FROM " + name + " WHERE title ='" + title + "'";
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(priceQuery);
			if (resultSet != null) {
				try {
					resultSet.next();
					System.out.println("Product title " + title + " has price " + resultSet.getInt("cost"));
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			} else {
				System.out.println("There is no product with such title");
			}
			statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void changePrice(String title, double newPrice) {
		String changeQuery = "UPDATE " + name + " SET cost = " + newPrice + " WHERE title = '" + title + "'";
		executeUpdate(changeQuery);
	}

	public void showProductsInPriceRange(double left, double right) {
		if (left > right) {
			double temp = left;
			left = right;
			right = temp;
		}
		String showQuery = "SELECT * FROM " + name + " WHERE cost BETWEEN " + left + " AND " + right;
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(showQuery);
			show(resultSet);
			statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
