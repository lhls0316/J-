package com.venustech.vwf.mic.actions.industrial;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.venustech.vwf.commons.view.web.annotations.Param;
import com.venustech.vwf.commons.view.web.util.HttpHelper;
import com.venustech.vwf.mic.listener.AppContextsListener;
import com.venustech.vwf.mic.service.http.IMicHttpClientService;

public class EngineRuleListAction extends AbstractIindustrialAction{
 
	private static final long serialVersionUID = 1L;

	private String e;
	private String f;
	

	public String getE() {
		return e;
	}
	@Param
	public void setE(String e) {
		this.e = e;
	}

	public String getF() {
		return f;
	}
	@Param
	public void setF(String f) {
		this.f = f;
	}

	/**
	 * 查看引擎规则列表展示action
	 */
	@Override
	@Action(results = { @Result(name = "success", location = RESULT_PATH
	+ "/industrial/cp10.jsp") })
	public String execute(){ 
		HttpServletRequest request = ServletActionContext.getRequest();
		request.setAttribute("engineIp", e);
		request.setAttribute("ruleStatus", f);
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

		return SUCCESS;
	}
	
}
