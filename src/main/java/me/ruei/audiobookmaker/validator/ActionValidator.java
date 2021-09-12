package me.ruei.audiobookmaker.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class ActionValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        if (!"transcode".equals(value) && !"ocr".equals(value) && !"tts".equals(value)) {
            throw new ParameterException("action操作必须为如下类型：transcode（PDF转图片）｜ocr（图片转文字）|tts（文字合成语音）");
        }
    }
}
