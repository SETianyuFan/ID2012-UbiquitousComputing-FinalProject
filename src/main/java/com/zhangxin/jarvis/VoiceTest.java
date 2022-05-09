package com.zhangxin.jarvis;

import com.iflytek.cloud.speech.*;
import com.zhangxin.jarvis.util.DebugLog;
import com.zhangxin.jarvis.util.JsonParser;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class VoiceTest {

    PictureTest pt =new PictureTest();
    private SpeechRecognizer speechRecognizer;

    private String resultText = "";

    private int time = 0;

    public String getResultText(){return this.resultText;}

    public VoiceTest(){
        this.speechRecognizer = SpeechRecognizer.createRecognizer();
    }

    public void speech(){
        if(!speechRecognizer.isListening())
            speechRecognizer.startListening(listener);
        else
            speechRecognizer.stopListening();
    }


    //编写监听器,创建匿名内部类
    private RecognizerListener listener = new RecognizerListener() {

        /**
         * when voice changing this function will show your voice value
         * 当在说话的过程中音量发生变化时会多次调用此函数，显示音量值
         */
        @Override
        public void onVolumeChanged(int arg0) {
            DebugLog.Log("onVolumeChanged enter      "+arg0);

        }

        /**
         * get the listen result and send to iflytek and get back Json result
         * 获取听写结果. 获取RecognizerResult类型的识别结果
         */
        @Override
        public void onResult(RecognizerResult result, boolean flag) {
            DebugLog.Log("onResult enter");
            //这个result就是远程解析的结果
            String strResult = null;
            //这里的捕获异常是我自己修改了JsonParser的这个静态方法，因为他在里面捕获了异常，所以我修改了，我在那里面又抛了一个异常
            //因为这个函数解析result的时候，如果不说话就会打印异常信息，所以受不了，我就把他修改了
            try{
                strResult = JsonParser.parseIatResult(result.getResultString());
            }catch(Exception e){
                strResult = "";
            }
            resultText = resultText + strResult;
            //第二次调用时结果为结尾标点符号，所以对两次结果进行了拼接
            time++;
            System.out.println(resultText);
            if(time==2){
                System.out.println(resultText);
                String str = resultText;
                //String str = "打开微信给张三发消息说明天几点上班";
                String app=str.substring(str.indexOf("打开")+2, str.indexOf("给"));
                String name = str.substring(str.indexOf("给")+1,str.indexOf("发"));
                System.out.println(name);
                String content = str.substring(str.indexOf("说")+1);
                System.out.println(content);
                RobotUtil robot = new RobotUtil();
                if(app.equals("微信")){
                    if (robot.topWeChat()) {
                        // 初始化
                        robot.init();
                        // 指定需要发送消息的联系人/群组
                        robot.queryItemForSendMessage(name); // 微信昵称/群名
                        // 发送字符串消息
                        robot.sendStrMessage(content);
                        // }
                        // 发送图片消息
                        //sendImgMessage("exceltest.jpg"); // 表情包的路径
                    }
                }
            }

            // for shut down the computer
            if(strResult.contains("关机")||strResult.contains("关闭电脑")){
                System.out.println("正在关机...");
                try {
                    Runtime.getRuntime().exec("shutdown /s /t " + 1);  //关机时间可以自动设置
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(strResult.contains("重启")||strResult.contains("重新启动")){
                System.out.println("正在重启...");
                try {
                    Runtime.getRuntime().exec("shutdown -r ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(strResult.toLowerCase().contains("music")){
                System.out.println("playing music...");

                try {
                    switch (pt.getEmotion()){
                        case "happiness":
                            browse("https://www.youtube.com/watch?v=ZbZSe6N_BXs&ab_channel=PharrellWilliamsVEVO");
                        case "sadness":
                            browse("https://www.youtube.com/watch?v=Jllu94-8PxI&ab_channel=brokxn");
                        case "disgust":
                            browse("https://www.youtube.com/watch?v=3clqk2U3T9Y&ab_channel=SandTagious");
                        case "contempt":
                            browse("https://www.youtube.com/watch?v=r7qovpFAGrQ&ab_channel=LilNasXVEVO");
                        case "fear":
                            browse("https://www.youtube.com/watch?v=m9We2XsVZfc&ab_channel=PrestigeGhost");
                        case "surprised":
                            browse("https://www.youtube.com/watch?v=__LU8E6dUsI&list=RDMM&start_radio=1&rv=Y-lI_tgQMMk&ab_channel=AkademiaFilmuiTelewizjiO");
                        case "anger":
                            browse("https://www.youtube.com/watch?v=L3wKzyIN1yk&list=RDMM&index=5&ab_channel=RagnBoneManVEVO");
                        case "neutral":
                            browse("https://www.youtube.com/watch?v=xFrGuyw1V8s&ab_channel=AbbaVEVO");
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pt.stopSampling();

                try {
                    Runtime.getRuntime().exec("shutdown -r ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


    }

    private void browse(String url) throws URISyntaxException, IOException {
        Desktop desktop =Desktop.getDesktop();
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)){
            URI uri = new URI(url);
            desktop.browse(uri);
        }
    }

    /*
     * 事件 扩展用接口，由具体业务进行约定
         * for some additional event
         * @see com.iflytek.cloud.speech.RecognizerListener#onEvent(int, int, int, java.lang.String)
         */
        @Override
        public void onEvent(int arg0, int arg1, int arg2, String arg3) {

        }

        @Override
        public void onError(SpeechError arg0) {
            DebugLog.Log(arg0.toString());
            DebugLog.Log("onError enter");
        }

        @Override
        public void onEndOfSpeech() {
            DebugLog.Log("onEndOfSpeech enter");
        }

        /*
         * 结束听写，恢复初始状态
         * finish listen and go back to init
         * @see com.iflytek.cloud.speech.RecognizerListener#onBeginOfSpeech()
         */
        @Override
        public void onBeginOfSpeech() {
            DebugLog.Log("onBeginOfSpeech enter");
        }
    };


    public static void main(String[] args) {
        //这句是必须的，注册的时候必须建一个应用，会分配一个appid，填在这里
        //set up a speechUtility witch use an id that give on website
        SpeechUtility.createUtility("appid=7f5bb88a");
        //When the class is initialized, these functions are called. Otherwise, the anonymous inner class can't listen. Listener methods may or may not be overridden
        //初始化这个类的时候，这些函数就调用了，如果不初始化，那个匿名内部类就没办法监听，监听器的方法有的需要重写，有的可以不重写


        VoiceTest speechTest = new VoiceTest();
        speechTest.pt.start();
        //When startListening is called, the new API automatically determines that the volume is silent and stops automatically, so you do not need to care about the stopping S
        //新版api当startListening被调用之后，自动判断音量为静音来自动停止，所以不需要关心停止的s
        java.util.Scanner wait = new Scanner(System.in);
        String input =wait.nextLine();
        speechTest.speech();
    }
}