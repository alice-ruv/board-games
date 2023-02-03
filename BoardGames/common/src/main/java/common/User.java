package common;

import javax.json.bind.annotation.JsonbProperty;

public class User
{
    @JsonbProperty(value = "userId")
    int userId;

    @JsonbProperty(value = "username")
     private String username;

    @JsonbProperty(value = "displayName")
    private String displayName;

    @JsonbProperty(value = "password")
    private String password;

    public User(){}

    public User(int userId, String username, String password, String displayName)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId)
    {
        this.userId = userId;
    }
    @SuppressWarnings("unused") //used by json convertor
    public String getUsername() {
        return username;
    }

    @SuppressWarnings("unused") //used by json convertor
    public void setUsername(String username)
    {
        this.username = username;
    }
    @SuppressWarnings("unused") //used by json convertor
    public String getPassword() {
        return password;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
}
