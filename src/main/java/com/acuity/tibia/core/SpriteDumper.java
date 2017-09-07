package com.acuity.tibia.core;

import SevenZip.Compression.LZMA.Decoder;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Zach on 9/6/2017.
 */
public class SpriteDumper {


    public SpriteDumper() throws IOException {
        File sprite = new File(getClass().getClassLoader().getResource("asset_tests/sprites-0ab38261005826ea657653b905f7e3185bf1790d414080e8f537b86242cbb6ac.bmp.lzma").getPath());

        Decoder decoder = new Decoder();

        FileInputStream fileInputStream = new FileInputStream(sprite);
        fileInputStream.skip(6);

        while ((fileInputStream.read() & 0x80) == 0x80) {
            System.out.println("Skipping header byte.");
        }

        byte[] properties = new byte[5];
        fileInputStream.read(properties);
        decoder.SetDecoderProperties(properties);

        fileInputStream.skip(8);

        ByteOutputStream buffer = new ByteOutputStream();
        decoder.Code(fileInputStream, buffer, -1);
        buffer.flush();

        byte[] bytes = buffer.getBytes();

        BufferedImage read = ImageIO.read(new ByteInputStream(bytes, bytes.length));
        ImageIO.write(read, "png", new File("image.png"));
    }

    public static void main(String[] args) {
        try {
            new SpriteDumper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
