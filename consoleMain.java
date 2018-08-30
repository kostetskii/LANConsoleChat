package console_chat;

public class consoleMain 
{    
    public static void main(String[] args) throws InterruptedException
    {   
        Thread srv = new Thread(() -> 
        {
            consoleServer s = new consoleServer();
            s.addClient();
        });
        srv.start();
        
        Thread.sleep(30);
        
        Thread cl2 = new Thread(() -> 
        {
            consoleClient c2 = new consoleClient("listener", "192.168.0.100");
            c2.connect();
            c2.receive();
        });
        cl2.start();
        
        Thread.sleep(30);
        
         Thread cl5 = new Thread(() -> 
        {
            consoleClient c5 = new consoleClient("5", "192.168.0.100");
            c5.connect();
            c5.receive();
        });
        cl5.start();
        
        Thread.sleep(30);
        
        Thread cl1 = new Thread(() -> 
        {
            consoleClient c1 = new consoleClient("ZZZZ", "192.168.0.100");
            c1.connect();
            c1.getOnline();
            c1.receive();
        });
        cl1.start();
    }
}
