package com.acuity.tibia.core;

import SevenZip.Compression.LZMA.Decoder;
import com.google.gson.Gson;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;

/**
 * Created by Zach on 9/6/2017.
 */
public class SpriteDumper {

    private static HashSet<String> set = new HashSet<>();

    private void dumpSpriteSheet(String assetPath, CatalogEntry entry) throws Exception{
        if (!entry.getType().equals("sprite")) return;
        if (set.contains(entry.getSpritetype() + "-" + entry.getArea())) return;
        set.add(entry.getSpritetype() + "-" + entry.getArea());



        Decoder decoder = new Decoder();
        FileInputStream fileInputStream = new FileInputStream(assetPath + entry.getFile() + ".lzma");
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

        BufferedImage sheet = ImageIO.read(new ByteInputStream(bytes, bytes.length));


        //0 = 12x12
        //1 = 12x6
        //2 = 6x12
        //3 = 6x6

        int xCols = (entry.getSpritetype() == 0 || entry.getSpritetype() == 1) ? 12 : 6;
        int yCols = (entry.getSpritetype() == 0 || entry.getSpritetype() == 2) ? 12 : 6;

        int xSize = 384 / xCols;
        int ySize = 384 / yCols;


        File sheetFolder = new File("dump/sprite_sheets/" + entry.getFirstspriteid() + "-" + entry.getLastspriteid());
        if (!sheetFolder.exists()) sheetFolder.mkdir();

        for (int x = 0; x < xCols; x++) {
            for (int y = 0; y < yCols; y++) {
                BufferedImage sprite = sheet.getSubimage(x * xSize, y * ySize, xSize, ySize);
                ImageIO.write(sprite, "png", new File(sheetFolder, x + "-" + y + ".png"));
            }
        }

        ImageIO.write(sheet, "png", new File(sheetFolder, "sheet.png"));
    }


    public SpriteDumper() throws IOException {
        String assetsPath = "C:\\Users\\Zach\\AppData\\Local\\Tibia\\packages\\Tibia\\assets\\";


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

        public int getSpritetype() {
            return spritetype;
        }

        public int getFirstspriteid() {
            return firstspriteid;
        }

        public int getLastspriteid() {
            return lastspriteid;
        }

        public int getArea() {
            return area;
        }
    }

    public static void main(String[] args) {
        try {
            new SpriteDumper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
