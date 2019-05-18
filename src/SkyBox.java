
import javax.swing.*;
import java.awt.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.image.*;


public class SkyBox extends JPanel {
    private static final int PWIDTH = 512;   // size of panel
    private static final int PHEIGHT = 512;
    private static final int BOUNDSIZE = 100;  // larger than world
    private static final String SKY_TEX = MazeOptions.skies[MazeOptions.mapSelection];  // sky texture

    private SimpleUniverse su;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;   // for environment nodes

    private MazeManager mazeMan;      // maze manager
    int selection = MazeOptions.mapSelection;

    public SkyBox(MazeManager maze, MiniMap minimap)
    // construct the scene and the main camera
    {
        mazeMan = maze;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();
        su = new SimpleUniverse(canvas3D);

        createSceneGraph();
        prepareViewPoint(minimap);

        su.addBranchGraph(sceneBG);
    }


    void createSceneGraph()
    // initilise the scene
    {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();     // add the lights
        addBackground();  // add the sky

        // add the textured floor
        TexturedFloor floor = new TexturedFloor();
        sceneBG.addChild(floor.getBG());
        sceneBG.addChild(mazeMan.getMaze());  // add maze, using MazeManager
        sceneBG.compile();   // fix the scene
    }


    private void lightScene()
    // *No* ambient light, 2 directional lights
    {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        // Set up the ambient light
        AmbientLight ambientLightNode = new AmbientLight(white);
        ambientLightNode.setInfluencingBounds(bounds);
        // sceneBG.addChild(ambientLightNode);   // ambient commented out

        // Set up the directional lights
        Vector3f light1D = new Vector3f(-1.0f, -1.0f, -1.0f);
        // left, down, backwards
        Vector3f light2D = new Vector3f(1.0f, -1.0f, 1.0f);
        // right, down, forwards

        DirectionalLight light1 =
                new DirectionalLight(white, light1D);
        light1.setInfluencingBounds(bounds);
        sceneBG.addChild(light1);

        DirectionalLight light2 =
                new DirectionalLight(white, light2D);
        light2.setInfluencingBounds(bounds);
        sceneBG.addChild(light2);
    }


    private void addBackground()
    // Add backdrop painted onto a inward facing sphere.
    // No use made of Background.
    // Seems more reliable on some older machines (?)
    {
        System.out.println("Loading sky texture: " + SKY_TEX);
        TextureLoader tex = new TextureLoader(SKY_TEX, null);

        // create an appearance and assign the texture
        Appearance app =  new Appearance();
        app.setTexture( tex.getTexture() );

        Sphere sphere = new Sphere(75.0f,    // radius to extend to edge of scene
                Sphere.GENERATE_NORMALS_INWARD |
                        Sphere.GENERATE_TEXTURE_COORDS, 4, app);   // default divs = 15

        String topImg = "";
        String leftImg = "";
        String rightImg = "";
        String backImg = "";
        String frontImg = "";
        if(selection == 0){
            topImg = SKY_TEX;
            leftImg = SKY_TEX;
            rightImg = SKY_TEX;
            backImg = SKY_TEX;
            frontImg = SKY_TEX;
        }else if(selection == 1){
            topImg = SKY_TEX;
            leftImg = "images/sky2ice.jpg";
            rightImg = "images/sky2forest.jpg";
            backImg = "images/bubbles.jpg";
            frontImg = "images/bubbles.jpg";
        }else{
            topImg = SKY_TEX;
            leftImg = SKY_TEX;
            rightImg = SKY_TEX;
            backImg = SKY_TEX;
            frontImg = SKY_TEX;
        }

        Box box = new Box(50f , 50f , 50f , Primitive.GENERATE_TEXTURE_COORDS | Primitive.GENERATE_NORMALS_INWARD, new Appearance());
        setFaceTexture(Box.FRONT,   new TextureLoader(frontImg, null).getTexture() , box);
        setFaceTexture(Box.LEFT,    new TextureLoader(leftImg, null).getTexture() , box);
        setFaceTexture(Box.RIGHT,   new TextureLoader(rightImg, null).getTexture() , box);
        setFaceTexture(Box.BACK,    new TextureLoader(backImg, null).getTexture() , box);
        setFaceTexture(Box.TOP,     new TextureLoader(topImg, null).getTexture() , box);
        setFaceTexture(Box.BOTTOM,  new TextureLoader(SKY_TEX, null).getTexture() , box);
        //setFaceTexture();
        sceneBG.addChild( box );
    } // end of addBackground()

    private void setFaceTexture(int faceID, Texture tex , Box b) {
        Appearance appearance = new Appearance();
        tex.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
        tex.setBoundaryModeT(Texture.CLAMP_TO_EDGE);
        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_FRONT);
        appearance.setPolygonAttributes(pa);
        appearance.setTexture(tex);
        b.getShape(faceID).setAppearance(appearance);
    }

    private void prepareViewPoint(MiniMap minimap) {
        // adjust viewpoint parameters
        View userView = su.getViewer().getView();
        userView.setFieldOfView(Math.toRadians(90.0));  // wider FOV
        userView.setBackClipDistance(20);      // can see a long way
        userView.setFrontClipDistance(0.05);   // can see close things

        ViewingPlatform vp = su.getViewingPlatform();

        // add a spotlight to viewpoint
        PlatformGeometry pg = new PlatformGeometry();
        pg.addChild(makeSpot());
        vp.setPlatformGeometry(pg);

        // fix starting position and orientation of viewpoint
        TransformGroup steerTG = vp.getViewPlatformTransform();
        initViewPosition(steerTG);

        // set up keyboard controls
        KeyBehavior keybeh = new KeyBehavior(mazeMan, minimap);
        keybeh.setSchedulingBounds(bounds);
        vp.setViewPlatformBehavior(keybeh);
    }


    private void initViewPosition(TransformGroup steerTG)
    // rotate and move the viewpoint
    {
        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);
        Transform3D toRot = new Transform3D();
        toRot.rotY(-Math.PI);
        // rotate 180 degrees around Y-axis, so facing along positive z-axis

        t3d.mul(toRot);
        t3d.setTranslation(mazeMan.getMazeStartPosn());  // place at maze start
        steerTG.setTransform(t3d);
    }


    private SpotLight makeSpot()
    // a spotlight to help the user see in the (relative) darkness
    {
        SpotLight spot = new SpotLight();
        spot.setPosition(0.0f, 0.5f, 0.0f);      // a bit above the user
        spot.setAttenuation(0.0f, 1.2f, 0.0f);   // linear attentuation
        spot.setSpreadAngle((float) Math.toRadians(30.0));  // smaller angle
        spot.setConcentration(5.0f);            // reduce strength quicker
        spot.setInfluencingBounds(bounds);
        return spot;
    }
}
