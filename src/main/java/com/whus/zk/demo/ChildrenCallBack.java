package com.whus.zk.demo;


import java.util.List;

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;

public class ChildrenCallBack implements ChildrenCallback {

	public ChildrenCallBack(){
		
	}

	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children) {
		for (String s : children) {
			System.out.println(s);
		}
		System.out.println("ChildrenCallback:" + path);
		System.out.println((String)ctx);	
	}

}
