package com.dinstone.agate.gateway;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import io.vertx.ext.web.impl.Utils;

public class DateFormatTest {
	public static void main(String[] args) {
		long timestamp = new Date().getTime();

		tdate(timestamp);
		ndate(timestamp);
	}

	private static void tdate(long timestamp) {
		DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

		long s = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			String ts = dateTimeFormat.format(new Date(timestamp));
		}
		long e = System.currentTimeMillis();

		System.out.println("tdate is " + (e - s));
	}

	private static void ndate(long timestamp) {
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
		long s = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			String ts = f.format(Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("GMT")));
//			String ts = Utils.formatRFC1123DateTime(timestamp);
		}
		long e = System.currentTimeMillis();

		System.out.println("ndate is " + (e - s));
	}
}
