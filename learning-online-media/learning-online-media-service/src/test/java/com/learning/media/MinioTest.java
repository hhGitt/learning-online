package com.learning.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author HH
 * @version 1.0
 * @description 测试minio的sdk
 * @date 2023/7/4 09:48
 **/
public class MinioTest {
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.181.135:9000")
            .credentials("minio_root", "minio_root")
            .build();

    /**
     * 上传文件
     *
     * @throws Exception 异常
     */
    @Test
    public void test_upload() throws Exception {
        // 通过拓展名得到媒体资源类型 mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        // 上传文件参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket("testbucket")
                .filename("D:\\下载\\Downloads\\高木同学.mp4") // 本地文件路径
                .object("test/01/高木同学.mp4") // 对象名
                .contentType(mimeType) // 设置媒体文件类型
                .build();
        // 上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }

    /**
     * 删除文件
     *
     * @throws Exception 异常
     */
    @Test
    public void test_delete() throws Exception {
        // 删除文件参数信息
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket("testbucket").object("高木同学.mp4").build();
        // 删除文件
        minioClient.removeObject(removeObjectArgs);
    }

    /**
     * 查询文件 从minio下载
     *
     * @throws Exception 异常
     */
    @Test
    public void test_get() throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("test/01/高木同学.mp4").build();
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        // 指定输出流
        FileOutputStream outputStream = new FileOutputStream("D:\\Test\\learning-online\\down\\1.mp4");
        IOUtils.copy(inputStream, outputStream);

        // 校验文件的完整性对文件的内容进行md5
        String source_md5 = DigestUtils.md5Hex(Files.newInputStream(new File("D:\\下载\\Downloads\\高木同学.mp4").toPath())); // minio中文件md5
        String local_md5 = DigestUtils.md5Hex(Files.newInputStream(new File("D:\\Test\\learning-online\\down\\1.mp4").toPath()));  // 本地
        if (source_md5.equals(local_md5)) {
            System.out.println("下载成功");
        } else {
            System.out.println("下载失败");
        }
    }

    // 将分块文件上传minio
    @Test
    public void uploadChunk() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (int i = 0; i < 5; i++) {
            // 上传文件参数信息
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket("testbucket")
                    .filename("D:\\Test\\learning-online\\upload\\chunk\\" + i) // 本地文件路径
                    .object("chunk/" + i) // 对象名
                    .build();
            // 上传文件
            minioClient.uploadObject(uploadObjectArgs);
        }
    }

    // 调用minio接口合并分块
    @Test
    public void testMerge() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        List<ComposeSource> sources = new ArrayList<>();
//        for (int i = 0; i < 25; i++) {
//            // 指定分块文件信息
//            ComposeSource composeSource = ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build();
//            sources.add(composeSource);
//        }
        // Stream
        List<ComposeSource> sources = Stream.iterate(0, i -> i++).limit(5).map(i -> ComposeSource.builder().bucket("testbucket").object("chunk/" + i).build()).collect(Collectors.toList());

        // 指定合并后的object信息
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources)
                .build();
        // 合并文件,size 1048576 must be greater than 5242880,minio最小块默认5M
        minioClient.composeObject(composeObjectArgs);
    }

    // 批量清理分块文件

}