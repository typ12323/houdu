package org.example;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        Robot robot = new Robot();
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            System.out.println("未找到可用串口");
            return;
        }
        SerialPort port = ports[0];
        if (!port.openPort()) {
            System.out.println("打开串口失败");
            return;
        }
        port.setComPortParameters(9600, 8, 1, 0);
        InputStream inputStream = port.getInputStream();
        while (true) {
            if (inputStream.available() > 0) {
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                String dataString = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();
                // 解析数据格式为“# 37 Test: 0.404 MM”
                Pattern pattern = Pattern.compile("\\d+\\.\\d+");
                Matcher matcher = pattern.matcher(dataString);
                while (matcher.find()) {
                    String lastDataValue = matcher.group();
                    System.out.println("提取的数据值：" + lastDataValue);
                    if (!lastDataValue.isEmpty() && !lastDataValue.equals("01.0") && !lastDataValue.equals("0.130")) {
                        double dataValue = Double.parseDouble(lastDataValue);
                        System.out.println("转换为double类型的数据值：" + dataValue);
                        for (char c : lastDataValue.toCharArray()) {
                            if (c != '-') {
                                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                                robot.keyPress(keyCode);
                                robot.keyRelease(keyCode);
                            }
                        }
                        robot.delay(100);
                        robot.keyPress(KeyEvent.VK_ENTER);
                        robot.keyRelease(KeyEvent.VK_ENTER);
                    }
                }
            }
        }
    }
}

