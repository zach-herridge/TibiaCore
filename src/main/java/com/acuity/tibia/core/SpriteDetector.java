package com.acuity.tibia.core;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

/**
 * Created by Zachary Herridge on 9/7/2017.
 */
public class SpriteDetector {


    public SpriteDetector() {
        int matchMethod = Imgproc.TM_CCOEFF;

        Mat image = Imgcodecs.imread(new File(getClass().getClassLoader().getResource("sprite_detection/image.png").getPath()).getAbsolutePath());
        Mat template = Imgcodecs.imread(new File(getClass().getClassLoader().getResource("sprite_detection/template.png").getPath()).getAbsolutePath());

        int result_cols = image.cols() - template.cols() + 1;
        int result_rows = image.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(image, template, result,  matchMethod);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        if (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }

        Imgproc.rectangle(image, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()), new Scalar(0, 255, 0));

        Imgcodecs.imwrite("output.png", image);

        System.out.println();
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new SpriteDetector();
    }
}
