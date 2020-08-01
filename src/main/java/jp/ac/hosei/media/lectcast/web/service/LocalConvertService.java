package jp.ac.hosei.media.lectcast.web.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LocalConvertService {

  @Value("${ffmpeg.path.ffmpeg}")
  private String ffmpegPath;

  @Value("${ffmpeg.path.ffprobe}")
  private String ffprobePath;

  public FFmpegFormat getFormat(final String filePathString) throws IOException {
    final FFprobe ffprobe = new FFprobe(ffprobePath);

    return ffprobe.probe(filePathString).getFormat();
  }

  @Async
  public CompletableFuture<String> convert(final String filePathString) throws IOException {
    final String convertedFilePathString = filePathString + ".converted";

    final FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
    final FFprobe ffprobe = new FFprobe(ffprobePath);

    final FFmpegBuilder builder = new FFmpegBuilder()
        .setInput(filePathString)
        .addOutput(convertedFilePathString)
        .setFormat("mp3")
        .done();
    FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
    executor.createJob(builder).run();

    return CompletableFuture.completedFuture(convertedFilePathString);
  }

}
