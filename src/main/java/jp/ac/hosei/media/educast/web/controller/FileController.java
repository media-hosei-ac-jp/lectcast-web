package jp.ac.hosei.media.educast.web.controller;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import jp.ac.hosei.media.educast.web.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Controller
@RequestMapping(path = "/files")
public class FileController {

    private static final int EXPIRE = 60 * 60 * 1000; // 1 hour

    @Value("${aws.cloudFront.distributionDomain}")
    private String distributionDomain;

    @Value("${aws.cloudFront.keyPairId}")
    private String keyPairId;

    @Autowired
    AmazonS3Service amazonS3Service;

    @GetMapping
    public String index() {
        return "files/index";
    }

    @GetMapping(path = "new")
    public String uploadForm() {
        return "files/uploadFile";
    }

    @PostMapping
    public String handleFileUpload(final MultipartFile multipartFile) {
        File file = null;
        try {
            // Create a temporary file
            final Path tmpPath = Files.createTempFile(Paths.get("/tmp"), "educast_", ".tmp");
            file = tmpPath.toFile();

            final byte[] bytes = multipartFile.getBytes();
            final BufferedOutputStream uploadFileStream = new BufferedOutputStream(new FileOutputStream(file));
            uploadFileStream.write(bytes);
            uploadFileStream.close();

            // Put an object
            amazonS3Service.putObject(file, "files/", multipartFile.getContentType(), multipartFile.getSize(), 600);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }
        }
        return "redirect:/";
    }

    @GetMapping("url/{key}")
    public String signedURL(@PathVariable("key") final String key, final Model model)
            throws InvalidKeySpecException, IOException {
        // load key of CloudFront
        final File privateKeyFile = new File(System.getProperty("user.home") + "/certs/pk-" + keyPairId + ".pem");

        // Generate a signed URL
        model.addAttribute("signedURL", CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                SignerUtils.Protocol.https,
                distributionDomain,
                privateKeyFile,
                key,
                keyPairId,
                getExpireTime()));
        return "files/url";
    }

    @GetMapping("download/{key}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> serveFile(@PathVariable("key") final String key) {
        return amazonS3Service.getObject("files/" + key);
    }

    private Date getExpireTime() {
        final Date time = new Date();
        final long end = time.getTime() + EXPIRE;
        time.setTime(end);
        return time;
    }

}
