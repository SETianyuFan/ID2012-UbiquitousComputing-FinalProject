package com.zhangxin.jarvis;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class pictureTest {
    public static void main(String[] args) throws IOException {
        Webcam webcam = Webcam.getDefault();
        Dimension[] nonStandardResolutions = new Dimension[] {
                WebcamResolution.PAL.getSize(),
                WebcamResolution.HD.getSize(),
                new Dimension(640, 480),
                new Dimension(1280, 720),
                new Dimension(1280, 960),
                new Dimension(1920, 1080),
        };
        int camera_w = 1280;
        int camera_h = 720;
        webcam.setCustomViewSizes(nonStandardResolutions);
        webcam.setViewSize(new Dimension(camera_w, camera_h));
        webcam.open();
        ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));
    }
}
