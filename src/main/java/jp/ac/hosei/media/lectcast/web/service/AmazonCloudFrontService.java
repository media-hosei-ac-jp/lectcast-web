package jp.ac.hosei.media.lectcast.web.service;

import com.amazonaws.services.cloudfront.CloudFrontCookieSigner;
import com.amazonaws.services.cloudfront.CloudFrontCookieSigner.CookiesForCustomPolicy;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.Cookie;
import jp.ac.hosei.media.lectcast.web.data.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AmazonCloudFrontService {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(AmazonCloudFrontService.class);

  private static final int EXPIRE = 24 * 60 * 60; // 1 day

  @Value("${aws.cloudFront.distributionDomain}")
  private String distributionDomain;

  @Value("${aws.cloudFront.keyPairId}")
  private String keyPairId;

  @Value("${lectcast.domain.app}")
  private String lectcastDomain;

  @Value("${lectcast.domain.cache}")
  private String lectcastCacheDomain;

  @Value("${secure.cookie}")
  private boolean isSecure;

  public String getStreamingFileUrlFromItem(final Item item) throws URISyntaxException {
    final String path = "/" + item.getChannel().getId() + "/" + item.getS3Key() + ".m3u8";
    final URI uri = new URI("https", lectcastCacheDomain, path, null);
    return uri.toString();
  }

  public String getSignedUrl(final String key)
      throws InvalidKeySpecException, IOException {
    final File privateKeyFile = new File(
        System.getProperty("user.home") + "/certs/pk-" + keyPairId + ".pem");

    // Generate a signed URL
    return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
        SignerUtils.Protocol.https,
        distributionDomain,
        privateKeyFile,
        key,
        keyPairId,
        getExpireTime());
  }

  public List<Cookie> getSignedCookies(final String resourcePath)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    final File privateKeyFile = new File(
        System.getProperty("user.home") + "/certs/pk-" + keyPairId + ".pem");
    logger.info(resourcePath);

    final CookiesForCustomPolicy cookies = CloudFrontCookieSigner.getCookiesForCustomPolicy(
        SignerUtils.Protocol.https,
        distributionDomain,
        privateKeyFile,
        resourcePath + "/*",
        keyPairId,
        getExpireTime(),
        null,
        "0.0.0.0/0");

    final List<Cookie> cookieList = new ArrayList<>();
    final Cookie cookiePolicy = new Cookie(cookies.getPolicy().getKey(),
        cookies.getPolicy().getValue());
    cookiePolicy.setDomain(lectcastDomain);
    cookiePolicy.setHttpOnly(false);
    cookiePolicy.setSecure(isSecure);
    cookieList.add(cookiePolicy);

    final Cookie cookieSignature = new Cookie(cookies.getSignature().getKey(),
        cookies.getSignature().getValue());
    cookieSignature.setDomain(lectcastDomain);
    cookieSignature.setHttpOnly(false);
    cookieSignature.setSecure(isSecure);
    cookieList.add(cookieSignature);

    final Cookie cookieKeyPairId = new Cookie(cookies.getKeyPairId().getKey(),
        cookies.getKeyPairId().getValue());
    cookieKeyPairId.setDomain(lectcastDomain);
    cookieKeyPairId.setHttpOnly(false);
    cookieKeyPairId.setSecure(isSecure);
    cookieList.add(cookieKeyPairId);

    return cookieList;
  }

  private Date getExpireTime() {
    final LocalDateTime expiresOn = LocalDateTime.now().plusSeconds(EXPIRE);
    final ZonedDateTime zonedExpiresOn = ZonedDateTime.of(expiresOn, ZoneId.systemDefault());
    return Date.from(zonedExpiresOn.toInstant());
  }

}
