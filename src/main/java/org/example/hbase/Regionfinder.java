package org.example.hbase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class Regionfinder {

    public static void main(String[] args) throws IOException {
        String table_name = "pii_table";
        String rowkey = "4240";
        TableName tablename = TableName.valueOf(table_name);
        byte[] keyInBytes = Bytes.toBytes(rowkey);
        System.out.println(Arrays.toString(keyInBytes));
//        String s = new String(keyInBytes, StandardCharsets.UTF_8);
//        System.out.println(s);

        HbaseConnection hbaseConnection = new HbaseConnection();
        Connection connection = hbaseConnection.getConnection();
        Table table = connection.getTable(tablename);
        RegionLocator regionLocater = connection.getRegionLocator(tablename);
        HRegionLocation regionLocation = regionLocater.getRegionLocation(keyInBytes);
//        Result result = table.get(new Get(keyInBytes));

        Scan scan = new Scan();
        scan.setCaching(20);
        ResultScanner scanner = table.getScanner(scan);
        for (Result result = scanner.next(); (result != null); result = scanner.next()) {
            Get get = new Get(result.getRow());
            Result entireRow = table.get(get);
            System.out.println(entireRow);
        }

//        if (result.isEmpty()) System.out.println("nothing");
//        else for (Cell cell : result.listCells()) {
////            String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
////            String value = Bytes.toString(CellUtil.cloneValue(cell));
////            System.out.printf("Qualifier : %s : Value : %s", qualifier, value);
//            System.out.println(cell);
//        }

//        if(result.isEmpty()){
//            System.out.println("Rowkey "+rowkey+" is not exist in any region. It will be placed in region : "+regionLocation.getRegion().getRegionNameAsString());
//        }else{
//            System.out.println("Table Name = " + tablename + "\n" + "Row Key = " + rowkey + "\n" + "Region Server = "
//                    + regionLocation.getServerName() + "\n" + "Region Name = "
//                    + regionLocation.getRegion().getRegionNameAsString() + "\n" + "Encoded Region Name = "
//                    + regionLocation.getRegion().getEncodedName());
//        }

        hbaseConnection.closeConnect();
    }
}
