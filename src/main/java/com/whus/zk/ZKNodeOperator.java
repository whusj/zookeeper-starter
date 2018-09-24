package com.whus.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 *
 * @Title: ZKConnectDemo.java
 * @Description: zookeeper 操作demo演示
 */
public class ZKNodeOperator implements Watcher {

    final static Logger log = LoggerFactory.getLogger(ZKConnect.class);
    public static final String zkServerPath = "192.168.202.61:2181,192.168.202.62:2181,192.168.202.63:2181";
    public static final Integer timeout = 30000;

    private ZooKeeper zooKeeper = null;

    public ZKNodeOperator (){

    }

    public ZKNodeOperator(String connectString){
        try {
            zooKeeper = new ZooKeeper(connectString, timeout , new ZKNodeOperator());
        } catch (IOException e) {
            e.printStackTrace();
            if (zooKeeper != null){
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void createZKNode(String path, byte[] data, List<ACL> acls){
        String result = "";
        try {
            /**
             * 同步或者异步创建节点，都不支持子节点的递归创建，异步有一个callback函数
             * 参数：
             * path：创建的路径
             * data：存储的数据的byte[]
             * acl：控制权限策略
             * 			Ids.OPEN_ACL_UNSAFE --> world:anyone:cdrwa
             * 			CREATOR_ALL_ACL --> auth:user:password:cdrwa
             * createMode：节点类型, 是一个枚举
             * 			PERSISTENT：持久节点
             * 			PERSISTENT_SEQUENTIAL：持久顺序节点
             * 			EPHEMERAL：临时节点
             * 			EPHEMERAL_SEQUENTIAL：临时顺序节点
             */
            //result = zooKeeper.create(path, data ,acls , CreateMode.PERSISTENT);//同步创建方式
            String ctx = "{'create': 'success'}";
            zooKeeper.create(path, data ,acls, CreateMode.PERSISTENT, new CreateCallBack(), ctx);//异步创建方式
        }catch (Exception e){

        }
    }

    public static void main(String[] args) throws  Exception{
        ZKNodeOperator zkServer = new ZKNodeOperator(zkServerPath);

        // 创建zk节点-同步方式
        //zkServer.getZooKeeper().create("/testnode","testnode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        //System.out.println("创建节点: " + "/testnode" + "\t成功...");

        // 创建zk节点-异步方式
        //String ctx = "{'create': 'success'}";
        //zkServer.getZooKeeper().create("/testnode","testnode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT,new CreateCallBack(),ctx);
        //new Thread().sleep(30000);

        /**
         * 参数：
         * path：节点路径
         * data：数据
         * version：数据状态
         */
        //Stat status = zkServer.getZooKeeper().setData("/testnode", "xyz1".getBytes(), 1);
        //System.out.println(status.getVersion());


        /**
         * 测试创建临时节点，会自动删除
         */
        //zkServer.getZooKeeper().create ("/tmpnode", "tmpnode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);


        /**
         * 参数：
         * path：节点路径
         * version：数据状态
         */
        //删除zk节点-同步方式
        //zkServer.getZooKeeper().delete("/test-delete-node",0);
        //System.out.println("同步删除节点: " + "/test-delete-node" +" 成功...");

        //删除zk节点-异步方式
        //String ctx = "{'delete': 'success'}";
        //zkServer.getZooKeeper().delete("/test-delete-node",0, new DeleteCallBack(), ctx);
        //new Thread().sleep(10000);

    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {

    }
}
