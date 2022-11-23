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
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

final class MCUtil {

	public static final void main(String[] args) throws AWTException, NativeHookException {
		Robot r = new Robot();

		GlobalScreen.registerNativeHook();

		SwingUtilities.invokeLater(() -> {

			JFrame frame = new JFrame("Mouse and Color Util");
			JLabel coordsLabel = new JLabel(
					"Coordinates: Waiting for Movement..."), hueLabel = new JLabel("Hue: Waiting...");

			GlobalScreen.addNativeMouseMotionListener(new NativeMouseMotionListener() {
				@Override
				public void nativeMouseMoved(NativeMouseEvent nativeEvent) {

					Point p = MouseInfo.getPointerInfo().getLocation();
					Color c = r.getPixelColor(p.x, p.y);

					coordsLabel.setText(String.format("Coordinates: (%d, %d)", p.x, p.y));
					hueLabel.setText(
							String.format("Hue: %f", Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null)[0]));
				}
			});

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
	}
}