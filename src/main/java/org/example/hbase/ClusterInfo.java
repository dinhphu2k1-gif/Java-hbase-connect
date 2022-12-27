package org.example.hbase;

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ClusterInfo {
    private String fileName = "requestMetric.txt";
    private Connection connection;
    private TableName table = TableName.valueOf("pii_table");
    private Admin admin;

    public ClusterInfo() {
        HbaseConnection hbaseConnection = new HbaseConnection();
        connection = hbaseConnection.getConnection();

        try {
            admin = connection.getAdmin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy lại lượng request của các server đã ghi nhận trong 1 ngày trước đó
     *
     * @return
     */
    public HashMap<String, Long> getOldRequestMetric() {
        HashMap<String, Long> oldRequestMetric = new HashMap<>();
        File file = new File(fileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Old:");
        while (scanner.hasNextLine()) {
            String server = null;
            try {
                server = scanner.next();
            } catch (Exception e) {
                break;
            }
            if (server == null) break;
            long numRequest = scanner.nextLong();
            System.out.println("server: " + server + "\trequest: " + numRequest);
            oldRequestMetric.put(server, numRequest);
        }

        return oldRequestMetric;
    }

    /**
     * lấy lượng request mới nhất của các server
     *
     * @return
     */
    public HashMap<ServerName, Long> getNewRequestMetric() throws IOException {
        HashMap<ServerName, Long> newRequestMetric = new HashMap<>();
        ClusterMetrics clusterMetrics = admin.getClusterMetrics();

        System.out.println("\nNew: ");
        Map<ServerName, ServerMetrics> serverMetricsMap = clusterMetrics.getLiveServerMetrics();
        for (Map.Entry<ServerName, ServerMetrics> entry : serverMetricsMap.entrySet()) {
            ServerName serverName = entry.getKey();
            ServerMetrics serverMetrics = entry.getValue();
            //getRequestCount Returns total Number of requests from the start of the region server.
            System.out.println("server: " + serverName.getHostname() + "\trequest: " + serverMetrics.getRequestCount());

            newRequestMetric.put(serverName, serverMetrics.getRequestCount());
        }

        return newRequestMetric;
    }

    public void updateRequestMetric(HashMap<ServerName, Long> newRequestMetric) {
        String content = "";
        for (Map.Entry<ServerName, Long> entry : newRequestMetric.entrySet()) {
            String server = entry.getKey().getHostname();
            long numRequest = entry.getValue();

            content += server + " " + numRequest + "\n";
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(fileName);
            byte[] strToBytes = content.getBytes();
            outputStream.write(strToBytes);

            System.out.print("File is created successfully with the content.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RegionMetrics getRegionMaxRequest() throws IOException {
        RegionMetrics regionMaxRequest = null;
        Collection<ServerName> serverNameList = admin.getRegionServers();

        long maxRequest = Long.MIN_VALUE;

        for (ServerName serverName : serverNameList) {
            List<RegionMetrics> regionMetricsList = admin.getRegionMetrics(serverName, table);
            long totlaReq = 0; // tổng request của server đó

            for (RegionMetrics regionMetrics : regionMetricsList) {
                long numReadReq = regionMetrics.getReadRequestCount();
                long numWriteRed = regionMetrics.getWriteRequestCount();

                totlaReq += numReadReq + numWriteRed;
                if (totlaReq > maxRequest) {
                    maxRequest = totlaReq;
                    regionMaxRequest = regionMetrics;
                }
            }
        }

        System.out.println("region max request: " + new String(regionMaxRequest.getRegionName(), StandardCharsets.UTF_8) + "\tnum request: " + maxRequest);

        return regionMaxRequest;
    }

    public void balance() throws IOException {
        HashMap<String, Long> oldRequestMetric = getOldRequestMetric(); // lượng request cũ của các server
        HashMap<ServerName, Long> newRequestMetric = getNewRequestMetric(); // lượng request mới của các server

        HashMap<ServerName, Long> requestMetric = new HashMap<>(); // lượng request mới của mỗi server trong 1 khoảng thời gian
        long totalRequest = 0; // lượng request mới
        for (Map.Entry<ServerName, Long> entry : newRequestMetric.entrySet()) {
            String server = entry.getKey().getHostname();
            long newRequest = entry.getValue();
            if (oldRequestMetric.get(server) == null) continue; //có thể server bị down họăc thêm server mới
            long oldRequest = oldRequestMetric.get(server);
            long request = 0;
            if (newRequest < oldRequest) { // có thể server khởi động lại
                request = newRequest;
            } else {
                request = newRequest - oldRequest;
            }

            totalRequest += request;
            requestMetric.put(entry.getKey(), request);
        }

        double averageRequest = totalRequest * 1.0 / newRequestMetric.size(); // trung bình lượng request mới
        HashMap<ServerName, Long> sortedMap = requestMetric.entrySet(). // sau khi sắp xếp thì server có lượng request ít nhất sẽ ở đầu
                stream().
                sorted(Map.Entry.comparingByValue()).
                collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        System.out.println(sortedMap);

        RegionMetrics regionMaxRequest = getRegionMaxRequest();
        Map.Entry<ServerName, Long> serverMinRequest = sortedMap.entrySet().iterator().next();
        ServerName destinationServer = serverMinRequest.getKey();
        long minRequest = serverMinRequest.getValue();
        if ((minRequest * 1.0/averageRequest) < 0.8 && regionMaxRequest != null) { // nếu server có lượng request chênh lệnh so với trung bình hơn 20% thì sẽ move
            System.out.println("region max request: " + new String(regionMaxRequest.getRegionName(), StandardCharsets.UTF_8));
            System.out.println("des server: " + destinationServer);

//            admin.move(RegionInfo.encodeRegionName(regionMaxRequest.getRegionName()).getBytes(), destinationServer);
        }

        updateRequestMetric(newRequestMetric);
    }


    public static void main(String[] args) throws IOException {
        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.balance();
        clusterInfo.close();
    }
}
