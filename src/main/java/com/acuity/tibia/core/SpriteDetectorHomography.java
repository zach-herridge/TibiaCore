package com.acuity.tibia.core;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Zachary Herridge on 9/8/2017.
 */
public class SpriteDetectorHomography {


    public SpriteDetectorHomography() {

        Mat image = Imgcodecs.imread("C:\\Users\\S3108772\\IdeaProjects\\TibiaCore\\src\\main\\resources\\sprite_detection\\image.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat template = Imgcodecs.imread("C:\\Users\\S3108772\\IdeaProjects\\TibiaCore\\src\\main\\resources\\sprite_detection\\template.png", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        int minHessian = 400;

        FeatureDetector surf = FeatureDetector.create(FeatureDetector.SIFT);

        MatOfKeyPoint keyPointsObject = new MatOfKeyPoint();
        surf.detect(template, keyPointsObject);

        MatOfKeyPoint keyPointsScene = new MatOfKeyPoint();
        surf.detect(image, keyPointsScene);

        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);

        Mat descObject = new Mat();
        extractor.compute(template, keyPointsObject, descObject);

        Mat descScene = new Mat();
        extractor.compute(image, keyPointsScene, descScene);

        FlannBasedMatcher flannBasedMatcher = new FlannBasedMatcher();
        MatOfDMatch matches = new MatOfDMatch();
        flannBasedMatcher.match(descObject, descScene, matches);

        List<DMatch> matchesList = matches.toList();

        Double maxDist = 0.0;
        Double minDist = 100.0;

        for(int i = 0; i < descObject.rows(); i++){
            Double dist = (double) matchesList.get(i).distance;
            if(dist < minDist) minDist = dist;
            if(dist > maxDist) maxDist = dist;
        }

        System.out.println("-- Max dist : " + maxDist);
        System.out.println("-- Min dist : " + minDist);

        LinkedList<DMatch> goodMatches = new LinkedList<>();
        MatOfDMatch gm = new MatOfDMatch();

        for(int i = 0; i < descObject.rows(); i++){
            if(matchesList.get(i).distance < 3*minDist){
                goodMatches.addLast(matchesList.get(i));
            }
        }

        gm.fromList(goodMatches);

        Mat imgMatches = new Mat();
        Features2d.drawMatches(
                template,
                keyPointsObject,
                image,
                keyPointsScene,
                gm,
                imgMatches,
                new Scalar(255,0,0),
                new Scalar(0,0,255),
                new MatOfByte(),
                2);

        Imgcodecs.imwrite("output.png", imgMatches);
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new SpriteDetectorHomography();
    }
}
