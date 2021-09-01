package com.example.utils.util.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zqh
 * @pageName
 * @create 2018-07-19 18:56
 * @desc 自赠数
 **/
public  class AutoNumber {
    //产生六位自增数
    private static int sequence = 0;
    private static int length = 6;

    /**
     * YY+6位自增长码(8位)
     * @return
     */
    public static  String getLocalTrmSeqNum() {
        sequence = sequence >= 999999 ? 1 : sequence + 1;
        String datetime = new SimpleDateFormat("yyyyMMdd")
                .format(new Date());
        String datetimeSub=datetime.substring(2,4);
        String s = Integer.toString(sequence);
        return datetimeSub +addLeftZero(s, length);
    }

    /**
     * 左填0
     * @author zqh
     * @param s
     * @param length
     * @return
     */
    public static String addLeftZero(String s, int length) {
        // StringBuilder sb=new StringBuilder();
        int old = s.length();
        if (length > old) {
            char[] c = new char[length];
            char[] x = s.toCharArray();
            if (x.length > length) {
                throw new IllegalArgumentException(
                        "Numeric value is larger than intended length: " + s
                                + " LEN " + length);
            }
            int lim = c.length - x.length;
            for (int i = 0; i < lim; i++) {
                c[i] = '0';
            }
            System.arraycopy(x, 0, c, lim, x.length);
            return new String(c);
        }
        return s.substring(0, length);

    }

}
