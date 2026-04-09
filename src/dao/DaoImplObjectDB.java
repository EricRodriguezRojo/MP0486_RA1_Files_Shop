package dao;

import java.io.File;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Employee;
import model.Product;

public class DaoImplObjectDB implements Dao {

	private static final String DB_FILE_PATH = System.getProperty("user.home")
			+ File.separator + "objectdb-data" + File.separator + "users.odb";

	private EntityManagerFactory emf;
	private EntityManager em;

	@Override
	public void connect() {
		File dbDir = new File(DB_FILE_PATH).getParentFile();
		if (dbDir != null && !dbDir.exists()) {
			dbDir.mkdirs();
		}
		String objectDbUrl = "objectdb:" + DB_FILE_PATH.replace('\\', '/');
		emf = Persistence.createEntityManagerFactory(objectDbUrl);
		em = emf.createEntityManager();
		seedDefaultEmployee();
	}

	private void seedDefaultEmployee() {
		Employee existing = em.find(Employee.class, 123);
		if (existing == null) {
			em.getTransaction().begin();
			em.persist(new Employee(123, "test", "test"));
			em.getTransaction().commit();
		}
	}

	@Override
	public void disconnect() {
		if (em != null && em.isOpen()) {
			em.close();
		}
		if (emf != null) {
			emf.close();
		}
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		try {
			Employee employee = em.find(Employee.class, employeeId);
			if (employee != null && employee.getPassword() != null && employee.getPassword().equals(password)) {
				return employee;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		return null;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> ProductsList) {
		return false;
	}

	@Override
	public boolean addProduct(Product p) {
		return false;
	}

	@Override
	public boolean updateProduct(Product p) {
		return false;
	}

	@Override
	public boolean deleteProduct(int id) {
		return false;
	}

}
