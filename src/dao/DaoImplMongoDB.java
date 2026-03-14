package dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import model.Employee;
import model.Product;
import model.Amount;

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
            
            Product product = new Product(name, new Amount(wPrice), available, stock);
            if (doc.containsKey("id")) product.setId(doc.getInteger("id"));
            
            inventory.add(product);
        }
        disconnect();
        return inventory;
    }

    @Override
    public boolean writeInventory(ArrayList<Product> productsList) {
        try {
            connect();
            MongoCollection<Document> collection = db.getCollection("historical_inventory");
            ArrayList<Document> documents = new ArrayList<>();
            
            for (Product p : productsList) {
                Document priceDoc = new Document("value", p.getWholesalerPrice().getValue())
                                          .append("currency", "€");
                
                Document doc = new Document("id", p.getId())
                                     .append("name", p.getName())
                                     .append("wholesalerPrice", priceDoc)
                                     .append("available", p.isAvailable())
                                     .append("stock", p.getStock())
                                     .append("created_at", new java.util.Date());
                documents.add(doc);
            }
            if (!documents.isEmpty()) collection.insertMany(documents);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            disconnect();
        }
    }

    @Override
    public boolean addProduct(Product p) {
        try {
            connect();
            MongoCollection<Document> collection = db.getCollection("inventory");
            Document priceDoc = new Document("value", p.getWholesalerPrice().getValue())
                                      .append("currency", "€");
  
            Document doc = new Document("id", p.getId())
                                 .append("name", p.getName())
                                 .append("wholesalerPrice", priceDoc)
                                 .append("available", p.isAvailable())
                                 .append("stock", p.getStock());
            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }

    @Override
    public boolean updateProduct(Product p) {
        try {
            connect();
            MongoCollection<Document> collection = db.getCollection("inventory");
            Bson filter = eq("id", p.getId());
            Bson updates = Updates.combine(
                Updates.set("name", p.getName()),
                Updates.set("wholesalerPrice.value", p.getWholesalerPrice().getValue()),
                Updates.set("available", p.isAvailable()),
                Updates.set("stock", p.getStock())
            );
            collection.updateOne(filter, updates);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }
    
    @Override
    public boolean deleteProduct(int productId) {
        try {
            connect();
            MongoCollection<Document> collection = db.getCollection("inventory");
            Bson filter = eq("id", productId);
            long deletedCount = collection.deleteOne(filter).getDeletedCount();
            return deletedCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }
}