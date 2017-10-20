package com.acuity.tibia.core;

import SevenZip.Compression.LZMA.Decoder;
import SevenZip.Compression.LZMA.Encoder;
import SevenZip.ICodeProgress;
import com.google.gson.Gson;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Zach on 9/6/2017.
 */
public class SpriteDumper {

    private void dumpSpriteSheet(String assetPath, CatalogEntry entry) throws Exception{
        if (!entry.getType().equals("sprite")) return;

        FileInputStream fileInputStream = new FileInputStream(assetPath + entry.getFile() + ".lzma");
        byte[] header = new byte[6];
        fileInputStream.read(header);

        ByteOutputStream extra = new ByteOutputStream();
        while (true) {
            int read = fileInputStream.read();
            extra.write(read);
            if ((read & 0x80) != 0x80){
                break;
            }
        }

        byte[] properties = new byte[5];
        fileInputStream.read(properties);

        Decoder decoder = new Decoder();
        Encoder encoder = decoder.SetDecoderPropertiesWithEncoder(properties);

        fileInputStream.skip(8);

        ByteOutputStream buffer = new ByteOutputStream();
        decoder.Code(fileInputStream, buffer, -1);
        buffer.flush();

        byte[] bytes = buffer.getBytes();

        BufferedImage sheet = ImageIO.read(new ByteInputStream(bytes, bytes.length));

        for (int i = 0; i < 384; i++) {
            for (int j = 0; j < 384; j++) {
                int rgb = sheet.getRGB(i, j);
                if (rgb != 16711935) sheet.setRGB(i, j, -4671305);
            }
        }

        ByteOutputStream byteOutputStream = new ByteOutputStream();
        ImageIO.write(sheet, "bmp", byteOutputStream);
        byte[] bytes1 = byteOutputStream.getBytes();
        ByteOutputStream byteOutputStream1 = new ByteOutputStream();
        encoder.Code(new ByteInputStream(bytes, bytes.length), byteOutputStream1, -1, -1, new ICodeProgress() {
            @Override
            public void SetProgress(long inSize, long outSize) {

            }
        });
        byte[] output = byteOutputStream1.getBytes();

        byte[] concat = concat(concat(header, extra.getBytes()), output);

        //Sheet sizes.
        //0 = 12x12
        //1 = 12x6
        //2 = 6x12
        //3 = 6x6

        int xCols = (entry.getSpriteType() == 0 || entry.getSpriteType() == 1) ? 12 : 6;
        int yCols = (entry.getSpriteType() == 0 || entry.getSpriteType() == 2) ? 12 : 6;

        int xSize = 384 / xCols;
        int ySize = 384 / yCols;

        File sheetFolder = new File("dump/sprite_sheets/" + entry.getFirstSpriteID() + "-" + entry.getLastSpriteID());
        if (!sheetFolder.exists()) sheetFolder.mkdir();

        for (int x = 0; x < xCols; x++) {
            for (int y = 0; y < yCols; y++) {
                BufferedImage sprite = sheet.getSubimage(x * xSize, y * ySize, xSize, ySize);
                ImageIO.write(sprite, "png", new File(sheetFolder, x + "-" + y + ".png"));
            }
        }


        new FileOutputStream(new File(sheetFolder, entry.getFile() + ".lzma")).write(concat);

        ImageIO.write(sheet, "png", new File(sheetFolder, "sheet.png"));
    }

    public byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public SpriteDumper(String assetsPath) throws IOException {
        String json = new String(Files.readAllBytes(new File(assetsPath + "catalog-content.json").toPath()));

        CatalogEntry[] entries = new Gson().fromJson(json, CatalogEntry[].class);
        for (CatalogEntry entire : entries) {
            try {
                dumpSpriteSheet(assetsPath, entire);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class CatalogEntry {

        private String type;
        private String file;
        private int spritetype;
        private int firstspriteid;
        private int lastspriteid;
        private int area;

        public String getType() {
            return type;
        }

        public String getFile() {
            return file;
        }

        public int getSpriteType() {
            return spritetype;
        }

        public int getFirstSpriteID() {
            return firstspriteid;
        }

        public int getLastSpriteID() {
            return lastspriteid;
        }

        public int getArea() {
            return area;
        }
    }

    public static void main(String[] args) {
        try {
            new SpriteDumper("C:\\Users\\Zach\\AppData\\Local\\Tibia\\packages\\Tibia\\assets\\");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
