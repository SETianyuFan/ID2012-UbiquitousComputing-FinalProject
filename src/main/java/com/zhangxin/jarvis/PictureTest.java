package com.zhangxin.jarvis;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.microsoft.azure.cognitiveservices.vision.faceapi.models.DetectedFace;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class PictureTest extends Thread {

    @Override
    public void run() {
        try {
            this.startSmpling();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> emotions= new ArrayList<String>(){
        {
            add("neutral");
            add("anger");
            add("disgust");
            add("contempt");
            add("fear");
            add("sadness");
            add("surprise");
            add("happiness");
        }
    };
    public boolean activeSampling = true;
    public int samplingTime= 5;
    public Webcam webcam ;
    public HashMap<String, Double> emotionsAverage= new HashMap<>();
    public void initCamera(){
        webcam = Webcam.getDefault();
        Dimension[] nonStandardResolutions = new Dimension[]{
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
        for (String emotion:
             emotions) {

            emotionsAverage.put(emotion, 0.0);
        }

    }
    public void closeCamera(){
        webcam.close();
    }
    public void startSmpling() throws IOException, InterruptedException {
        initCamera();
        activeSampling = true;
        int newWeight=1;
        int oldWeight=4;
        while(activeSampling){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(webcam.getImage(), "PNG", baos);
            byte[] bytes = baos.toByteArray();
            EmotionDetection emotionDetection = new EmotionDetection();
            for (DetectedFace face : emotionDetection.detectFaces(bytes)) {
                emotionsAverage.put("neutral",(oldWeight*emotionsAverage.get("neutral")+newWeight*face.faceAttributes().emotion().neutral())/(10*(oldWeight+newWeight)));
                emotionsAverage.put("anger", (oldWeight*emotionsAverage.get("anger")+newWeight*face.faceAttributes().emotion().anger())/(oldWeight+newWeight));
                emotionsAverage.put("disgust", (oldWeight*emotionsAverage.get("disgust")+newWeight*face.faceAttributes().emotion().disgust())/(oldWeight+newWeight));
                emotionsAverage.put("contempt", (oldWeight*emotionsAverage.get("contempt")+newWeight*face.faceAttributes().emotion().contempt())/(oldWeight+newWeight));
                emotionsAverage.put("fear", (oldWeight*emotionsAverage.get("fear")+newWeight*face.faceAttributes().emotion().fear())/(oldWeight+newWeight));
                emotionsAverage.put("sadness", (oldWeight*emotionsAverage.get("sadness")+newWeight*face.faceAttributes().emotion().sadness())/(oldWeight+newWeight));
                emotionsAverage.put("surprise", (oldWeight*emotionsAverage.get("surprise")+newWeight*face.faceAttributes().emotion().surprise())/(oldWeight+newWeight));
                emotionsAverage.put("happiness", (oldWeight*emotionsAverage.get("happiness")+newWeight*face.faceAttributes().emotion().happiness())/(oldWeight+newWeight));
            }
            sleep(5);

        }
    }
    public void stopSampling(){
        activeSampling=false;
        closeCamera();
    }
    public String getEmotion(){
        Double maxvalue = 0.0;
        String dominatingEmotion = "";
        for (String emotion: emotions
             ) {
            if(maxvalue< emotionsAverage.get(emotion)){
                maxvalue=emotionsAverage.get(emotion);
                dominatingEmotion = emotion;
            }

        }
        return dominatingEmotion;
    }
    public static void main(String[] args) throws IOException {
        //ImageIO.write(webcam.getImage(), "PNG", new File("hello-world.png"));
    }
}
