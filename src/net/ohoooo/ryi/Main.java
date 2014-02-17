/*
 * Created on 2014/02/18
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.ohoooo.ryi;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

/**
 * @author Suguru Oho
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			File inputFile = new File(args[0]);
			BufferedImage originalImage = ImageIO.read(inputFile);

			Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("GIF");
			if (!it.hasNext()) {
				throw new Exception();
			}

			ImageWriter writer = it.next();
			ImageOutputStream out = ImageIO.createImageOutputStream(new File(
					deleteSuffix(inputFile.getName()) + ".gif"));
			writer.setOutput(out);

			writer.prepareWriteSequence(null);

			IIOMetadata meta = writer.getDefaultImageMetadata(
					ImageTypeSpecifier.createFromRenderedImage(originalImage), null);
			String format = meta.getNativeMetadataFormatName();
			IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree(format);

			int count = 0;
			byte[] data = { 0x01, (byte) ((count >> 0) & 0xff),
					(byte) ((count >> 8) & 0xff) };
			IIOMetadataNode list = new IIOMetadataNode("ApplicationExtensions");
			IIOMetadataNode node = new IIOMetadataNode("ApplicationExtension");
			node.setAttribute("applicationID", "NETSCAPE");
			node.setAttribute("authenticationCode", "2.0");
			node.setUserObject(data);
			list.appendChild(node);
			root.appendChild(list);

			node = new IIOMetadataNode("GraphicControlExtension");
			node.setAttribute("disposalMethod", "none");
			node.setAttribute("userInputFlag", "FALSE");
			node.setAttribute("transparentColorFlag", "FALSE");
			node.setAttribute("delayTime", "10");
			node.setAttribute("transparentColorIndex", "0");
			root.appendChild(node);
			meta.setFromTree(format, root);
			writer.writeToSequence(new IIOImage(originalImage, null, meta), null);

			for (int i = 15; i < 360; i += 15) {
				System.out.println(i);

				node = new IIOMetadataNode("GraphicControlExtension");
				node.setAttribute("disposalMethod", "none");
				node.setAttribute("userInputFlag", "FALSE");
				node.setAttribute("transparentColorFlag", "FALSE");
				node.setAttribute("delayTime", "10");
				node.setAttribute("transparentColorIndex", "0");
				root.appendChild(node);
				meta.setFromTree(format, root);
				writer.writeToSequence(new IIOImage(rotate(originalImage, i), null,
						meta), null);

			}

			writer.endWriteSequence();
			out.close();
		} catch (IIOInvalidTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String deleteSuffix(String fname) {
		int point = fname.lastIndexOf(".");
		if (point != -1) {
			return fname.substring(0, point);
		}
		return fname;
	}

	private static BufferedImage rotate(BufferedImage image, int degree) {
		AffineTransform af = new AffineTransform();
		af.rotate(Math.toRadians(degree), image.getWidth() / 2,
				image.getHeight() / 2);
		BufferedImage reti = new BufferedImage(image.getWidth(), image.getHeight(),
				image.getType());
		Graphics2D g2 = (Graphics2D) reti.getGraphics();
		g2.drawImage(image, af, null);

		reti.flush();
		return reti;
	}
}
