package com.jemmy.framework.component.validate_code;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;

public class ValidateCodeUtil {

    /**
     * 随机产生数字与字母组合的字符串
     */
    private static final String randString = "0123456789abcdefghijklmnopqrstuvwxyz";

    /**
     * 图片宽
     */
    private static final int width = 95;

    /**
     * 图片高
     */
    private static final int height = 25;

    /**
     * 干扰线数量
     */
    private static final int lineSize = 40;

    /**
     * 随机产生字符数量
     */
    private static final int stringNum = 4;

    private static final Random random = new Random();

    /**
     * 获得字体
     */
    private static Font getFont() {
        return new Font("Fixedsys", Font.BOLD, 18);
    }

    /**
     * 获得颜色
     */
    private static Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /**
     * 获取验证码实体
     * @param aging 时效，单位：毫秒
     * @return 实体
     */
    public static ValidateCode getCode(Long aging) {
        // 绘制随机字符
        String randomString = "";

        for (int i = 1; i <= stringNum; i++) {
            randomString = getRandomString(random.nextInt(randString.length()));
        }

        return new ValidateCode(randomString, aging);
    }

    /**
     * 生成随机图片
     */
    public static ValidateCode getImageCode(Long aging) {
        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        // 产生 Image 对象的 Graphics 对象， 可以在图像上进行各种绘制操作
        Graphics g = image.getGraphics();
        // 图片大小
        g.fillRect(0, 0, width, height);
        // 字体大小
        g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        // 字体颜色
        g.setColor(getRandColor(110, 133));
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drowLine(g);
        }

        // 绘制随机字符
        String randomString = "";

        for (int i = 1; i <= stringNum; i++) {
            randomString = drowString(g, randomString, i);
        }

        g.dispose();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            String base64Img = Base64.encodeBase64String(outputStream.toByteArray());

            return new ValidateCode("data:image/png;base64," + base64Img, randomString, aging);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 绘制字符串
     */
    private static String drowString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = getRandomString(random.nextInt(randString.length()));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    /**
     * 绘制干扰线
     */
    private static void drowLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 获取随机的字符
     */
    public static String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }

}
