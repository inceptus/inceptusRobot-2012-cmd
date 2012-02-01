package org.inceptus;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.inceptus.commands.drive.DriveWithJoystick;
import org.inceptus.commands.drive.GyroBalanceCommand;

public class OI {

    private Joystick driveController;
    
    private static OI instance = null;

    private OI() {
        this.driveController = new Joystick(RobotMap.driveJoyPort);
        
        JoystickButton balance = new JoystickButton(driveController, RobotMap.balanceButton);
        
        balance.whileHeld(new GyroBalanceCommand());
        balance.whenReleased(new DriveWithJoystick());
        
    }

    public static OI getInstance() {
        if (instance == null)
            instance = new OI();
        return instance;
    }

    public Joystick getDriveController() {
        return driveController;
    }
}
