package jp.ac.hosei.media.lectcast.web.service;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mediaconvert.AWSMediaConvert;
import com.amazonaws.services.mediaconvert.AWSMediaConvertAsyncClientBuilder;
import com.amazonaws.services.mediaconvert.model.AacAudioDescriptionBroadcasterMix;
import com.amazonaws.services.mediaconvert.model.AacCodecProfile;
import com.amazonaws.services.mediaconvert.model.AacCodingMode;
import com.amazonaws.services.mediaconvert.model.AacRateControlMode;
import com.amazonaws.services.mediaconvert.model.AacRawFormat;
import com.amazonaws.services.mediaconvert.model.AacSettings;
import com.amazonaws.services.mediaconvert.model.AacSpecification;
import com.amazonaws.services.mediaconvert.model.AudioCodec;
import com.amazonaws.services.mediaconvert.model.AudioCodecSettings;
import com.amazonaws.services.mediaconvert.model.AudioDefaultSelection;
import com.amazonaws.services.mediaconvert.model.AudioDescription;
import com.amazonaws.services.mediaconvert.model.AudioSelector;
import com.amazonaws.services.mediaconvert.model.AudioTypeControl;
import com.amazonaws.services.mediaconvert.model.ContainerSettings;
import com.amazonaws.services.mediaconvert.model.ContainerType;
import com.amazonaws.services.mediaconvert.model.CreateJobRequest;
import com.amazonaws.services.mediaconvert.model.CreateJobResult;
import com.amazonaws.services.mediaconvert.model.HlsAudioOnlyContainer;
import com.amazonaws.services.mediaconvert.model.HlsGroupSettings;
import com.amazonaws.services.mediaconvert.model.HlsIFrameOnlyManifest;
import com.amazonaws.services.mediaconvert.model.HlsSettings;
import com.amazonaws.services.mediaconvert.model.Input;
import com.amazonaws.services.mediaconvert.model.JobSettings;
import com.amazonaws.services.mediaconvert.model.M3u8Settings;
import com.amazonaws.services.mediaconvert.model.Output;
import com.amazonaws.services.mediaconvert.model.OutputGroup;
import com.amazonaws.services.mediaconvert.model.OutputGroupSettings;
import com.amazonaws.services.mediaconvert.model.OutputGroupType;
import com.amazonaws.services.mediaconvert.model.OutputSettings;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AmazonMediaConvertService {

  @Value("${aws.region}")
  private String region;

  @Value("${aws.bucketName}")
  private String bucketName;

  @Value("${aws.mediaConvert.serviceEndpoint}")
  private String serviceEndpoint;

  @Value("${aws.mediaConvert.queue}")
  private String queue;

  @Value("${aws.iam.role}")
  private String role;

  public CreateJobResult create(final String key) {
    final String audioSourceName = "Audio Selector 1";

    final JobSettings jobSettings = new JobSettings()
        .withInputs(
            new Input()
                .withAudioSelectors(
                    new HashMap<String, AudioSelector>() {
                      {
                        put(
                            audioSourceName,
                            new AudioSelector()
                                .withOffset(0)
                                .withDefaultSelection(AudioDefaultSelection.DEFAULT)
                                .withProgramSelection(1)
                        );
                      }
                    }
                )
                .withFileInput("s3://" + bucketName + "/files/audio/" + key)
        )
        .withOutputGroups(
            new OutputGroup()
                .withName("Apple HLS")
                .withOutputs(
                    new Output()
                        .withContainerSettings(
                            new ContainerSettings()
                                .withContainer(ContainerType.M3U8)
                                .withM3u8Settings(
                                    new M3u8Settings()
                                )
                        )
                        .withAudioDescriptions(
                            new AudioDescription()
                                .withAudioTypeControl(AudioTypeControl.FOLLOW_INPUT)
                                .withAudioSourceName(audioSourceName)
                                .withCodecSettings(
                                    new AudioCodecSettings()
                                        .withCodec(AudioCodec.AAC)
                                        .withAacSettings(
                                            new AacSettings()
                                                .withAudioDescriptionBroadcasterMix(
                                                    AacAudioDescriptionBroadcasterMix.NORMAL)
                                                .withBitrate(96000)
                                                .withRateControlMode(AacRateControlMode.CBR)
                                                .withCodecProfile(AacCodecProfile.LC)
                                                .withCodingMode(AacCodingMode.CODING_MODE_2_0)
                                                .withRawFormat(AacRawFormat.NONE)
                                                .withSampleRate(48000)
                                                .withSpecification(AacSpecification.MPEG4)
                                        )
                                )
                        )
                        .withOutputSettings(
                            new OutputSettings()
                                .withHlsSettings(
                                    new HlsSettings()
                                        .withAudioGroupId("program_audio")
                                        .withAudioOnlyContainer(HlsAudioOnlyContainer.AUTOMATIC)
                                        .withIFrameOnlyManifest(HlsIFrameOnlyManifest.EXCLUDE)
                                )
                        )
                        .withNameModifier("_audio")
                ).withOutputGroupSettings(
                new OutputGroupSettings()
                    .withType(OutputGroupType.HLS_GROUP_SETTINGS)
                    .withHlsGroupSettings(
                        new HlsGroupSettings()
                            .withSegmentLength(10)
                            .withMinSegmentLength(0)
                            .withDestination("s3://" + bucketName + "/files/hls/" + key + "/")
                    )
            )
        );

    final CreateJobRequest jobParam = new CreateJobRequest()
        .withSettings(jobSettings)
        .withQueue(queue)
        .withRole(role);
    return getMediaConvert().createJob(jobParam);
  }

  private AWSMediaConvert getMediaConvert() {
    return AWSMediaConvertAsyncClientBuilder.standard()
        .withEndpointConfiguration(new EndpointConfiguration(serviceEndpoint, region))
        .build();
  }

}
