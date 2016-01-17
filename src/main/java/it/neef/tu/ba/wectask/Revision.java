package it.neef.tu.ba.wectask;

/**
 * Created by gehaxelt on 17.01.16.
 */
public class Revision {
    int id;
    String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Revision: " + String.valueOf(this.id) + ", " + this.username;
    }
}
