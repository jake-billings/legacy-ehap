package com.eakjb.ehap.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class EHAPRequest implements EHAPConstants {
	private static final String protocol = "EHAP";
	private static final float version = 1.1F;
	
	public static final String SEPARATOR = ";";
	public static final String ADDITIONALDATASEPARATOR = ",";
	public static final String ADDITIONALDATAITEMSEPARATOR = ":";
	public static final String PROTOCOLSEPARATOR = ":";
	
	private long timeStamp=getNewTimeStamp();
	private String address;
	private String addressFrom="";
	private EHAPRequestType type;
	private int datatype=0;
	private String data;
	private HashMap<String,String> additionalData = new HashMap<String,String>();
	
	private boolean sent=false;

	public EHAPRequest(String address, EHAPRequestType type, String data) {
		this.address=address;
		this.type=type;
		this.data=data;
		
		try {
			addressFrom="EHAP://"+InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.err.println("Host Error Initiating EHAPRequest");
			e.printStackTrace(System.err);
		}
	}
	
	public EHAPRequest(String address, EHAPRequestType type) {
		this(address, type, "");
	}
	
	//Misc. Methods
	public void send(Socket s, boolean updateTimeStamp) throws IOException {
		PrintWriter writer = new PrintWriter(s.getOutputStream());
		
		if (updateTimeStamp) updateTimeStamp();
		
		writer.print(toString());
		writer.flush();
		writer.close();
		
		s.close();
		
		sent=true;
	}
	
	public void send(Socket s) throws IOException {
		send(s,true);
	}
	
	public void send(String addr, int port) throws IOException {
		System.out.println(addr+":"+port);
		Socket s = new Socket(addr, port);
		send(s);
		s.close();
	}
	
	public void send() throws IOException {
		send(getIndexAddress(0)[1],PORT);
	}
	
	public String[] getIndexAddress(int index) {
		return getIndexAddressStat(address,index);
	}
	
	//[0]=protocol
	//[1]=address
	public static String[] getIndexAddressStat(String addr, int index) {
		return addr.replaceAll("//", "").split("/")[index].split(":");
	}
	
	public void updateTimeStamp() {
		this.timeStamp=getNewTimeStamp();
	}
	
	public static long getNewTimeStamp() {
		return System.currentTimeMillis()/1000L;
	}
	
	//From strings
	public static EHAPRequest fromString(String reqStr) {
		try {
			String[] arr = reqStr.split(SEPARATOR);
			EHAPRequest req = new EHAPRequest(arr[3],EHAPRequestType.valueOf(arr[5]));
			req.setTimeStamp(Long.parseLong(arr[2]));
			req.setAddress(arr[3]);
			req.setAddressFrom(arr[4]);
			req.setDatatype(Integer.parseInt(arr[6]));
			if (arr.length>7) req.setData(arr[7]);
			if (arr.length>8) req.setAdditionalData(additionalDataFromString(arr[8]));
			return req;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(String.format("Request string '%s' is malformed and does not follow %s %f.  Too few items were provided.",reqStr,protocol,version),e);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("Request string '%s' is malformed and does not follow %s %f.  Time stamp or data type malformed.",reqStr,protocol,version),e);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("Request string '%s' is malformed and does not follow %s %f.  Additional data may be malformed.",reqStr,protocol,version),e);
		}
	}
	
	public static HashMap<String,String> additionalDataFromString(String str) {
		try {
			String[] arr=str.split(ADDITIONALDATASEPARATOR);
			HashMap<String,String> result = new HashMap<String,String>();
			for (String item : arr) {
				String[] arr2 = item.split(":");
				result.put(arr2[0], arr2[1]);
			}
			return result;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Error parsing additional data.  Is data malformed?",e);
		}
	}
	
	//To Strings
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(protocol);
		result.append(SEPARATOR);
		result.append(version);
		result.append(SEPARATOR);
		result.append(timeStamp);
		result.append(SEPARATOR);
		result.append(address);
		result.append(SEPARATOR);
		result.append(addressFrom);
		result.append(SEPARATOR);
		result.append(type);
		result.append(SEPARATOR);
		result.append(datatype);
		result.append(SEPARATOR);
		result.append(data);
		result.append(SEPARATOR);
		result.append(getAdditionalDataString());
		result.append(SEPARATOR);
		return result.toString();
	}
	
	public String getAdditionalDataString() {
		StringBuffer result = new StringBuffer();
		for (String key : additionalData.keySet()) {
			result.append(key+":"+additionalData.get(key)+ADDITIONALDATASEPARATOR);
		}
		return result.toString();
	}
	
	//Getters and setters
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressFrom() {
		return addressFrom;
	}

	public void setAddressFrom(String addressFrom) {
		this.addressFrom = addressFrom;
	}

	public EHAPRequestType getType() {
		return type;
	}

	public void setType(EHAPRequestType type) {
		this.type = type;
	}

	public int getDatatype() {
		return datatype;
	}

	public void setDatatype(int datatype) {
		this.datatype = datatype;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public HashMap<String, String> getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(HashMap<String, String> additionalData) {
		this.additionalData = additionalData;
	}

	public String getProtocol() {
		return protocol;
	}

	public float getVersion() {
		return version;
	}
	
	public boolean isSent() {
		return sent;
	}
}