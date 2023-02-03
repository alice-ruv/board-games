package com.example.boardgamesserver;

import javax.ws.rs.*;
import static common.Paths.*;

@Path(TEST)
public class TestApi { //helps to check connectivity from client to server
    @GET
    @Produces("application/string")
    public String test() {

        return "Ok";
    }

}

