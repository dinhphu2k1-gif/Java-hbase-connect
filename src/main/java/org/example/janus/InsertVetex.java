package org.example.janus;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphVertex;
import org.janusgraph.core.Transaction;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class InsertVetex {

    private JanusGraph graph;

    public InsertVetex() {
//        graph = JanusGraphFactory.open("/home/phukaioh/Documents/Java-hbase-connect/conf/janus-hbase-es.properties");
        JanusGraphFactory.Builder config = JanusGraphFactory.build();
//        config.set("storage.backend", "hbase");
//        config.set("storage.hostname", "m1,m2");
//        config.set("storage.hbase.table", "pii_table");
        config.set("storage.backend", "inmemory");
//        config.set("index.search.backend", "elasticsearch");
//        config.set("index.search.hostname", "m1,m2");
//        config.set("index.search.index-name", "janusgraph_pii_index");
//
        graph = config.open();
        System.out.println("connect graph!!!");
    }

    public void insertPii() {
        // 14:55
        HashMap<String, Object> properties = new HashMap<>();
        UUID id = UUID.randomUUID();
        properties.put("id", id);
        properties.put("createdTime", 2);
        properties.put("updatedTime", 2);
        properties.put("pii",id.toString());
        properties.put("isActive", false);

        JanusGraphVertex v = graph.addVertex("pii");
        properties.forEach(v::property);
        graph.tx().commit();
    }

    public void insertWeb() {
        // 5:38, 8:42, 13:52
        HashMap<String, Object> properties = new HashMap<>();
        UUID id = UUID.randomUUID();
        properties.put("id", id);
        properties.put("createdTime", 1);
        properties.put("updatedTime", 1);
        properties.put("vertexLabel", "webPc");
        properties.put("isActive", true);
        properties.put("domain", "kenh14.vn");
        properties.put("domainGuid", "550c2ef62d101838cc8ebac3aa1b1342");
        properties.put("matchDomain", "-1");
        properties.put("fingerprint", "-1");
        properties.put("guid", "4834636432245384050");
        properties.put("ga", "GA1.2.651809764.1658740303");

        JanusGraphVertex v = graph.addVertex("webPc");
        properties.forEach(v::property);
        graph.tx().commit();
    }

    public void close() {
        graph.close();
    }

    public static void main(String[] args) {
        InsertVetex insertVetex = new InsertVetex();
        insertVetex.insertPii();
//        insertVetex.insertWeb();

        insertVetex.close();
    }
}

