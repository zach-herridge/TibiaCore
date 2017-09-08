package com.acuity.tibia.core;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

/**
 * Created by Zachary Herridge on 9/7/2017.
 */
public class SpriteDetector {

    public SpriteDetector() {
        int matchMethod = Imgproc.TM_CCORR_NORMED;

        Mat image = Imgcodecs.imread("C:\\Users\\Zach\\Dropbox\\Code\\Code Projects\\AcuityBotting\\TibiaCore\\src\\main\\resources\\sprite_detection\\windowOnly.png");
        showImage(image);
        Mat template = Imgcodecs.imread("C:\\Users\\Zach\\Dropbox\\Code\\Code Projects\\AcuityBotting\\TibiaCore\\src\\main\\resources\\sprite_detection\\ScaledTemplate.png");
        Mat mask = Imgcodecs.imread("C:\\Users\\Zach\\Dropbox\\Code\\Code Projects\\AcuityBotting\\TibiaCore\\src\\main\\resources\\sprite_detection\\mask2.png");
        showImage(template);

        System.out.println(System.currentTimeMillis());
        int result_cols = image.cols() - template.cols() + 1;
        int result_rows = image.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_8UC4);

        Imgproc.matchTemplate(image, template, result,  matchMethod, mask);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

        Imgproc.rectangle(image, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()), new Scalar(0, 255, 0));

        System.out.println(System.currentTimeMillis());

        Imgcodecs.imwrite("output.png", image);

        System.out.println();
    }


    private void showImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);


        JFrame jFrame = new JFrame();
        JLabel jLabel = new JLabel(new ImageIcon(image));
        jFrame.setSize(300, 300);
        jFrame.setContentPane(jLabel);
        jFrame.setVisible(true);
    }


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new SpriteDetector();
    }
}
