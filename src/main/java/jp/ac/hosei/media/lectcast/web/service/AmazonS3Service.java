package jp.ac.hosei.media.lectcast.web.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AmazonS3Service {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(AmazonS3Service.class);

  private static final String KEY_ORIGINAL_PREFIX = "original";

  private static final String KEY_STREAMING_PREFIX = "streaming";

  private static final String KEY_CONVERTED_PREFIX = "converted";

  private static final String COMMON_PREFIX = "files";

  @Value("${aws.accessKeyId}")
  private String accessKeyId;

  @Value("${aws.secretAccessKey}")
  private String secretAccessKey;

  @Value("${aws.region}")
  private String region;

  @Value("${aws.bucketName}")
  private String bucketName;

  @Autowired
  ResourceLoader resourceLoader;

  public String generateKey() {
    return UUID.randomUUID().toString();
  }

  public PutObjectResult putObject(final File file, final String key, final String contentType,
      final long contentLength, final int maxAge) {
    try {
      final InputStream inputStream = new FileInputStream(file);
      final ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentType(contentType);
      objectMetadata.setContentLength(contentLength);
      objectMetadata.setCacheControl("public, max-age=" + maxAge);
      final PutObjectResult result = getAmazonS3()
          .putObject(bucketName, key, inputStream, objectMetadata);
      inputStream.close();    // Close the stream
      return result;
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  // @Async annotation ensures that the method is executed in a different background thread
  // but not consume the main thread.
  @Async
  public byte[] downloadFile(final String keyName) {
    final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
        .withCredentials(
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
        .withRegion(region)
        .build();

    byte[] content = null;
    logger.info("Downloading an object with key= " + keyName);
    final S3Object s3Object = amazonS3.getObject(bucketName, keyName);
    final S3ObjectInputStream stream = s3Object.getObjectContent();
    try {
      content = IOUtils.toByteArray(stream);
      logger.info("File downloaded successfully.");
      s3Object.close();
    } catch(final IOException ex) {
      logger.info("IO Error Message= " + ex.getMessage());
    }
    return content;
  }

  public ResponseEntity<InputStreamResource> getObject(final String key) {
    try {
      final S3Object s3Object = getAmazonS3()
          .getObject(bucketName, key);
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

  public String getOriginalKey(final String channelId, final String itemS3Key, final String fileName, final String extension) {
    return String.join("/", new String[]{KEY_ORIGINAL_PREFIX, channelId, itemS3Key, fileName}) + "." + extension;
  }

  public String getStreamingKey(final String channelId, final String itemS3Key) {
    return String.join("/", new String[]{KEY_STREAMING_PREFIX, channelId, itemS3Key}) + "_hls.m3u8";
  }

  public String getConvertedKey(final String channelId, final String itemS3Key) {
    return String.join("/", new String[]{KEY_CONVERTED_PREFIX, channelId, itemS3Key}) + ".mp3";
  }

  private AmazonS3 getAmazonS3() {
    return AmazonS3ClientBuilder.standard()
        .withCredentials(
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
        .withRegion(region)
        .build();
  }

}
