
import java.awt.AWTEvent;
import java.awt.event.*;
import java.util.Enumeration;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.vp.*;

public class KeyBehavior extends ViewPlatformBehavior {

    //Movement variables for minimap view rotation and movement
    private static final int mmForward = 0;
    private static final int mmLeft = 1;
    private static final int mmBack = 2;
    private static final int mmRight = 3;

    //Camera movement variables
    private final static double moveAmount = 0.1; //Moves camera forward or backwards
    private final static double rotateAmount = Math.PI / 70; //Rotation amount in radians

    /*
     Vectors to move the player camera in the desired direction,
     based on the variables above.
     */
    private static final Vector3d forward = new Vector3d(0, 0, -moveAmount);
    private static final Vector3d backwards = new Vector3d(0, 0, moveAmount);
    private static final Vector3d left = new Vector3d(-moveAmount, 0, 0);
    private static final Vector3d right = new Vector3d(moveAmount, 0, 0);
    private static final Vector3d down = new Vector3d(0, -moveAmount, 0);
    private static final Vector3d up = new Vector3d(0, moveAmount, 0);

    // key names
    private int forwardKey = KeyEvent.VK_UP;
    private int backKey = KeyEvent.VK_DOWN;
    private int leftKey = KeyEvent.VK_LEFT;
    private int rightKey = KeyEvent.VK_RIGHT;
    private WakeupCondition keyPress;

    //Movement variables
    private MazeManager maze; // Used for checking possible moves
    private MiniMap mm; // Minimap view
    private int zOffset; // to stop movement down below the floor

    // Transformations used to move/rotate camera and birdseye
    private Transform3D t3d = new Transform3D();
    private Transform3D toMove = new Transform3D();
    private Transform3D toRot = new Transform3D();
    private Vector3d trans = new Vector3d();

    //Set key press to effect the main camera and minimap
    public KeyBehavior(MazeManager mazeMan, MiniMap minimap) {
        keyPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        maze = mazeMan;
        mm = minimap;
        zOffset = 0;
    }

    //Generated method by ViewPlatformBehavior class
    public void initialize() {
        wakeupOn(keyPress);
    }

    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] event;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                for (int i = 0; i < event.length; i++) {
                    if (event[i].getID() == KeyEvent.KEY_PRESSED)
                        processKeyEvent((KeyEvent) event[i]);
                }
            }
        }
        wakeupOn(keyPress);
    }
    //End of generated class

    //Determine whether to do a regular movement or alt movement
    private void processKeyEvent(KeyEvent eventKey) {
        int keyCode = eventKey.getKeyCode();

        //If alt is pressed, player can move up like flying
        if (eventKey.isAltDown()) // alt + forward/backwards
            altMove(keyCode);
        else
            standardMove(keyCode); //Regular forward/backwards movement and rotation
    }

    //Regular movement for player camera and minimap position icon
    private void standardMove(int keycode) {
        if (keycode == forwardKey) //Move forward
            moveCamera(forward, mmForward);
        else if (keycode == backKey) //Move backward
            moveCamera(backwards, mmBack);
        else if (keycode == leftKey) //Rotate camera left
            doRotateY(rotateAmount, mmLeft);
        else if (keycode == rightKey) //Rotate camera right
            doRotateY(-rotateAmount, mmRight);
    }

    private void altMove(int keycode) {
        if (keycode == backKey) { //Move down
            if (zOffset > 0) {
                doMove(down);
                zOffset--;
            }
        } else if (keycode == forwardKey) { //Move up
            doMove(up);
            zOffset++;
        } else if (keycode == leftKey)
            moveCamera(left, mmLeft); //Move left
        else if (keycode == rightKey)
            moveCamera(right, mmRight); //Move right
    }

    // Move classes

    private void moveCamera(Vector3d theMove, int dir)
    //Calculate next move and determine whether there is a wall or not
    {
        Point3d nextLoc = calcMove(theMove);
        //Checks to see if next move is a wall
        if (maze.canMoveTo(nextLoc.x, nextLoc.z)) {
            targetTG.setTransform(t3d);
            mm.setMove((int) Math.round(nextLoc.x), (int) Math.round(nextLoc.z)); //Sets birdseye to new coordinate
        } else // there is an obstacle
            mm.bangAlert(); //Alert is shown that you ran into a wall
    }

    //Calculate position of next move into a Point3D(x,y,z) object
    private Point3d calcMove(Vector3d theMove)
    //Calculates next move but does not update camera until wall check has been made
    {
        targetTG.getTransform(t3d); // targetTG is the ViewPlatform's transform
        toMove.setTranslation(theMove);
        t3d.mul(toMove);
        t3d.get(trans);
        return new Point3d(trans.x, trans.y, trans.z);
    }

    private void doMove(Vector3d theMove)
    // Move the player camera
    {
        targetTG.getTransform(t3d); //Get transformation
        toMove.setTranslation(theMove); //Set movement
        t3d.mul(toMove); //Set value of transformation to movement
        targetTG.setTransform(t3d); //Do the movement transformation
    }

    //Rotate camera
    private void doRotateY(double radians, int dir) {
        targetTG.getTransform(t3d); //Get transformation
        toRot.rotY(radians); //Set rotation
        t3d.mul(toRot); //Set value of transformation to rotation
        targetTG.setTransform(t3d); //Do the rotation transformation
    }

}
