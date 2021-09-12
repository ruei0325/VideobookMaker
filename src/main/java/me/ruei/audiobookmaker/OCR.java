package me.ruei.audiobookmaker;

import cn.xfyun.api.GeneralWordsClient;
import cn.xfyun.config.OcrWordsEnum;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class OCR {

    public void execute(Params params) {
        var jpgs = checkInputAndGetFiles(params);
        checkOutput(params);
        var configStr = readConfigStr(params);
        var client = buildXunfeiSDKClient(configStr);
        for (var jpg : jpgs) {
            if (!jpg.getPath().endsWith(".jpg")) {
                continue;
            }

            var text = fetchOCRText(client, jpg);
            writeResultToFile(jpg, text, params);
        }

        System.out.println("完成" + params.input + "下文件识别");
    }

    private String fetchOCRText(GeneralWordsClient client, File img) {
        try {
            var imgBytes = readImageBytes(img);
            var response = client.generalWords(Base64.getEncoder().encodeToString(imgBytes));

            return parseOCRResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("提取" + img.getName() + "文字失败");
        }
    }

    private String parseOCRResponse(String response) {
        var resp = JSONObject.parseObject(response);
        var data = resp.getJSONObject("data");
        var block = data.getJSONArray("block");
        if (block == null) {
            throw new RuntimeException("回调异常：" + response);
        }
        var lines = block.getJSONObject(0).getJSONArray("line");

        var content = new StringBuilder();
        for (var line : lines) {
            var index = lines.indexOf(line);
            var item = lines.getJSONObject(index);
            var words = item.getJSONArray("word");
            content.append(words.getJSONObject(0).get("content"));
        }

        return content.toString();
    }

    private byte[] readImageBytes(File img) throws IOException {
        var in = new FileInputStream(img);
        var all = in.readAllBytes();
        in.close();

        return all;
    }

    private GeneralWordsClient buildXunfeiSDKClient(String configStr) {
        var config = JSONObject.parseObject(configStr);
        var appId = config.get("appId").toString();
        var apiKey = config.get("apiKey").toString();

        return new GeneralWordsClient
                .Builder(appId, apiKey, OcrWordsEnum.PRINT)
                .build();
    }

    private void writeResultToFile(File jpg, String text, Params params) {
        var outName = jpg.getName() + ".txt";
        var outPath = params.output + File.separator + outName;
        var outFile = new File(outPath);
        if (outFile.exists()) {
            outFile.delete();
        }

        try {
            var file = Files.createFile(Paths.get(outPath));
            Files.write(file, text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("写文件失败");
        }

        System.out.println("提取" + jpg.getName() + "文字成功");
    }

    private String readConfigStr(Params params) {
        try {
            return Files.readString(Paths.get(params.config));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取配置文件失败");
        }
    }

    private File[] checkInputAndGetFiles(Params params) {
        var input = new File(params.input);
        var jpgs = input.listFiles();
        if (jpgs == null) {
            throw new RuntimeException("输入文件夹为空");
        }

        return jpgs;
    }

    private void checkOutput(Params params) {
        var output = new File(params.output);
        if (!output.isDirectory()) {
            throw new RuntimeException("输出必须是文件夹");
        }
    }
}
