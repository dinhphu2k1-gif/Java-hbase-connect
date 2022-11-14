package org.example;

import org.apache.phoenix.schema.SortOrder;
import org.apache.phoenix.schema.types.PLong;

import java.nio.charset.StandardCharsets;


public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        System.out.println("Driver class loaded successfully");
    }
}
