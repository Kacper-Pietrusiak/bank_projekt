package database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

class Validators {

    public boolean createUserValidator(String id, String email,String pesel, String account, MongoCollection<Document> collection){

        if(
                collection.countDocuments(Filters.eq("email", email)) == 0 &&
                collection.countDocuments(Filters.eq("pesel", pesel)) == 0 &&
                collection.countDocuments(Filters.eq("id", id)) == 0 &&
                collection.countDocuments(Filters.eq("account", account)) == 0
        ) {return true;}

        return false;
    }


}
