	package dao;
	
	import com.mongodb.client.MongoClient;
	import com.mongodb.client.MongoClients;
	import com.mongodb.client.MongoCollection;
	import com.mongodb.client.MongoDatabase;
	import org.bson.Document;

import model.Amount;
import model.Employee;
	import model.Product;
	
	import java.util.ArrayList;
	
	import static com.mongodb.client.model.Filters.and;
	import static com.mongodb.client.model.Filters.eq;
	
	public class DaoImplMongoDB implements Dao {
	
	    private MongoClient client;
	    private MongoDatabase db;
	
	    @Override
	    public void connect() {
	        client = MongoClients.create("mongodb://localhost:27017");
	        db = client.getDatabase("shop");
	    }
	
	    @Override
	    public void disconnect() {
	        if (client != null) {
	            client.close();
	        }
	    }
	
	    @Override
	    public Employee getEmployee(int employeeId, String password) {
	        connect();
	        MongoCollection<Document> users = db.getCollection("users");
	       
	        Document user = users.find(
	                and(eq("employeeId", employeeId), eq("password", password))
	        ).first();
	        disconnect();
	        if (user == null) return null;
	        return new Employee(
	                user.getInteger("employeeId"),
	                user.getString("name"),
	                user.getString("password")
	        );
	    }
	
	    @Override
	    public ArrayList<Product> getInventory() {
	        ArrayList<Product> inventory = new ArrayList<>();
	        connect();
	        MongoCollection<Document> collection = db.getCollection("inventory");
	        
	        for (Document doc : collection.find()) {
	            String name = doc.getString("name");
	            boolean available = doc.getBoolean("available");
	            int stock = doc.getInteger("stock");
	            
	            Document priceDoc = (Document) doc.get("wholesalerPrice");
	            double wPrice = 0.0;
	            if (priceDoc != null) {
	                Object val = priceDoc.get("value");
	                if (val instanceof Number) wPrice = ((Number) val).doubleValue();
	            }
	            
	            Product product = new Product();
	            product.setName(name);
	            product.setAvailable(available);
	            product.setStock(stock);
	            
	            product.setPrice(wPrice * 2);
	            
	            if (doc.containsKey("id")) {
	                product.setId(doc.getInteger("id"));
	            }
	            
	            inventory.add(product);
	        }
	        disconnect();
	        return inventory;
	    }
	
	    @Override
	    public boolean writeInventory(ArrayList<Product> productsList) {
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
	    public boolean deleteProduct(String name) {
	        return false;
	    }
	}