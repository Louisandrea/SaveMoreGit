package Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SharedCost implements Serializable
{
    String name;
    String email;
    String sender_email;
    float price;
    float total_amount;

    public SharedCost()
    {

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public float getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getSender_email() {
        return sender_email;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(float total_amount) {
        this.total_amount = total_amount;
    }


    //Mapping the value to Firebase
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("sender_email", sender_email);
        result.put("price", price);
        result.put("total_amount", total_amount);
        result.put("timestamp", ServerValue.TIMESTAMP);

        return result;
    }


}
