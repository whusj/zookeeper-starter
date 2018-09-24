package com.whus.zk;


import org.apache.zookeeper.AsyncCallback.VoidCallback;

public class DeleteCallBack implements VoidCallback {
    @Override
    public void processResult(int rc, String path, Object ctx) {
        System.out.println("DeleteCallBack->删除节点: " + path+" 成功...");
        System.out.println("DeleteCallBack->ctx: " +(String)ctx);
    }
}
