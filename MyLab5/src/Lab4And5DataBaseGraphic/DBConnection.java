package Lab4And5DataBaseGraphic;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.LineListener;
import javax.swing.JOptionPane;

import Lab4And5DataBaseGraphic.*;

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

	public ArrayList<Good> getData() {
		String showQuery = "SELECT * FROM " + name;
		var arrayResult = new ArrayList<Good>();
		try (Statement statement = connection.createStatement()){
			
			ResultSet resultSet = statement.executeQuery(showQuery);
			arrayResult = show(resultSet);
			//statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return arrayResult;
	}

	private void createProductsTable(int N) {
		String dropQuery = "SHOW TABLES LIKE '" + name + "'";
		try (Statement statement = connection.createStatement()){
			
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
			//statement.close();
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

	private boolean executeUpdate(String query) {
		
		try(Statement statement = connection.createStatement()) {
			
			statement.executeUpdate(query);
			//statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}

	private void clearTable() {
		String clearQuery = "TRUNCATE TABLE " + name;
		executeUpdate(clearQuery);
	}

	public boolean addProduct(String title, double cost) {
		String addQuery = "INSERT INTO " + name + " (prodid, title, cost) VALUES (" + prodid + ", '" + title + "', "
				+ cost + ')';
		var t = executeUpdate(addQuery);
		++prodid;
		return t;
	}

	public boolean deleteProduct(String title) {
		var sizeOld = getData().size();
		String deleteQuery = "DELETE FROM " + name + " WHERE title = '" + title + "'";
		executeUpdate(deleteQuery);
		var sizeNew = getData().size();
		return sizeNew != sizeOld;
	}

	private ArrayList<Good> show(ResultSet resultSet) throws SQLException {
		var arrayResult = new ArrayList<Good>();
		if (resultSet != null) {
			try(resultSet) {
			while (resultSet.next()) {
				int prodid = resultSet.getInt("prodid");
				String title = resultSet.getString("title");
				double cost = resultSet.getDouble("cost");
				arrayResult.add(new Good(prodid, title, cost));
			}
			//resultSet.close();
			}
			catch (SQLException e) {
                System.out.println(e.getMessage());
            }

			return arrayResult;
		} else {
			System.out.println("There are no records");
		}
		return null;
	}

	public Good getPriceByTitle(String title) {
		String priceQuery = "SELECT * FROM " + name + " WHERE title ='" + title + "'";
		try(Statement statement = connection.createStatement()) {
			
			ResultSet resultSet = statement.executeQuery(priceQuery);
			if (resultSet != null) {
				try {
					resultSet.next();
					return new Good(resultSet.getInt("prodid"), title, resultSet.getDouble("cost"));
				} catch (SQLException e) {

				}
			} else {
			}
			//statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		JOptionPane.showMessageDialog(null, title + " not found");
		return null;
	}

	public void changePrice(String title, double newPrice) {
		if (DBConnection.instance().getPriceByTitle(title) == null)
			return;
		String changeQuery = "UPDATE " + name + " SET cost = " + newPrice + " WHERE title = '" + title + "'";
		executeUpdate(changeQuery);
	}

	public ArrayList<Good> showProductsInPriceRange(double left, double right) {
		ArrayList<Good> result = new ArrayList<Good>();
		if (left > right) {
			double temp = left;
			left = right;
			right = temp;
		}
		String showQuery = "SELECT * FROM " + name + " WHERE cost BETWEEN " + left + " AND " + right;
		try (Statement statement = connection.createStatement()){
			
			ResultSet resultSet = statement.executeQuery(showQuery);
			result = show(resultSet);
			//statement.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
}
