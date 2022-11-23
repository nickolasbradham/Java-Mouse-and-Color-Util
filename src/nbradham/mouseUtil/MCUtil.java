package nbradham.mouseUtil;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

/**
 * The entire utility.
 * 
 * @author Nickolas Bradham
 *
 */
public final class MCUtil implements NativeKeyListener, NativeMouseMotionListener {

	private final JLabel coordsLabel = new JLabel("P to Pause   Coordinates: Waiting for Movement..."),
			hueLabel = new JLabel("Hue: Waiting...");
	JFrame frame = new JFrame("Mouse and Color Util");
	private final Robot r;
	private long lastUpdate = 0;
	private boolean unpaused = true;

	/**
	 * Constructs a new MCUtil
	 * 
	 * @throws AWTException Thrown by {@link Robot#Robot()}.
	 */
	private MCUtil() throws AWTException {
		r = new Robot();
	}

	/**
	 * Builds and shows the GUI. Registers listeners.
	 * 
	 * @throws NativeHookException Thrown by
	 *                             {@link GlobalScreen#registerNativeHook()}.
	 */
	private void start() throws NativeHookException {
		SwingUtilities.invokeLater(() -> {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new FlowLayout());
			frame.add(coordsLabel);
			frame.add(hueLabel);
			frame.pack();

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {

					try {
						GlobalScreen.unregisterNativeHook();
					} catch (NativeHookException e1) {
						e1.printStackTrace();
					}
				}
			});

			frame.setVisible(true);
		});

		GlobalScreen.registerNativeHook();
		GlobalScreen.addNativeKeyListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
		if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_P)
			unpaused = !unpaused;
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
		long time = System.currentTimeMillis();
		if (unpaused && time - lastUpdate > 16 && frame.isVisible()) {
			Point p = MouseInfo.getPointerInfo().getLocation();
			Color c = r.getPixelColor(p.x, p.y);

			coordsLabel.setText(String.format("P to Pause   Coordinates: (%d, %d)", p.x, p.y));
			hueLabel.setText(String.format("Hue: %f", Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[0]));
			lastUpdate = time;
		}
	}

	/**
	 * Constructs and starts a new MCUtil instance.
	 * 
	 * @param args Ignored.
	 * @throws AWTException        Thrown by {@link #MCUtil()}.
	 * @throws NativeHookException Thrown by {@link #start()}.
	 */
	public static final void main(String[] args) throws AWTException, NativeHookException {
		new MCUtil().start();
	}
}