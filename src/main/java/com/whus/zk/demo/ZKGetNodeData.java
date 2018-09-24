package com.whus.zk.demo;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @Description: zookeeper 获取节点数据的demo演示
 */
public class ZKGetNodeData implements Watcher{

    final static Logger log = LoggerFactory.getLogger(ZKConnect.class);
    public static final String zkServerPath = "192.168.202.61:2181,192.168.202.62:2181,192.168.202.63:2181";
    public static final Integer timeout = 30000;
    private static Stat stat = new Stat();

    private ZooKeeper zookeeper = null;

    public ZKGetNodeData(){

    }

    public ZKGetNodeData(String connectString){
        try {
            zookeeper = new ZooKeeper(zkServerPath, timeout , new ZKGetNodeData());
        } catch (IOException e) {
            e.printStackTrace();
            if (zookeeper != null){
                try {
                    zookeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    private static CountDownLatch countDown = new CountDownLatch(1);

    public static void main(String[] args)throws Exception{

        ZKGetNodeData zkServer = new ZKGetNodeData(zkServerPath);

        /**
         * 参数：
         * path：节点路径
         * watch：true或者false，注册一个watch事件
         * stat：状态
         */
        byte[] resByte = zkServer.getZookeeper().getData("/imooc", true, stat);
        String result = new String(resByte);
        System.out.println("当前值: " + result);
        countDown.await();
    }

    @Override
    public void process(WatchedEvent event) {
        try {
            if (event.getType() == Event.EventType.NodeDataChanged){
                System.out.println("ZKGetNodeData->process(): NodeDataChanged");
                ZKGetNodeData zkServer = new ZKGetNodeData(zkServerPath);
                byte[] resByte = zkServer.getZookeeper().getData("/imooc", false, stat);
                String result = new String(resByte);
                System.out.println("更改后的值: " + result);
                System.out.println("版本号变化dversion: " + stat.getVersion());
            }else if(event.getType() == Event.EventType.NodeCreated){
                System.out.println("ZKGetNodeData->process(): NodeCreated");


            }else if(event.getType() == Event.EventType.NodeChildrenChanged){
                System.out.println("ZKGetNodeData->process(): NodeChildrenChanged");


            }else if(event.getType() == Event.EventType.NodeDeleted){
                System.out.println("ZKGetNodeData->process(): NodeDeleted");

            }
            countDown.countDown();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ZooKeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(ZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
    }
}
