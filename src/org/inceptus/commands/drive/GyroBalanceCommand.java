/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.inceptus.commands.drive;

import org.inceptus.commands.CommandBase;
import org.inceptus.subsystems.Drive;

/**
 *
 * @author inceptus
 */
public class GyroBalanceCommand extends CommandBase {
    private Drive drive = Drive.getInstance();
    
    public GyroBalanceCommand() {
        requires(drive);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        drive.balance();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
