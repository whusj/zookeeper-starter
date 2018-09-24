package com.whusj.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class CuratorOperator {

    public CuratorFramework client = null;
    public static final String zkServerPath = "192.168.202.61:2181,192.168.202.62:2181,192.168.202.63:2181";
    public static final Integer timeout = 30000;

    /**
     * 实例化zk客户端
      */
    public CuratorOperator() {

        /**
         * 同步创建zk示例，原生api是异步的
         *
         * curator连接zookeeper的策略:ExponentialBackoffRetry
         * baseSleepTimeMs: 初始sleep的时间
         * maxRetries: 最大重试次数
         * maxSleepMS: 最大重试时间
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,5);

        /**
         * curator连接zookeeper的策略:RetryNTimes
         * n: 重试的次数
         * sleepMsBetweenRetries: 每次重试间隔的时间
         */
        //RetryPolicy retryPolicy = new RetryNTimes(3,5000);

        /**
         * curator连接zookeeper的策略:RetryOneTime
         * sleepMsBetweenRetry: 每次重试间隔的时间
         */
        //RetryPolicy retryPolicy = new RetryOneTime(5000);

        /**
         * 永远重试，不推荐使用
         */
        //RetryPolicy retryPolicy = new RetryForever(1000);

        /**
         * curator连接zookeeper的策略:RetryUntilElapsed
         * maxElapsedTimeMs: 最大重试时间
         * sleepMsBetweenRetries: 每次重试间隔
         * 重试时间超过maxElapsedTimeMs后，就不再重试
         */
        //RetryPolicy retryPolicy = new RetryUntilElapsed(10000,3000);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServerPath)
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy).namespace("workspace").build();
        client.start();
    }

    /**
     * @Description: 关闭zk客户端连接
     */
    private void closeZKClient() {
        if (client != null){
            this.client.close();
        }

    }

    public static void main(String[] args) throws Exception{
        //实例化
        CuratorOperator cto = new CuratorOperator();
        boolean isZKCuratorStarted = cto.client.isStarted();
        Thread.sleep(10000);
        System.out.println("当前客户的状态: " + (isZKCuratorStarted ? "连接中" : "已关闭"));

        String nodePath = "/super/imooc";

        //创建节点
        /**
        String nodePath = "/super/imooc/1/2/3/4/5/6";
        byte[] data = "superme".getBytes();
        cto.client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(nodePath,data);
        */

        //更新节点数据
        //String nodePath = "/super/imooc";
        //byte[] newData = "batman111".getBytes();
        //cto.client.setData().withVersion(1).forPath(nodePath, newData);

        //删除节点(可级联删除)
        /**
        String nodePath = "/super/imooc/1/2/3/4/5/6";
        cto.client.delete()
                .guaranteed()               //如果删除失败，那么在后端还是继续会删除，直到成功
                .deletingChildrenIfNeeded() //如果有子结点，就删除
                .withVersion(0)
                .forPath("/super/imooc/1");
         */

        /**
         * 读取节点数据
         */
        /**
        Stat stat = new Stat();
        String nodePath = "/super/imooc";
        byte[] data = cto.client.getData().storingStatIn(stat).forPath(nodePath);
        System.out.println("节点" + nodePath + "的数据为: " + new String(data));
        System.out.println("改节点的版本号为: " + stat.getVersion());
        */

        //查询子节点
        /**
        String nodePath = "/super/imooc";
        List<String> childNodes = cto.client.getChildren().forPath(nodePath);
        System.out.println("开始打印子节点: ");
        for (String s : childNodes){
            System.out.println(s);
        }
        */

        //判断节点是否存在，如果不存在则为空
        //String nodePath = "/super/imooc43";
        //Stat statExist = cto.client.checkExists().forPath(nodePath);
        //System.out.println("返回值: " + statExist);

        //watcher事件 当使用usingWatcher的时候，监听只会触发一次，监听完毕后就销毁
        //cto.client.getData().usingWatcher(new MyWatcher()).forPath(nodePath);
        //cto.client.getData().usingWatcher(new MyCuratorWatcher()).forPath(nodePath);

        //为节点添加watcher
        /**
        //NodeCache: 监听数据节点的变更，会触发事件
        final NodeCache nodeCache = new NodeCache(cto.client, nodePath);
        //buildInitial: 初始化的时候获取node的值并且缓存
        nodeCache.start(true);
        if (nodeCache.getCurrentData() != null){
            System.out.println("节点初始化数据为: " + new String(nodeCache.getCurrentData().getData()));
        }else{
            System.out.println("节点初始化数据为空...");
        }
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("nodeCache.getPath(): "+nodeCache.getPath());
                String data = null;
                String path = null;
                if(nodeCache.getCurrentData() != null && nodeCache.getCurrentData().getData() != null){
                    data = new String(nodeCache.getCurrentData().getData());
                    path = nodeCache.getCurrentData().getPath();
                }
                System.out.println("节点路径: " + path+"数据: " + data);
            }
        });
        */

        //为子节点添加watcher
        //PathChildrenCache: 监听数据节点的增删改，会触发事件
        String childNodePathCache = nodePath;
        //cacheData: 设置缓存节点的数据状态
        final PathChildrenCache childrenCache = new PathChildrenCache(cto.client, childNodePathCache, true);
        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT: 异步初始化，初始化之后会触发事件
         * NORMAL: 异步初始化
         * BUILD_INITIAL_CACHE: 同步初始化
         */
         childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

         List<ChildData> childDataList = childrenCache.getCurrentData();
         System.out.println("当前数据节点的子节点数据列表: ");
         for (ChildData cd : childDataList){
             String childData = new String(cd.getData());
         }

         childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
             @Override
             public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                 if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
                     System.out.println("子节点初始化ok...");
                 }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                     System.out.println("添加子节点: " + event.getData().getPath());
                     System.out.println("子节点数据: " + new String(event.getData().getData()));
                 }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                     System.out.println("删除子节点: " + event.getData().getPath());
                 }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                     System.out.println("修改子节点路径: " + event.getData().getPath());
                     System.out.println("修改子节点数据: " + new String(event.getData().getData()));
                 }
             }
         });


        Thread.sleep(100000);
        cto.closeZKClient();
    }
}
