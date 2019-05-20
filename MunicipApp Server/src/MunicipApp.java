import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MunicipApp 
{
    public static void main(String[] args)
    {
        ServerSocket server;
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        Object lock = new Object();
        ReconnectPool pool=new ReconnectPool(lock);
        try
        {
            server=new ServerSocket(4444);
            System.out.println("Server started");
            do
            {
                try{
                    socket=server.accept();
                    System.out.println("Client's IP: "+socket.getInetAddress());
                    out=new ObjectOutputStream(socket.getOutputStream());
                    in=new ObjectInputStream(socket.getInputStream());
                    new ServerThread(socket,in,out,pool).start();
                }catch(Exception ignore){}
                
            }while(true);

        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    
    
    
}
