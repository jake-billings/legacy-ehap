package com.eakjb.ehap.core;

public enum EHAPRequestType {
	PULL,
	PUSH,
	SET;
	
	public String toString() {
		return this.name();
	}
}
