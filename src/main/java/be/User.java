package be;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private int User_Id;
    private String Username;
    private String Password;
    private String Role;
    private String ProfileImagePath; // <-- New field
    private byte[] ProfileImage;

    // For loading from the database
    public User(int User_Id, String Username, String Password, String Role, String ProfileImagePath, byte[] ProfileImage) {
        this.User_Id = User_Id;
        this.Username = Username;
        this.Password = Password;
        this.Role = Role;
        this.ProfileImagePath = ProfileImagePath != null ? ProfileImagePath : "/images/profileImageTest.jpg";
        this.ProfileImage = ProfileImage;
    }

    // Overloaded constructor without profile path (for backward compatibility)
    public User(int User_Id, String Username, String Password, String Role) {
        this(User_Id, Username, Password, Role, null, null);
    }

    // For creating new users (before assigning an ID)
    public User(String Username, String Password, String Role) {
        this.Username = Username;
        this.Password = Password;
        this.Role = Role;
    }

    public User(String Username) {
        this.Username = Username;
    }

    public int getUser_Id() {
        return User_Id;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getRole() {
        return Role;
    }

    public String getProfileImagePath() {
        return ProfileImagePath;
    }

    public byte[] getProfileImage() {return ProfileImage;}

    // I am not gonna create setter for id bc I don't think we should change id

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public void setRole(String Role) {
        this.Role = Role;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.ProfileImagePath = profileImagePath;
    }

    public void setProfileImage(byte[] profileImage) {this.ProfileImage = profileImage;}

    @Override
    public String toString() {
        return Username;
    }

    private final StringProperty status = new SimpleStringProperty("Available");  // Default value

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }
}