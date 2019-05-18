
import java.awt.*;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;

public class MiniMap extends JPanel {
    private static final int PWIDTH = 240; // size of panel
    private static final int PHEIGHT = 240;

    private static final int NUM_DIRS = 4;
    // these dirs have counter-clockwise ordering when viewed from above
    private static final int FORWARD = 0;
    private static final int LEFT = 1;
    private static final int BACK = 2;
    private static final int RIGHT = 3;

    private static final String BANG_MSG = "WALL!";
    // Warning message when the player hits an obstacle

    private MazeManager maze;

    private Image mazeImage; //Maze birdseye image
    private Image playerIcon; //Icon for players position
    private Image playerIcons[]; //Multiple icons for rotated player icon
    private int arrowWidth, arrowHeight;
    private Point moves[];

    private Point currentPos; //Player current position in image
    private int step; // distance a player moves in the image
    private int compass; // the current compass heading

    private boolean showBang; // true if player tried to move through a wall
    private Font msgFont;

    //Initialize minimap
    public MiniMap(MazeManager mazeMan) {
        maze = mazeMan;
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
        msgFont = new Font("SansSerif", Font.BOLD, 24);

        mazeImage = maze.getMazeImage(); // get the maze image
        initMoves();
        loadIcons();
        initPosition();
        repaint();
    }

    //Initializes movements for the player icon
    private void initMoves() {
        moves = new Point[NUM_DIRS];
        step = maze.getMMPos();
        moves[FORWARD] = new Point(0, step); // move downwards on-screen
        moves[LEFT] = new Point(step, 0); // right on-screen
        moves[BACK] = new Point(0, -step); // up on-screen
        moves[RIGHT] = new Point(-step, 0); // left on-screen
    }

    //Initializes player icon, can rotate in different directions if wanted (Such as an arrow)
    //We used a ball icon so no rotation is needed
    private void loadIcons() {
        playerIcons = new Image[NUM_DIRS];

        ImageIcon imIcon = new ImageIcon("images/ball.png");
        playerIcons[FORWARD] = imIcon.getImage();
        arrowWidth = imIcon.getIconWidth();
        arrowHeight = imIcon.getIconHeight();

        playerIcons[LEFT] = new ImageIcon("images/ball.png").getImage();
        playerIcons[BACK] = new ImageIcon("images/ball.png").getImage();
        playerIcons[RIGHT] = new ImageIcon("images/ball.png").getImage();
    }

    //Set initial position of the player icon based on the starting camera position
    private void initPosition() {
        currentPos = maze.getImageStartPosn();
        playerIcon = playerIcons[FORWARD];
        showBang = false;
    }


    //Sets the position of new player icon based on current camera coordinates
    //Called from KeyBehavior class
    public void setMove(int xdir, int zdir) {
        currentPos.x = xdir * 6; //Multiplied by 6 to get grid layout instead of pixel count
        currentPos.y = zdir * 6;
        repaint();
    }

    public void bangAlert()
    // Request a redraw so that the bang message will be displayed
    {
        showBang = true;
        repaint();
        playOof();
    }

    public void paintComponent(Graphics g)
    //Paint the maze image, then the player, then the bang message if it is set.
    {
        super.paintComponent(g); // repaint standard stuff first
        g.drawImage(mazeImage, 0, 0, null); // draw the maze

        int xPos = currentPos.x + step / 2 - arrowWidth / 2;
        int yPos = currentPos.y + step / 2 - arrowHeight / 2;
        g.drawImage(playerIcon, xPos, yPos, null); // draw the player

        if (showBang) { // show the bang message
            g.setColor(Color.red);
            g.setFont(msgFont);
            g.drawString(BANG_MSG, PWIDTH / 2, PHEIGHT * 2);
            showBang = false;
        }
    }

    //Sound effect for hitting wall
    public void playOof() {
        try {
            AudioInputStream audioInputStream = AudioSystem
                    .getAudioInputStream(new File("roblox-death-sound_1.wav").getAbsoluteFile());
            javax.sound.sampled.Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}