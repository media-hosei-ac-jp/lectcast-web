package jp.ac.hosei.media.educast.web.service;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Service
public class AmazonCloudFrontService {

    private static final int EXPIRE = 5 * 60 * 1000; // 5 minute

    @Value("${aws.cloudFront.distributionDomain}")
    private String distributionDomain;

    @Value("${aws.cloudFront.keyPairId}")
    private String keyPairId;

    public String getSignedUrl(final String key) throws InvalidKeySpecException, IOException {
        final File privateKeyFile = new File(System.getProperty("user.home") + "/certs/pk-" + keyPairId + ".pem");

        // Generate a signed URL
        return CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                SignerUtils.Protocol.https,
                distributionDomain,
                privateKeyFile,
                key,
                keyPairId,
                getExpireTime());
    }

    private Date getExpireTime() {
        final Date time = new Date();
        final long end = time.getTime() + EXPIRE;
        time.setTime(end);
        return time;
    }

}
