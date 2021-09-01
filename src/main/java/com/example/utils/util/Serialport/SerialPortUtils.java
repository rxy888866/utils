package com.example.utils.util.Serialport;


import com.alibaba.fastjson.JSONObject;
import gnu.io.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 串口参数的配置 串口一般有如下参数可以在该串口打开以前进行配置： 包括串口号，波特率，输入/输出流控制，数据位数，停止位和奇偶校验。
 */
// 注：串口操作类一定要继承SerialPortEventListener
// 注：串口操作类一定要继承SerialPortEventListener
@Controller
@RequestMapping("/webDemo")
public class SerialPortUtils implements SerialPortEventListener {
    // 检测系统中可用的通讯端口类
    private static CommPortIdentifier commPortId;
    // 枚举类型
    private static Enumeration<CommPortIdentifier> portList;
    // RS232串口
    private static SerialPort serialPort;
    // 输入流
    private static InputStream inputStream;
    // 输出流
    private static OutputStream outputStream;
    // 保存串口返回信息
    private static String data;
    // 保存串口返回信息十六进制
    private static String dataHex;



    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }








    /**
     * 初始化串口
     * @author LinWenLi
     * @date 2018年7月21日下午3:44:16
     * @Description: TODO
     * @param: paramConfig  存放串口连接必要参数的对象（会在下方给出类代码）
     * @return: void
     * @throws
     */
    @SuppressWarnings("unchecked")
    public void init(com.example.utils.util.Serialport.ParamConfig paramConfig) {
        // 获取系统中所有的通讯端口
        portList = CommPortIdentifier.getPortIdentifiers();
        // 记录是否含有指定串口
        boolean isExsist = false;
        // 循环通讯端口
        while (portList.hasMoreElements()) {
            commPortId = portList.nextElement();
            // 判断是否是串口
            if (commPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                // 比较串口名称是否是指定串口
                if (paramConfig.getSerialNumber().equals(commPortId.getName())) {
                    // 串口存在
                    isExsist = true;
                    // 打开串口
                    try {
                        // open:（应用程序名【随意命名】，阻塞时等待的毫秒数）
                        serialPort = (SerialPort) commPortId.open(Object.class.getSimpleName(), 2000);
                        // 设置串口监听
                        serialPort.addEventListener(this);
                        // 设置串口数据时间有效(可监听)
                        serialPort.notifyOnDataAvailable(true);
                        // 设置串口通讯参数:波特率，数据位，停止位,校验方式
                        serialPort.setSerialPortParams(paramConfig.getBaudRate(), paramConfig.getDataBit(),
                                paramConfig.getStopBit(), paramConfig.getCheckoutBit());
                    } catch (Exception e) {
                        String s = e.toString();
                        if (s.contains("gnu.io.PortInUseException")){
                           System.err.println("串口占用");
                        }else{
                            e.printStackTrace();
                        }
                    }
                    // 结束循环
                    break;
                }
            }
        }
        // 若不存在该串口则抛出异常
        if (!isExsist) {
            System.err.println("不存在该串口");
        }
    }

    /**
     * 实现接口SerialPortEventListener中的方法 读取从串口中接收的数据
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI: // 通讯中断
            case SerialPortEvent.OE: // 溢位错误
            case SerialPortEvent.FE: // 帧错误
            case SerialPortEvent.PE: // 奇偶校验错误
            case SerialPortEvent.CD: // 载波检测
            case SerialPortEvent.CTS: // 清除发送
            case SerialPortEvent.DSR: // 数据设备准备好
            case SerialPortEvent.RI: // 响铃侦测
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 输出缓冲区已清空
                break;
            case SerialPortEvent.DATA_AVAILABLE: // 有数据到达
                // 调用读取数据的方法
                readComm();
                break;
            default:
                break;
        }
    }

    /**
     * 读取串口返回信息
     * @author LinWenLi
     * @date 2018年7月21日下午3:43:04
     * @return: void
     */
    public void readComm() {
        try {
            inputStream = serialPort.getInputStream();
            // 通过输入流对象的available方法获取数组字节长度
            byte[] readBuffer = new byte[inputStream.available()];
            // 从线路上读取数据流
            int len = 0;
            while ((len = inputStream.read(readBuffer)) != -1) {
                // 直接获取到的数据
                data = new String(readBuffer, 0, len).trim();
                 //转为十六进制数据
                dataHex = bytesToHexString(readBuffer);
                System.out.println("data:" + data);
                System.out.println("dataHex:" + dataHex);// 读取后置空流对象
                inputStream.close();
                inputStream = null;
                if (data != null && !"".equals(data) ){break;}

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





    /**
     * 发送信息到串口
     * @author LinWenLi
     * @date 2018年7月21日下午3:45:22
     * @param: data
     * @return: void
     * @throws
     */
    public void sendComm(String data) {
        byte[] writerBuffer = null;
        try {
            writerBuffer = hexToByteArray(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            outputStream = serialPort.getOutputStream();
            outputStream.write(writerBuffer);
            outputStream.flush();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭串口
     * @author LinWenLi
     * @date 2018年7月21日下午3:45:43
     * @Description: 关闭串口
     * @param:
     * @return: void
     * @throws
     */
    public void closeSerialPort() {
        if (serialPort != null) {
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            serialPort.close();
            serialPort = null;
        }
    }

    /**
     * 十六进制串口返回值获取
     */
    public String getDataHex() {
        String result = dataHex;
        // 置空执行结果
        dataHex = null;
        // 返回执行结果
        return result;
    }

    /**
     * 串口返回值获取
     */
    public String getData() {
        String result = data;
        // 置空执行结果
        data = null;
        // 返回执行结果
        return result;
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            // 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 数组转换成十六进制字符串
     * @param
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        // 实例化串口操作类对象
        SerialPortUtils serialPort = new SerialPortUtils();
        // 创建串口必要参数接收类并赋值，赋值串口号，波特率，校验位，数据位，停止位
        com.example.utils.util.Serialport.ParamConfig paramConfig = new com.example.utils.util.Serialport.ParamConfig("COM2", 9600, 0, 8, 1);
        // 初始化设置,打开串口，开始监听读取串口数据
        serialPort.init(paramConfig);
        // 调用串口操作类的sendComm方法发送数据到串口
//        serialPort.sendComm("FEF10A000000000000000AFF");
        serialPort.readComm();
        // 关闭串口
        serialPort.closeSerialPort();
    }


}