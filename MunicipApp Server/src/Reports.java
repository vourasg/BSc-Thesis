import java.io.Serializable;


public class Reports implements Serializable
{
    private int ReportID;
    private int UserID;
    private String ReportCategory;
    private String ReportType;
    private String Description;
    private byte[] Image;
    private String ImageComment;
    private double latitude;
    private double longitude;
    private int UpVotes;
    private int DownVotes;
    private String Locality;
    private String Date;
    

    public Reports(){
    }

    public Reports(int ReportID, int UserID, String ReportCategory, String ReportType,String Description, byte[] Image, String ImageComment, double latitude,double longitude,String Locality,int upvotes,int downvotes,String date)
    {
        this.ReportID=ReportID;
        this.UserID=UserID;
        this.ReportCategory=ReportCategory;
        this.ReportType=ReportType;
        this.Description=Description;
        this.Image=Image;
        this.ImageComment=ImageComment;
        this.latitude=latitude;
        this.longitude=longitude;
        this.UpVotes=upvotes;
        this.DownVotes=downvotes;
        this.Locality=Locality;
        this.Date=date;
    }
    
    public void setDescription(String Description){
        this.Description=Description;
    }
    
    public String getDescription(){
        return this.Description;
    }
    
    public void setDate(String date){
        this.Date=date;
    }



    public String getDate() {
        return Date;
    }

    public void setReportID(int ReportID){
        this.ReportID=ReportID;
    }

    public void setUserID(int UserID){
        this.UserID=UserID;
    }

    public void setReportCategory(String ReportCategory){
        this.ReportCategory=ReportCategory;
    }

    public void setReportType(String ReportType){
        this.ReportType=ReportType;
    }

    public void setImage(byte[] Image){
        this.Image=Image;
    }
    
    public void setImageComment(String ImageComment){
        this.ImageComment=ImageComment;
    }

    public void setLocation(double latitude,double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public int getReportID(){
        return this.ReportID;
    }

    public int getUserID(){
        return this.UserID;
    }

    public String getReportCategory(){
        return this.ReportCategory;
    }

    public String getReportType(){
        return this.ReportType;
    }

    public byte[] getImage(){
        return this.Image;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }
    
    public String getLocality(){
        return this.Locality;
    }
    
    public void setUpVotes(int upvotes){
        this.UpVotes=upvotes;
    }
    
    public int getUpVotes(){
        return this.UpVotes;
    }
    
    public void setDownVotes(int downvotes){
        this.DownVotes=downvotes;
    }
    
    public int getDownVotes(){
        return this.DownVotes;
    }
    
    public void setLocality(String Locality){
        this.Locality=Locality;
    }
    
    public String getImageComment(){
        return this.ImageComment;
    }
}