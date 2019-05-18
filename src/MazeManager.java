
import java.awt.*;
import java.io.*;
import java.awt.image.*;

import javax.vecmath.*;
import javax.media.j3d.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;

public class MazeManager {
    //Dimension variables
    private static final int LEN = 40; // max sides of maze plan (should be even)
    // the maze can be smaller than 40*40
    private static final double USER_HEIGHT = 2.0; // where the user's eyes are
    private static final int IMAGE_LEN = 240; // size of 2D maze image (should be a multiple of LEN)
    private static final int MINIMAP_MOVEMENT = IMAGE_LEN / LEN; //Movement length between player icon transformations


    // obstacle dimensions (for both block types)
    private final static float RADIUS = 0.5f;
    // for a block, RADIUS == length of a side/2
    private final static float HEIGHT = 3.0f;

    // Default colors for blocks in no texture is applied
    private final static Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private final static Color3f specular = new Color3f(1.0f, 1.0f, 1.0f);
    private final static Color3f blue = new Color3f(1.0f, 1.0f, 1.0f);
    private final static Color3f medgreen = new Color3f(1.0f, 1.0f, 1.0f);

    //Random textures every time you open the maze
    private final static String BLOCK_TEX = MazeOptions.walls[MazeOptions.mapSelection];
    private final static String CYL_TEX = MazeOptions.doors[MazeOptions.mapSelection];//"images/cobbles.jpg";

    private Appearance blockApp, block2App; // obstacle appearances

    private char[][] maze; // stores the input maze plan;
    /*
     * The coords are maze[Z]{X] i.e. x-coords along row, z-coords running down
     * Remember that the maze plan's y-coords have been mapped to 3D scene z-coords.
     */

    private int xStartPosn, zStartPosn; // the player's starting coordinate

    private BranchGroup mazeBG; // scene subgraph for the maze
    private BufferedImage mazeImg; // holds the 2D image for the maze

    public MazeManager(String fn) {
        initialiseVars();
        readFile(fn);
        buildMaze();
    }

    private void initialiseVars() {
        maze = new char[LEN][LEN]; //Holds chars from text file
        for (int z = 0; z < LEN; z++) // an empty maze
            for (int x = 0; x < LEN; x++)
                maze[z][x] = ' ';

        xStartPosn = LEN / 2; // default position (top row, in the middle)
        zStartPosn = 0;

        // initialise the two maze representations
        mazeBG = new BranchGroup();
        mazeImg = new BufferedImage(IMAGE_LEN, IMAGE_LEN, BufferedImage.TYPE_INT_ARGB);

        // the Appearance nodes used by all the blocks and cylinders
        blockApp = makeApp(blue, BLOCK_TEX); // blue texture for blocks
        block2App = makeApp(medgreen, CYL_TEX); // green texture for style blocks
    }

    private Appearance makeApp(Color3f colObs, String texFnm) {
        Appearance app = new Appearance();

        // mix the texture and the material colour
        TextureAttributes ta = new TextureAttributes();
        ta.setTextureMode(TextureAttributes.MODULATE);
        app.setTextureAttributes(ta);

        // load and set the texture
        System.out.println("Loading obstacle texture from " + texFnm);
        TextureLoader loader = new TextureLoader(texFnm, null);
        Texture2D texture = (Texture2D) loader.getTexture();
        app.setTexture(texture); // set the texture

        // add a colored material
        Material mat = new Material(colObs, black, colObs, specular, 150.f);
        mat.setLightingEnable(true);
        app.setMaterial(mat);
        return app;
    }

    private void readFile(String filename)
    // Initialise maze[][] by reading the maze plan from filename
    {
        System.out.println("Reading maze plan from " + filename);
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            char charLine[];
            int numRows = 0;
            //Iterate through textfile ti create char array of maze
            while ((numRows < LEN) && ((line = br.readLine()) != null)) {
                charLine = line.toCharArray();
                int x = 0;
                while ((x < LEN) && (x < charLine.length)) { // ignore any extra chars
                    maze[numRows][x] = charLine[x];
                    x++;
                }
                numRows++;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading maze plan from " + filename);
            System.exit(0);
        }
    }

    private void buildMaze()
	/*
	Create branchgroup and buffered image to represent maze
	's' = starting position
	'b' = regular block
	'c' = stylized block
	'e' = end block
	*/ {
        System.out.println("Building maze");

        char ch;
        Graphics g = (Graphics) mazeImg.createGraphics();
        g.setColor(Color.white);

        //Iterate through maze char array and initialize each block
        for (int z = 0; z < LEN; z++) {
            for (int x = 0; x < LEN; x++) {
                ch = maze[z][x];
                if (ch == 's') { // starting position
                    xStartPosn = x;
                    zStartPosn = z;
                    maze[z][x] = ' '; // clear cell
                } else if (ch == 'b') { // block
                    mazeBG.addChild(makeBlock(ch, x, z, blockApp));
                    drawMMBlock(g, x, z); //Draw minimap block
                } else if (ch == 'c') { // style block
                    mazeBG.addChild(makeBlock(ch, x, z, block2App));
                    drawMMBlock2(g, x, z); //Draw minimap block
                }
            }
        }
        g.dispose();
    }

    private TransformGroup makeBlock(char ch, int x, int z, Appearance app)
    // place an obstacle (block/style block) at (x,z) with appearance app
    {
        Primitive block;
        if (ch == 'b') //Main block
            block = new Box(RADIUS, HEIGHT / 2, RADIUS, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS,
                    app);
        else //Secondary block type
            block = new Box(RADIUS, HEIGHT / 2, RADIUS, Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS,
                    app);

        // position the obstacle so its base is resting on the floor at (x,z)
        TransformGroup position = new TransformGroup();
        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(x, HEIGHT / 2, z)); // move up
        position.setTransform(trans);
        position.addChild(block);
        return position;
    }

    //Draw minimap
    private void drawMMBlock(Graphics g, int i, int j)
    // draw a blue box in the 2D image
    {
        g.setColor(Color.blue);
        g.fillRect(i * MINIMAP_MOVEMENT, j * MINIMAP_MOVEMENT, MINIMAP_MOVEMENT, MINIMAP_MOVEMENT);
    }

    private void drawMMBlock2(Graphics g, int i, int j)
    // draw a green circle in the 2D image
    {
        g.setColor(Color.green);
        g.fillRect(i * MINIMAP_MOVEMENT, j * MINIMAP_MOVEMENT, MINIMAP_MOVEMENT, MINIMAP_MOVEMENT);
    }

    public BranchGroup getMaze()
    // called by SkyBox to get a reference to the 3D maze
    {
        return mazeBG;
    }

    public Vector3d getMazeStartPosn()
    // called by SkyBox
    {
        return new Vector3d(xStartPosn, USER_HEIGHT, zStartPosn);
    }

    public BufferedImage getMazeImage()
    // called by MiniMap to get a reference to the 2D maze
    {
        return mazeImg;
    }

    public Point getImageStartPosn()
    // called by MiniMap
    {
        return new Point(xStartPosn * MINIMAP_MOVEMENT, zStartPosn * MINIMAP_MOVEMENT);
    }

    public int getMMPos()
    // called by MiniMap. It will use MINIMAP_MOVEMENT to
    // convert a user's move into a move in terms of pixels
    {
        return MINIMAP_MOVEMENT;
    }

    //Checks to see if a collision is going to occur on the next move
    public boolean canMoveTo(double xCoord, double zCoord)
    // Does the next move (xCoord, zCoord) contain a wall? If not, move camera else throw wall message
    // Called by the KeyBehavior object to test a possible move.
    {
        //Exact camera position
        double xAxis = xCoord;
        double zAxis = zCoord;

        //Rounded coordinate block to calculate minimap position
        int x = (int) Math.round(xCoord);
        int z = (int) Math.round(zCoord);

        System.out.println("x-axis: " + x);
        System.out.println("z-axis: " + z);
        System.out.println("Double x-axis: " + xAxis);
        System.out.println("Double z-axis: " + zAxis);

        try {
            if ((x < 0) || (x >= LEN) || (z < 0) || (z >= LEN)) { //Outside of maze boundaries
                System.out.println(maze[z][x]);
                return true; // True, can always move outside of maze since there will be no walls
            }

            //Check 0.2 area around the player camera for walls.
            //0.2 is added so that camera does not clip into walls
            //Check for 'b' blocks
            if ((maze[(int) Math.round(zCoord + 0.3)][(int) Math.round(xCoord + 0.3)] == 'b')
                    || (maze[(int) Math.round(zCoord - 0.2)][(int) Math.round(xCoord - 0.2)] == 'b')
                    || (maze[(int) Math.round(zCoord + 0.2)][(int) Math.round(xCoord - 0.2)] == 'b')
                    || (maze[(int) Math.round(zCoord - 0.2)][(int) Math.round(xCoord + 0.2)] == 'b')) {
                System.out.println("Collision block: b");
                return false; //False, cannot move there
                //Check for 'c' blocks
            } else if ((maze[(int) Math.round(zCoord + 0.3)][(int) Math.round(xCoord + 0.3)] == 'c')
                    || (maze[(int) Math.round(zCoord - 0.3)][(int) Math.round(xCoord + 0.3)] == 'c')
                    || (maze[(int) Math.round(zCoord + 0.3)][(int) Math.round(xCoord - 0.3)] == 'c')
                    || (maze[(int) Math.round(zCoord - 0.3)][(int) Math.round(xCoord - 0.3)] == 'c')) {
                System.out.println("Collision block: c");
                return false; // False, location is occupied by block
                //Check for end block 'e'
            } else if ((maze[(int) Math.round(zCoord + 0.3)][(int) Math.round(xCoord + 0.3)] == 'e')
                    || (maze[(int) Math.round(zCoord - 0.3)][(int) Math.round(xCoord + 0.3)] == 'e')
                    || (maze[(int) Math.round(zCoord + 0.3)][(int) Math.round(xCoord - 0.3)] == 'e')
                    || (maze[(int) Math.round(zCoord - 0.3)][(int) Math.round(xCoord - 0.3)] == 'e')) {
                new EndScreen(); //Completed maze, show endscreen dialog box
                System.out.println("End Block");
                return false;
            }
            //If endblock is not specified, leaving the maze is also considered winning
        } catch (Exception ex) {
            new EndScreen();
            System.out.println("Completed the maze!!!");
        }
        return true;

    }
}
