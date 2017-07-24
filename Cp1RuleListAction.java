package com.venustech.vwf.mic.actions.industrial;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import com.venustech.vwf.commons.view.web.annotations.Param;
import com.venustech.vwf.commons.view.web.util.HttpHelper;

public class Cp1RuleListAction extends AbstractIindustrialAction{
 
	private static final long serialVersionUID = 1L;

	private String ip;
	
	public String getIp() {
		return ip;
	}
	
	@Param
	public void setIp(String ip) {
		this.ip = ip;
	}


	/**
	 * 查看引擎规则列表展示action
	 */
	@Override
	public String execute(){ 
		
		String seid = (String)this.getReq().getSession().getAttribute("seid");
		if(seid == null || seid.trim().equals("")){
			try {
				HttpHelper.redirect(ServletActionContext.getRequest(),
						ServletActionContext.getResponse(), "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			if(!this.getIndustrialService().checkSeidValid(seid)){
				try {
					HttpHelper.redirect(ServletActionContext.getRequest(),
							ServletActionContext.getResponse(), "");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String rule  = this.getIndustrialService().ruleList(ip, seid);
		   this.sendJson(rule);
		  return JSON;
	}
}
