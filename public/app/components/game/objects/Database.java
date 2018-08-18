package Game;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
public class Database 
{
	public Long userId;
	public String userNameResult;
	public Boolean active = false;
    private Connection con;
    private PreparedStatement ppardStatmnt;
    private ResultSet resultSt;
    private boolean databaseConnected = false;
    
    
    public Database()
    {
    	
    	makeConnection();

    }
    
    
    
    public void makeConnection() {
    	
    	if (databaseConnected == false)
    	{
    		dbConnect();
    	}	
	}



	public Boolean checkLogin(String userName, String userPassword)
    {
        try {
            ppardStatmnt = con.prepareStatement("select * from users where username=? and password=?");
        	
        	userPassword = getMD5(getMD5("d3f58g"+userPassword+"95yIj3"));
        		
        	ppardStatmnt.setString(1, userName); //this replaces the 1st  "?" in the query for username
        	ppardStatmnt.setString(2, userPassword);    //this replaces the 2st  "?" in the query for password
            
        	//executes the prepared statement
        	resultSt=ppardStatmnt.executeQuery();
        	
            if(resultSt.next())
            {
            	userId = (Long) resultSt.getObject(1);
                userNameResult = (String) resultSt.getObject(2);
                
                active = (Boolean) resultSt.getObject(5);
                
                if(active == false){
                	
                	 return false;
                }
                
                else
                {
                	
                	//TRUE if the query founds any corresponding data
                    return true;
                }
            	
            }
            else
            {
                return false;
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("error while validating"+e);
            return false;
        }
    }


    private void dbConnect()
    {
    	try{
            
            //MAKE SURE YOU KEEP THE mysql_connector.jar file in java/lib folder
            //ALSO SET THE CLASSPATH
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cake","root","");
            databaseConnected = true;
           }
        catch (Exception e) 
        {
            System.out.println(e);
        }
    }
    
    
    
    public static String getMD5(String input) 
    {
        try
        {
          MessageDigest md = MessageDigest.getInstance("MD5");
          md.update(input.getBytes());

          byte byteData[] = md.digest();

          //convert the byte to hex format method 1
          StringBuffer sb = new StringBuffer();
          for (int i = 0; i < byteData.length; i++)
          {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100,
                                       16).substring(1));
          }
          return sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
          throw new RuntimeException(e);
        }
    }
    

	public void dbDisconnect() {
		try{
			ppardStatmnt.close();
			con.close();
		}
		
	catch(SQLException sqlException){
		sqlException.printStackTrace();
	}
		finally{
			databaseConnected = false;
		}
		
	}

}