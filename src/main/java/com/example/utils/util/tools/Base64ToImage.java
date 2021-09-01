package com.example.utils.util.tools;

import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author zqh
 * @pageName
 * @create 2018-07-27 10:20
 * @desc 将64码转化成图片
 **/
public class Base64ToImage {

    public static String base64ToImage(List<String> baseList,int userID,String relativelyPath,String partName)
    {

        if (baseList.size()>0)
        {

            File file=new File(relativelyPath+"\\"+partName+"\\"+userID+"\\"+TimeTool.formateLongTime());
            if(!file.exists())
            {
                file.mkdirs();
            }
            for(int i=0;i<baseList.size();i++)
            {
                BASE64Decoder decoder = new BASE64Decoder();
                try
                {
                    //Base64解码
                    byte[] b = decoder.decodeBuffer(baseList.get(i).replace("data:image/png;base64,",""));
                    for(int j=0;j<b.length;++j)
                    {
                        if(b[j]<0)
                        {//调整异常数据
                            b[j]+=256;
                        }
                    }
                    //生成jpeg图片
                    String imgFilePath = file+"\\"+i+".jpg";//新生成的图片
                    OutputStream out = new FileOutputStream(imgFilePath);
                    out.write(b);
                    out.flush();
                    out.close();
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage().toString());
                    return "";
                }

            }
            return  file.toString();
        }
        return "";
    }
    
    public static String[] base64ToImage(List<String> baseList, String bashPath, String savePath) {
        String[] filePath = new String[baseList.size()];
        if (baseList.size() > 0) {

            File file = new File(bashPath + savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            for (int i = 0; i < baseList.size(); i++) {
                BASE64Decoder decoder = new BASE64Decoder();
                try {
                    // Base64解码
                    byte[] b = decoder.decodeBuffer(baseList.get(i).replace("data:image/png;base64,", ""));
                    for (int j = 0; j < b.length; ++j) {
                        if (b[j] < 0) {// 调整异常数据
                            b[j] += 256;
                        }
                    }
                    // 生成jpeg图片
                    String fileName = TimeTool.formateLongTime() + ".jpg";// 新生成的图片
                    OutputStream out = new FileOutputStream(file + File.separator + fileName);
                    out.write(b);
                    out.flush();
                    out.close();
                    filePath[i] = savePath + fileName;
                } catch (Exception e) {
                    System.out.println(e.getMessage().toString());
                    return filePath;
                }

            }
            return filePath;
        }
        return filePath;
    }
    
    /**
     * 
     * @Function remoteImageToLocalImage
     * @Description 该函数的功能描述
     *
     * @param picAddress 萤石抓图地址
     * @param savePath 保存地址
     * @return 返回结果描述
     * @throws 异常描述
     *
     * @version v1.0.0
     * @author Yy
     * @date 2019年4月28日 下午2:15:18 
     *
     * Modification History:
     * Date         Author          Version            Description
     *---------------------------------------------------------*
     * 2019年4月28日     Yy           v1.0.0               修改原因
     */
    public static String remoteImageToLocalImage(String picAddress, String savePath, String fileName) {
        String filePath = "";
        //通过输入流获取图片数据  
        InputStream inStream = null;
        FileOutputStream outStream = null;
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            URL url = new URL(picAddress);
            //打开链接  
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
            //设置请求方式为"GET"  
            conn.setRequestMethod("GET");  
            //超时响应时间为5秒  
            conn.setConnectTimeout(5 * 1000);  
            inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
            byte[] data = readInputStream(inStream);  
            //new一个文件对象用来保存图片，默认保存当前工程根目录  
            File imageFile = new File(file + File.separator + fileName);  
            //创建输出流  
            outStream = new FileOutputStream(imageFile);  
            //写入数据  
            outStream.write(data);
            filePath = imageFile.getAbsolutePath();
        } catch (Exception e) {
             e.printStackTrace();
             return filePath;
        } finally {
            try {
                //关闭输出流  
                outStream.close();
            } catch (IOException e) {
                 e.printStackTrace();
            }  
        }
        return filePath;
    }
    
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }  

}
