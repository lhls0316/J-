

package com.venustech.vwf.mic.service.etl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.venustech.vwf.commons.core.annotations.Service;
import com.venustech.vwf.commons.utils.https.HttpClient;
import com.venustech.vwf.commons.utils.properties.ReadProperties;
import com.venustech.vwf.mic.common.util.PasswordSafe;
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;  

@Service
public class EtlService  implements IetlServices {
	
	private String url = ReadProperties.readdbValue("auth_url");
	
	@Override
	//所有Etl显示
	public String etlList(String seid) {  
		String etl = HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=show&d=state");
		JSONArray array = new JSONArray();
		if(etl.startsWith("[")){
			array   = JSONArray.fromObject(etl);
		}
        return array.toString();                                                                                                                                                                                                                                                                                                                                                
	}

	@Override
	//ETL所有引擎配置修改
	public String addEtl(String json,String seid) {
		String etl ="";
		JSONObject  obj = JSONObject.fromObject(json);
		JSONObject  objFlag = new JSONObject();
		//标识是哪种模式
		//String etl_status = obj.getString("etl_status");
		
		//Mtx引擎IP地址
		String engine_IP= obj.getString("engine_IP");
	    String etl_ip= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=ms_host&e="+engine_IP);
		
		objFlag = JSONObject.fromObject(etl_ip);
		String flag = objFlag.getString("result");
		if("false".equals(flag)){
			etl+= "Mtx引擎IP地址配置失败！";
		}
		
		//下载文件类型
		String engine_FileTypes =obj.getString("engine_FileTypes");
		String old_engine_FileTypes =obj.getString("old_engine_FileTypes");
		if(!engine_FileTypes.equals(old_engine_FileTypes)){
	     	String etl_type= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=threadstatus&e="+engine_FileTypes);
	    	
	     	objFlag = JSONObject.fromObject(etl_type);
			flag = objFlag.getString("result");
			if("false".equals(flag)){
				etl+= "下载文件类型配置失败！";
			  }
		}
		
		//下载速度限流
		String etl_CurrentLimit =obj.getString("etl_CurrentLimit");
		String old_etl_CurrentLimit =obj.getString("old_etl_CurrentLimit");
		if(!etl_CurrentLimit.equals(old_etl_CurrentLimit)){
			String etl_speed= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=download_speed&e="+etl_CurrentLimit);
			
			objFlag = JSONObject.fromObject(etl_speed);
			flag = objFlag.getString("result");
			if("false".equals(flag)){
				etl+= "限流配置失败！";
			  }
		}
		
		//数据库模式
		String etl_Database = obj.getString("etl_Database");
		JSONObject  obj_engine = JSONObject.fromObject(etl_Database);
		
		String ip = obj_engine.getString("ip");
		String old_ip = obj_engine.getString("old_ip");
		
	    if(!ip.equals(old_ip)){
			//ip接口
		    String etl_Ip= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=db_host&e="+ip);
			objFlag = JSONObject.fromObject(etl_Ip);
			flag = objFlag.getString("result");
			if("false".equals(flag)){
				etl+= "数据库Ip配置失败！";
			  }
		}
	    
		String port = obj_engine.getString("port");
		String old_port = obj_engine.getString("old_port");
		if(!port.equals(old_port)){
			//端口接口
		    String etl_port= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=db_port&e="+port);
		    
		    objFlag = JSONObject.fromObject(etl_port);
			flag = objFlag.getString("result");
			if("false".equals(flag)){
				etl+= "数据库端口号配置失败！";
			  }
		}
		
		String username = obj_engine.getString("username");
		String old_username = obj_engine.getString("old_username");
		if(!username.equals(old_username)){
			//用户名登录接口
		    String etl_user= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=db_user&e="+username);
		    
		    objFlag = JSONObject.fromObject(etl_user);
			flag = objFlag.getString("result");
			if("false".equals(flag)){
				etl+= "数据库用户登录配置失败！";
			  }
		}
		
		String password = obj_engine.getString("password");
		String old_password = obj_engine.getString("old_password");
		if(!password.equals(old_password)){
			password = PasswordSafe.getPassword(password);
			//数据库密码接口
		    String etl_pass= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=db_pswd&e="+password);
		    
		    objFlag = JSONObject.fromObject(etl_pass);
			flag = objFlag.getString("result");
			if("false".equals(flag)){
				etl+= "数据库密码配置失败！";
			  }
		}

		//syslog模式
		String etl_Syslog = obj.getString("etl_Syslog");
		obj_engine = JSONObject.fromObject(etl_Syslog);
	
		String mtd_syslog_ip = obj_engine.getString("mtd_syslog_ip");
		mtd_syslog_ip=mtd_syslog_ip.replace("\n", ",");
		
	    String mtd_syslog= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d=mtdsyslog&e="+mtd_syslog_ip+"&f=1"); 
		objFlag = JSONObject.fromObject(mtd_syslog);
		flag = objFlag.getString("result");
			if("false".equals(flag)){
				etl+= "Syslog配置失败！";
			 }

		if("".equals(etl)){
			etl="配置成功！";
		}
		return etl;
	}

	@Override
	//单一修改Etl配置
	public String modifySingleEtl(String json,String seid) {
        String etl ="";
        JSONObject  obj = JSONObject.fromObject(json);
        JSONObject  objFlag = new JSONObject();
        String flag ="";
        String engine_IP = obj.getString("engine_IP");
        String engine_FileTypes = obj.getString("engine_FileTypes");
        
        String fileTypes= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d="+engine_IP+"&e=threadstatus&f="+engine_FileTypes);
        //System.out.println(engine_IP+","+engine_FileTypes);
        if(fileTypes.startsWith("{")){
        	objFlag = JSONObject.fromObject(fileTypes);
        	flag = objFlag.getString("result");
        }
			if("false".equals(flag)){
				etl+= "配置下载文件类型失败！";
			}
			
		String mtd_syslog_ip = obj.getString("mtd_syslog_ip");
		String mtd_syslog = HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=modify&d="+engine_IP+"&e=mtdsyslog&f="+mtd_syslog_ip+"&g=1");
		
		if(mtd_syslog.startsWith("{")){
        	objFlag = JSONObject.fromObject(mtd_syslog);
        	flag = objFlag.getString("result");
        }
			if("false".equals(flag)){
				etl+= "配置syslog失败！";
			}
		
		if("".equals(etl)){
			etl="配置成功！";
		}
		return etl;
	}

	@Override
	public String startEtl(String seid) {
		String etl ="";
	    etl = HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=start");
  
	    if(!"".equals(etl)){
	    	JSONObject  obj = JSONObject.fromObject(etl);
			String flag = obj.getString("result");
			if("true".equals(flag)){
				etl= "启动成功！";
			}else{
				etl= "启动失败！";
			}
	    }else{
	    	etl= "启动失败！";
	    }
    	
		return etl;
	}

	@Override
	public String stopEtl(String seid) {
		String etl ="";
	    etl= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=stop");
	    if(!"".equals(etl)){
	    	JSONObject  obj = JSONObject.fromObject(etl);
			String flag = obj.getString("result");
			if("true".equals(flag)){
			   etl= "停止成功！";
			}else{
			   etl= "停止失败！";
			}
	    }else{
	    	etl= "停止失败！";
	    }
    	
		return etl;
	}

	@Override
	public String rebootEtl(String seid) {
		String etl ="";
		etl= HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=reboot");
		
		etl = etl.substring(0, etl.indexOf("\n"));
		if(!"".equals(etl)){
		JSONObject  obj = JSONObject.fromObject(etl);
 		String flag = obj.getString("result");
 		if("true".equals(flag)){
 			etl= "重启成功！";
	 	}else{
	 		etl= "重启失败！";
	 	 }
		}else{
			etl= "重启失败！";
		}
		return etl;
	}

	@Override
	public String detailEngine(String seid) {
		
		String etl = HttpClient.getRequestResultInfo(url, "a="+seid+"&b=etl&c=show&d=defaultset");
		String engine = HttpClient.getRequestResultInfo(url, "a="+seid+"&b=network&c=mtx&d=list");
		JSONArray array = new JSONArray();
		JSONObject  obj = new JSONObject();
		if(engine.startsWith("[")){
			array = JSONArray.fromObject(engine);
		}

		if(etl.startsWith("{")){
			 obj = JSONObject.fromObject(etl);
		}
		
		if(!obj.containsKey("result")){
			//所有引擎IP
			String ip="";
			for(int i=0;i<array.size();i++){
				JSONObject objs = array.getJSONObject(i);
				ip+= objs.getString("ip")+",";
			}
			if(!"".equals(ip)){
				ip=ip.substring(0,ip.length()-1);
			}
			obj.element("engine_ip", ip);
			
			//处理IP
			String etl_ip = obj.getString("eng_ip").replace(" ", ",");
			obj.element("eng_ip", etl_ip);
			
			//修改最大下载速度
			String limit = obj.getString("etl_download_speed");
			if(limit.contains("M")){
			   limit=limit.replace("M", "");
			   obj.element("etl_download_speed", limit);
			}else if(limit.contains("K")){
			   java.text.DecimalFormat   df=new  java.text.DecimalFormat("#.##");  
			   double limit_k = (double) (Integer.parseInt(limit.replace("K", "")))/1024;
			   obj.element("etl_download_speed", df.format(limit_k)+"");
			}
			
			String password = obj.getString("db_pswd");
			//解密
			password=PasswordSafe.getPassCode(password);
			obj.element("db_pswd", password);
			
			//syslog配置模式
			String syslog = obj.getString("syslog");
			if(syslog.startsWith("[")){
				array = JSONArray.fromObject(syslog);
				for(int i=0;i<array.size();i++){
					JSONObject objs = array.getJSONObject(i);
					if(objs.containsKey("mtd_syslog")){
						String mtd_ip=objs.getString("mtd_syslog");
						obj.element("mtd_syslog_ip", mtd_ip);
						obj.element("mtd_status", objs.getString("status"));
					}else if(objs.containsKey("dns_syslog")){
						String dns_ip=objs.getString("dns_syslog");
						obj.element("dns_syslog_ip", dns_ip);
						obj.element("dns_status", objs.getString("status"));
					}
				}
			}
		}
		return obj.toString();
	}

	@Override
	public String dataBaseTest(String json) {
	   Connection conn=null;
	   String sql="select current_date";
	   JSONObject  obj = JSONObject.fromObject(json);
	   String ip = obj.getString("ip");
	   String port = obj.getString("port");
	   String name = obj.getString("name");
	   String password = obj.getString("password");
	   String msg="";
	   try {
				  Class.forName("com.mysql.jdbc.Driver");//指定连接类型  
		    	  conn =DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/mysql",name,password);//获取连接 
		    	  PreparedStatement pst  = conn.prepareStatement(sql);//准备执行语句
		    	  ResultSet ret = pst.executeQuery();//执行语句，得到结果集  
		          ret.close();  
		          conn.close();  
		          pst.close(); 
	            msg="数据库连接成功！";
	        } catch (Exception e) { 
	        	e.printStackTrace();
	        	msg="数据库连接失败！";
	        }
		return msg;
   }
}
