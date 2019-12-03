package model;

import java.util.UUID;

public class User {

    private UUID id;

    private String userName;

    public User(String userName){
        this.userName = userName;
        this.id = UUID.randomUUID();
    }

    public User(String userName, String id){
        this.userName = userName;
        this.id = UUID.fromString(id);
    }

    public String getID(){
        return id.toString();
    }

    @Override
    public String toString() {
        return String.format("{id:%s, username:%s}", id, userName);
    }

}
