import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;


public class TexturedFloor {
    private final static int FLOOR_LENGTH = 160;  // should be even
    private final static String FLOOR_IMAGE = MazeOptions.grounds[MazeOptions.mapSelection];
    private final static int STEP = 2;

    private BranchGroup floorBG;

    public TexturedFloor() {
        ArrayList coordinates = new ArrayList();
        floorBG = new BranchGroup();

        // create coords for the quad
        for (int z = FLOOR_LENGTH / 2; z >= (-FLOOR_LENGTH / 2) + STEP; z -= STEP) {  // front to back
            for (int x = -FLOOR_LENGTH / 2; x <= (FLOOR_LENGTH / 2) - STEP; x += STEP)  // left to right
                createCoords(x, z, coordinates);
        }

        Vector3f upNorm = new Vector3f(0.0f, 1.0f, 0.0f);   // pointing upwards
        floorBG.addChild(new TexturedPlane(coordinates, FLOOR_IMAGE, upNorm));
    }

    private void createCoords(int x, int z, ArrayList coordinates) {
        // points created in counter-clockwise order, from front left

        Point3f point1 = new Point3f(x, 0.0f, z);
        Point3f point2 = new Point3f(x + STEP, 0.0f, z);
        Point3f point3 = new Point3f(x + STEP, 0.0f, z - STEP);
        Point3f point4 = new Point3f(x, 0.0f, z - STEP);
        coordinates.add(point1);
        coordinates.add(point2);
        coordinates.add(point3);
        coordinates.add(point4);
    }

    public BranchGroup getBG() {
        return floorBG;
    }

}
