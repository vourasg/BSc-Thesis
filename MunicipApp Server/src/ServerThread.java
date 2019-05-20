
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.net.SocketException;



public class ServerThread extends Thread
{
    private Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    private static String DeviceID;
    private static String UserID;
    private static int DBuserID;
    private static String Password;
    public static Connection conn;
    ArrayList <Reports> List,UserList,LocalityList;
    private String DB_User_Name="server";
    private String DB_Password="A1b2C3d4E5f6G7h8I9j10K11";
    private String login_type;
    ReconnectPool pool;
    private SecureRandom random;
    private String randomString;

    
    
    ServerThread(Socket socket,ObjectInputStream in,ObjectOutputStream out,ReconnectPool pool)
    {
        super();
        this.socket=socket;
        this.in=in;
        this.out=out;
        this.pool=pool;
        this.random = new SecureRandom();
        try{
            Class.forName("org.gjt.mm.mysql.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/municipapp", DB_User_Name, DB_Password);
            
        }catch(ClassNotFoundException | SQLException ex){ex.printStackTrace(System.err);}
    }

        
        
    @Override
    public void run()
    {
        String response;


        //Read connect
        try
        {
            boolean connected=false;
            while(!connected)
            {
                response=(String)in.readObject();
                if(response.equalsIgnoreCase("CONNECT"))
                {
                    System.err.println("Action: "+response);
                    //Read Device
                    DeviceID=(String)in.readObject();
                    System.out.println("DeviceID "+DeviceID);
                    login_type="facebook";
                    UserID=(String)in.readObject();
                    System.out.println("UserID "+UserID);
                    //Check user if blocked
                    if(checkNew())
                    {
                        out.writeObject("YES");
                        out.flush();
                        addNew((String)in.readObject(),(String)in.readObject(),(String)in.readObject());
                    }
                    else{
                        out.writeObject("NO");
                        out.flush();
                    }
                    
                    if(checkBlocked())
                    {
                        out.writeObject("YES");
                        out.flush();
                        System.exit(0);
                    }
                    else
                    {
                        out.writeObject("NO");
                        out.flush();
                        out.writeObject(DBuserID);
                        out.flush();
                        randomString=new BigInteger(130, random).toString(32);
                        out.writeObject(randomString);
                        out.flush();
                        pool.Lock();
                        pool.setLists(true, randomString, DBuserID, UserID);
                        pool.Unlock();
                        
                    }
                }
                
                else if(response.equals("VALIDATE"))
                {
                    System.err.println("Action: "+response);
                    login_type="app";
                    UserID=(String)in.readObject();
                    Password=(String)in.readObject();
                    if(Validate())
                    {
                        //write true
                        out.writeObject(true);
                        out.flush();

                        DeviceID=(String)in.readObject();
                        if(checkBlocked())
                        {
                            out.writeObject("YES");
                            out.flush();
                            this.interrupt();
                        }
                        else
                        {
                            out.writeObject("NO");
                            out.flush();
                            //write user name and image
                            Object[] info;
                            info=getInfo();
                            //write name
                            if(info[0]!=null)
                                out.writeObject(info[0].toString());
                            else
                                out.writeObject(null);
                            out.flush();
                            //write image
                            if(info[1]!=null)
                                out.writeObject(info[1]);
                            else
                                out.writeObject(null);
                            out.flush();
                            //write bday
                            if(info[2]!=null)
                                out.writeObject(info[2].toString());
                            else
                                out.writeObject(null);
                            out.flush();
                            
                            out.writeObject(DBuserID);
                            out.flush();
                            randomString=new BigInteger(130, random).toString(32);
                            out.writeObject(randomString);
                            out.flush();
                            pool.Lock();
                            pool.setLists(true, randomString, DBuserID, UserID);
                            pool.Unlock();
                        }
                    }
                    else
                    {
                        out.writeObject(false);
                        out.flush();
                    }
                    
                }
                else if(response.equals("REGISTER"))
                {
                    System.err.println("Action: "+response);
                    String email=(String)in.readObject();
                    System.out.println("email: "+email);
                    String password=(String)in.readObject();
                    System.out.println("password: "+password);
                    String username=(String)in.readObject();
                    System.out.println("name: "+username);
                    String register=Register(email,password,username);
                    out.writeObject(register);
                    out.flush();
                    if(register.equalsIgnoreCase("ok"))
                        this.interrupt();

                }
                
                else if(response.equals("RECONNECT"))
                {
                    boolean hasLock=false;
                    System.err.println("Action: "+response);
                    try{
                        randomString=(String)in.readObject();
                        System.err.println("User String: "+randomString);
                        pool.Lock();
                        hasLock=true;
                        Object[] userData=pool.check(randomString);
                        if(userData!=null)
                        {
                            System.err.println("User: "+DBuserID+" reconnected");
                            out.writeObject("reconnected");
                            out.flush();
                            DBuserID=(int)userData[0];
                            UserID=(String)userData[1];
                            connected=true;
                            
                        }
                        else
                        {
                            System.err.println("User unable to reconnect");
                            out.writeObject("reconnect");
                            out.flush();
                        }
                        pool.Unlock();
                        hasLock=false;
                    }catch(SocketException e){if(hasLock) pool.Unlock();}
                }
                else if(response.equals("LOGOUT"))
                {
                    try{
                        System.err.println("logout");
                        socket.close();
                        out.close();
                        in.close();
                        conn.close();
                        pool.Lock();
                        pool.setLists(false, randomString, DBuserID, UserID);
                        pool.Unlock();
                        this.interrupt();
                    }catch(IOException | SQLException ex){ex.printStackTrace(System.err);}
                }
               
            }
        
        }catch(IOException | ClassNotFoundException ex){ex.printStackTrace(System.err);}
        
        //Generate session random string
        
        
        //Read action
        response="";
        try{
            response=(String)in.readObject();
        }catch(IOException | ClassNotFoundException ex){ex.printStackTrace(System.err);}
        
        try
        {
            if(response.equalsIgnoreCase("SET_INFO"))
            {
                System.err.println("Action: "+response);
                String s = (String)in.readObject();
                switch (s) {
                    case "name":
                        out.writeObject(setInfo(1,in.readObject(),null));
                        break;
                    case "email":
                        out.writeObject(setInfo(2,in.readObject(),in.readObject()));
                        break;
                    case "birthday":
                        out.writeObject(setInfo(3,in.readObject(),null));
                        break;
                    case "password":
                        out.writeObject(setInfo(4,in.readObject(),(String)in.readObject()));
                        break;
                    case "image":
                        out.writeObject(setInfo(5,in.readObject(),null));
                        break;
                }
                out.flush();

            }
            if(response.equalsIgnoreCase("ADD"))
            {
                System.err.println("Action: "+response);
                Reports report;
                String userID,Report_cat,Report_type,Description,ImageComment,Locality;
                double Latitude,Longitude;
                byte[] Image;
                //Read userID
                userID=(String)in.readObject();
                //Read Report Category
                Report_cat=(String)in.readObject();
                //Read Report Type
                Report_type=(String)in.readObject();
                //Read Report Description
                Description=(String)in.readObject();
                //Read image bytes
                if(((String)in.readObject()).equals("image"))
                {
                    Image=(byte[])in.readObject();
                    ImageComment=(String)in.readObject();
                }
                else
                {
                    Image=null;
                    ImageComment=null;
                }
                //Read Latitude
                Latitude=(double)in.readObject();
                //Read Longitude
                Longitude=(double)in.readObject();
                //Read Locality
                Locality=(String)in.readObject();

                report=new Reports(0,DBuserID,Report_cat,Report_type,Description,Image!=null?Image:null,ImageComment,Latitude,Longitude,Locality,0,0,"");

                if(Add(report))
                {
                    System.err.println("ADDED");
                    out.writeObject("ADDED");
                    out.flush();
                }
                else
                {
                    System.err.println("ERROR");
                    out.writeObject("ERROR");
                    out.flush();
                }

            }
            else if(response.equals("Details"))
            {
                System.err.println("Action: "+response);
                int report_id=(int)in.readObject();
                Reports report=Review(5," ",report_id).get(0);
                if(report!=null)
                {
                    //write sending
                    out.writeObject("SENDING");
                    out.flush();
                    //write reportID
                    out.writeObject(report.getReportID());
                    out.flush();
                    //write usersID
                    out.writeObject(report.getUserID());
                    out.flush();
                    //write Report Category
                    out.writeObject(report.getReportCategory());
                    out.flush();
                    //write Report Type
                    out.writeObject(report.getReportType());
                    out.flush();
                    //write Report Type
                    out.writeObject(report.getDescription());
                    out.flush();
                    //write image
                    out.writeObject(report.getImage());
                    out.flush();
                    //write image comment
                    out.writeObject(report.getImageComment());
                    out.flush();
                    //write Report Latitude
                    out.writeObject(report.getLatitude());
                    out.flush();
                    //write Report Longitude
                    out.writeObject(report.getLongitude());
                    out.flush();
                    //write Locality
                    out.writeObject(report.getLocality());
                    out.flush();
                    //write up votes
                    out.writeObject(report.getUpVotes());
                    out.flush();
                    //write down votes
                    out.writeObject(report.getDownVotes());
                    out.flush();

                    out.writeObject(checkAction(report_id));
                    out.flush();

                }


            }

            else if(response.equals("upvote"))
            {
                System.err.println("Action: "+response);
                int id=(int)in.readObject();
                String result=upvote(id);
                out.writeObject(result);
                out.flush();
                System.out.println("upvote result: "+result);
            }
            else if(response.equals("downvote"))
            {
                System.err.println("Action: "+response);
                int id=(int)in.readObject();
                String result=downvote(id);
                out.writeObject(result);
                out.flush();
                System.out.println("downvote result: "+result);

            }

            else if(response.equals("delete"))
            {
                System.err.println("Action: "+response);
                int id=(int)in.readObject();
                String result=delete(id);
                out.writeObject(result);
                out.flush();
                System.out.println("Delete result: "+result);

            }
            else if(response.equals("comment"))
            {
                System.err.println("Action: "+response);
                int report_id=(int)in.readObject();
                String comment=(String)in.readObject();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                if(addComment(report_id,comment,sdf.format(cal.getTime())))
                    out.writeObject("OK");
                else
                    out.writeObject("ERROR");
                out.flush();

            }
            else if(response.equalsIgnoreCase("achievements"))
            {
                System.err.println("Action: "+response);
                ArrayList <String> personal;
                ArrayList <String> topSingle;
                ArrayList <String> topTotal;

                personal=personalAchievements();
                topSingle=topAchievements(1);
                topTotal=topAchievements(2);

                //write personal score (ArrayList<String>)
                out.writeObject(personal);
                out.flush();
                 //write top single scores (ArrayList<String>)
                out.writeObject(topSingle);
                out.flush();
                 //write top total score (ArrayList<String>)
                out.writeObject(topTotal);
                out.flush();
            }
            else if(response.equals("refresh_comments"))
            {
                System.err.println("Action: "+response);
                ArrayList<Comments> comments;
                // Read reports_id
                int report_id=(int)in.readObject();
                System.err.println("search vomments for: "+report_id);
                comments=getComments(report_id);
                // Write comments count
                out.writeObject(comments.size());
                for(Comments comments1 : comments)
                {
                    // Write level
                    out.writeObject(comments1.level);
                    out.flush();
                    //write comment_id
                    out.writeObject(comments1.id);
                    out.flush();
                    // write comment's owner
                    out.writeObject(comments1.user_id);
                    out.flush();
                    // Write comment text
                    out.writeObject(comments1.comment);
                    out.flush();
                    // Write comment's date
                    out.writeObject(comments1.date);
                    out.flush();
                    if(comments1.facebook_id!=null)
                    {
                        out.writeObject("facebook");
                        out.flush();
                        out.writeObject(comments1.facebook_id);
                        out.flush();
                        out.writeObject(comments1.username);
                    }
                    else
                    {
                        if(comments1.user_image!=null)
                            out.writeObject("app_image");
                        else
                            out.writeObject("app");
                        out.flush();

                        out.writeObject(comments1.username);
                        if(comments1.user_image!=null)
                        {
                            out.flush();
                            out.writeObject(comments1.user_image);
                        }
                    }
                    out.flush();


                }

            }
            else if(response.equals("refresh_open"))
            {
                System.err.println("Action: "+response);
                String locality=(String)in.readObject();
                LocalityList=Review(2,locality,0);
                out.writeObject(LocalityList.size());
                out.flush();

                for (Reports LocalityList1 : LocalityList) {
                    out.writeObject(LocalityList1.getReportID());
                    out.flush();

                    out.writeObject(LocalityList1.getReportCategory());
                    out.flush();
                    out.writeObject(LocalityList1.getReportType());
                    out.flush();
                    out.writeObject(LocalityList1.getDescription());
                    out.flush();                        
                    out.writeObject(LocalityList1.getLatitude());
                    out.flush();
                    out.writeObject(LocalityList1.getLongitude());
                    out.flush();
                    out.writeObject(LocalityList1.getLocality());
                    out.flush();
                    out.writeObject(LocalityList1.getDate());
                    out.flush();
                }
            }
            else if(response.equals("refresh_progress"))
            {
                System.err.println("Action: "+response);
                String locality=(String)in.readObject();
                LocalityList=Review(3,locality,0);
                out.writeObject(LocalityList.size());
                out.flush();

                for (Reports LocalityList1 : LocalityList) {
                    out.writeObject(LocalityList1.getReportID());
                    out.flush();

                    out.writeObject(LocalityList1.getReportCategory());
                    out.flush();
                    out.writeObject(LocalityList1.getReportType());
                    out.flush();
                    out.writeObject(LocalityList1.getDescription());
                    out.flush();                        
                    out.writeObject(LocalityList1.getLatitude());
                    out.flush();
                    out.writeObject(LocalityList1.getLongitude());
                    out.flush();
                    out.writeObject(LocalityList1.getLocality());
                    out.flush();
                    out.writeObject(LocalityList1.getDate());
                    out.flush();
                }
            }
            else if(response.equals("refresh_my"))
            {
                System.err.println("Action: "+response);
                UserList=Review(1,"",0);

                out.writeObject(UserList.size());
                out.flush();

                for (Reports UserList1 : UserList) {
                    out.writeObject(UserList1.getReportID());
                    out.flush();

                    out.writeObject(UserList1.getReportCategory());
                    out.flush();
                    out.writeObject(UserList1.getReportType());
                    out.flush();
                    out.writeObject(UserList1.getDescription());
                    out.flush();
                    out.writeObject(UserList1.getLatitude());
                    out.flush();
                    out.writeObject(UserList1.getLongitude());
                    out.flush();
                    out.writeObject(UserList1.getLocality());
                    out.flush();
                    out.writeObject(UserList1.getDate());
                    out.flush();
                }
            }
            else if(response.equals("refresh_closed"))
            {
                System.err.println("Action: "+response);
                String locality=(String)in.readObject();
                LocalityList=Review(4,locality,0);
                out.writeObject(LocalityList.size());
                out.flush();

                for (Reports LocalityList1 : LocalityList) {

                    out.writeObject(LocalityList1.getReportID());
                    out.flush();
                    out.writeObject(LocalityList1.getReportCategory());
                    out.flush();
                    out.writeObject(LocalityList1.getReportType());
                    out.flush();
                    out.writeObject(LocalityList1.getDescription());
                    out.flush();
                    out.writeObject(LocalityList1.getLatitude());
                    out.flush();
                    out.writeObject(LocalityList1.getLongitude());
                    out.flush();
                    out.writeObject(LocalityList1.getLocality());
                    out.flush();
                    out.writeObject(LocalityList1.getDate());
                    out.flush();
                }
            }
            else if(response.equals("report_app"))
            {
                out.writeObject(appReport((String)in.readObject()));
                out.flush();
            }
            else if(response.equals("subComment"))
            {
                System.err.println("Action: "+response);
                int comment_id=(int)in.readObject();
                System.out.println("subComment: "+comment_id);
                String comment=(String)in.readObject();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                out.writeObject(subComment(comment_id,comment,sdf.format(cal.getTime())));
                out.flush();
            }
            else if(response.equals("deleteComment"))
            {
                System.err.println("Action: "+response);
                int comment_id=(int)in.readObject();
                System.out.println("deleteComment: "+comment_id);
                out.writeObject(deleteComment(comment_id));
                out.flush();
            }
            else if(response.equals("reportComment"))
            {
                System.err.println("Action: "+response);
                int comment_id=(int)in.readObject();
                System.out.println("reportComment: "+comment_id);
                out.writeObject(reportComment(comment_id));
                out.flush();
            }
            


        }catch(IOException | ClassNotFoundException | NumberFormatException ex){
            try{
                socket.close();
                out.close();
                in.close();
                conn.close();
                this.interrupt();
            }catch(IOException | SQLException ex2){ex2.printStackTrace(System.out);}
        }

        try{
            System.err.println("EXIT");
            socket.close();
            out.close();
            in.close();
            conn.close();
            this.interrupt();
        }catch(IOException | SQLException ex){ex.printStackTrace(System.err);}
        
    }
    
    
    
    public void connectDB()
    {
        try{
            Class.forName("org.gjt.mm.mysql.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/municipapp", DB_User_Name, DB_Password);
        }catch(ClassNotFoundException | SQLException ex){ex.printStackTrace(System.err);}
    }
    
    
    
    public boolean checkNew()
    {
        String query;
        Statement statement;
        ResultSet rs;
        String id;
        
        try
        {
            if(conn.isClosed())
                connectDB();
            query = "SELECT facebook_id,id FROM users";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next())
            {
                id=rs.getString(1);
                System.out.println("Checking new:   "+UserID.equals(id));

                if(UserID.equals(id))
                {
                    DBuserID=rs.getInt(2);
                    return false;
                }
                    
                
            }
        }catch(SQLException ex){ex.printStackTrace(System.err);}
        return true;
        
    }
    
    
    
    public void addNew(String name,String email,String birthday)
    {
        String query;
        Statement statement;
        ResultSet rs;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        java.util.Date utilDate;
        java.sql.Date sqlDate;
        int count=0;
        
        try
        {
            if(conn.isClosed())
                connectDB();
            
            query = " INSERT INTO users (fb_email, fb_name, facebook_id, birthday) VALUES (?, ?, ?, ?);";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString       (1, email);
            preparedStmt.setString       (2, name);
            preparedStmt.setString       (3, UserID);
            if(birthday!=null)
            {
                utilDate = format.parse(birthday);
                sqlDate = new java.sql.Date(utilDate.getTime());
                preparedStmt.setDate         (4, sqlDate);
            }
            else
                preparedStmt.setDate         (4, null);
            

            preparedStmt.execute();
            
            
            query = "SELECT id FROM users WHERE facebook_id='"+UserID+"';";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            rs.next();
            DBuserID=(rs.getInt("id"));
        }catch(SQLException | ParseException ex){ex.printStackTrace(System.err);}
    }
    
    
    public void registerUserDeviceComb()
    {
        String query;
        ResultSet rs;
        Statement statement;
        PreparedStatement preparedStmt;
        try
        {
            if(conn.isClosed())
                connectDB();
            query = "SELECT id FROM users_device WHERE user_id="+DBuserID+" AND device_id='"+DeviceID+"';";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            
            if(!rs.next())
            {
                query = " INSERT INTO users_device (user_id,device_id) VALUES (?, ?);";
                preparedStmt = conn.prepareStatement(query);
                preparedStmt.setInt    (1, DBuserID);
                preparedStmt.setString (2, DeviceID);
                preparedStmt.execute();
            }
            
        }catch(SQLException ex){}
    }
    
    
    public boolean checkBlocked()
    {
        String query;
        Statement statement;
        ResultSet rs;
        try{
            if(conn.isClosed())
                connectDB();
            query = "SELECT * FROM reported_devices";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next())
            {
                String device_id=rs.getString(2);
                if(device_id.equals(DeviceID))
                    return true;
            }
            
            query = "SELECT * FROM reported_users";

            rs = statement.executeQuery(query);
            while (rs.next())
            {
                String user_id=rs.getString(2);
                if(user_id.equals(String.valueOf(DBuserID)))
                    return true;
                
            }

            
        }catch(Exception ex){ex.printStackTrace(System.err);}
        
        return false;
    }
    
    
    
    
    
    
    public boolean Validate()
    {
        try
        {
            if(conn.isClosed())
                connectDB();
            String query = "SELECT id,email, password "
                    + "FROM users "
                    + "WHERE email='"+UserID+"' AND password='"+Password+"';";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if(rs.next())
            {
                DBuserID=rs.getInt("id");
                return true;
            }
                
            
        }catch(Exception ex){ex.printStackTrace(System.err);}
        return false;
        
    }
    
    
    private Object[] getInfo()
    {
        Object[] info=new Object[3];
        info[0]=null;
        info[2]=null;
        try
        {
            if(conn.isClosed())
                connectDB();
            String query = "SELECT username,image,date_format(birthday, '%d/%m/%Y') "
                    + " from users "
                    + " where id='"+DBuserID+"'";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            
            rs.next();
            info[0]=rs.getString(1);
            System.out.println("getinfo(name)"+info[0].toString());
            info[2]=rs.getString(3);
            byte b[];
            Blob blob;
            blob=rs.getBlob(2);
            if(blob!=null)
            {
                b=blob.getBytes(1,(int)blob.length());
                info[1]=blob.getBytes(1,(int)blob.length());
            }
            else
                info[1]=null;
            
            return info;
        }catch(Exception ex){ex.printStackTrace(System.err);}
        return null;
    }
    
    
    private String setInfo(int flag,Object obj1,Object obj2) throws IOException
    {
        String query;
        PreparedStatement preparedStmt;
        try
        {
            if(conn.isClosed())
                connectDB();
            if(flag==1)
            {
                try{
                    query="UPDATE users SET username='"+(String)obj1+"' WHERE id='"+DBuserID+"';";
                    preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    return "name_ok";
                }catch(SQLException ex)
                {
                    ex.printStackTrace();
                    return "name_in_use";
                }
            }
            if(flag==2)
            {
                query = "SELECT id,password "
                    + " from users "
                    + " where id='"+DBuserID+"' AND password='"+(String)obj1+"';";
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(query);
            
                if(rs.next())
                {
                    try
                    {
                        query="UPDATE users SET email='"+(String)obj2+"' WHERE id='"+DBuserID+"';";
                        preparedStmt = conn.prepareStatement(query);
                        preparedStmt.execute();
                    }catch(SQLException ex){System.err.println("email in use"); return "email_is_use";}
                    
                }
                else
                    return "wrong_password";
                return "email_ok";

            }
            if(flag==3)
            {
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

                try {
                    java.util.Date utilDate = format.parse((String)obj1);
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    System.out.println("setinfo(date):"+sqlDate);
                
                    query="UPDATE users SET birthday='"+sqlDate+"' WHERE id='"+DBuserID+"';";
                    preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    return "bday_ok";
                    
                } catch (ParseException e) {
                    e.printStackTrace(System.err);
                    return "wrong_bday";
                }
            }
            if(flag==4)
            {
                query = "SELECT id,password "
                    + " from users "
                    + " where id='"+DBuserID+"' AND password='"+(String)obj1+"';";
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(query);
            
                if(rs.next())
                {
                    query="update users set password='"+(String)obj2+"' where ID="+DBuserID+";";
                    preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                }
                else
                    return "wrong_password";
                return "password_ok";
            }
            if(flag==5)
            {               
                query="UPDATE users SET image=? WHERE id='"+DBuserID+"';";
                preparedStmt = conn.prepareStatement(query);
                InputStream input = new ByteArrayInputStream((byte[])obj1);
                preparedStmt.setBinaryStream (1, input,((byte[])obj1).length);
                preparedStmt.execute();
                return "image_ok";
            }
        }catch(SQLException ex){ex.printStackTrace(System.err); return "error";}
        return "error";
    }
    
    
    
    public String Register(String email,String password,String name)
    {
        String query;
        PreparedStatement preparedStmt;
        try
        {
            if(conn.isClosed())
                connectDB();
            query = "INSERT INTO users (email, password) VALUES (?, ?)";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString       (1, email);
            preparedStmt.setString       (2, password);
            preparedStmt.execute();
        }catch(SQLException ex){ex.printStackTrace(System.err);return "email";}
        
        try
        {
            if(conn.isClosed())
                connectDB();
            query = "UPDATE users SET username =(?) WHERE email='"+email+"';";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString       (1, name);
            preparedStmt.execute();
        }catch(SQLException ex){ex.printStackTrace(System.err);return "name";}
        return "ok";
        
    }
    
    
    
    public boolean Add(Reports report)
    {
        try
        {
            if(conn.isClosed())
                connectDB();
            String query = " INSERT INTO reports (user_id, Category, Type,Description, Latitude, Longitude, Locality, Image, ImageComment, Upvotes, Downvotes)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt       (1, report.getUserID());
            preparedStmt.setString       (2, report.getReportCategory());
            preparedStmt.setString       (3, report.getReportType());
            preparedStmt.setString       (4, report.getDescription());
            preparedStmt.setDouble       (5, report.getLatitude());
            preparedStmt.setDouble       (6, report.getLongitude());
            preparedStmt.setString       (7, report.getLocality());
            if(report.getImage()==null)
            {
                preparedStmt.setBinaryStream (8, null);
                preparedStmt.setString       (9, " ");
            }
            else
            {
                InputStream input = new ByteArrayInputStream(report.getImage());
                preparedStmt.setBinaryStream (8, input,report.getImage().length);
                preparedStmt.setString       (9, report.getImageComment());
            }
            preparedStmt.setInt          (10, 0);
            preparedStmt.setInt          (11, 0);
            

            
            preparedStmt.execute();

        }catch ( SQLException  ex){
            ex.printStackTrace(System.err);            
            return false;
        }
        return true;
    }
    
    
    
    
    public void changePassword(String password)
    {
        String query;
        
        try
        {
            if(conn.isClosed())
                connectDB();
            
            query="update users set password="+password+" where ID="+DBuserID+";";
                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
            
        }catch(Exception ex){ex.printStackTrace(System.err);}
        
    }
        
    
    
    
    
    public ArrayList<Reports> Review(int flag, String Locality,int id)
    {
        String query;
        ArrayList <Reports> List1=new ArrayList<>();
        try
        {
            if(conn.isClosed())
                connectDB();
            if(flag==1)
                query = "SELECT * FROM reports WHERE user_id="+DBuserID+";";
            else if(flag==2)
                query = "SELECT * FROM reports WHERE locality LIKE '%"+Locality+"%' AND state='open';";
            else if(flag==3)
                query = "SELECT * FROM reports WHERE locality LIKE '%"+Locality+"%' AND state='in progress';";
            else if(flag==4)
                query = "SELECT * FROM reports WHERE locality LIKE '%"+Locality+"%' AND state='closed';";
            else
                query = "SELECT * FROM reports WHERE id ="+id+";";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next())
            {                
                byte b[];
                Blob blob;
                blob=rs.getBlob("Image");

                Reports report = new Reports();
                report.setReportID(rs.getInt("ID"));
                report.setUserID(rs.getInt("user_id"));
                report.setReportCategory(rs.getString("Category"));
                report.setReportType(rs.getString("Type"));
                report.setDescription(rs.getString("Description"));
                report.setLocation(rs.getDouble("Latitude"), rs.getDouble("Longitude"));
                report.setLocality(rs.getString("Locality"));
                report.setUpVotes(rs.getInt("Upvotes"));
                report.setDownVotes(rs.getInt("Downvotes"));
                Date date=rs.getDate("date");
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                report.setDate(df.format(date));
                
                if(blob!=null)
                {
                    report.setImage(blob.getBytes(1,(int)blob.length()));
                    report.setImageComment(rs.getString("ImageComment"));
                }
                else
                {
                    report.setImage(null);
                    report.setImageComment(" ");
                }
                

                //System.out.println(rs.getString(1)+"\n"+rs.getString(2)+"\n"+rs.getString(3)+"\n"+rs.getString(4)+"\n"+"\n"+rs.getDouble(6)+"\n"+rs.getDouble(7)+"\n"+rs.getInt(8)+"\n"+rs.getInt(9));            
                List1.add(report);
            }
            
            return List1;
        }catch (Exception ex){
            ex.printStackTrace(System.err);
            return null;
        }
    }
    
    
    public ArrayList<Comments> getComments (int id)
    {
        ArrayList <Comments>comments=new ArrayList<>();
        String L1_query; 
        String L2_query;
        String user_info_query;
        ResultSet L1_rs;
        ResultSet L2_rs;
        ResultSet user_info_rs;
        Statement statement,statement2,statement3;
        Comments comment;
        int comment_id,user_id,subcomment_id,subuser_id;
        String commentS,date,subcommentS,subdate;
        
        try
        {
            if(conn.isClosed())
                connectDB();
            
            L1_query= "SELECT id,user_id,comment,date FROM comments "
                + "WHERE report_id="+id+" AND parent_id IS NULL;";
            
            user_info_query="SELECT username,facebook_id,image,fb_name FROM users WHERE id=";
            
            statement = conn.createStatement();
            statement2 = conn.createStatement();
            statement3 = conn.createStatement();
            L1_rs = statement.executeQuery(L1_query);
            while (L1_rs.next())
            {
                comment_id=L1_rs.getInt(1);
                user_id=L1_rs.getInt(2);
                commentS=L1_rs.getString(3);
                date=L1_rs.getString(4);
                
                comment=new Comments(comment_id,user_id,1,commentS,null,null,null,date);
                
                user_info_rs=statement2.executeQuery(user_info_query+String.valueOf(user_id));
                if(user_info_rs.first())
                {
                    if(user_info_rs.getString(1)!=null)
                    {
                        comment.username=user_info_rs.getString(1);
                        if(user_info_rs.getBlob(3)!=null)
                            comment.user_image=user_info_rs.getBlob(3).getBytes(1,(int)user_info_rs.getBlob(3).length());
                    }
                    else
                    {
                        comment.facebook_id=user_info_rs.getString(2);
                        comment.username=user_info_rs.getString(4);
                    }
                            
                }
                comments.add(comment);
            }
                
            int i;
            for(i=0; i<comments.size(); i++)
            {
                L2_query="SELECT id,user_id,comment,date FROM comments "
                    + "WHERE parent_id="+comments.get(i).id;
                L2_rs = statement3.executeQuery(L2_query);
                while(L2_rs.next())
                {
                    comment=new Comments(L2_rs.getInt(1),L2_rs.getInt(2),2,L2_rs.getString(3),null,null,null,L2_rs.getString(4));
                    user_info_rs=statement.executeQuery(user_info_query+L2_rs.getInt(2));
                    if(user_info_rs.first())
                    {
                        if(user_info_rs.getString(1)!=null)
                        {
                            comment.username=user_info_rs.getString(1);
                            if(user_info_rs.getBlob(3)!=null)
                                comment.user_image=user_info_rs.getBlob(3).getBytes(1,(int)user_info_rs.getBlob(3).length());
                        }
                        else
                            comment.facebook_id=user_info_rs.getString(2);
                        comments.add(++i,comment);
                    }
                
                }
            }

  
        }catch(Exception ex){ex.printStackTrace(System.err);}
        
        
        return comments;
    }
    
    
    public boolean addComment(int id,String comment,String date)
    {
        String query="INSERT INTO comments (comment,report_id,date,user_id) VALUES(?,?,?,?);";
        try
        {
            if(conn.isClosed())
                connectDB();
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString       (1, comment);
            preparedStmt.setInt          (2, id);
            preparedStmt.setString       (3, date);
            preparedStmt.setInt          (4, DBuserID);
            
            preparedStmt.execute();
            return true;
        }catch(Exception ex){ex.printStackTrace(System.err);}
        return false;
    }
    
    
    public String subComment(int id,String comment,String date)
    {
        String query;
        ResultSet rs;
        Statement statement;
        try
        {
            if(conn.isClosed())
                connectDB();
            query="SELECT * FROM comments WHERE id="+id+";";
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            if(!rs.first())
                return "parent comment deleted";
            
            query="INSERT INTO comments (comment,parent_id,date,user_id) VALUES(?,?,?,?);";
            if(conn.isClosed())
                connectDB();
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString       (1, comment);
            preparedStmt.setInt          (2, id);
            preparedStmt.setString       (3, date);
            preparedStmt.setInt          (4, DBuserID);
            
            preparedStmt.execute();
            return "completed";
        }catch(Exception ex){ex.printStackTrace(System.err);}
        return "comment not found";
    }
    
    
    public String deleteComment(int id)
    {
        String query="SELECT * FROM comments WHERE id="+id+" AND user_id="+DBuserID;
        Statement statement;
        ResultSet rs;
        try
        {
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            if(!rs.first())
                return "comment already deleted";
            query="DELETE FROM comments WHERE ID="+id+";";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.execute();
            return "deleted";
        }catch(Exception ex){ex.printStackTrace(System.err);}
        return "error";
    }
    
    
    public String reportComment(int id)
    {
        String query="SELECT * FROM reported_comments WHERE id="+id+" AND user_id="+DBuserID+";";
        Statement statement;
        ResultSet rs;
        try
        {
            if(conn.isClosed())
                connectDB();
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            if(rs.first())
                return "comment already reported";
            query="INSERT INTO reported_comments (comment_id,user_id) VALUES (?,?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt       (1, id);
            preparedStmt.setInt       (2, DBuserID);

            
            preparedStmt.execute();
            return "deleted";
        }catch(Exception ex){ex.printStackTrace(System.err);}
        return "error";
    }
    
    
    
    public String appReport(String description)
    {
        String query="INSERT INTO app_reports (userid,description) VALUES (?,?);";
        PreparedStatement statement;
        try
        {
            
            if(conn.isClosed())
                connectDB();
            
            statement = conn.prepareStatement(query);
            statement.setInt       (1, DBuserID);
            statement.setString    (2, description);
            statement.execute();
            return "OK";
        }catch(Exception ex){ex.printStackTrace(System.err);}
        return "error";
    }
    
    
    
    public String upvote(int id)
    {
        try
        {
            if(conn.isClosed())
                connectDB();
            String query = "SELECT ID,user_id,Upvotes,Downvotes FROM reports WHERE ID="+id+";";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            int userID=rs.getInt("user_id");
            if(DBuserID==(userID))
                return "ERROR";
            
            if(!checkAction(id).equals("upvoted"))
                if(checkAction(id).equals("downvoted"))
                {
                    int downvotes=rs.getInt("Downvotes");
                    int upvotes=rs.getInt("Upvotes");
                    
                    query="update reports set Downvotes="+(downvotes-1)+" where ID="+id+";";
                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    
                    query="update reports set Upvotes="+(upvotes+1)+" where ID="+id+";";
                    preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    
                    query="update actions set action='upvoted' where RepID="+id+" AND user_id="+DBuserID+";";
                    preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    return "Completed";
                }
                else
                {
                    int upvotes=rs.getInt("Upvotes");
                    query="update reports set Upvotes="+(upvotes+1)+" where ID="+id+";";
                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    
                    query = " insert into actions (user_id, RepID, action) values ("+DBuserID+","+id+",'upvoted')";
                    preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    
                    return "Completed";
                }
            else {
                return "ERROR";
            }

        }catch(Exception ex){ex.printStackTrace(System.err);}
        
        return "ERROR";
    }
    
    
    public String downvote(int id)
    {
        try
        {
            if(conn.isClosed())
                connectDB();
            String query = "SELECT ID,user_id,Downvotes,Upvotes,Downvotes FROM reports WHERE ID="+id+";";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            int userID=rs.getInt("user_id");
            if(DBuserID==(userID))
                return "ERROR";
            if(!checkAction(id).equals("upvoted"))
                if(checkAction(id).equals("downvoted"))
                {
                    return "ERROR";
                }
                else
                {
                    int downvotes=rs.getInt("Downvotes");
                    query="update reports set Downvotes="+(downvotes+1)+" where ID="+id+";";
                    PreparedStatement preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    
                    query = " insert into actions (user_id, RepID, action) values ("+DBuserID+","+id+",'downvoted')";
                    preparedStmt = conn.prepareStatement(query);
                    preparedStmt.execute();
                    
                    return "Completed";
                }
            else {
                int downvotes=rs.getInt("Downvotes");
                int upvotes=rs.getInt("Upvotes");
                
                query="update reports set Downvotes="+(downvotes+1)+" where ID="+id+";";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.execute();
                
                query="update reports set Upvotes="+(upvotes-1)+" where ID="+id+";";
                preparedStmt = conn.prepareStatement(query);
                preparedStmt.execute();
                
                query="update actions set action='downvoted' where RepID="+id+" AND user_id="+DBuserID+";";
                preparedStmt = conn.prepareStatement(query);
                preparedStmt.execute();
                return "Completed";
            }
            
        }catch(Exception ex){ex.printStackTrace(System.err);}
        
        return "ERROR";
    }
    
    
    public String delete(int id)
    {
        try
        {
            if(conn.isClosed())
                connectDB();
            String query = "SELECT user_id FROM reports WHERE ID="+id+";";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            int userID=rs.getInt("user_id");
            if(DBuserID!=userID)
                return "ERROR";
            
            query="DELETE FROM reports WHERE ID="+id+";";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.execute();
            
            
            query="DELETE FROM comments WHERE report_id="+id+";";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.execute();
            
            
            
            return "Completed";
        }catch(Exception ex){ex.printStackTrace(System.err);}
        
        return "ERROR";
    }
    
    
    public String checkAction(int id)
    {
        try
        {
            if(conn.isClosed())
                connectDB();
            String query = "SELECT action FROM actions WHERE RepID="+id+" AND user_id='"+DBuserID+"';";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);  
            if(rs.first())
            {
                String action = rs.getString("action");
                return action;
            }
            return "nothing";
            
        }catch(SQLException ex){
            ex.printStackTrace(System.err);
            return null;
        }
        
    }
    
    
    public ArrayList<String> personalAchievements()
    {
        ArrayList<String> list=new ArrayList<>();
        ResultSet rs;
        String query;
        Statement statement;
        int i;
        try
        {
            if(conn.isClosed())
                connectDB();
            query = "SELECT user_id, single FROM achievements ORDER BY single DESC;";
            statement = conn.createStatement();
            rs = statement.executeQuery(query); 
            i=0;
            while(rs.next())
            {
                i++;
                if(rs.getInt(1)==DBuserID)
                {
                    list.add(String.valueOf(rs.getInt(2)));
                    if(rs.getInt(2)==0)
                        list.add(String.valueOf(0));
                    else
                        list.add(String.valueOf(i));
                    break;
                }
            }
            
            
            if(conn.isClosed())
                connectDB();
            query = "SELECT user_id, total FROM achievements ORDER BY total DESC;";
            statement = conn.createStatement();
            rs = statement.executeQuery(query); 
            i=0;
            while(rs.next())
            {
                i++;
                if(rs.getInt(1)==DBuserID)
                {
                    list.add(String.valueOf(rs.getInt(2)));
                    if(rs.getInt(2)==0)
                        list.add(String.valueOf(0));
                    else
                        list.add(String.valueOf(i));
                    break;
                }
            }

        }catch(SQLException ex){
            ex.printStackTrace(System.err);
            return null;
        }
        return list;
    }
    
    
    
    
    public ArrayList<String> topAchievements(int flag)
    {
        ArrayList<String> list=new ArrayList<>();
        String query;
        Statement statement;
        ResultSet rs;
        try
        {
            if(conn.isClosed())
                connectDB();
            if(flag==1)
                query = "SELECT achievements.user_id,achievements.single,users.username,users.fb_name FROM achievements,users WHERE users.id=achievements.user_id AND achievements.single>0 ORDER BY achievements.single DESC LIMIT 10 ;";
            else
                query = "SELECT achievements.user_id,achievements.total,users.username,users.fb_name FROM achievements,users WHERE users.id=achievements.user_id AND achievements.total>0 ORDER BY achievements.total DESC LIMIT 10 ;";
            statement = conn.createStatement();
            rs = statement.executeQuery(query);  
            while(rs.next())
            {
                list.add(String.valueOf(rs.getInt(1)));
                list.add(String.valueOf(rs.getInt(2)));
                if(rs.getString(3)!=null)
                    list.add(rs.getString(3));
                else
                    list.add(rs.getString(4));
            }

        }catch(SQLException ex){
            ex.printStackTrace(System.err);
        }
        return list;
    }
}
