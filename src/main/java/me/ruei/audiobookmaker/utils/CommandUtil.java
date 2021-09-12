package me.ruei.audiobookmaker.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CommandUtil {

    public static void exec(String command, Long timeout, ByteArrayOutputStream out, ByteArrayOutputStream err) throws IOException {
        var cmd = CommandLine.parse(command);
        var streamHandler = new PumpStreamHandler(out, err);

        var executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.setWatchdog(new ExecuteWatchdog(timeout));
        executor.execute(cmd);
    }
}
