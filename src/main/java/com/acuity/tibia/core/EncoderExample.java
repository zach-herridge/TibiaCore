package com.acuity.tibia.core;

import SevenZip.Compression.LZMA.Decoder;
import SevenZip.Compression.LZMA.Encoder;

import java.io.*;

/**
 * Created by Zachary Herridge on 9/11/2017.
 */
public class EncoderExample {

    public static void main( String[] args ) throws IOException
    {
        // first compress
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream( baos );
        ps.print("I will try decoding this text.");
        ps.close();

        byte[] buf = baos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream( buf );
        baos = new ByteArrayOutputStream();

        Encoder enc = new Encoder();
        enc.SetEndMarkerMode(true);
        enc.WriteCoderProperties( baos );
        enc.Code( bis, baos, -1, -1, null );

        byte[] bytes = baos.toByteArray();
        System.out.println("Encoded as: " + bytes.length);
    }


}
