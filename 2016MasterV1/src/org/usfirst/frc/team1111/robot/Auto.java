package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;
import variables.Sensors;
import variables.Sensors.Encoders;

public class Auto {

	static class Movement {

		public static final double TEMP_DEFAULT_AUTO_SPEED = Motors.HALF_POWER;

		public static void driveDriveMotors(double power)
		{
			Motors.motorDriveBackLeft.set(power);
			Motors.motorDriveBackRight.set(power);
			Motors.motorDriveFrontLeft.set(-1 * power);
			Motors.motorDriveFrontRight.set(-1 * power);
		}

		public static void driveToDistance(double distance)
		{
			if (Sensors.Encoders.resetEncoders)
			{
				Encoders.resetEncoders();
				Encoders.resetEncoders = false;
			}

			if (Encoders.encoderDriveLeft.getDistance() < distance)
				driveDriveMotors(TEMP_DEFAULT_AUTO_SPEED);
			else
				stopDriveMotors();
		}

		public static void orient(double targetAngle)
		{
			if (Sensors.navX.getYaw() > targetAngle + 5.0)
				turnInPlace("left", TEMP_DEFAULT_AUTO_SPEED);
			else if (Sensors.navX.getYaw() < targetAngle - 5.0)
				turnInPlace("right", TEMP_DEFAULT_AUTO_SPEED);
			else
				stopDriveMotors();
		}

		public static void stopDriveMotors()
		{
			Motors.motorDriveBackLeft.set(Motors.NO_POWER);
			Motors.motorDriveBackRight.set(Motors.NO_POWER);
			Motors.motorDriveFrontLeft.set(Motors.NO_POWER);
			Motors.motorDriveFrontRight.set(Motors.NO_POWER);
		}

		public static void turnInPlace(String direction, double speed)
		{
			if (direction.equals("left"))
			{
				Robot.subState = "Turning Left...";

				Motors.motorDriveFrontLeft.set(speed);
				Motors.motorDriveBackLeft.set(speed);
				Motors.motorDriveFrontRight.set(-speed);
				Motors.motorDriveBackRight.set(-speed);
			} else if (direction.equals("right"))
			{
				Robot.subState = "Turning Right...";

				Motors.motorDriveFrontRight.set(speed);
				Motors.motorDriveBackRight.set(speed);
				Motors.motorDriveFrontLeft.set(-speed);
				Motors.motorDriveBackLeft.set(-speed);
			}
		}

	}

	private static final double ANGLE_TO_GOAL = 0;
	private static final double ANGLE_TO_SHOOTING_SPOT = 0;
	private static final double DISTANCE_ACROSS_LOW_BAR = 0;
	private static final double DISTANCE_TO_SHOOTING_SPOT = 0;

	static int progress = 0;
	//TODO Shoot subversions, what can we go over, what will mess with encoders, what is the same? And can we do anything else?
	public static void lowBar()
	{
		if(Timer.getMatchTime() < 5.0)
		{
			if(Motors.motorArm.getEncPosition() > /*Temp ArmPos*/ -1795)
				Motors.motorArm.set(Motors.ARM_POWER);
			else if(Motors.motorArm.getEncPosition() < -1805)
				Motors.motorArm.set(-Motors.ARM_POWER);
			else
				Motors.motorArm.set(Motors.NO_POWER);
		}else
			Motors.motorArm.set(Motors.NO_POWER);
		//else
			//Movement.stopDriveMotors();
	}
	
	public static void lowBarShoot()//Includes shooting
	{

		if(progress == 0)
			if(Sensors.Encoders.encoderDriveLeft.get() < DISTANCE_ACROSS_LOW_BAR)
				Movement.driveToDistance(DISTANCE_ACROSS_LOW_BAR);
			else
				progress++;
		else if(progress == 1)
		{
			Sensors.navX.reset();
			progress++;
		}else if(progress == 2)
			if(Sensors.navX.getYaw() < ANGLE_TO_SHOOTING_SPOT - 5 || Sensors.navX.getYaw() > ANGLE_TO_SHOOTING_SPOT + 5)
				Movement.orient(ANGLE_TO_SHOOTING_SPOT);
			else
				progress++;
		else if(progress == 3)
		{
			Sensors.Encoders.resetEncoders();
			progress++;
		}else if(progress == 4)
			if(Sensors.Encoders.encoderDriveLeft.get() < DISTANCE_TO_SHOOTING_SPOT)
				Movement.driveToDistance(DISTANCE_TO_SHOOTING_SPOT);
			else
				progress++;
		else if(progress == 5)
		{
			Sensors.navX.reset();
			progress++;
		}else if(progress == 6)
			if(Sensors.navX.getYaw() < ANGLE_TO_GOAL - 5.0 || Sensors.navX.getYaw() > ANGLE_TO_GOAL + 5.0)
				Movement.orient(ANGLE_TO_GOAL);
			else
				progress++;
		else if(progress == 7)
			shoot();
	}

	public static void moat()
	{
		if(Timer.getMatchTime() <= 3.0)//TODO Better timings.
		{
			Motors.motorDriveBackLeft.set(-1);
		Motors.motorDriveBackRight.set(1);
		Motors.motorDriveFrontLeft.set(-1);
		Motors.motorDriveFrontRight.set(1);
	}
		else 
			Movement.stopDriveMotors();
	}

	public static void ramparts()//TODO Better timings.
	{
		if(Timer.getMatchTime() <= 3.0){
			Motors.motorDriveBackLeft.set(-1);
		Motors.motorDriveBackRight.set(1);
		Motors.motorDriveFrontLeft.set(-1);
		Motors.motorDriveFrontRight.set(1);
	}
		else 
			Movement.stopDriveMotors();
	}

	public static void roughTerrain()//TODO Better timings.
	{
		if(Timer.getMatchTime() <= 2.25)
		{
			Motors.motorDriveBackLeft.set(-1);
		Motors.motorDriveBackRight.set(1);
		Motors.motorDriveFrontLeft.set(-1);
		Motors.motorDriveFrontRight.set(1);
		
		SmartDashboard.putNumber("Back Right Output Voltage", Motors.motorDriveBackRight.getOutputVoltage());
		SmartDashboard.putNumber("Back Left Output Voltage", Motors.motorDriveBackLeft.getOutputVoltage());
		SmartDashboard.putNumber("Front Right Output Voltage", Motors.motorDriveFrontRight.getOutputVoltage());
		SmartDashboard.putNumber("Front Left Output Voltage", Motors.motorDriveFrontLeft.getOutputVoltage());
		SmartDashboard.putNumber("Time", Timer.getMatchTime());
		
	}
		else 
			Movement.stopDriveMotors();
	}

	static Double startTime = 0.0;

	private static void shoot()//TODO bring the shooting setup in from Operator
	{
		//		if(startTime == 0.0)
		//				startTime = Timer.getMatchTime();
		//		if(Timer.getMatchTime() - startTime < 3.5)//TODO better spinup time
		//			Motors.motorShooter.set(Motors.SHOOTER_POWER);
		//		else
		//		{
		//			Motors.motorInnerIntake.set(Motors.INNER_INTAKE_POWER);
		//			progress++;
		//		}
	}
}