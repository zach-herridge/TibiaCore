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
        File sprite = new File("C:\\Users\\Zach\\Desktop\\sprites-3d2c6e6a565b9d5bd03b5445fe07c1aae1f3ffd31653a7de6dc42cf0f4e70caa.bmp.lzma");


        Decoder deocoder = new Decoder();


        ByteOutputStream buffer = new ByteOutputStream();

        FileInputStream fileInputStream = new FileInputStream(sprite);
        fileInputStream.skip(6);

        int pos = 11;

        while ((fileInputStream.read() & 0x80) == 0x80) {
            System.out.println("Skipping");
            pos++;
        }

        byte[] properties = new byte[5];
        fileInputStream.read(properties);
        deocoder.SetDecoderProperties(properties);

        pos += 8;
        fileInputStream.skip(8);

        System.out.println(pos);
        deocoder.Code(fileInputStream, buffer, fileInputStream.available());


        byte[] bytes = buffer.getBytes();


        BufferedImage read = ImageIO.read(new ByteInputStream(bytes, bytes.length));

        new FileOutputStream("test.bmp").write(bytes);



    }

    public static void main(String[] args) {
        try {
            new SpriteDumper();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
