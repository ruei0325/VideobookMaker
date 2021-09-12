package me.ruei.audiobookmaker.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class ConfigValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        var config = new File(value);
        if (!config.exists()) {
            throw new ParameterException("配置文件不存在");
        }

        if (!config.isFile()) {
            throw new ParameterException("配置文件必须是一个文件");
        }

        if (!value.endsWith(".json")) {
            throw new ParameterException("配置文件必须是一个json文件");
        }
    }
}
