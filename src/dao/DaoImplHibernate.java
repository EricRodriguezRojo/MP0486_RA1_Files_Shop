package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


import model.Amount;
import model.Employee;
import model.Product;
import model.ProductHistory;
import model.Sale;

public class DaoImplHibernate implements Dao {

	private static SessionFactory sessionFactory;
	
	@Override
	public void connect() {
	    if (sessionFactory == null) {
	        sessionFactory = new Configuration().configure("hibernate.cfg.xml")
	                                            .addAnnotatedClass(Product.class)
	                                            .addAnnotatedClass(ProductHistory.class)
	                                            .addAnnotatedClass(Employee.class)
	                                            .buildSessionFactory();
	    }
	}

	@Override
    public void disconnect() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

	@Override
	public Employee getEmployee(int employeeId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public ArrayList<Product> getInventory() {
        connect();
        Session session = sessionFactory.openSession();
        List<Product> products = session.createQuery("from Product", Product.class).list();
        session.close();
        return new ArrayList<>(products);
    }
	
	@Override
	public boolean writeInventory(ArrayList<Product> productsList) {
	    Session session = sessionFactory.openSession();
	    Transaction tx = session.beginTransaction();
	    try {
	        for (Product p : productsList) {
	            ProductHistory history = new ProductHistory(p);
	            session.persist(null);
	        }
	        tx.commit();
	        return true;
	    } catch (Exception e) {
	        if (tx != null) tx.rollback();
	        return false;
	    } finally {
	        session.close();
	    }
	}

	
	@Override
	public boolean addProduct(Product p) {
	    Session session = sessionFactory.openSession();
	    Transaction tx = session.beginTransaction();
	    try {
	        session.persist(p); 
	        tx.commit();
	        return true;
	    } catch (Exception e) {
	        if (tx != null) tx.rollback();
	        return false;
	    } finally {
	        session.close();
	    }
	}

	@Override
	public boolean updateProduct(Product p) {
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    try {
	        tx = session.beginTransaction();
	        session.merge(p);
	        tx.commit();
	        return true;
	    } catch (Exception e) {
	        if (tx != null) tx.rollback();
	        e.printStackTrace();
	        return false;
	    } finally {
	        session.close();
	    }
	}

	@Override
	public boolean deleteProduct(String name) {
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    try {
	        tx = session.beginTransaction();
	        Product p = session.createQuery("from Product where name = :n", Product.class)
	                           .setParameter("n", name)
	                           .uniqueResult();
	        if (p != null) {
	            session.remove(p);
	            tx.commit();
	            return true;
	        }
	        return false;
	    } catch (Exception e) {
	        if (tx != null) tx.rollback();
	        e.printStackTrace();
	        return false;
	    } finally {
	        session.close();
	    }
	}

	

}