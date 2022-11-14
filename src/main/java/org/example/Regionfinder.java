package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class Regionfinder {

    public static void main(String[] args) throws IOException {
        String table_name = "pii_table";
        String rowkey = "100321161256";
        TableName tablename = TableName.valueOf(table_name);
        byte[] keyInBytes = Bytes.toBytes(rowkey);
//        String s = new String(keyInBytes, StandardCharsets.UTF_8);
//        System.out.println(s);
//        for (byte k : keyInBytes) {
//            System.out.println(k);
//        }
        HbaseConnection hbaseConnection = new HbaseConnection();
        Connection connection = hbaseConnection.getConnection();
        Table table = connection.getTable(tablename);
        RegionLocator regionLocater = connection.getRegionLocator(tablename);
        HRegionLocation regionLocation = regionLocater.getRegionLocation(keyInBytes);
        Result result = table.get(new Get(keyInBytes));

        for (Cell cell : result.listCells()) {
            String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
            String value = Bytes.toString(CellUtil.cloneValue(cell));
            System.out.printf("Qualifier : %s : Value : %s", qualifier, value);
        }

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
