import javax.swing.*;

//Dialog box showing that the player has reached the end of the maze
public class EndScreen extends JFrame {
    public EndScreen() {
        if (Maze3D.flag == 0) {
            JFrame frame = new JFrame("Congratulations!");
            String[] options = {"Continue", "Exit"};

            int choice = JOptionPane.showOptionDialog(frame,
                    "Congratulations! You bested the maze.",
                    "Completed the maze!",
                    JOptionPane.WARNING_MESSAGE, 0,
                    new ImageIcon("images/party.jpg"), options, options[0]
            );

            if (choice == 1) {
                System.exit(0);
            } else {
                Maze3D.flag += 1;
            }
            setVisible(true);
        }
    }
}
