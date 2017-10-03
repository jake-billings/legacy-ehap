package com.eakjb.ehap.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eakjb.ehap.core.EHAPConstants;
import com.eakjb.ehap.core.EHAPRequest;
import com.eakjb.ehap.core.EHAPRequestType;

@WebServlet({ "/EHAPDevices.do", "/index.do" })
public class EHAPDevicesServlet extends EHAPServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req,res);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		super.doReq(req, res);
		//Init variables
		int errCode=0;
		String result = "Request Successfully Sent.";
		String redirect = getConstant("EHAPDevicesServletRedirect","WEB-INF/components/index.jsp");

		//Set redirect URL
		if (req.getParameter(REDIRECTPARAMETER)!=null&&req.getParameter(REDIRECTPARAMETER)!="") {
			redirect=req.getParameter(REDIRECTPARAMETER);
		} else {
			errCode=1;
		}

		//Load address parameter
		String addressParameter = req.getParameter(ADDRESSPARAMETER);
		if (addressParameter==null||addressParameter=="") {
			errCode=2;
			addressParameter=getConstant("DEFAULTSERVERADDRESS","EHAP://localhost");
		}

		//Load EHAP request
		EHAPRequest eres;		
		try {
			EHAPRequest ereq = new EHAPRequest(addressParameter, EHAPRequestType.PULL);
			ereq.setDatatype(3); //Client list data type
			ereq.getAdditionalData().put(RESPONSEADDITIONALDATAKEY,ADDITIONALDATASAMESOCK);
			ereq.getAdditionalData().put(getConstant("ADDITIONALDATATRANSITIONTIMEOUTKEY","transitiontimeout"),
					getConstant("ADDITIONALDATATRANSITIONTIMEOUT","1"));

			Socket sock = new Socket(ereq.getIndexAddress(0)[1], PORT);
			PrintWriter writer = new PrintWriter(sock.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			writer.print(ereq);
			writer.flush();

			eres = EHAPRequest.fromString(escapeHTML(reader.readLine()));

			writer.close();
			reader.close();			
			sock.close();

		} catch (IOException e) {
			this.handleError(req, res, -4, "Error sending EHAP request in servlet.", redirect, e);
			return;			
		} catch (Exception e) {
			this.handleError(req, res, -6, "Error parsing response.", redirect, e);//TODO update based on notebook
			return;			
		}

		//Split 
		String[] clients={};
		try {
			if (eres.getData()!=null&&eres.getData()!="") clients=eres.getData().split(CLIENTSEPARATOR);
		} catch (ArrayIndexOutOfBoundsException e) {
			this.handleError(req, res, -7, "Error parsing client info response data.", redirect, e);
			return;					
		}
		/*
		 * 
		 * The following was replaced by JSP
		//Build HTML
		String html="";
		if (clients!=null && clients.length>0) {
			html+="<table class='clienttable'><tr><td><b>Address</b></td><td><b>State</b></td><td><b>On/Off</b></td></tr>";
			for (String client : clients) {
				String[] info = client.split(CLIENTINFOSEPARATOR);
				if (info.length>1) {
					html+="<tr><td>";
					html+=info[0];
					html+="</td><td>";
					
					String value="Error: invalid value from server";
					if (TRUEVALUES.contains(info[1])) {
						value="On";
					} else if (FALSEVALUES.contains(info[1])) {
						value="Off";
					} else if (TRANSVALUES.contains(info[1])) {
						value="Transitioning";
					} else {
						value=info[1];
					}
					
					html+= value;
					html+="</td><td>";
					html+="<form method='POST' action='"+getConstant("SENDREQUESTPATH","SendEHAPRequest.do")+"' class='deviceon'>";
					html+="<input type='hidden' value='True' name='data' />";
					html+="<input type='hidden' value='"+info[0]+"' name='address' />";
					html+="<input type='hidden' value='SET' name='type' />";
					html+="<input type='submit' value='On' />";
					html+="</form>";
					html+="<form method='POST' action='"+getConstant("SENDREQUESTPATH","SendEHAPRequest.do")+"' class='deviceoff'>";
					html+="<input type='hidden' value='False' name='data' />";
					html+="<input type='hidden' value='"+info[0]+"' name='address' />";
					html+="<input type='hidden' value='SET' name='type' />";
					html+="<input type='submit' value='Off' />";
					html+="</form>";
					html+="</td></tr>";
				} else {
					html+="<tr><td>Malformed Client</td></tr>";
				}
			}
			html+="</table>";
		} else {
			html+="<p class='message'>"+getConstant("NODEVICESMESSAGE","No EHAP Devices")+"</p>";
		}
		req.setAttribute(getConstant("CLIENTHTMLTABLE","clienthtmltable"), html);
		*/
		
		String[] formattedClients = clients.clone();
		for (int i=0;i<formattedClients.length;i++) {
			String client=formattedClients[i];
			String[] info=client.split(CLIENTINFOSEPARATOR);
			
			//Convert to numerical values
			if (EHAPConstants.FALSEVALUES.contains(info[1])) {
				formattedClients[i]=client.replace(info[1], "0");
			} else if (EHAPConstants.TRUEVALUES.contains(info[1])) {
				formattedClients[i]=client.replace(info[1], "1");
			} else if (EHAPConstants.TRANSVALUES.contains(info[1])) {
				formattedClients[i]=client.replace(info[1], "3");
			}
			
			Pattern p = Pattern.compile("[A-Za-z0-9]/");
			Matcher m = p.matcher(client.split(CLIENTINFOSEPARATOR)[Integer.parseInt(getConstant("CLIENTADDRINDEX", "0"))]);
			int offset=0;
			while (m.find()) {
				formattedClients[i]=formattedClients[i].substring(0, m.end()+offset)+"<br/>"+formattedClients[i].substring(m.end()+offset, formattedClients[i].length());
				offset+=5;
			}
			/*
			String[] info = formattedClients[i].split(CLIENTINFOSEPARATOR);
			info[0]="<div class='deviceaddr'>"+info[0]+"</div>";
			info[1]="<div class='devicestate'>"+info[1]+"</div>";
			formattedClients[i]=info[0]+CLIENTINFOSEPARATOR+info[1];
			*/
		}
		
		//System.out.println(Arrays.toString(formattedClients));
		
		req.setAttribute(getConstant("CLIENTARRAYATTRIBUTE","clientarray"), clients);
		req.setAttribute(getConstant("CLIENTARRAYFORMATTEDATTRIBUTE","formattedclientarray"), formattedClients);
		req.setAttribute(getConstant("CLIENTINFOSEPARATORATTRIBUTE", "clientinfoseparator"), CLIENTINFOSEPARATOR);
		req.setAttribute(getConstant("CLIENTADDRINDEXATTRIBUTE", "clientaddrindex"), Integer.parseInt(getConstant("CLIENTADDRINDEX", "0")));
		req.setAttribute(getConstant("CLIENTSTATEINDEXATTRIBUTE", "clientstateindex"), Integer.parseInt(getConstant("CLIENTSTATEINDEX", "1")));
		sendDispatch(req, res, errCode, result, redirect);
	}

}
