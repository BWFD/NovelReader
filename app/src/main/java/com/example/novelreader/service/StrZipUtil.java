package com.example.novelreader.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StrZipUtil {
    public static String compress(String input) throws IOException {
        if (input == null || input.isEmpty()) {
            return input;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipOs = new GZIPOutputStream(out);
        gzipOs.write(input.getBytes(StandardCharsets.UTF_8));
        gzipOs.close();
        return out.toString("ISO-8859-1");
    }

    public static String uncompress(String zippedStr) throws IOException {
        if (zippedStr == null || zippedStr.isEmpty()) {
            return zippedStr;
        }
        ByteArrayInputStream in = new ByteArrayInputStream(zippedStr.getBytes("ISO-8859-1"));
        GZIPInputStream gzipIs = new GZIPInputStream(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int n;
        while ((n = gzipIs.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString("UTF-8");
    }
}
