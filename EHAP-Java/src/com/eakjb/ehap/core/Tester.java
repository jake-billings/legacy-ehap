package com.eakjb.ehap.core;

import java.io.IOException;

public class Tester {

	public static void main(String[] args) {
		String str="EHAP;1.1;1394246786;EHAP://192.168.1.104;EHAP://localhost;SET;0;True;spam1:spam2,spam3:spam4;";
		EHAPRequest req = EHAPRequest.fromString(str);
		System.out.println(req.getIndexAddress(0)[1]);
		System.out.println(req.toString());
		try {
			req.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
