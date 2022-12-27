package org.example.hbase;

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterInfo {


    public static void main(String[] args) throws IOException {
        HbaseConnection hbaseConnection = new HbaseConnection();
        Connection connection = hbaseConnection.getConnection();

        Admin admin = connection.getAdmin();
        TableName table = TableName.valueOf("pii_table");

        System.out.println("cluster master:" + admin.getClusterMetrics().getMasterName());

        ClusterStatus clusterStatus = admin.getClusterStatus();
        Map<ServerName, ServerMetrics> serverMetricsMap = clusterStatus.getLiveServerMetrics();
        for (Map.Entry<ServerName, ServerMetrics> entry : serverMetricsMap.entrySet()) {
            System.out.println("server name: " + entry.getKey());
            ServerMetrics serverMetrics = entry.getValue();
            System.out.println("request per second: " + serverMetrics.getRequestCountPerSecond());
        }


//        Collection<ServerName> servers = admin.getRegionServers();
//        RegionMetrics regionMaxReq = null; // region có lượng read request nhiều nhất
//
//        ServerName sourceMove = null; // server chứa region ở trên
//        ServerName destinationMove = null; // server có lượng i/o it nhất
//
//        long maxRegionReadReq = 0;
//        long minServerReq = Long.MAX_VALUE;
//
//        for (ServerName serverName : servers) {
//            List<RegionMetrics> regionMetricsList = admin.getRegionMetrics(serverName, table);
//            long totlaReq = 0; // tổng request của server đó
//
//            for (RegionMetrics regionMetrics : regionMetricsList) {
//                long numReadReq = regionMetrics.getReadRequestCount();
//                long numWriteRed = regionMetrics.getWriteRequestCount();
//
//                totlaReq += numReadReq + numWriteRed;
//                if (numReadReq > maxRegionReadReq) {
//                    maxRegionReadReq = numReadReq;
//                    regionMaxReq = regionMetrics;
//                    sourceMove = serverName;
//                }
//            }
//
//            if (totlaReq < minServerReq) {
//                minServerReq = totlaReq;
//                destinationMove = serverName;
//            }
//        }
//
//        if (sourceMove != null && destinationMove != null) {
//            System.out.println("Result: ");
//            System.out.println("Region max: ");
//            System.out.println("region name: " + new String(regionMaxReq.getRegionName(), StandardCharsets.UTF_8));
//            System.out.println("num read request: " + regionMaxReq.getReadRequestCount());
//            System.out.println("num write request: " + regionMaxReq.getWriteRequestCount());
//
//            System.out.println("source server move: " + sourceMove);
//            System.out.println("destination server move: " + destinationMove);
//
//            long t1 = System.currentTimeMillis();
//
//            System.out.println(RegionInfo.encodeRegionName(regionMaxReq.getRegionName()));
//            admin.move(RegionInfo.encodeRegionName(regionMaxReq.getRegionName()).getBytes(), destinationMove);
//            long t2 = System.currentTimeMillis();
//            System.out.println("time move: " + (t2 - t1) + " ms");
//        }

        connection.close();

    }
}
