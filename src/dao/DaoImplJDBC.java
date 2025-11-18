package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplJDBC implements Dao {
	Connection connection;
	private static final String getInventory_query = "SELECT product, price, available, Stock FROM inventory";

	@Override
	public void connect() {
		// Define connection parameters
		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		try {
			this.connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";
		
		try (PreparedStatement ps = connection.prepareStatement(query)) { 
    		ps.setInt(1,employeeId);
    	  	ps.setString(2,password);
    	  	//System.out.println(ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            		employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
            	}
            }
        } catch (SQLException e) {
			// in case error in SQL
			e.printStackTrace();
		}
    	return employee;
	}

	@Override	
	public ArrayList<Product> getInventory() {
		ArrayList<Product> inventory = new ArrayList<>();
		try (PreparedStatement preparedStatement = connection.prepareStatement(getInventory_query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String name = resultSet.getString("product");
                    double price = resultSet.getDouble("wholesalerPrice");
                    boolean available = resultSet.getBoolean("available"); 
                    int stock = resultSet.getInt("Stock");
                    Product product = new Product(name, new Amount(price), available, stock);
                    inventory.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		return inventory;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> productsList) {

	    String sql = "INSERT INTO historical_inventory (id_product, name, wholesalerPrice, available, stock, created_at) "
	            + "VALUES (?, ?, ?, ?, ?, NOW())";

	    try (PreparedStatement ps = connection.prepareStatement(sql)) {

	        for (Product p : productsList) {

	            ps.setInt(1, p.getId());
	            ps.setString(2, p.getName());
	            ps.setDouble(3, p.getWholesalerPrice().getValue());
	            ps.setBoolean(4, p.isAvailable());
	            ps.setInt(5, p.getStock());

	            ps.addBatch();
	        }

	        ps.executeBatch();
	        return true;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}



}
