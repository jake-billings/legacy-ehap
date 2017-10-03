package com.eakjb.ehap.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eakjb.ehap.core.EHAPRequest;
import com.eakjb.ehap.core.EHAPRequestType;

@WebServlet("/SendEHAPRequest.do")
public class EHAPSendRequestServlet extends EHAPServlet {
	private static final long serialVersionUID = 1L;
    
    public EHAPSendRequestServlet() {
        super();
    }
    
	protected void doPost(HttpServletRequest hreq, HttpServletResponse hres) throws ServletException, IOException {
		super.doReq(hreq, hres);
		//Initialize result variables
		int errCode=0;
		String result = "Request Successfully Sent.";
		String redirect = getConstant("EHAPDSendRequestServletRedirect","index.do");
		
		if (hreq.getParameter(REDIRECTPARAMETER)!=null&&hreq.getParameter(REDIRECTPARAMETER)!="") {
			redirect=hreq.getParameter(REDIRECTPARAMETER);
		} else {
			errCode=1;
		}
		
		//Load parameters
		String addr = hreq.getParameter(ADDRESSPARAMETER);
		String data = hreq.getParameter(DATAPARAMETER);
		EHAPRequestType type = null;
		
		//Parse req type enum
		try {
			type = EHAPRequestType.valueOf(hreq.getParameter("type"));
		} catch (IllegalArgumentException e) {
			this.handleError(hreq, hres, -1, "Error parsing request type enum.", redirect, e);
			return;		
		} catch (NullPointerException e) {
			this.handleError(hreq, hres, -5, "Error parsing request type enum.  Type is null.", redirect, e);
			return;					
		}
		
		//Error handling (empty variables)
		if (addr==null||addr=="") {
			errCode=-2;
			result="Error: no address";
		}
		if ((data==null||data=="")&&(type!=null)&&(type.equals(EHAPRequestType.SET)||type.equals(EHAPRequestType.PUSH))) {
			errCode=-3;
			result="Error: no data on SET or PUSH";
		}
		
		if (errCode<0) {
			sendDispatch(hreq,hres,errCode,result,redirect);
			return;
		}
		
		EHAPRequest ereq = new EHAPRequest(addr,type);
		ereq.setData(data);
		
		try {
			ereq.send();
		} catch (IOException e) {
			this.handleError(hreq, hres, -4, "Error sending EHAP request in servlet.", redirect, e);
			return;			
		} catch (Exception e) {
			this.handleError(hreq, hres, -4, "Error sending EHAP request in servlet.", redirect, e);
			return;			
		}
		
		//sendDispatch(hreq,hres,errCode,result,redirect);
		hres.sendRedirect(redirect);
	}

}