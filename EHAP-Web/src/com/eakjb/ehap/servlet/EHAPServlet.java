package com.eakjb.ehap.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eakjb.ehap.core.EHAPConstants;

public abstract class EHAPServlet extends HttpServlet implements EHAPConstants {
	private static final long serialVersionUID = 130980699902220218L;
	
	//These can be servlet context initialization parameters witht the param-name of the value of the variable
	public String ERRORPAGE="ERRORPAGE";
	public String ERRORATTRIBUTE="ERRORATTRIBUTE";
	public String ERRORIDATTRIBUTE="ERRORIDATTRIBUTE";
	public String ERRORCODEATTRIBUTE="ERRORCODEATTRIBUTE";
	public String MESSAGEATTRIBUTE="MESSAGEATTRIBUTE";
	public String REDIRECTPARAMETER="REDIRECTPARAMETER";
	public String ADDRESSPARAMETER="ADDRESSPARAMETER";
	public String DATAPARAMETER="DATAPARAMETER";
	
	public String RESPONSEADDITIONALDATAKEY="RESPONSEADDITIONALDATAKEY";
	public String ADDITIONALDATASAMESOCK="samesock";
	public String CLIENTSEPARATOR="CLIENTSEPARATOR";
	public String CLIENTINFOSEPARATOR="CLIENTINFOSEPARATOR";
	
	private HashMap<String,String> constants = new HashMap<String,String>();
	
	@Override
	public void init() {
		ERRORPAGE=getConstant(ERRORPAGE, "error.jsp");
		ERRORATTRIBUTE=getConstant(ERRORATTRIBUTE,"error");
		ERRORIDATTRIBUTE=getConstant(ERRORIDATTRIBUTE, "errorid");
		ERRORATTRIBUTE=getConstant(ERRORATTRIBUTE, "error");
		ERRORCODEATTRIBUTE=getConstant(ERRORCODEATTRIBUTE, "errcode");
		MESSAGEATTRIBUTE=getConstant(MESSAGEATTRIBUTE,"msg");
		REDIRECTPARAMETER=getConstant(REDIRECTPARAMETER,"redirect");
		ADDRESSPARAMETER=getConstant(ADDRESSPARAMETER,"address");
		DATAPARAMETER=getConstant(DATAPARAMETER,"data");
		RESPONSEADDITIONALDATAKEY=getConstant(RESPONSEADDITIONALDATAKEY,"response");
		ADDITIONALDATASAMESOCK=getConstant(ADDITIONALDATASAMESOCK,"samesock");
		CLIENTSEPARATOR=getConstant(CLIENTSEPARATOR,",");
		CLIENTINFOSEPARATOR=getConstant(CLIENTINFOSEPARATOR,"-");
	}
	
	protected void addConstant(String name, String defaultValue) {
		String initParamVal = getServletContext().getInitParameter(name);
		constants.put(name, initParamVal!=null&&initParamVal!="" ? initParamVal : defaultValue);
	}
	
	protected String getConstant(String name, String defaultValue) {
		if (!constants.containsKey(name)) {
			addConstant(name,defaultValue);
		}
		return constants.get(name);
	}
	
	protected void doReq(HttpServletRequest req,HttpServletResponse res) {
		req.getSession().setAttribute(getConstant("USERATTRIBUTE","loggedIn"), true);
	}

	protected void sendDispatch(HttpServletRequest req,HttpServletResponse res, int errCode,String result,String redirect) throws ServletException, IOException {
		String path = errCode < 0 ? ERRORPAGE : redirect;
		req.setAttribute(ERRORCODEATTRIBUTE, errCode);
		req.setAttribute(MESSAGEATTRIBUTE, result);
		RequestDispatcher dispatcher = req.getRequestDispatcher(path);
		dispatcher.forward(req,res);
	}
	
	protected void handleError(HttpServletRequest req,HttpServletResponse res,int errCode,String msg,String redirect,Exception e) throws ServletException, IOException {		
		System.err.println(msg);
		e.printStackTrace(System.err);
		
		req.setAttribute(ERRORATTRIBUTE, e);
		req.setAttribute(ERRORIDATTRIBUTE, errCode);

		sendDispatch(req,res,errCode,msg,redirect);		
	}
	
	/**
	 * Credit to http://www.java2s.com/Code/Java/Servlets/Escapeandunescapestring.htm
	 * @param str
	 * @return
	 */
	public static String escapeHTML(String str) {
	    if (str == null || str.length() == 0)
	      return "";

	    StringBuffer buf = new StringBuffer();
	    int len = str.length();
	    for (int i = 0; i < len; ++i) {
	      char c = str.charAt(i);
	      switch (c) {
	      case '&':
	        buf.append("&amp;");
	        break;
	      case '<':
	        buf.append("&lt;");
	        break;
	      case '>':
	        buf.append("&gt;");
	        break;
	      case '"':
	        buf.append("&quot;");
	        break;
	      case '\'':
	        buf.append("&apos;");
	        break;
	      default:
	        buf.append(c);
	        break;
	      }
	    }
	    return buf.toString();
	  }
}

