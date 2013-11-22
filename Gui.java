package jump61;

import javax.swing.JWindow;
import javax.swing.JFrame;
import javax.swing.JLabel;
/** The Graphical User Interface.  Invoked with the "--display" switch
 *  from the command line.
 * @author Alan Ponte
 */
public class Gui {

	/**Creates and shows the GUI.*/
	public void createAndShowGui() {
		JFrame MainFrame = new JFrame("Jump61");
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel MainLabel = new JLabel(" ");
		MainFrame.getContentPane().add(MainLabel);
		MainFrame.pack();
		MainFrame.setVisible(true);
	}
	
    /** Main entry point for the GUI.
     * Sets up appropriate elements.
     */
    public void run() {
    	createAndShowGui();

    }

}
