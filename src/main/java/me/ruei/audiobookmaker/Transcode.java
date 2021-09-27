package me.ruei.audiobookmaker;

import me.ruei.audiobookmaker.utils.CommandUtil;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class Transcode {

    public void execute(Params params) {
        var pdf = new File(params.input);
        if (!pdf.exists()) {
            throw new RuntimeException("PDF文件不存在");
        }

        var pages = 0;
        try {
            var pdDocument = PDDocument.load(pdf);
            if (pdDocument.isEncrypted()) {
                throw new RuntimeException("PDF文件已加密");
            }

            pages = pdDocument.getNumberOfPages();
            pdDocument.close();
        } catch (IOException e) {
            throw new RuntimeException("解析PDF文件失败，请检查源文件是否损坏");
        }

        for (int i = 0; i < pages; i++) {
            var page = i + 1;
            var outPath = params.output + File.separator + page + ".jpg";
            var cmd = getImageMagicCommand(params.input, outPath, i);
            var out = new ByteArrayOutputStream();
            var err = new ByteArrayOutputStream();

            try {
                CommandUtil.exec(cmd, 60 * 1000L, out, err);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("第" + page + "页转码失败：" + err);
            }

            System.out.println("第" + page + "页转码成功");
        }

        System.out.println("PDF转码成功");
    }

    private String getImageMagicCommand(String input, String output, int page) {
        return MessageFormat.format("magick convert -density 100 -quality 75 {0}[{1}] {2}", input, page, output);
    }
}
