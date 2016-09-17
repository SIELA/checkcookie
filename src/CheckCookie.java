
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
			///////////////////////////////////////////////////连接数据库
			String characterSet = "utf8";
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			String dburl = "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding="+characterSet;//通过此url建立连接
			
			try {
				conn = (Connection) DriverManager.getConnection(dburl,"root","");
				System.out.println("数据库已连接!");
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
//			///////////////////////////////////////////////清空原来的数据库
			sql = "DROP DATABASE IF EXISTS ant";
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}            
			System.out.println("\n数据库已清空！");
			///////////////////////////////////////////////创建数据库
			try {
				sql = "CREATE DATABASE IF NOT EXISTS ant";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				
				sql = "USE ant";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				
				sql = "CREATE TABLE IF NOT EXISTS `content` ( "   
					+"	 `contentID` int(11) NOT NULL AUTO_INCREMENT, "
					+"	 `Host` text NOT NULL COMMENT '访问的服务器ID',"
					+"	 `Cookie` LONGBLOB NULL COMMENT '保存的Cookie',"
					+"	  `Page` longtext NOT NULL COMMENT '具体页面',"
					+"	  `Referer` longtext NOT NULL COMMENT '请求地址',"
					+"	  PRIMARY KEY (`contentID`)"
					+"	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='抓取到的内容';";
				
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				

				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}             //创建一个操作数据库的对象
			
			//////////////////////////////////////分析报文
	        try {
	                String encoding = "UTF-8";
	                String   cookie = "";
	                String   page = "";
	                String   host = "";
	                String   referer = "";
	                File file = new File(filePath);
	                
	                //使用数据库
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

	                    	System.out.printf("\r正在读取捕获包行:" + i++);
	                    	//报文分行
	                    	if (lineTxt.contains("GET")&&lineTxt.contains("HTTP/1.1")) {
	                    		piece = !piece;
	                    	}
	                    	
	                    	//如果一个报文以另一个报文的开始结束
	                    	if(piece == false) {
	                    		//如果没有抓到cookie就不存储
	                    		if(cookie.equals("")) {
	                    			
	                    		}
	                    		else {
		    						sql = "INSERT INTO content (Page,Host,Cookie,Referer) VALUES ( '" + page + "' , '" + host + "','" + cookie + "','"+ referer +"');";
		    						stmt = conn.createStatement();
		    						try {
		    							stmt.executeUpdate(sql);
		    							System.out.println("插入网站 :" + host);
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
		                    		//去掉域名最后的/方便连接成一个完整的
		                    		if(referer.lastIndexOf("/") == referer.length()-1) {
		                    			referer = referer.substring(0, referer.length()-1);
		                    		}

		                    	}
	                    	}
	                    	lineTxt = bufferedReader.readLine();
	                    	
	                }read.close();
	                //最后一个报文
					sql = "INSERT INTO content (Page,Host,Cookie,Referer) VALUES ( '" + page + "' , '" + host + "','" + cookie + "','"+ referer +"');";
					stmt = conn.createStatement();
					stmt.executeUpdate(sql);
					System.out.println("插入网站 :" + host);
					file.delete();
		        }else{
		            System.out.println("类“CheckCookie”发生错误 !");
		        }
	        } catch (Exception e) {
	            System.out.println("CheckCookie”发生错误 !");
	            e.printStackTrace(); 
	        }
	    }
	     
	    public CheckCookie() {
	        String filePath = "Captured.data";
	        readTxtFile(filePath);
	        System.out.println("分析完毕 !");
	    }
	    
	    public static void main(String[] args) {
	        String filePath = "Captured.data";
	        readTxtFile(filePath);
	        System.out.println("分析完毕 !");
		}
}
