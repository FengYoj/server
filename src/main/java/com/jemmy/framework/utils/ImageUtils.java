package com.jemmy.framework.utils;

import com.jemmy.framework.component.resources.image.ResourceImage;
import com.jemmy.framework.utils.request.Uri;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;

public class ImageUtils {

    public static BufferedImage getImage(String url) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        var httpRequest = HttpRequest.newBuilder()
                .uri(Uri.of(url).build())
                .GET()
                .build();

        HttpResponse<byte[]> res = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

        ByteArrayInputStream stream = new ByteArrayInputStream(res.body());

        return ImageIO.read(stream);
    }

    public static BufferedImage getImage(ResourceImage img) throws IOException, InterruptedException {
        return getImage(img.getUrl());
    }

    public static File setPngDpi(BufferedImage image, int dpi) throws IOException {

        final String formatName = "png";

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata, dpi);

            File file = File.createTempFile("SETPNGDPI_", ".png");

            final ImageOutputStream stream = ImageIO.createImageOutputStream(file);

            writer.setOutput(stream);
            writer.write(metadata, new IIOImage(image, null, metadata), writeParam);

            return file;
        }

        return null;
    }

    public static BufferedImage byteToBufferedImage(byte[] bytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        return ImageIO.read(in);
    }

    private static void setDPI(IIOMetadata metadata, Integer dpi) throws IIOInvalidTreeException {

        // for PMG, it's dots per millimeter
        double dotsPerMilli = 1.0 * dpi / 10 / 2.54d;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

}
