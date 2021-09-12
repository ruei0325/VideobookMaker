package me.ruei.audiobookmaker;

import com.beust.jcommander.JCommander;

public class Application {

    public static void main(String[] args) {
        Params params = new Params();
        JCommander.newBuilder()
                .addObject(params)
                .build()
                .parse(args);

        if (params.help) {
            JCommander.newBuilder().addObject(params).build().usage();
            return;
        }


        if ("transcode".equals(params.action)) {
            var t = new Transcode();
            t.execute(params);
        } else if ("ocr".equals(params.action)) {
            var o = new OCR();
            o.execute(params);
        } else if ("tts".equals(params.action)) {
            var t = new TTS();
            t.execute(params);
        }
    }
}
