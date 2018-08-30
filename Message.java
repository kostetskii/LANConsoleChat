
package console_chat;

import java.io.Serializable;

public class Message implements Serializable
{
    public static int id = 0;
   
    private final String msg;
    private final String name;
    
    private String to = null;
    
    public Message(String name, String msg)
    {
        ++id;
        this.msg = msg;
        this.name = name;
    }
    
    public Message(String name, String msg, String to)
    {
        ++id;
        this.msg = msg;
        this.name = name;
        this.to = to;
    }
    
    public String getMessage()
    {
        return msg;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getTo()
    {
        return to;
    }
}