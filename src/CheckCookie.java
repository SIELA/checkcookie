
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Connection;

public class CheckCookie {
	   public static void readTxtFile(String filePath) {
		   
			String sql = null;
			Statement stmt = null;
			Connection conn = null;
			///////////////////////////////////////////////////�������ݿ�
			String characterSet = "utf8";
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			String dburl = "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding="+characterSet;//ͨ����url��������
			
			try {
				conn = (Connection) DriverManager.getConnection(dburl,"root","");
				System.out.println("���ݿ�������!");
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
//			///////////////////////////////////////////////���ԭ�������ݿ�
			sql = "DROP DATABASE IF EXISTS ant";
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}            
			System.out.println("\n���ݿ�����գ�");
			///////////////////////////////////////////////�������ݿ�
			try {
				sql = "CREATE DATABASE IF NOT EXISTS ant";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				
				sql = "USE ant";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				
				sql = "CREATE TABLE IF NOT EXISTS `content` ( "   
					+"	 `contentID` int(11) NOT NULL AUTO_INCREMENT, "
					+"	 `Host` text NOT NULL COMMENT '���ʵķ�����ID',"
					+"	 `Cookie` LONGBLOB NULL COMMENT '�����Cookie',"
					+"	  `Page` longtext NOT NULL COMMENT '����ҳ��',"
					+"	  `Referer` longtext NOT NULL COMMENT '�����ַ',"
					+"	  PRIMARY KEY (`contentID`)"
					+"	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='ץȡ��������';";
				
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				

				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}             //����һ���������ݿ�Ķ���
			
			//////////////////////////////////////��������
	        try {
	                String encoding = "UTF-8";
	                String   cookie = "";
	                String   page = "";
	                String   host = "";
	                String   referer = "";
	                File file = new File(filePath);
	                
	                //ʹ�����ݿ�
					sql = "use ant;";
					stmt = conn.createStatement();
					stmt.executeUpdate(sql);
					
	                if(file.isFile() && file.exists()) {
	                    InputStreamReader read = new InputStreamReader(
	                    new FileInputStream(file),encoding);
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String lineTxt = null;
	                    lineTxt = bufferedReader.readLine();
	                    boolean piece = false;
	                    int i = 1;
	                    while((lineTxt!= null)) {

	                    	System.out.printf("\r���ڶ�ȡ�������:" + i++);
	                    	//���ķ���
	                    	if (lineTxt.contains("GET")&&lineTxt.contains("HTTP/1.1")) {
	                    		piece = !piece;
	                    	}
	                    	
	                    	//���һ����������һ�����ĵĿ�ʼ����
	                    	if(piece == false) {
	                    		//���û��ץ��cookie�Ͳ��洢
	                    		if(cookie.equals("")) {
	                    			
	                    		}
	                    		else {
		    						sql = "INSERT INTO content (Page,Host,Cookie,Referer) VALUES ( '" + page + "' , '" + host + "','" + cookie + "','"+ referer +"');";
		    						stmt = conn.createStatement();
		    						try {
		    							stmt.executeUpdate(sql);
		    							System.out.println("������վ :" + host);
		    						} catch (SQLException e) {
			    						sql = "INSERT INTO content (Page,Host,Cookie) VALUES ( '" + page + "' , '" + host + "','" + cookie + "');";
		    						}
		    						
		    						piece = !piece;
		    						page = lineTxt.split("GET ")[1].split(" ")[0];;
		    						host = "";
		    						cookie = "";
		    						referer = "";
	                    		}
	                    	} else {
								if (lineTxt.contains("GET")&&lineTxt.contains("HTTP/1.1")) {
			                    	page = lineTxt.split("GET ")[1].split(" ")[0];
		                    	} else if(lineTxt.contains("Host: ")) {
		                    		host = lineTxt.split("Host: ")[1];
		                    	} else if(lineTxt.contains("Cookie: ")&&!lineTxt.contains("Set-Cookie")) {
		                    		cookie = lineTxt.split("Cookie: ")[1];
		                    	} else if(lineTxt.contains("Referer: ")) {
		                    		referer = lineTxt.split("Referer: ")[1];
		                    		//ȥ����������/�������ӳ�һ��������
		                    		if(referer.lastIndexOf("/") == referer.length()-1) {
		                    			referer = referer.substring(0, referer.length()-1);
		                    		}

		                    	}
	                    	}
	                    	lineTxt = bufferedReader.readLine();
	                    	
	                }read.close();
	                //���һ������
					sql = "INSERT INTO content (Page,Host,Cookie,Referer) VALUES ( '" + page + "' , '" + host + "','" + cookie + "','"+ referer +"');";
					stmt = conn.createStatement();
					stmt.executeUpdate(sql);
					System.out.println("������վ :" + host);
					file.delete();
		        }else{
		            System.out.println("�ࡰCheckCookie���������� !");
		        }
	        } catch (Exception e) {
	            System.out.println("CheckCookie���������� !");
	            e.printStackTrace(); 
	        }
	    }
	     
	    public CheckCookie() {
	        String filePath = "Captured.data";
	        readTxtFile(filePath);
	        System.out.println("������� !");
	    }
	    
	    public static void main(String[] args) {
	        String filePath = "Captured.data";
	        readTxtFile(filePath);
	        System.out.println("������� !");
		}
}
