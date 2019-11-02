package model;

import java.util.UUID;

public class User {

    private UUID id = UUID.randomUUID();

    public User(){

    }

    @Override
    public String toString() {
        return String.format("{id:%s}", id);
    }
}
