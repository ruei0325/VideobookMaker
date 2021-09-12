# AudiobookMaker

> 简易的有声书制作程序

## 原理

- 使用Imagemagick将PDF转换成图片
- 使用科大讯飞OCR API识别图片，生成文字
- 使用科大讯飞TTS API将文字合成语音

## 使用

### 下载与安装

```shell
git clone git@github.com:ruei0325/VideobookMaker.git
mvn package
```

### 帮助
```shell
java -jar AudiobookMaker.jar -h
```

### PDF转码
- 输入PDF文件，输出到某个目录
```shell
java -jar AudiobookMaker.jar -a transcode -i ./1.pdf -o ./images
```

### 提取图片文字
- 输入图片文件夹（图片为.jpg后缀，否则不识别）
- 输出到某个目录
```shell
java -jar AudiobookMaker.jar -a ocr -i ./images -o ./text -c xfocr.json
```

配置文件样例（讯飞控制台-印刷文字识别 页面中获取）
```json
{
  "appId": "",
  "apiKey": ""
}
```

### 图片文字合成语音
- 输入文字文件夹（文件为.txt后缀，否则不是别）
- 输出到某个目录
```shell
java -jar AudiobookMaker.jar -a tts -i ./text -o ./mp3 -c xftts.json
```

配置文件样例（讯飞控制台-在线语音合成 页面中获取）
```json
{
  "appId": "",
  "apiKey": "",
  "apiSecret": ""
}
```

## QA

### 如何选择PDF中的几页进行转码？
暂未实现。若等不及迭代，可以自行使用其他软件对PDF进行裁剪后使用。

### 如何指定一张图片识别文字？
暂未实现指定图片识别，不过可以在文件夹下只放一张图来完成(- < -)

### 如何指定一段文字来合成语音
暂未实现指定文本片段识别，不过可以在文件夹下只放一个文本文件来完成(- < -)

### 为什么不做到一步到位？（转码-识别-合成）
图片识别文字时，会将页眉、页码、图片中的文字等元素也识别出来，还是需要手工修改一遍文案，才能获得最佳体验噢~