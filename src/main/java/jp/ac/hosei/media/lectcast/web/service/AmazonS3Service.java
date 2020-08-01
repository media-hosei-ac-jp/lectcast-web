package jp.ac.hosei.media.lectcast.web.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AmazonS3Service {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(AmazonS3Service.class);

  private static final String COMMON_PREFIX = "files";

  @Value("${aws.accessKeyId}")
  private String accessKeyId;

  @Value("${aws.secretAccessKey}")
  private String secretAccessKey;

  @Value("${aws.region}")
  private String region;

  @Value("${aws.bucketName}")
  private String bucketName;

  public String generateKey() {
    return UUID.randomUUID().toString();
  }

  public PutObjectResult putObject(final File file, final String key, final String fileName,
      final String prefix, final String contentType,
      final long contentLength, final int maxAge) {
    try {
      final InputStream inputStream = new FileInputStream(file);
      final ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentType(contentType);
      objectMetadata.setContentLength(contentLength);
      objectMetadata.setCacheControl("public, max-age=" + maxAge);
      final PutObjectResult result = getAmazonS3()
          .putObject(bucketName,
              String.join("/", new String[]{COMMON_PREFIX, prefix, key, fileName}),
              inputStream, objectMetadata);
      inputStream.close();    // Close the stream
      return result;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ResponseEntity<InputStreamResource> getObject(final String key, final String prefix) {
    try {
      final S3Object s3Object = getAmazonS3()
          .getObject(bucketName, String.join("/", new String[]{COMMON_PREFIX, prefix, key}));
      final BufferedInputStream bufferedInputStream = new BufferedInputStream(
          s3Object.getObjectContent());

      final ResponseEntity<InputStreamResource> entity = ResponseEntity.ok()
          .contentLength(s3Object.getObjectMetadata().getContentLength())
          .contentType(
              MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()))
          .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
          .body(new InputStreamResource(bufferedInputStream));
      s3Object.close();   // Close the object
      return entity;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private AmazonS3 getAmazonS3() {
    return AmazonS3ClientBuilder.standard()
        .withCredentials(
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
        .withRegion(region)
        .build();
  }

}
