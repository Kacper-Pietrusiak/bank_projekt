package database;
import java.util.Random;
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
    public static String generateAccountNumber() {
        Random random = new Random();
        // Używamy nextLong, aby uniknąć problemu zbyt dużych liczb dla int
        long result = Math.abs(random.nextLong() % 10000000000L);

        // Dodaj zero z przodu, jeśli liczba ma mniej niż 10 cyfr
        String resultString = String.format("%010d", result);

        return resultString;
    }

    // tworzenie użytkownika
    public String createUser(String name, String password, String email, String pesel) {
        // Create a new Document representing the user
        collection = database.getCollection("clients");

        String bankAccount = generateAccountNumber();
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
                    .append("bank_account", bankAccount)
                    .append("isBanker", false)
                    .append("isBlocked", false);

            // Insert the new user document into the collection
            collection.insertOne(newUser);
            return "User created successfully!";
        } else {
            return "Email or PESEL is not unique. Please choose a different one. Try again.";
        }
    }

    //edycja danych klienta
    public String updateClient(String id, String name, String email) {
        collection = database.getCollection("clients");
        Document userDocument = collection.find(eq("_id", id)).first();

        if (userDocument != null) {
            Document updateFields = new Document();
            updateFields.append("name", name);
            updateFields.append("email", email);

            Document updateDocument = new Document("$set", updateFields);
            collection.updateOne(eq("_id", id), updateDocument);

            return "Update successful. New name: " + name + ", new email: " + email;
        } else {
            return "User with ID " + id + " not found.";
        }
    }

    // wpłata na konto !!!Przerobic żeby zwracało stringa
    public String payment(String id, int amount) {
        collection = database.getCollection("clients");
        Document userDocument = collection.find(eq("_id", id)).first();

        if (userDocument != null) {
            // Pobierz bieżący stan konta użytkownika
            int currentBalance = userDocument.getInteger("balance", 0);

            // Dodaj kwotę do salda
            int newBalance = currentBalance + amount;

            // Zaktualizuj dokument w kolekcji z nowym saldem
            collection.updateOne(eq("_id", id), inc("balance", +amount));

            return "Payment successful. New balance: " + (currentBalance+amount);
        } else {
            return "User with account " + id + " not found.";
        }
    }

    // wypłata z konta !!!Przerobic żeby zwracało stringa
    public String paycheck(String id, int amount) {
        collection = database.getCollection("clients");
        Document userDocument = collection.find(eq("_id", id)).first();

        if (userDocument != null) {
            // Pobierz bieżący stan konta użytkownika
            int currentBalance = userDocument.getInteger("balance", 0);

            // Sprawdź czy użytkownik ma wystarczająco środków
            if (currentBalance >= amount) {
                // Odejmij kwotę od salda i zaktualizuj dokument w kolekcji
                collection.updateOne(eq("_id", id), inc("balance", -amount));

                return "Paycheck successful. New balance: " + (currentBalance - amount);
            } else {
                return "Insufficient funds. Paycheck failed.";
            }
        } else {
            return "User with account " + id + " not found.";
        }
    }

    //transfer z jednego konta na drugie
    public String transfer(String fromAccount, String toAccount, int amount) {
        collection = database.getCollection("clients");

        // Find the sender's document
        Document senderDocument = collection.find(eq("_id", fromAccount)).first();

        // Find the receiver's document
        Document receiverDocument = collection.find(eq("bank_account", toAccount)).first();

        if (senderDocument != null && receiverDocument != null) {
            // Get the current balances of sender and receiver
            int senderBalance = senderDocument.getInteger("balance", 0);
            int receiverBalance = receiverDocument.getInteger("balance", 0);

            // Check if the sender has enough funds for the transfer
            if (senderBalance >= amount) {
                // Deduct the amount from the sender and add it to the receiver
                collection.updateOne(eq("_id", fromAccount), inc("balance", -amount));
                collection.updateOne(eq("bank_account", toAccount), inc("balance", amount));

                return "Transfer successful. New balances - Sender: " + (senderBalance - amount) +
                        ", Receiver: " + (receiverBalance + amount) ;
            } else {
                return "Insufficient funds. Transfer failed.";
            }
        } else {
            return  "One or both of the accounts not found.";
        }
    }


    // pokaż balans konta
    public String showAccountBalance(String id) {
        collection = database.getCollection("clients");

        // Find the user's document
        Document userDocument = collection.find(eq("_id", id)).first();

        if (userDocument != null) {
            // Get the current balance of the user
            int currentBalance = userDocument.getInteger("balance", 0);

            return "Your balance is: " + currentBalance;

        } else {

            return "User with account " + id + " not found.";
        }
    }

    // wyszukanie użytkownika po id
    public String findUserById(String id) {
        collection = database.getCollection("clients");
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            return "name: " + doc.get("name")+ " " + "email: " + doc.get("email")+ " " + "pesel: " + doc.get("pesel");
        } else {
            return "No matching documents found." ;
        }
    }

    public String checkRole(boolean isBanker){
        if(isBanker){
            return "banker";
        }
        return "user";
    }

    // logowanie
    public String login(String email, String password) {
        collection = database.getCollection("clients");

        // Check if there is a user with the provided email and password
        Document userDocument = collection.find(and(eq("email", email), eq("password", password))).first();

        if (userDocument != null) {


            String userId = userDocument.getString("_id");
            String userName = userDocument.getString("name");
            String role = checkRole(userDocument.getBoolean("isBanker"));


            if (userId != null) {
                System.out.println("Login successful! User ID: " + userId);
                return userId + " " + role + " " + userName;
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



//    public static void main(String args[]) {
//
//        QuickStart db = new QuickStart();
//        db.createUser("Marcin Lewandowski", "Lewandowski", "marcin@lewandowski.com", "20202021234");
//        db.createUser("Janusz Kowalski", "Kowalski", "janusz@kowalski.com", "20204321232");
//        db.createUser("Adam Nowak", "Nowak", "adam@nowak.com", "23304321232");
//        db.closeConnection();
//    }

}