package jp.ac.hosei.media.educast.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class AmazonS3Service {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.bucketName}")
    private String bucketName;

    public String putObject(final File file, final String prefix, final String contentType,
                            final long contentLength, final int maxAge) {
        try {
            final InputStream inputStream = new FileInputStream(file);
            final String key = prefix + UUID.randomUUID().toString();
            final ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setCacheControl("public, max-age=" + maxAge);
            getAmazonS3().putObject(bucketName, key, inputStream, objectMetadata);
            return key;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<InputStreamResource> getObject(final String key) {
        final S3Object s3Object = getAmazonS3().getObject(bucketName, key);
        return ResponseEntity.ok()
                .contentLength(s3Object.getObjectMetadata().getContentLength())
                .contentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()))
                .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }

    private AmazonS3 getAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
                .withRegion(region)
                .build();
    }

}
