package console_chat;

import java.io.*;
import java.net.*;

public class consoleClient
{
    private final String name;
    private final String ip;
    private final int port;
    
    private Socket s;
    private ObjectOutputStream Oout;
    private ObjectInputStream Oin;
    
    public consoleClient(String name, String ip)
    {
        port = 8080;
        this.name = name;
        this.ip = ip;
    }
    
    public boolean connect()
    {
        try
        {
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 10000);
            Oout = new ObjectOutputStream(s.getOutputStream());
            Oin = new ObjectInputStream(s.getInputStream());
            Oout.writeObject(new Message(name, "%join"));
            return true;
        }
        catch (SocketException e)
        {
            stop();
            System.out.println(name + "> unable to connect during 10sec\n" + e.toString());
            return false;
        }
        catch(IOException e)
        {
            stop();
            System.out.println(name + "> error in connect():\n" + e.toString());
            return false;
        } 
    }
    
    public boolean send(String m)
    {
        Message msg = new Message(name, m);
        try 
        {
            Oout.writeObject(msg);
            Oout.flush();
            return true;
        } 
        catch (IOException ex) 
        {
            System.out.println(name + "> error in send():\n" + ex.toString());
            return false;
        }
    }
    
    public boolean send(String m, String to)
    {
        Message msg = new Message(name, m, to);
        try 
        {
            Oout.writeObject(msg);
            Oout.flush();
            return true;
        } 
        catch (IOException ex) 
        {
            System.out.println(name + "> error in sendDirect():\n" + ex.toString());
            return false;
        }
    }
    
    public Message receiveOnce() throws Exception
    {   
        Message msg;
        msg = (Message)Oin.readObject();
        return msg;
    }
    
    public void receive() //значення не вертає, юзається тільки для TUI
    {
        try 
        {
            while (isReady())
            {
                Message msg = receiveOnce();
                System.out.println(name + "> received:\n" + 
                        msg.getName() + ": " + msg.getMessage());
            }
        } 
        catch (IOException ex) 
        {
            System.out.println(name + " error in receive():\n" + ex.toString());
            stop();
        } 
        catch (Exception ex) 
        {
            System.out.println(name + " error in receive():\n" + ex.toString());
            stop();
        }
    }
    
    public boolean isReady()
    {
        return s.isConnected();
    }
    
    public boolean getOnline()
    {
        try 
        {
            Message msg = new Message(name, "%online", name);
            Oout.writeObject(msg);
            System.out.println(name + "> requested online");
            return true;
        } 
        catch (IOException ex) 
        {
           System.out.println(name + "I/O error in getOnline()");
           return false;
        }
    }
    
    public boolean stop()
    {
        send(" " + name + "left :(");
        send("%off");
        try 
        {
            Oout.flush();
            Oout.close();
            Oin.close();
            s.close();
            return true;
        }
        catch(IOException e)
        {
            System.out.println(name + "> error in stop():\n" + e.toString());
            return false;
        }
    }
}