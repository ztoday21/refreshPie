package com.eink.epdc;

public class Main {

	public Main() {
		System.loadLibrary("epd");
	}

	public native int FullRefresh2();

}
