package common;

import javax.json.bind.annotation.JsonbProperty;

public class SignUpRequest
{
    @JsonbProperty(value = "username")
    private String username;

    @JsonbProperty(value = "displayName")
    private String displayName;

    @JsonbProperty(value = "password")
    private String password;
    
    @SuppressWarnings("unused") //used by json convertor
    public SignUpRequest(){}

    public SignUpRequest(String username, String password, String displayName)
    {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

