package org.example;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * Unit test for simple App.
 */
public class AppTest {
    public static void main(String[] args) throws DecoderException, UnsupportedEncodingException {
//        byte[] arr = Hex.decodeHex("\\x90\\x00\\x00\\x00\\x00\\x00\\x00\\x80");
//        System.out.println(arr);
//        String encode = "test";
//        System.out.println(encode.toCharArray());
//
//        byte[] arr = Bytes.toBytes("414");
//        System.out.println(Arrays.toString(arr));
//        System.out.println(Hex.encodeHex(arr));
//        System.out.println(Arrays.toString(Hex.decodeHex("343134".toCharArray())));
        byte[] arr = {(byte) 144, 0, 0, 0, 0, 0, 0, (byte) 128};
        String s = new String(arr, StandardCharsets.UTF_16LE);
        System.out.println(s);

//        String hex = Bytes.toStringBinary(Bytes.toBytesBinary("\\xA0dg111as1\\xB3\\x18\\x92"));
//        System.out.println(hex);
//        byte[] bytes = Hex.decodeHex(hex.toCharArray());
//        System.out.println(new String(bytes, "UTF-8"));

        String originalInput = "test input";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        System.out.println(encodedString);
    }
}
