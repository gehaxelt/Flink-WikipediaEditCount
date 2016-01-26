package it.neef.tu.ba.wectask;

/**
 * Created by gehaxelt on 26.01.16.
 */
public class User {
    private String username;
    private int editCount;

    public User() {
        this.username ="";
        this.editCount = 0;
    }

    public User(String username) {
        this.username = username;
        this.editCount = 0;
    }

    public User(String username, int editCount) {
        this.username = username;
        this.editCount = editCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getEditCount() {
        return editCount;
    }

    public void setEditCount(int editCount) {
        this.editCount = editCount;
    }

    @Override
    public String toString() {
        return "User: " + this.username + ", " + String.valueOf(this.editCount);
    }
}
