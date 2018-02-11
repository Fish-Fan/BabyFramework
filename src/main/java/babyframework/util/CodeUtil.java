package babyframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CodeUtil {
    private static final Logger logger = LoggerFactory.getLogger(CodeUtil.class);

    /**
     * 将URL编码
     * @return
     */
    public static String encodeURL(String source) {
        String target = "";
        try {
            target = URLEncoder.encode(source,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("encode URL failure",e);
            e.printStackTrace();
        }
        return target;
    }

    /**
     * 将URL解码
     * @param source
     * @return
     */
    public static String decodeURL(String source) {
        String target = "";
        try {
            target = URLDecoder.decode(source,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("decode URL failure",e);
            e.printStackTrace();
        }
        return target;
    }

}
