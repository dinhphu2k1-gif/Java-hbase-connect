package org.example.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class HbaseConnection {
    private final static String HBASE_ZOOKEEPER_QUORUM = "";

    private final static String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "2181";

    private Connection connection;

    public HbaseConnection() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum","m1,m2");
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        try {
            connection = ConnectionFactory.createConnection(conf);
            System.out.println("init connection!!!!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnect() {
        try {
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        HbaseConnection hbaseConnection = new HbaseConnection();
        Connection connection = hbaseConnection.getConnection();
        connection.close();
    }
}
