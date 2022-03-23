package com.jemmy.framework.component.resources.video;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoUtils {

    /**
     * 获取视频封面
     * @param video 视频文件
     * @return 封面文件
     */
    public static File getCover(File video) throws IOException {
        FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(video);

        ff.start();

        int ftp = ff.getLengthInFrames();
        Frame frame;
        File cover = null;
        int flag = 0;

        while (flag <= ftp) {

            frame = ff.grabImage();

				/*
				对视频的第五帧进行处理
				 */
            if (frame != null && flag == 5) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage srcImage = converter.getBufferedImage(frame);

                int width = srcImage.getWidth();
                int height = srcImage.getHeight();

                BufferedImage thumbnailImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                thumbnailImage.getGraphics().drawImage(srcImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

                cover = File.createTempFile("video_cover-", ".png");
                ImageIO.write(thumbnailImage, "png", cover);

                break;
            }

            flag++;
        }

        ff.stop();

        return cover;
    }
}
