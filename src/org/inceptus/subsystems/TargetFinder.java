/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inceptus.subsystems;

/**
 *
 * @author inceptus
 */
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCamera.ExposureT;
import edu.wpi.first.wpilibj.camera.AxisCamera.WhiteBalanceT;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.*;
import edu.wpi.first.wpilibj.image.Image;

/**
 * Identifies rectangular targets on back boards using Axis camera. Processing
 * images and getting target information are in separate methods to allow for
 * the processing to occur in its own thread if desired.
 * 
*/
/*
 * Team 2264 10.22.64.11 Admin; root Team 2265 10.22.65.11 Admin; root
 */
public class TargetFinder {

    private final int redLow = 200;
    private final int redHigh = 256;
    private final int greenLow = 0;
    private final int greenHigh = 100;
    private final int blueLow = 0;
    private final int blueHigh = 100;
    private final int bboxWidthMin = 400;
    private final int bboxHeightMin = 400;
    private final float inertiaXMin = .5f;//originally .32f
    private final float inertiaYMin = .25f;//originally .18f
    private final double ratioMin = 1;
    private final double ratioMax = 2;
    private final int camBrightness = 0;
    private final int camColor = 100;
    private final WhiteBalanceT camWhiteBalance = WhiteBalanceT.automatic;
    private final ExposureT camExposure = ExposureT.hold;
    public static final int IMAGE_WIDTH = 320;
    public static final int IMAGE_HEIGHT = 240;
    AxisCamera cam;
    private Target highTarget = Target.NullTarget;
    private Target target1 = Target.NullTarget;
    private Target target2 = Target.NullTarget;
    private Target target3 = Target.NullTarget;
    private Target target4 = Target.NullTarget;
    private CriteriaCollection boxCriteria;
    private CriteriaCollection inertiaCriteria;

    public TargetFinder() {
        System.out.println("TargetFinder() begin " + Timer.getFPGATimestamp());

        cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k320x240);
        cam.writeBrightness(camBrightness);
        cam.writeColorLevel(camColor);
        cam.writeWhiteBalance(camWhiteBalance);
        cam.writeExposureControl(camExposure);
        cam.writeMaxFPS(15);
        cam.writeExposurePriority(AxisCamera.ExposurePriorityT.none);
        cam.writeCompression(50);
        //System.out.println("TargetFinder() * " + cam.toString() + "[" + Timer.getFPGATimestamp());
        boxCriteria = new CriteriaCollection();
        inertiaCriteria = new CriteriaCollection();
        boxCriteria.addCriteria(NIVision.MeasurementType.IMAQ_MT_BOUNDING_RECT_WIDTH,
                30, bboxWidthMin, false);
        boxCriteria.addCriteria(NIVision.MeasurementType.IMAQ_MT_BOUNDING_RECT_HEIGHT,
                40, bboxHeightMin, false);
        inertiaCriteria.addCriteria(NIVision.MeasurementType.IMAQ_MT_NORM_MOMENT_OF_INERTIA_XX,
                0, inertiaXMin, true);
        inertiaCriteria.addCriteria(NIVision.MeasurementType.IMAQ_MT_NORM_MOMENT_OF_INERTIA_YY,
                0, inertiaYMin, true);
        Timer.delay(7);
    }

    private void addTarget(Target t) {
        // Fill the first empty target slot.
        if (target1.isNull()) {
            target1 = t;
        } else if (target2.isNull()) {
            target2 = t;
        } else if (target3.isNull()) {
            target3 = t;
        } else if (target4.isNull()) {
            target4 = t;
        }
    }

    public boolean processImage() {
        boolean debugWriteImages = true;
        boolean success = cam.freshImage();
        if (success) {
            try {
                System.out.println("In Try loop");
                ColorImage im = cam.getImage();
                System.out.println("Got image");
                if (debugWriteImages) {
                    im.write("image1.jpg");
                    System.out.println("Wrote color image");
                }
                BinaryImage thresholdIm = im.thresholdRGB(redLow, redHigh,
                        greenLow, greenHigh,
                        blueLow, blueHigh);
                if (debugWriteImages) {
                    thresholdIm.write("image2.jpg");
                    System.err.println("Wrote Threshold Image");
                }
                BinaryImage filteredBoxIm = thresholdIm.particleFilter(boxCriteria);
                ParticleAnalysisReport[] xparticles = filteredBoxIm.getOrderedParticleAnalysisReports();
                System.out.println(xparticles.length + " particles at " + Timer.getFPGATimestamp());
                BinaryImage filteredInertiaIm = filteredBoxIm.particleFilter(inertiaCriteria);
                ParticleAnalysisReport[] particles = filteredInertiaIm.getOrderedParticleAnalysisReports();
                System.out.println(particles.length + " particles at " + Timer.getFPGATimestamp());
                // Loop through targets, find highest one.
                // Targets aren't found yet.
                highTarget = Target.NullTarget;
                target1 = Target.NullTarget;
                target2 = Target.NullTarget;
                target3 = Target.NullTarget;
                target4 = Target.NullTarget;
                System.out.println("Targets created");
                double minY = IMAGE_HEIGHT; // Minimum y <-> higher in image.
                for (int i = 0; i < particles.length; i++) {
                    Target t = new Target(i, particles[i]);
                    if (t.ratio > ratioMin && t.ratio < ratioMax) {
                        addTarget(t);
                        if (t.centerY <= minY) {
                            highTarget = t;
                        }
                    }
                    System.out.println("Target " + i + ": (" + t.centerX + "," + t.centerY + ") Distance: " + getDistance(t));
                }
                System.out.println("Best target: " + highTarget.index);
                System.out.println("Distance to the target: " + getDistance(highTarget));
                if (debugWriteImages) {
                    filteredBoxIm.write("image3.jpg");
                    filteredInertiaIm.write("image4.jpg");
                    System.out.println("Wrote Images");
                }
                // Free memory from images.
                im.free();
                thresholdIm.free();
                filteredBoxIm.free();
                filteredInertiaIm.free();
            } catch (AxisCameraException ex) {
                System.out.println("Axis Camera Exception Gotten" + ex.getMessage());
                ex.printStackTrace();
            } catch (NIVisionException ex) {
                System.out.println("NIVision Exception Gotten - " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return success;
    }
//Method assumes the camera is looking at the rectangle straight on: can be adjusted later

    public double getDistance(Target t) {
        boolean debugMyProc = true;
        double result;
        
        if (debugMyProc)
        {
            writeDebug("t.rawBboxHeight=" + t.rawBboxHeight);
            writeDebug("Center x, y (" + t.centerX + "," + t.centerY + ")");
            writeDebug("Cornerr x, y (" + t.rawBboxCornerX + "," + t.rawBboxCornerY + ")");
        }
        result = 3185.6 / (t.rawBboxHeight * Math.tan(0.4101));
        return (result * 1.0125);
    }

    public Target getHighestTarget() {
        return highTarget;
    }

    public Target getTarget1() {
        return target1;
    }

    public Target getTarget2() {
        return target2;
    }

    public Target getTarget3() {
        return target3;
    }

    public Target getTarget4() {
        return target4;
    }
    private void writeDebug(String pMsg)
    {
        System.out.println(pMsg);
    }
}
