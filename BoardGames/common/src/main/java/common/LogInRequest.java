package common;

import javax.json.bind.annotation.JsonbProperty;

public class LogInRequest
{
    @JsonbProperty(value = "username")
    private String username;

    @JsonbProperty(value = "password")
    private String password;
    @SuppressWarnings("unused") //used by json convertor
    public LogInRequest(){}

    public LogInRequest(String username, String password)
    {
        this.username = username;
        this.password = password;
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

}
