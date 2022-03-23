package com.jemmy.framework.utils.file;

import com.jemmy.framework.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    private final File file;

    public FileUtils(File file) {
        this.file = file;
    }

    public FileUtils(String file) {
        this.file = new File(file);
    }

    public Boolean isExists() {
        return file.exists();
    }

    public String getContent() {
        try {
            StringBuilder result = new StringBuilder();

            BufferedReader br = new BufferedReader(new FileReader(file));

            String s;

            while((s = br.readLine()) != null){
                result.append(System.lineSeparator()).append(s);
            }

            br.close();

            return result.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean setContent(String val) {
        try {
            FileWriter writer;
            writer = new FileWriter(file);
            writer.write(val);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static FileUtils of(File file) {
        return new FileUtils(file);
    }

    public static FileUtils of(String file) {
        return new FileUtils(file);
    }

    public boolean save(String path) throws IOException {

        File targetFile = new File(path);

        File parentFile = targetFile.getParentFile();

        if (!parentFile.exists() && !parentFile.mkdirs()) {
            return false;
        }

        // Does not exist and fails to create
        if (!targetFile.exists() && !targetFile.createNewFile()) {
            return false;
        }

        try {
            org.apache.commons.io.FileUtils.copyFile(this.file, targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getName());

        // 拷贝文件
        org.apache.commons.io.FileUtils.copyFile(multipartFile.getResource().getFile(), file);

        return file;
    }

    /**
     * 流 转 文件
     * @param ins 流
     * @param file 文件
     */
    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载文件
     * @param url 路径
     * @return 文件实体
     */
    public static FileInfo download(String url) throws IOException {
        URL u = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) u.openConnection();

        // 设置超时间为3秒
        conn.setConnectTimeout(3*1000);

        // 防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        conn.connect();

        // 得到输入流
        InputStream inputStream = conn.getInputStream();

        // 创建模板文件
        File file = File.createTempFile("download_", "." + getSuffix(url, conn));

        // 流转文件
        inputStreamToFile(inputStream, file);

        return new FileInfo(file, conn.getContentType());
    }

    public static String getSuffix(String url, HttpURLConnection conn) throws IOException {

        String urlSuffix = getSuffix(url);

        if (StringUtils.isExist(urlSuffix)) {
            return urlSuffix;
        }

        String connSuffix = URLConnection.guessContentTypeFromStream(conn.getInputStream());

        if (connSuffix != null) {
            return connSuffix.split("/")[1];
        }

        return conn.getContentType().split("/")[1];
    }

    public static String getSuffix(String url) {
        Matcher matcher = Pattern.compile("\\S*[?]\\S*").matcher(url);

        String[] spUrl = url.split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];

        if(matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            return spEndUrl[0].split("\\.")[1];
        }

        String[] split = endUrl.split("\\.");

        if (split.length > 1) {
            return split[1];
        }

        return null;
    }

    /**
     * 解压 zip 文件
     * @param zipPath zip 文件路径
     * @param descDir 解压路径
     * @return 是否解压成功
     */
    public static Boolean unZip(String zipPath, String descDir) {
        try {
            File zipFile = new File(zipPath);

            if (!zipFile.exists()) {
                throw new IOException("解压文件不存在！");
            }

            File pathFile = new File(descDir);

            if (!pathFile.exists() && !pathFile.mkdirs()) {
                throw new IOException("创建文件夹失败！");
            }

            ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));

            for (Enumeration<?> entries = zip.entries(); entries.hasMoreElements();) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");
                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));

                if (!file.exists() && !file.mkdirs()) {
                    throw new IOException("创建文件失败！");
                }

                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                // 输出文件路径信息
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;

                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }

                in.close();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 拷贝文件夹
     * @param source 源文件夹
     * @param dest 目标文件夹
     */
    public static void copyDirectory(File source, File dest) {
        try {
            org.apache.commons.io.FileUtils.copyFileToDirectory(source, dest);
        } catch (IOException e) {
            throw new RuntimeException("拷贝文件异常", e);
        }
    }

    /**
     * 删除文件夹或文件
     *
     * @param file 文件或者目录
     * @return 删除结果
     */
    public static boolean delete(File file) {
        // 如果文件不存在，则退出
        if (!file.exists()) {
            return false;
        }

        if (file.isFile()) {
            return file.delete();
        } else {
            File[] files = file.listFiles();

            if (files != null) {
                for (File f : files) {
                    delete(f);
                }
            }
        }

        return file.delete();
    }
}
