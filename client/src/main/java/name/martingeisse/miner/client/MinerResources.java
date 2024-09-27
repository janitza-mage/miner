/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client;

import name.martingeisse.miner.client.launcher.assets.LauncherAssets;
import name.martingeisse.miner.client.util.lwjgl.FixedWidthFont;
import name.martingeisse.miner.client.util.lwjgl.Font;
import name.martingeisse.miner.client.util.lwjgl.StackdTexture;
import name.martingeisse.miner.common.cubetype.CubeTypes;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ClasspathLocation;
import org.newdawn.slick.util.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 */
public class MinerResources {

	/**
	 * the instance
	 */
	private static MinerResources instance;

	/**
	 * Initializes the instance of this class and loads all resources.
	 * @throws IOException on I/O errors
	 */
	public synchronized static void initializeInstance() throws IOException {
		instance = new MinerResources();
	}

	/**
	 * Getter method for the instance.
	 * @return the instance
	 */
	public static MinerResources getInstance() {
		return instance;
	}

	/**
	 * the cubeTextures
	 */
	private final StackdTexture[] cubeTextures;

	/**
	 * the clouds
	 */
	private final StackdTexture clouds;

	/**
	 * the font
	 */
	private final Font font;

	/**
	 * the footstep
	 */
	private final SoundTemplate footstep;

	/**
	 * the hitCube
	 */
	private final SoundTemplate hitCube;

	/**
	 * the landOnGround
	 */
	private final SoundTemplate landOnGround;

	/**
	 * Constructor. The resources are immediately loaded.
	 * @throws IOException on I/O errors
	 */
	private MinerResources() throws IOException {

		// load cube textures
		final String[] cubeTextureNames = CubeTypes.CUBE_TEXTURE_FILENAMES;
		cubeTextures = new StackdTexture[cubeTextureNames.length];
		for (int i = 0; i < cubeTextures.length; i++) {
			cubeTextures[i] = new StackdTexture(LauncherAssets.class, cubeTextureNames[i], false);
		}

		// load special textures
		clouds = new StackdTexture(LauncherAssets.class, "clouds.png", false);
		font = new FixedWidthFont(loadImage("font.png"), 8, 16);

		// load sounds
		ResourceLoader.addResourceLocation(new ClasspathLocation());
		footstep = loadOggSound("footstep-1.ogg");
		hitCube = loadOggSound("hit-cube-1.ogg");
		landOnGround = loadOggSound("land.ogg");

	}

	/**
	 * @param filename the filename of the OGG, relative to the assets folder
	 * @return the sound
	 * @throws IOException on I/O errors
	 */
	private Audio loadOggSound(final String filename) throws IOException {
		try (InputStream inputStream = LauncherAssets.class.getResourceAsStream(filename)) {
			return AudioLoader.getAudio("OGG", inputStream);
		}
	}

	/**
	 * Loads a PNG image the AWT way.
	 * @param filename the filename of the PNG, relative to the assets folder
	 * @return the luminance buffer
	 * @throws IOException on I/O errors
	 */
	private BufferedImage loadImage(final String filename) throws IOException {
		try (InputStream inputStream = LauncherAssets.class.getResourceAsStream(filename)) {
			return ImageIO.read(inputStream);
		}
	}

	/**
	 * Getter method for the cubeTextures.
	 * @return the cubeTextures
	 */
	public StackdTexture[] getCubeTextures() {
		return cubeTextures;
	}

	/**
	 * Getter method for the clouds.
	 * @return the clouds
	 */
	public StackdTexture getClouds() {
		return clouds;
	}

	/**
	 * Getter method for the font.
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Draws text to the current raster position.
	 * @param s the text to draw
	 * @param zoom the zoom factor for the text
	 */
	public void drawText(final String s, final float zoom) {
		glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
		glBindTexture(GL_TEXTURE_2D, 0);
		font.drawText(s, zoom, Font.ALIGN_CENTER, Font.ALIGN_CENTER);
	}

	/**
	 * Getter method for the footstep.
	 * @return the footstep
	 */
	public SoundTemplate getFootstep() {
		return footstep;
	}

	/**
	 * Getter method for the hitCube.
	 * @return the hitCube
	 */
	public SoundTemplate getHitCube() {
		return hitCube;
	}

	/**
	 * Getter method for the landOnGround.
	 * @return the landOnGround
	 */
	public SoundTemplate getLandOnGround() {
		return landOnGround;
	}

}
