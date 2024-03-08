package database;
import java.util.UUID;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class QuickStart {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public QuickStart() {
        // Initialize the MongoDB connection, database, and collection in the constructor
        String uri = "mongodb+srv://kacper:ZAQ!2wsx@cluster1.wismxou.mongodb.net/?retryWrites=true&w=majority&appName=Cluster1";
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("bank");
    }

    // tworzenie użytkownika
    public void createUser(String name, String password, String email, String pesel) {
        // Create a new Document representing the user
        collection = database.getCollection("clients");

        String bankAccount = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();

        Validators val = new Validators();
        boolean isValid = val.createUserValidator(id, email, pesel, bankAccount, collection);
        // Check if email and pesel are unique
        if (isValid) {

            Document newUser = new Document("name", name)
                    .append("_id", id)
                    .append("email", email)
                    .append("password", password)
                    .append("pesel", pesel)
                    .append("balance", 0)
                    .append("bank_account", bankAccount);

            // Insert the new user document into the collection
            collection.insertOne(newUser);
            System.out.println("User created successfully!");
        } else {
            System.out.println("Email or PESEL is not unique. Please choose a different one. Try again.");
        }
    }

    // wpłata na konto
    public void payment(String account, int amount) {
        collection = database.getCollection("clients");
        Document userDocument = collection.find(eq("bank_account", account)).first();

        if (userDocument != null) {
            // Pobierz bieżący stan konta użytkownika
            int currentBalance = userDocument.getInteger("balance", 0);

            // Dodaj kwotę do salda
            int newBalance = currentBalance + amount;

            // Zaktualizuj dokument w kolekcji z nowym saldem
            collection.updateOne(eq("bank_account", account), inc("balance", newBalance));

            System.out.println("Payment successful. New balance: " + newBalance);
        } else {
            System.out.println("User with account " + account + " not found.");
        }
    }

    // wypłata z konta
    public void paycheck(String account, int amount) {
        collection = database.getCollection("clients");
        Document userDocument = collection.find(eq("bank_account", account)).first();

        if (userDocument != null) {
            // Pobierz bieżący stan konta użytkownika
            int currentBalance = userDocument.getInteger("balance", 0);

            // Sprawdź czy użytkownik ma wystarczająco środków
            if (currentBalance >= amount) {
                // Odejmij kwotę od salda i zaktualizuj dokument w kolekcji
                collection.updateOne(eq("bank_account", account), inc("balance", -amount));

                System.out.println("Paycheck successful. New balance: " + (currentBalance - amount));
            } else {
                System.out.println("Insufficient funds. Paycheck failed.");
            }
        } else {
            System.out.println("User with account " + account + " not found.");
        }
    }

    //transfer z jednego konta na drugie
    public void transfer(String fromAccount, String toAccount, int amount) {
        collection = database.getCollection("clients");

        // Find the sender's document
        Document senderDocument = collection.find(eq("bank_account", fromAccount)).first();

        // Find the receiver's document
        Document receiverDocument = collection.find(eq("bank_account", toAccount)).first();

        if (senderDocument != null && receiverDocument != null) {
            // Get the current balances of sender and receiver
            int senderBalance = senderDocument.getInteger("balance", 0);
            int receiverBalance = receiverDocument.getInteger("balance", 0);

            // Check if the sender has enough funds for the transfer
            if (senderBalance >= amount) {
                // Deduct the amount from the sender and add it to the receiver
                collection.updateOne(eq("bank_account", fromAccount), inc("balance", -amount));
                collection.updateOne(eq("bank_account", toAccount), inc("balance", amount));

                System.out.println("Transfer successful. New balances - Sender: " + (senderBalance - amount) +
                        ", Receiver: " + (receiverBalance + amount));
            } else {
                System.out.println("Insufficient funds. Transfer failed.");
            }
        } else {
            System.out.println("One or both of the accounts not found.");
        }
    }

    // wyszukanie użytkownika po id
    public void findUserById(String id) {
        collection = database.getCollection("clients");
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            System.out.println(doc.toJson());
        } else {
            System.out.println("No matching documents found.");
        }
    }

    // pokaż balans konta
    public void showAccountBalance(String account) {
        collection = database.getCollection("clients");

        // Find the user's document
        Document userDocument = collection.find(eq("bank_account", account)).first();

        if (userDocument != null) {
            // Get the current balance of the user
            int currentBalance = userDocument.getInteger("balance", 0);

            System.out.println("Account Balance for account " + account + ": " + currentBalance);
        } else {
            System.out.println("User with account " + account + " not found.");
        }
    }


    // logowanie
    public String login(String email, String password) {
        collection = database.getCollection("clients");

        // Check if there is a user with the provided email and password
        Document userDocument = collection.find(and(eq("email", email), eq("password", password))).first();

        if (userDocument != null) {
            String userId = userDocument.getString("_id");

            if (userId != null) {
                System.out.println("Login successful! User ID: " + userId);
                return userId;
            } else {
                System.out.println("Error retrieving user ID. Login failed.");
                return null;
            }
        } else {
            System.out.println("Invalid email or password. Login failed.");
            return null;
        }
    }


    // Close the MongoDB connection when the instance is no longer needed
    public void closeConnection() {
        mongoClient.close();
    }

    public static void main(String args[]) {

        QuickStart db = new QuickStart();


        db.login("adam@nowak.com", "Nowak");

    }

    }
