package com.whus.zk.demo;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import java.util.List;


public class CreateCallBack implements AsyncCallback.StringCallback {

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("CreateCallBack->创建节点: " + path+ "\t成功...");
        System.out.println("CreateCallBack->ctx: " + (String)ctx);
    }
}
