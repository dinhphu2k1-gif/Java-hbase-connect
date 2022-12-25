package org.example.hbase;

import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.RegionMetrics;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.protobuf.generated.ClusterStatusProtos;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ClusterInfo {
    public static void main(String[] args) throws IOException {
        HbaseConnection hbaseConnection = new HbaseConnection();
        Connection connection = hbaseConnection.getConnection();

        Admin admin = connection.getAdmin();
        TableName table = TableName.valueOf("pii_table");

        Collection<ServerName> servers = admin.getRegionServers();
        for (ServerName serverName : servers) {
            List<RegionMetrics> regionMetricsList = admin.getRegionMetrics(serverName, table);
            for (RegionMetrics regionMetrics : regionMetricsList) {
                System.out.println("region name: " + regionMetrics.getRegionName().toString());
                System.out.println("num read request: " + regionMetrics.getReadRequestCount());
                System.out.println("num write request: " + regionMetrics.getWriteRequestCount());
                System.out.println(regionMetrics.getStoreFileSize());
            }
        }



        connection.close();

    }
}
