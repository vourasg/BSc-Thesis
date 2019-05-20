import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ReconnectPool implements Serializable{
    
    private final ArrayList<String> randomStrings;
    private final ArrayList<Integer> DBuserIDs;
    private final ArrayList<String> UserIDs;
    private final Object lock;
    private boolean locked;
    
    ReconnectPool(Object lock)
    {
        this.lock=lock;
        this.randomStrings = new ArrayList<>();
        this.DBuserIDs = new ArrayList<>();
        this.UserIDs = new ArrayList<>();
        this.locked = false;
    }
    
    //Epistrefei true an einai kleidomeno
    public synchronized boolean Locked(){
        return this.locked; 
    }
    
    //Kleidonei to locked
    public synchronized void Lock(){ 
        if(this.locked)
            try{
                wait();
            }catch(InterruptedException ex){}
        this.locked=true; 
    }
    
    //Xekleidonei to locked
    public synchronized void Unlock(){
        this.locked=false;
        notify();
    }

    
    public void setLists(boolean flag, String randomString,int DBuserID,String UserID){
        if(flag){
            this.DBuserIDs.add(DBuserID);
            this.randomStrings.add(randomString);
            this.UserIDs.add(UserID);
        }
        else{
            this.DBuserIDs.remove(DBuserID);
            this.randomStrings.remove(randomString);
            this.UserIDs.remove(UserID);
        }
    }
    
    public Object[] check(String randomString){
        System.out.println("Current size: "+randomStrings.size());
        for(int i=0; i<randomStrings.size(); i++)
            System.out.println(randomStrings.get(i));
        if(this.randomStrings.indexOf(randomString)!=-1){
            Object[] data = new Object[2];
            data[0] = DBuserIDs.get(this.randomStrings.indexOf(randomString));
            data[1] = UserIDs.get(this.randomStrings.indexOf(randomString));
            return data;
        }
        return null;
    }
    
}
