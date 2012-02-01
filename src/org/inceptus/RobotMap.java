package org.inceptus;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    //Joysticks
    public final static int driveJoyPort = 1;
    public final static int armJoyPort = 2;
    //Drive Motors
    public final static int leftFrontDrivePort = 2;
    public final static int rightFrontDrivePort = 3;
    public final static int leftRearDrivePort = 4;
    public final static int rightRearDrivePort = 1;
    //Conveyor motors
    public final static int lowerConveyorDrivePort = 5;
    public final static int upperConveyorDrivePort = 6;
    //Shooting motor
    public final static int shootingWheelDrivePort = 7;
    //Threshold values to eliminate accidental driving
    public final static double magnitudeThreshold = .1;
    //Scale the magnitude
    public final static double magnitudeMin = .2;
    public final static double magnitudeMax = .7;
    //Buttons
    public final static int balanceButton = 5;
    //Slow twist
    public final static double twistMultiplier = .4;
}
