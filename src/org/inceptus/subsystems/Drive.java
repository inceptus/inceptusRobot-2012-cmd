/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inceptus.subsystems;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.inceptus.RobotMap;

/**
 *
 * @author inceptus
 */
public class Drive extends Subsystem {

    private static Drive instance = null;
    
    private SpeedController leftFront;
    private SpeedController rightFront;
    private SpeedController leftRear;
    private SpeedController rightRear;
    
    private RobotDrive robotDrive;
    //accelerometer
    private ADXL345_I2C accelerometer;
    //Gyro
    private Gyro gyro;
    private Drive() {
        this.leftFront = new Jaguar(RobotMap.leftFrontDrivePort);
        this.rightFront = new Jaguar(RobotMap.rightFrontDrivePort);
        this.leftRear = new Jaguar(RobotMap.leftRearDrivePort);
        this.rightRear = new Jaguar(RobotMap.rightRearDrivePort);
        
        this.robotDrive = new RobotDrive(leftFront, leftRear, rightFront, rightRear);
        this.robotDrive.setSafetyEnabled(false);
        this.robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, false);
        this.robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
        this.robotDrive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        this.robotDrive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
        
        //create the accelerometer object
        //this.accelerometer = new ADXL345_I2C(1, ADXL345_I2C.DataFormat_Range.k16G);
        // create the gyro object
        //this.gyro = new Gyro(RobotMap.DEFAULT_ANNALOG_SLOT, RobotMap.ANALOG_CHANNEL_GYRO);
    }
    
    public static Drive getInstance() {
        if (instance == null)
            instance = new Drive();
        return instance;
    }
    
    public void driveWithJoystick(Joystick joystick) {
        //throttle
        double T = joystick.getRawAxis(3);
        //Adjust to 0-1 range
        T = ((T+1)/2);
        //Scale the magnitude down to not overpower the motors
        T = RobotMap.magnitudeMin + (T * (RobotMap.magnitudeMax - RobotMap.magnitudeMin));
        //
        double x = joystick.getRawAxis(1);
        double y = joystick.getRawAxis(2);
        double rot = joystick.getRawAxis(4);

        y = y * -1;

        x = x * (1 - T);
        y = y * (1 - T);
        rot = rot * RobotMap.twistMultiplier;

        this.robotDrive.mecanumDrive_Cartesian(x, y, rot, 0.0);
        //System.out.println(this.accelerometer.getAcceleration(ADXL345_I2C.Axes.kY));
        
    }
    public void balance(){
        /*final double ratio = .02;
        double angle = this.gyro.getAngle();
        double speed = (angle* ratio);
        System.out.println(angle + "\t" + speed);
        if (angle >= 5 &&  angle <= -5){
            this.robotDrive.mecanumDrive_Cartesian(0.0, 0.0, 0.0, 0.0);          
        }
        else if(angle <= 35 && angle >= -35){
            this.robotDrive.mecanumDrive_Cartesian(0.0, -speed, 0.0, 0.00);
        }
        else{
            this.robotDrive.stopMotor();
        }*/
    }
//    public void balance(){
//        double angle = this.gyro.getAngle();
//        System.out.println(angle);
//        if (angle >= 5 && angle <= 12.5){
//            this.robotDrive.mecanumDrive_Cartesian(0.0, -0.1, 0.0, 0.0);
//        }
//        else if (angle <= -5 && angle >= -12.5){
//            this.robotDrive.mecanumDrive_Cartesian(0.0, 0.1, 0.0, 0.0);
//        }
//        else if (angle > 12.5 && angle <= 15){
//            this.robotDrive.mecanumDrive_Cartesian(0.0, -0.30, 0.0, 0.0);
//        }
//        else if (angle < -12.5 && angle >= -15){
//            this.robotDrive.mecanumDrive_Cartesian(0.0, 0.30, 0.0, 0.0);
//        }
//        else if (angle > 15){
//            this.robotDrive.mecanumDrive_Cartesian(0.0, -0.35, 0.0, 0.0);
//        }
//        else if (angle < -15){
//            this.robotDrive.mecanumDrive_Cartesian(0.0, 0.35, 0.0, 0.0);
//        }
//        else{
//            this.robotDrive.stopMotor();
//        }
//    }
    
    public void resetGyro(){
        this.gyro.reset();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}