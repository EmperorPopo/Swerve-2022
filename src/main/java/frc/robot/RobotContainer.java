// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Button;
import frc.robot.commands.DefaultDriveCommand;
import frc.robot.commands.DriveForward;
import frc.robot.commands.TestSequencial;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.DrivetrainSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  public static DrivetrainSubsystem m_drivetrainSubsystem = new DrivetrainSubsystem();
  public static IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  public static ShooterSubsystem shooterSubsystem = new ShooterSubsystem();
  public static ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  private final XboxController m_controller = new XboxController(0);
  private final XboxController m_controller2 = new XboxController(1);
  //public static TurretSubsystem m_TurretSubsystem = new TurretSubsystem();


  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    
    // Set up the default command for the drivetrain.
    // The controls are for field-oriented driving:
    // Left stick Y axis -> forward and backwards movement
    // Left stick X axis -> left and right movement
    // Right stick X axis -> rotation
    m_drivetrainSubsystem.setDefaultCommand(new DefaultDriveCommand(
            m_drivetrainSubsystem,
            () -> -modifyAxis(m_controller.getLeftY() / 1.5) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
            () -> -modifyAxis(m_controller.getLeftX() / 1.5) * DrivetrainSubsystem.MAX_VELOCITY_METERS_PER_SECOND,
            () -> -modifyAxis(m_controller.getRightX() / 1.5) * DrivetrainSubsystem.MAX_ANGULAR_VELOCITY_RADIANS_PER_SECOND
    ));
  

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Back button zeros the gyroscope
    new Button(m_controller::getBackButton)
            // No requirements because we don't need to interrupt anything
            .whenPressed(m_drivetrainSubsystem::zeroGyroscope);

    new Button(m_controller::getRightBumper)
            .whenPressed(intakeSubsystem::turnOn);
    new Button(m_controller::getRightBumper)
            .whenPressed(shooterSubsystem::feedIntake);
    new Button(m_controller::getRightBumper)
            .whenReleased(intakeSubsystem::turnOff);
    new Button(m_controller::getRightBumper)
            .whenReleased(shooterSubsystem::zero);

    new Button(m_controller::getLeftBumper)
            .whenPressed(intakeSubsystem::reverse);
    new Button(m_controller::getLeftBumper)
            .whenPressed(shooterSubsystem::feedIntake);
    new Button(m_controller::getLeftBumper)
            .whenReleased(intakeSubsystem::reverse);
    new Button(m_controller::getLeftBumper)
            .whenReleased(shooterSubsystem::zero);


    new Button(m_controller::getXButton)
            .whenPressed(shooterSubsystem::shoot);
    new Button(m_controller::getXButton)
            .whenReleased(shooterSubsystem::zero);

    new Button(m_controller::getBButton)
            .whenPressed(shooterSubsystem::reverse);
    new Button(m_controller::getBButton)
            .whenReleased(shooterSubsystem::zero);

    new Button(m_controller::getYButton)
            .whenPressed(shooterSubsystem::feedIntake);
    new Button(m_controller::getYButton)
            .whenReleased(shooterSubsystem::zero);

    new Button(m_controller2::getRightBumper)
            .whenPressed(climbSubsystem::moveUp);
    new Button(m_controller2::getLeftBumper)
            .whenPressed(climbSubsystem::moveDown);
    new Button(m_controller2::getRightBumper)
            .whenReleased(climbSubsystem::zero);
    new Button(m_controller2::getLeftBumper)
            .whenReleased(climbSubsystem::zero);

    new Button(m_controller2::getAButton)
            .whenPressed(climbSubsystem::turn);
    new Button(m_controller2::getBButton)
            .whenPressed(climbSubsystem::straight);


    }
          

          
    //new Button(m_controller::getAButton)
            // No requirements because we don't need to interrupt anything
            //.whileHeld(m_TurretSubsystem.TurretSubsystem());
    //new Button(m_controller::getBButton)
            // No requirements because we don't need to interrupt anything
            //.whileHeld(m_TurretSubsystem::MoveTurretRight);        
  

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return new TestSequencial(m_drivetrainSubsystem);
  }

  private static double deadband(double value, double deadband) {
    if (Math.abs(value) > deadband) {
      if (value > 0.0) {
        return (value - deadband) / (1.0 - deadband);
      } else {
        return (value + deadband) / (1.0 - deadband);
      }
    } else {
      return 0.0;
    }
  }

  private static double modifyAxis(double value) {
    // Deadband
    value = deadband(value, 0.05);

    // Square the axis
    value = Math.copySign(value * value, value);

    return value;
  }
}
