/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client;

import name.martingeisse.gleng.graphics.FixedWidthFont;
import name.martingeisse.gleng.graphics.Font;
import name.martingeisse.gleng.graphics.Texture;
import name.martingeisse.gleng.sound.SoundTemplate;
import name.martingeisse.gleng.util.GlengImageResourceUtil;
import name.martingeisse.miner.common.cubetype.CubeTypes;

import java.util.List;
import java.util.stream.Stream;

public class MinerResources {

	public static List<Texture> cubeTextures;
	public static Texture clouds;
	public static Font font;
	public static SoundTemplate footstep;
	public static SoundTemplate hitCube;
	public static SoundTemplate landOnGround;

	public static void load() {

		// load cube textures
		cubeTextures = Stream.of(CubeTypes.CUBE_TEXTURE_FILENAMES).map(Texture::loadFromClasspath).toList();

		// load special textures
		clouds = Texture.loadFromClasspath("/clouds.png");
		font = new FixedWidthFont(GlengImageResourceUtil.loadClasspathImageResource("font.png"), 8, 16);

		// load sounds
		footstep = SoundTemplate.loadFromClasspath("/footstep-1.ogg");
		hitCube = SoundTemplate.loadFromClasspath("/hit-cube-1.ogg");
		landOnGround = SoundTemplate.loadFromClasspath("/land.ogg");

	}

}
