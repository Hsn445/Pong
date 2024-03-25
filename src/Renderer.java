// Import Necessary Java and Javax Packages
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

// Class creates renderer that allows render of JFrames
public class Renderer extends JPanel {
	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Pong.pong.renderer((Graphics2D) g);
	}
}