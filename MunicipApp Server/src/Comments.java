
import java.io.Serializable;


public class Comments implements Serializable
{
    public int id;
    public int user_id;
    public int level;
    public String comment;
    public String username;
    public String facebook_id;
    public byte[] user_image;
    public String date;
    
    Comments(int id,int user_id,int level,String comment,String username,byte[] user_image,String facebook_id,String date)
    {
        this.id=id;
        this.user_id=user_id;
        this.level=level;
        this.comment=comment;
        this.username=username;
        this.user_image=user_image;
        this.facebook_id=facebook_id;
        this.date=date;
    }
}
