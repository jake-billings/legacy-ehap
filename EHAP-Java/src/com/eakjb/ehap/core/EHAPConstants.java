package com.eakjb.ehap.core;

import java.util.Arrays;
import java.util.List;

public interface EHAPConstants {
	public static final int PORT = 3264;
	public static final String[] TRUEVALUESARR = {"True","true","TRUE","yes","YES","Yes","1"};
	public static final String[] FALSEVALUESARR = {"False","false","FALSE","NO","No","no","0"};
	public static final String[] TRANSVALUESARR = {"3"};
	
	public static final List<String> TRUEVALUES = Arrays.asList(TRUEVALUESARR);
	public static final List<String> FALSEVALUES = Arrays.asList(FALSEVALUESARR);
	public static final List<String> TRANSVALUES = Arrays.asList(TRANSVALUESARR);
}