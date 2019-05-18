
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Maze3D extends JFrame {
    public static int flag = 0;

    public Maze3D(String args[]) {
        super("The Labyrinth");

        String mazeFile = null;
        if (args.length == 1)
            mazeFile = args[0];
        else if (args.length == 0)
            mazeFile = MazeOptions.mazeMap[MazeOptions.mazeFileNum]; // default maze file
        else {
            System.out.println("Usage: java Maze3D <fileName>");
            System.exit(0);
        }

        MazeManager maze = new MazeManager(mazeFile);
        MiniMap minimap = new MiniMap(maze); //Minimap

        SkyBox w3d = new SkyBox(maze, minimap);

        //Create content pane to hold player camera and minimap
        Container c = getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.X_AXIS));
        c.add(w3d); // main camera pane
        c.add(Box.createRigidArea(new Dimension(8, 0))); // some space

        Box vertBox = Box.createVerticalBox();
        vertBox.add(Box.createRigidArea(new Dimension(0, 8))); // space
        vertBox.add(minimap); //minimap pane
        c.add(vertBox);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false); // fixed size display
        setVisible(true);
    }

    //Start game
    public static void main(String[] args) {
        //	new Maze3D(args);
        new StartScreen(args);
    }
}

class StartScreen extends JFrame {
    public StartScreen(String[] args) {
        String[] greetings = {"Wilkommen to Die Maze!", "Welcome to Maze", "Hello World!", "Ain't no minecraft."};
        setPreferredSize(new Dimension(500, 375));
        setSize(new Dimension(500, 375));
        Container pane = getContentPane();
        pane.setBackground(Color.black);
        pane.setLayout(null);
        JButton start = new JButton("Easy");
        JButton start2 = new JButton("Hard");
        JButton exit = new JButton("Exit");
        JLabel mazeLabel = new JLabel("");
        JLabel mazeGreeting = new JLabel("Do you know the way? \n not of the devil...");
        int rand = (int) Math.random() * greetings.length + 1 - 1;
        mazeLabel.setText("The Labyrinth");
        mazeLabel.setBackground(Color.white);
        pane.add(start);
        pane.add(start2);
        pane.add(exit);
        pane.add(mazeLabel);
        pane.add(mazeGreeting);

        start.setBounds(100, 150, 100, 30);
        start2.setBounds(275, 150, 100, 30);
        exit.setBounds(185, 200, 100, 30);
        mazeLabel.setBounds(155, 50, 500, 50);
        mazeGreeting.setBounds(155, 250, 500, 50);

        start.setBackground(Color.green);
        start2.setBackground(Color.YELLOW);
        exit.setBackground(Color.RED);

        start.setFont(new Font("btnFont", Font.BOLD, 14));
        start2.setFont(new Font("btnFont", Font.BOLD, 14));
        exit.setFont(new Font("btnFont", Font.BOLD, 14));

        mazeLabel.setFont(new Font("f", Font.BOLD, 36));
        mazeLabel.setBackground(Color.white);
        mazeGreeting.setFont(new Font("f", Font.ITALIC, 12));
        mazeGreeting.setBackground(Color.white);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MazeOptions.mazeFileNum = 0;
                int m = (int) (Math.random() * (MazeOptions.skies.length + 1 - 1));
                MazeOptions.mapSelection = m;
                new Maze3D(args);

            }
        });

        start2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MazeOptions.mazeFileNum = 1;
                int m = (int) (Math.random() * (MazeOptions.skies.length));
                MazeOptions.mapSelection = m;
                new Maze3D(args);
            }
        });
        setVisible(true);

    }
}

