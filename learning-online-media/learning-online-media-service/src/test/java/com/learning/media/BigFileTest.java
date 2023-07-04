package com.learning.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 测试大文件上传方法
 * @date 2023/7/4 15:21
 **/
public class BigFileTest {
    // 分块上传
    @Test
    public void testChunk() throws IOException {
        // 源文件
        File sourceFile = new File("D:\\下载\\Downloads\\Video\\阴阳师.mp4");
        // 分块目录
        String chunkFilePath = "D:\\Test\\learning-online\\upload\\chunk\\";
        // 分块文件大小
        int chunkSize = 1024 * 1024 * 5;
        // 分块文件个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        // 使用流从源文件读取数据，向分块文件中写数据
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        // 缓存区
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            // 分块文件写入流
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
            raf_rw.close();
        }
        raf_r.close();
    }

    // 将分块进行合并
    @Test
    public void testMerge() throws IOException {
        // 分块目录
        File chunkFolder = new File("D:\\Test\\learning-online\\upload\\chunk\\");
        // 源文件
        File sourceFile = new File("D:\\Test\\learning-online\\down\\阴阳师.mp4");
        // 合并后文件
        File mergeFile = new File("D:\\Test\\learning-online\\down\\阴阳师_down.mp4");
        // 取出所有分块文件
        File[] files = chunkFolder.listFiles();
        // 将数组转为list
        List<File> fileList = Arrays.asList(files);
        fileList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
        // 向合并文件写流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        // 缓存区
        byte[] bytes = new byte[1024];
        // 遍历分块文件，合并文件
        for (File file : fileList) {
            // 读分块流
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
            }
            raf_r.close();
        }
        raf_rw.close();
        // 合并完成，比较源文件
        String sourceMd5 = DigestUtils.md5Hex(new FileInputStream(sourceFile));
        String mergeMd5 = DigestUtils.md5Hex(new FileInputStream(mergeFile));
        if (sourceMd5.equals(mergeMd5)) {
            System.out.println("相同");
        } else {
            System.out.println("不相同");
        }
    }
}
