package com.whus.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKNodeExist implements Watcher {

    final static Logger log = LoggerFactory.getLogger(ZKConnect.class);
    public static final String zkServerPath = "192.168.202.61:2181,192.168.202.62:2181,192.168.202.63:2181";
    public static final Integer timeout = 30000;

    private ZooKeeper zookeeper = null;

    public ZKNodeExist() {}

    public ZKNodeExist(String connectString) {
        try {
            zookeeper = new ZooKeeper(connectString, timeout, new ZKNodeExist());
        } catch (IOException e) {
            e.printStackTrace();
            if (zookeeper != null) {
                try {
                    zookeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static CountDownLatch countDown = new CountDownLatch(1);

    public static void main(String[] args) throws Exception{
        ZKNodeExist zkServer = new ZKNodeExist(zkServerPath);

        /**
         * 参数：
         * path：节点路径
         * watch：watch
         */
        Stat stat = zkServer.getZookeeper().exists("/imooc-fake", true);
        if (stat != null) {
            System.out.println("查询的节点版本为dataVersion：" + stat.getVersion());
        } else {
            System.out.println("该节点不存在...");
        }
        countDown.await();
    }

    public ZooKeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(ZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeCreated) {
            System.out.println("节点创建");
            countDown.countDown();
        } else if (event.getType() == Event.EventType.NodeDataChanged) {
            System.out.println("节点数据改变");
            countDown.countDown();
        } else if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("节点删除");
            countDown.countDown();
        }
    }
}
