package me.ruei.audiobookmaker;

import com.beust.jcommander.Parameter;
import me.ruei.audiobookmaker.validator.ActionValidator;
import me.ruei.audiobookmaker.validator.ConfigValidator;

public class Params {

    @Parameter(
            names = {"-action", "-a"},
            required = true,
            description = "操作类型：transcode（PDF转图片）｜ocr（图片转文字）|tts（文字合成语音）",
            validateWith = ActionValidator.class
    )
    String action;

    @Parameter(
            names = {"-input", "-i"},
            required = true,
            description = "输入文件(夹)"
    )
    String input;

    @Parameter(
            names = {"-output", "-o"},
            required = true,
            description = "输出文件夹"
    )
    String output;

    @Parameter(
            names = {"-config", "-c"},
            description = "讯飞OCR和TTS配置文件",
            validateWith = ConfigValidator.class
    )
    String config;

    @Parameter(
            names = {"-help", "-h", "-usage"},
            description = "帮助",
            help = true
    )
    Boolean help;
}
