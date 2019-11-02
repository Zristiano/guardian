package model;

import java.util.UUID;

public class User {

    private UUID id = UUID.randomUUID();

    private String userName;

    public User(String userName){
        this.userName = userName;
    }

    public String getID(){
        return id.toString();
    }

    @Override
    public String toString() {
        return String.format("{id:%s, username:%s}", id, userName);
    }
}
