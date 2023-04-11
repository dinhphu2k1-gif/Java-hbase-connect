package org.example.hbase;

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class HbaseCRUD {
    HbaseConnection hbaseConnection;
    Connection connection;
    Admin admin;

    public HbaseCRUD() {
        hbaseConnection = new HbaseConnection();
        connection = hbaseConnection.getConnection();

        try {
            admin = connection.getAdmin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable(String tableName) throws IOException {
//        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName))
//                .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("mapping")).build())
////                .setValue(TableDescriptorBuilder.SPLIT_POLICY, RegionSplitter.HexStringSplit.class.getName())
//                .build();
//
//        String startRow = "000";
//        String endRow = "999";
//        byte[][] splits = Bytes.split(Bytes.toBytes(startRow), Bytes.toBytes(endRow), numRegions - 1);
//
//        try {
////            admin.createTable(tableDescriptor, Bytes.toBytes("000000000000000000"), Bytes.toBytes("9999999999999999999"), numRegions);
//            admin.createTable(tableDescriptor, splits);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        TableName name = TableName.valueOf(tableName);

        if (admin.tableExists(name)) {
            System.out.println("Table " + name.getNameAsString() + " exist!!!");
            deleteTable(tableName);
//            return;
        }


        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(name)
                .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("mapping")).build());

        List<ColumnFamilyDescriptor> familyDescriptors = new ArrayList<>();

        ColumnFamilyDescriptorBuilder builder = ColumnFamilyDescriptorBuilder
                .newBuilder(Bytes.toBytes("mapping"))
                .setTimeToLive(7776000)
                .setCompressionType(Compression.Algorithm.GZ);
        familyDescriptors.add(builder.build());


        tableDescriptorBuilder.setColumnFamilies(familyDescriptors);
        tableDescriptorBuilder.setRegionSplitPolicyClassName("HexStringSplit");
//        tableDescriptorBuilder.

//        String[] splits = {"166", "332", "498", "664", "830"};
//        byte[][] splitPoints = new byte[splits.length][];
//        for (int i = 0; i < splits.length; i++) {
//            splitPoints[i] = Bytes.toBytes(splits[i]);
//        }

//        admin.createTable(tableDescriptorBuilder.build(), splitPoints);
    }

    public void truncate(String tableName) throws IOException {
        if (admin.tableExists(TableName.valueOf(tableName))) {
            admin.disableTable(TableName.valueOf(tableName));
            admin.truncateTable(TableName.valueOf(tableName), true);
        }
    }

    public void cloneTable(String sourceTable, String targetTable) throws IOException {
        TableDescriptor sourceTableDesc = admin.getDescriptor(TableName.valueOf(sourceTable));
        TableDescriptorBuilder targetTableDescBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(targetTable));

        for (ColumnFamilyDescriptor cfDesc : sourceTableDesc.getColumnFamilies()) {
            targetTableDescBuilder.setColumnFamily(cfDesc);
        }

//        admin.cloneTable(sourceTable, targetTable, targetTableDescBuilder.build(), false);
        admin.cloneTableSchema(TableName.valueOf(sourceTable), TableName.valueOf(targetTable), false);
    }

    public void deleteTable(String name) throws IOException {
        TableName tableName = TableName.valueOf(name);

        if (admin.tableExists(tableName)) {
            // Disable the table before dropping
            admin.disableTable(tableName);

            // Drop the table
            admin.deleteTable(tableName);

            System.out.println("Table " + tableName.getNameAsString() + " dropped successfully.");
        } else {
            System.out.println("Table " + tableName.getNameAsString() + " does not exist.");
        }


    }

    public void pushGuidToPii() throws IOException {
        TableName tableName = TableName.valueOf("test2");
        Table table = connection.getTable(tableName);

        byte[] rowkey = Bytes.toBytes("1");
        byte[] family = Bytes.toBytes("pii");
        byte[] qualifier = Bytes.toBytes("pii");
        byte[] value = Bytes.toBytes("2");

        Put put = new Put(rowkey);
        put.addColumn(family, qualifier, value);

        table.put(put);
    }

    public void getPiiFromGuid() throws IOException {
        TableName tableName = TableName.valueOf("test2");
        Table table = connection.getTable(tableName);

        // Create get object
        byte[] rowkey = Bytes.toBytes("1");
        Get get = new Get(rowkey);

        // Get data
        Result result = table.get(get);
        byte[] family = Bytes.toBytes("pii");
        byte[] qualifier = Bytes.toBytes("pii");
        byte[] value = result.getValue(family, qualifier);

        // Print value
        System.out.println(Bytes.toString(value));
    }

    public void moveRegion(String tableName, String regionName, String dest) throws IOException {
        ClusterMetrics clusterMetrics = admin.getClusterMetrics();
        List<ServerName> serverNameList = clusterMetrics.getServersName();

        ServerName destServerName = null;
        for (ServerName serverName : serverNameList) {
            System.out.println(serverName.getHostname());
            if (serverName.getHostname().equals(dest)) {
                destServerName = serverName;
            }
        }

        RegionInfo regionMove = null;

        List<RegionInfo> regionInfos = admin.getRegions(TableName.valueOf(tableName));
        for (RegionInfo regionInfo : regionInfos) {
            System.out.println(regionInfo.getEncodedName());
            if (regionInfo.getEncodedName().equals(regionName)) {
                regionMove = regionInfo;
            }
        }

        System.out.println("region move: " + regionMove.getEncodedName());
        System.out.println("des server: " + destServerName.getHostname());

        admin.move(regionMove.getEncodedNameAsBytes(), destServerName);
    }

    public void close() {
        try {
            connection.close();
            admin.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
//        if ("--func".equals(args[0])) {
//            String func = args[1];
//            HbaseCRUD hbaseCRUD = new HbaseCRUD();
////        hbaseCRUD.createTable("test3", 12);
////        hbaseCRUD.truncate("domain_guid_to_pii_v2");
////        hbaseCRUD.truncate("guid_to_pii_v2");
////        hbaseCRUD.splitTable("test2", 3);
////        hbaseCRUD.pushGuidToPii();
////        hbaseCRUD.getPiiFromGuid();
//
////        hbaseCRUD.cloneTable("domain_guid_to_pii_v2", "pii_to_guid_v2");
//
//            switch (func){
//                case "moveRegion":
//                    String tableName = args[2];
//                    String regionName = args[3];
//                    String serverName = args[4];
//
//                    hbaseCRUD.moveRegion(tableName, regionName, serverName);
//            }
//
//            hbaseCRUD.close();
//        } else {
//            System.out.println("wrong param!!");
//        }

        HbaseCRUD hbaseCRUD = new HbaseCRUD();
//        hbaseCRUD.deleteTable("pii_test");
        hbaseCRUD.moveRegion("pii_table_v3", "de39e34b1642d1674628904d79322fc5", "hadoop2339");
        hbaseCRUD.close();

    }
}
