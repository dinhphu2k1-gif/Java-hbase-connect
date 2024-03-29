package org.example.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class HbaseConnection {
    private final static String HBASE_ZOOKEEPER_QUORUM = "192.168.23.37,192.168.23.39,192.168.23.41";

    private final static String HBASE_ZOOKEEPER_PORT = "2181";

    private Connection connection;

    public HbaseConnection() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum",HBASE_ZOOKEEPER_QUORUM);
        conf.set("hbase.zookeeper.property.clientPort", HBASE_ZOOKEEPER_PORT);
        conf.set("hbase.client.scanner.caching", "5000");

        try {
            connection = ConnectionFactory.createConnection(conf);
            System.out.println("init connection!!!!");
            System.out.println("hbase.client.scanner.caching default value -> " + connection.getConfiguration().get("hbase.client.scanner.caching"));
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
