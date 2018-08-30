package console_chat;

import java.util.*;
import java.net.*;
import java.io.*;

public class consoleServer 
{
    private String ip;
    private final int port;
    private Map<String, ObjectOutputStream> outsMap;
    
    public consoleServer()
    {
        outsMap = new HashMap<>();
        port = 8080;
        System.out.println("SERVER> created");
    }
    
    /*public void getIP() throws IOException
    {
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while(en.hasMoreElements())
        {
            NetworkInterface ni=(NetworkInterface) en.nextElement();
            Enumeration ee = ni.getInetAddresses();
            while(ee.hasMoreElements()) 
            {
                InetAddress ia= (InetAddress) ee.nextElement();
                String a = ia.getHostAddress();
                if (a.contains("192"))
                    ip = a;
            }
        }
    }*/
    
    public void addClient()
    {
        while (true)
        {
            try(ServerSocket ss = new ServerSocket(port))
            {
                Socket incoming = ss.accept();
                System.out.println("SERVER> new client conncted");
                Thread handlerThread = new Thread(new ServerHandler(incoming));
                handlerThread.start();
            }
            catch(IOException e)
            {
                System.out.println("SERVER> cannot add client");
            }
        }
    }
    
    public synchronized void sendAll(Message msg)
    {
        for (Map.Entry<String, ObjectOutputStream> entry: outsMap.entrySet())
        {   
            ObjectOutputStream out = entry.getValue();
            String key  = entry.getKey();
            try 
            {
                out.writeObject(msg);
                out.flush();
            } 
            catch (IOException ex) 
            {
                outsMap.remove(key);
            }
        }   
    }
    
    public synchronized void sendTo(Message msg)
    {
        String to = msg.getTo();
        try
        {
            ObjectOutputStream out = outsMap.get(to);
            if (out != null)
            {
                out.writeObject(msg);
                out.flush();
            }
        }
        catch(IOException e)
        {
            sendTo(new Message("SERVER", "error while sending to: " + to, to));
        }
    }
    
    public synchronized void showOnline(String n)
    {
        String to = n;
        String onlineList = "USERS ONLINE:";
        
        if (outsMap.size() >= 1)
        {
            for (String name: outsMap.keySet())
            {
                onlineList = onlineList + "\n" + name;
            }
            Message online = new Message("SERVER" , onlineList, to);
            sendTo(online);
        }
        else
        {
            sendTo(new Message("SERVER", "NO USERS ONLINE", to));
        }
    }
    
    private class ServerHandler implements Runnable
    {
        private final Socket s;
        private ObjectInputStream Oin;
        private ObjectOutputStream Oout;
        
        private InputStream inStream;
        private OutputStream outStream;
        
        ServerHandler(Socket incoming)
        {
            s = incoming;
        }
        
        @Override
        public void run()
        {
            connect();
            start();
        }
        
        public void start()
        {
            Message msg;
            String n;
            String m;
            
            while (s.isConnected())
            {
                try 
                {
                    msg = receive();
                    n = msg.getName();
                    m = msg.getMessage();
                    
                    if (m.contains("%off"))
                        outsMap.remove(n, Oout);
                    else if (m.contains("%join"))
                    {
                        outsMap.put(n, Oout);
                        sendAll(new Message(n, "joined the chat"));
                    }
                    else if (!(msg.getTo() == null))
                    {
                        if (m.equals("%online"))
                            showOnline(n);
                        else
                            sendTo(msg);
                    }
                    else if (msg.getTo() == null)
                        sendAll(msg);
                }
                catch (Exception ex) 
                {
                    System.out.println("SERVER> waiting...");
                }
            } 
        }
        
        public boolean connect()
        {
            try
            {
                inStream = s.getInputStream();
                outStream = s.getOutputStream();
                Oin = new ObjectInputStream(inStream);
                Oout = new ObjectOutputStream(outStream);
                return true;
            }
            catch(IOException e)
            {
                System.out.println("SERVER> error in connect():\n" + e.toString());
                return false;
            }
        }
        
        public Message receive() throws Exception
        {
            Message msg;
            msg = (Message)Oin.readObject();
            return msg;            
        }
        
        public int numOfClients()
        {
            return outsMap.size();
        }
        
        public boolean stop()
        {
            try 
            {
                Oout.flush();
                Oout.close();
                Oin.close();
                s.close();
                System.out.println("SERVER> stopped");
                return true;
            } 
            catch (IOException ex) 
            {
                System.out.println("SERVER> error in stop():\n" + ex.toString());
                return false;
            }
        }
    }
}