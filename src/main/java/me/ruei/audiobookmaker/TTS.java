package me.ruei.audiobookmaker;

import cn.xfyun.api.TtsClient;
import cn.xfyun.model.response.TtsResponse;
import cn.xfyun.service.tts.AbstractTtsWebSocketListener;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Response;
import okhttp3.WebSocket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TTS {

    public void execute(Params params) {
        checkOutput(params);
        var txtFiles = checkInputAndGetFiles(params);
        var configStr = readConfigStr(params);
        var client = buildXunfeiSDKClient(configStr);

        for (var txtFile : txtFiles) {
            if (!txtFile.getName().endsWith(".txt")) {
                continue;
            }

            try {
                var content = getOriginText(txtFile);
                var mp3File = new File(params.output + File.separator + txtFile.getName() + ".mp3");
                fetchTTS(client, content, txtFile, mp3File);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(txtFile.getName() + "合成失败");
            }
        }

    }

    private void fetchTTS(TtsClient client, String content, File originFile, File mp3File) {
        try {
            client.send(content, new AbstractTtsWebSocketListener(mp3File) {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println(originFile.getName() + "合成成功");
                }

                @Override
                public void onFail(WebSocket webSocket, Throwable t, Response response) {
                    System.out.println(originFile.getName() + "合成失败: " + t.toString());
                    System.out.println(originFile.getName() + "合成失败: " + response.toString());
                }

                @Override
                public void onBusinessFail(WebSocket webSocket, TtsResponse response) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(originFile.getName() + "合成失败");
        }
    }

    private String getOriginText(File originFile) {
        try {
            return Files.readString(Paths.get(originFile.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取原始文件" + originFile.getName() + "内容失败");
        }
    }

    private TtsClient buildXunfeiSDKClient(String configStr) {
        var config = JSONObject.parseObject(configStr);
        var appId = config.get("appId").toString();
        var apiKey = config.get("apiKey").toString();
        var apiSecret = config.get("apiSecret").toString();

        try {
            return new TtsClient.Builder()
                    .signature(appId, apiKey, apiSecret)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("TTS Client初始化失败，请检查配置文件是否有误");
        }
    }

    private File[] checkInputAndGetFiles(Params params) {
        var input = new File(params.input);
        var files = input.listFiles();
        if (files == null) {
            throw new RuntimeException("输入文件夹为空");
        }

        return files;
    }

    private void checkOutput(Params params) {
        var output = new File(params.output);
        if (!output.isDirectory()) {
            throw new RuntimeException("输出必须是文件夹");
        }
    }

    private String readConfigStr(Params params) {
        try {
            return Files.readString(Paths.get(params.config));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取配置文件失败");
        }
    }
}
