package com.whusj.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKConnectSessionWatcher implements Watcher {

    final static Logger log = LoggerFactory.getLogger(ZKConnect.class);
    public static final String zkServerPath = "192.168.202.61:2181,192.168.202.62:2181,192.168.202.63:2181";
    public static final Integer timeout = 30000;

    public static void main(String[] args) throws Exception{
        ZooKeeper zk = new ZooKeeper(zkServerPath,timeout, new ZKConnectSessionWatcher());

        long sessionId = zk.getSessionId();
        String ssid = "0x" + Long.toHexString(sessionId);
        System.out.println("sessionId: " + ssid);
        byte[] sessionPassword = zk.getSessionPasswd();

        log.warn("客户端开始连接zookeeper服务器...");
        log.warn("连接状态：{}", zk.getState());
        new Thread().sleep(10000);
        log.warn("连接状态：{}", zk.getState());
        new Thread().sleep(10000);

        // 开始会话重连
        log.warn("开始会话重连...");
        ZooKeeper zkSession = new ZooKeeper(zkServerPath,timeout,new ZKConnectSessionWatcher(),sessionId,sessionPassword);

        log.warn("重新连接状态zkSession：{}", zkSession.getState());
        new Thread().sleep(10000);
        log.warn("重新连接状态zkSession：{}", zkSession.getState());
    }

    @Override
    public void process(WatchedEvent event) {

    }
}
