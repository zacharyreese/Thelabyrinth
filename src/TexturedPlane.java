import java.util.ArrayList;
import javax.vecmath.*;
import javax.media.j3d.*;

import com.sun.j3d.utils.image.*;


public class TexturedPlane extends Shape3D {
    private QuadArray plane;
    private int numPoints;

    public TexturedPlane(ArrayList coordinates, String fnm, Vector3f normal) {
        numPoints = coordinates.size();
        plane = new QuadArray(numPoints,
                GeometryArray.COORDINATES |
                        GeometryArray.TEXTURE_COORDINATE_2 |
                        GeometryArray.NORMALS);
        createGeometry(coordinates, normal);
        createAppearance(fnm);
    }

    private void createGeometry(ArrayList coordinates, Vector3f normal) {
        // set coordinates
        Point3f[] points = new Point3f[numPoints];
        coordinates.toArray(points);
        plane.setCoordinates(0, points);

        // assign texture coords to each quad
        // counter-clockwise, from bottom left
        TexCoord2f[] tcoords = new TexCoord2f[numPoints];
        for (int i = 0; i < numPoints; i = i + 4) {
            tcoords[i] = new TexCoord2f(0.0f, 0.0f);
            tcoords[i + 1] = new TexCoord2f(1.0f, 0.0f);
            tcoords[i + 2] = new TexCoord2f(1.0f, 1.0f);
            tcoords[i + 3] = new TexCoord2f(0.0f, 1.0f);
        }
        plane.setTextureCoordinates(0, 0, tcoords);

        for (int i = 0; i < numPoints; i++)
            plane.setNormal(i, normal);

        setGeometry(plane);
    }

    private void createAppearance(String mazeFile) {
        Appearance app = new Appearance();

        Material mat = new Material();
        mat.setLightingEnable(true);
        app.setMaterial(mat);

        System.out.println("Loading texture for plane from " + mazeFile);
        TextureLoader loader = new TextureLoader(mazeFile, null);
        Texture2D text2d = (Texture2D) loader.getTexture();
        app.setTexture(text2d);

        TextureAttributes ta = new TextureAttributes();
        ta.setTextureMode(TextureAttributes.MODULATE);
        app.setTextureAttributes(ta);

        setAppearance(app);
    }

} 