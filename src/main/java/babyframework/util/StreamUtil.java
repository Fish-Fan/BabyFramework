package babyframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StreamUtil {
    private static final Logger logger = LoggerFactory.getLogger(StreamUtil.class);

    /**
     * 从输入流中获取字符串
     * @param is
     * @return
     */
    public static String getString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.error("get string failure",e);
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void copyStream(InputStream is, OutputStream os) {
        try {
            int length;
            byte[] buffer = new byte[1024 * 4];
            while ((length = is.read(buffer,0,buffer.length)) != -1) {
                os.write(buffer,0,length);
                os.flush();
            }
        } catch (IOException e) {
            logger.error("copy stream failure",e);
            e.printStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
