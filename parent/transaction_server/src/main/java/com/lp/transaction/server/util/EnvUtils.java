package com.lp.transaction.server.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class EnvUtils {
	private static Boolean WRITABLE = null;

	@SuppressWarnings("rawtypes")
	public static String getThisIp() {
		try {
			Enumeration allNetInterfaces = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
						.nextElement();
				Enumeration addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			;
		}
		return "unkwon";
	}

	public static Boolean isWritableNode() {
		return WRITABLE;
	}

	public static void isWritableNode(Boolean isWritable) {
		WRITABLE = isWritable;
	}

	public static void main(String[] args) {
		long d1 = System.currentTimeMillis();
		System.err.println(getThisIp());
		System.err.println(System.currentTimeMillis() - d1);
	}
}
