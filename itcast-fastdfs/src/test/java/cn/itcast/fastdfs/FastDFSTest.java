package cn.itcast.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author JW
 * @createTime 2018/10/24 3:52 PM
 * @desc todo
 */
public class FastDFSTest {

    @Test
    public void test() throws Exception {

        //追踪服务器文件的路径
        String path = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();

        //设置全局的配置
        ClientGlobal.init(path);

        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageServer storageServer = null;

        //创建存储服务器客户端
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        /**
         * 上传文件
         * 参数1:文件路径
         * 参数2:文件类型后缀
         * 参数3:文件的属性信息
         *
         * 返回结果:
         * group1
         * M00/00/00/wKgMqFvP9fGAfYrmAAcXYOaEDQo570.jpg
         */
        String[] upload_file = storageClient.upload_file("/Users/apple/Pictures/mm/010640t7xw5wxdesz5yykm.jpg", "jpg", null);

        if (upload_file != null && upload_file.length > 0) {
            for (String s : upload_file) {
                System.out.println(s);
            }
        }

        //获取存储服务器信息
        String groupName = upload_file[0];
        String fileName = upload_file[1];

        ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, groupName, fileName);
        for (ServerInfo serverInfo : serverInfos) {
            System.out.println("ip=" + serverInfo.getIpAddr() + ",port=" + serverInfo.getPort());
        }

        //组合可的访问路径
        String url = "http://" + serverInfos[0].getIpAddr() + "/" + groupName + "/" + fileName;
        System.out.println(url);
    }

    @Test
    public void test1() {
        String s1 = "hello";
        String s2 = "he" + new String("llo");
        System.out.println(s1 == s2);
        System.out.println(s1.equals(s2));

    }

    @Test
    public void test2() {
        int a = 8;
        System.out.println(++a + 1);
        System.out.println(a++ + 1);
    }
}
