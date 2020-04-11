package com.dinstone.agate.manager;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;

public class ConsulClientTest {

	public static void main(String[] args) {
		KeyValueClient kvc = Consul.builder().build().keyValueClient();

		String key = "agate/apps/default/gw-app";
		String value = "{\n" + 
				"    \"cluster\": \"default\",\n" + 
				"    \"appName\": \"gw-app\",\n" + 
				"    \"prefix\": \"/consul\",\n" + 
				"    \"apiName\": \"consul\",\n" + 
				"    \"path\": \"/*\",\n" + 
				"    \"rateLimit\": {\n" + 
				"        \"permitsPerSecond\": 5000\n" + 
				"    },\n" + 
				"    \"backend\": {\n" + 
				"        \"params\": [\n" + 
				"            {\n" + 
				"                \"feParamName\": \"path\",\n" + 
				"                \"feParamType\": \"PATH\",\n" + 
				"                \"beParamName\": \"path\",\n" + 
				"                \"beParamType\": \"path\"\n" + 
				"            }\n" + 
				"        ],\n" + 
				"        \"urls\": [\n" + 
				"            \"http://localhost:8500/ui/dc1/intentions\",\n" + 
				"            \"http://localhost:8500/ui/dc1/intentions\"\n" + 
				"        ]\n" + 
				"    }\n" + 
				"}";
		boolean ok = kvc.putValue(key, value);
		
		System.out.println(ok);
	}

}
