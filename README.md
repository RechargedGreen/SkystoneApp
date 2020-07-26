# SkystoneApp
7236's 2019-2020 code for Skystone, written in [Kotlin](https://kotlinlang.org/).

Special thanks to [Gluten Free's](https://bitbucket.org/PeterTheEarthling/ftcroverruckus/src/master/) code for state machine and hardware architecture inspiration, [Acme Robotic's](https://github.com/acmerobotics/relic-recovery) code for vision inspiration and FROGBots' [Rev Extensions 2](https://github.com/OpenFTC/RevExtensions2) as a guide for bulk reads.

The code is based on a non linear state machine architecture using 3 passive wheel odometry modules for localization and a costum Pure Pursuit for autonomous navigation. This README will explain the function of each fundamental class or package.

## Code Highlights
- [Teleop](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/leaguebot/teleop/LeagueTeleOp.kt)
 
We use a large state machine for the control scheme of the robot. The hardware classes will then use seperate state machines to run to the desired state. For teleop controls we had four goals. Eliminate any manual mechanism control to reduce driver error and decrease need to dedicate limited mental bandwidth to the robot, eliminate any uneeded coordination between drivers to reduce driver error and increase flexibility, to centralize controls into the joysticks, bumpers and triggers as much as possible to avoid needing to move fingers or thumbs around to control common decisions and to make it easily possible to practice with only one driver.
  
  - [Autonomous](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/leaguebot/autos/)
 
 **OdometryBeingDumbAuto.kt** is a secondary parking only auto. **FourStone.kt** is the primary main auto.
 
 The [companion object](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/leaguebot/autos/FourStone.kt#L34) contains variables able to be tuned by Acme Robotic's [FTCDashboard](https://github.com/acmerobotics/ftc-dashboard)
 
 The [grabY](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/leaguebot/autos/FourStone.kt#L91) property along with the [cycle](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/leaguebot/autos/FourStone.kt#L89) variable automates choosing the location to grab a new stone stone

- Odometry

In order to accurately and quickly drive around the field we used a passive [three wheel]() odometry system. We tested [two wheel]() odometry but found the latency and different timing of the gyro introduced an unnacceptabe amount of x/y drift into the system. We store the position as [static variables](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/movement/basicDriveFunctions/DrivePosition.kt) to make it easy to use it in state machines in any class. We also have properties to automatically mirrior positions between alliances.

- [Pure Pursuit](https://github.com/RechargedGreen/SkystoneApp/blob/master/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/movement/PurePursuit.kt)

We used a heavily costumized version of the Pure Pursuit algorithm shown on [Gluten Free's YouTube channel](https://www.youtube.com/user/ElectricWizzz). We tried [Road Runner](https://github.com/acmerobotics/road-runner) from Acme robotics, but while going fast in auto tuning the trajectry followers took up too much time. Pure Pursuit paths are very imprecise, but they worked for our usage.

- [DriveMovment.kt](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/movement/basicDriveFunctions/DriveMovement.kt)

DriveMovement contains all the core movement of the drive train. It contains shared code for [gamepad control](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/movement/basicDriveFunctions/DriveMovement.kt#L65), and [field centric](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/movement/basicDriveFunctions/DriveMovement.kt#L48) movement for autonomous.

- [SimpleMotion](https://github.com/RechargedGreen/SkystoneApp/blob/master/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/movement/SimpleMotion.kt) 

SimpleMotion contains go to position algorithms at the core of most autonomous movement

- [bulkLib](SkystoneApp/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/bulkLib/
) 

bulkLib contains all our custom hardware classes

- Geometry classes

[Geometry.kt](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/field/Geometry.kt) contains the commonly used Pose and Point classes. [Angle](TeamCode/src/main/java/org/firstinspires/ftc/teamcode/movement/Angle.kt) contains a wrapper for holding radians and degrees in the same class.

