package com.venustech.vwf.mic.actions.industrial;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.venustech.vwf.commons.utils.properties.ReadProperties;
import com.venustech.vwf.commons.view.web.util.HttpHelper;
import com.venustech.vwf.mic.actions.BasicAction;
import com.venustech.vwf.mic.listener.AppContextsListener;
import com.venustech.vwf.mic.service.http.IMicHttpClientService;

public class Cp1ShowAction extends BasicAction{
	/**
	 * 引擎管理action
	 */
	private static final long serialVersionUID = 1L;

	@Override
	@Action(results = { @Result(name = "success", location = RESULT_PATH
	+ "/industrial/cp1.jsp") })
	public String execute(){ 
		String org_flag =ReadProperties.readdbValue("org_flag");
		if(org_flag!=null){
			getReq().setAttribute("org_flag", org_flag);
		}else{
			getReq().setAttribute("org_flag", "0");
		}
		
		String seid = (String)this.getReq().getSession().getAttribute("seid");
		if(seid == null || seid.trim().equals("")){
			try {
				HttpHelper.redirect(ServletActionContext.getRequest(),
						ServletActionContext.getResponse(), "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			IMicHttpClientService micHttpClientService = (IMicHttpClientService)AppContextsListener.getBean("micHttpClientService");
			if(!micHttpClientService.checkSeidValid(seid)){
				try {
					HttpHelper.redirect(ServletActionContext.getRequest(),
							ServletActionContext.getResponse(), "");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return SUCCESS;
	}
}
