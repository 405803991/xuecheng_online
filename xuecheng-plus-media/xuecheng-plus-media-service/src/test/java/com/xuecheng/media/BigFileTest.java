package com.xuecheng.media;

import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BigFileTest {

    //分块测试
    @Test
    public void testChunk() throws IOException {
        // 源文件
        File sourceFile = new File("C:\\Users\\SheYue\\Videos\\2023-04-06 13-30-11.mp4");
        // 分块后存储路径
        String chunkFilePath = "C:\\Users\\SheYue\\Videos\\chunk\\";
        // 分块大小
        int chunkSize = 1024*1024*1;
        // 分块文件个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        // 使用流读取文件然后写
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");

        byte[] bytes = new byte[1024];
        for(int i = 0; i < chunkNum; i ++){
            File chunkFile = new File(chunkFilePath + i);
            RandomAccessFile raf_rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while((len=raf_r.read(bytes))!=-1){
                raf_rw.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) break;
            }
            raf_rw.close();
        }
        raf_r.close();
    }

    //合并测试
    @Test
    public void testMerge() throws IOException {
        File chunkFolder = new File("C:\\Users\\SheYue\\Videos\\chunk");
        File mergeFile = new File("C:\\Users\\SheYue\\Videos\\video-2.mp4");
        File sourceFile = new File("C:\\Users\\SheYue\\Videos\\2023-04-06 13-30-11.mp4");

        File[] files = chunkFolder.listFiles();

        List<File> fileList = Arrays.asList(files);
        // 对分块文件排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        byte[] bytes = new byte[1024];
        // 向合并文件写入流
        for (File chunkfile : fileList) {
            RandomAccessFile raf_r = new RandomAccessFile(chunkfile, "r");
            int len = -1;
            while((len=raf_r.read(bytes))!=-1){
                raf_rw.write(bytes, 0, len);
            }
            raf_r.close();
        }
        raf_rw.close();

        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        String s1 = DigestUtils.md5Hex(mergeFileStream);
        String s2 = DigestUtils.md5Hex(sourceFileStream);

        if (s1.equals(s2)) {
            System.out.println("合并成功");
        }


    }

}
