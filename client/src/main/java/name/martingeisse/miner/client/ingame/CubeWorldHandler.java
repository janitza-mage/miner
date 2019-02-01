/**
 * Copyright (c) 2012 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame;

import name.martingeisse.common.util.ThreadUtil;
import name.martingeisse.miner.client.MinerResources;
import name.martingeisse.miner.client.ingame.engine.EngineParameters;
import name.martingeisse.miner.client.ingame.engine.FrameRenderParameters;
import name.martingeisse.miner.client.ingame.engine.WorldWorkingSet;
import name.martingeisse.miner.client.ingame.gui.InventoryPage;
import name.martingeisse.miner.client.ingame.gui.MainMenuPage;
import name.martingeisse.miner.client.ingame.player.Player;
import name.martingeisse.miner.client.ingame.player.PlayerProxy;
import name.martingeisse.miner.client.ingame.visual.OtherPlayerVisualTemplate;
import name.martingeisse.miner.client.util.frame.AbstractIntervalFrameHandler;
import name.martingeisse.miner.client.util.frame.FrameDurationSensor;
import name.martingeisse.miner.client.util.frame.IFrameHandler;
import name.martingeisse.miner.client.util.glworker.GlWorkUnit;
import name.martingeisse.miner.client.util.glworker.GlWorkerLoop;
import name.martingeisse.miner.client.util.lwjgl.*;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.geometry.AxisAlignedDirection;
import name.martingeisse.miner.common.geometry.RectangularRegion;
import name.martingeisse.miner.common.network.c2s.CubeModification;
import name.martingeisse.miner.common.util.ProfilingHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glWindowPos2i;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * TODO: document me
 *
 */
public class CubeWorldHandler implements IFrameHandler {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(CubeWorldHandler.class);

	/**
	 * the MAX_STAIRS_HEIGHT
	 */
	public static final double MAX_STAIRS_HEIGHT = 0.8;

	public static boolean disableLeftMouseButtonBecauseWeJustClosedTheGui = false;

	/**
	 * the player
	 */
	private final Player player;

	/**
	 * the resources
	 */
	private final MinerResources resources;

	/**
	 * the workingSet
	 */
	private final WorldWorkingSet workingSet;

	/**
	 * the infoButtonPressed
	 */
	private boolean infoButtonPressed;

	/**
	 * the rayActionSupport
	 */
	private final RayActionSupport rayActionSupport;
	
	/**
	 * the captureRayActionSupport
	 */
	private boolean captureRayActionSupport;

	/**
	 * the wireframe
	 */
	private boolean wireframe;

	/**
	 * the grid
	 */
	private boolean grid;

	/**
	 * the screenWidth
	 */
	private final int screenWidth;

	/**
	 * the screenHeight
	 */
	private final int screenHeight;

	/**
	 * the aspectRatio
	 */
	private final float aspectRatio;

	/**
	 * the currentCubeType
	 */
	private byte currentCubeType = 1;

	/**
	 * the frameDurationSensor
	 */
	private final FrameDurationSensor frameDurationSensor;

	/**
	 * the playerProxies
	 */
	private List<PlayerProxy> playerProxies;

	/**
	 * the minusPressed
	 */
	private boolean minusPressed;

	/**
	 * the flashMessageCounter
	 */
	private int flashMessageCounter = 0;

	/**
	 * the footstepSound
	 */
	private RegularSound footstepSound;

	/**
	 * the walking
	 */
	private boolean walking;

	/**
	 * the cooldownFinishTime
	 */
	private long cooldownFinishTime;

	/**
	 * the previousConnectionProblemInstant
	 */
	private Instant previousConnectionProblemInstant = new Instant();
	
	/**
	 * the otherPlayerVisualTemplate
	 */
	private final OtherPlayerVisualTemplate otherPlayerVisualTemplate;

	/**
	 * The sectionLoadHandler -- checks often (100 ms), but doesn't re-request frequently (5 sec)
	 * to avoid re-requesting a section again and again while the server is loading it.
	 * 
	 * TODO should be resettable for edge cases where frequent reloading is needed, such as
	 * falling down from high places.
	 * This handler checks if sections must be loaded.
	 */
	private AbstractIntervalFrameHandler sectionLoadHandler = new AbstractIntervalFrameHandler(100) {

		private int requestCooldown = 0;

		@Override
		protected void onIntervalTimerExpired() {
			if (requestCooldown == 0) {
				Ingame.get().getProtocolClient().getSectionGridLoader().setViewerPosition(player.getSectionId());
				if (Ingame.get().getProtocolClient().getSectionGridLoader().update()) {
					requestCooldown = 50;
				}
			} else {
				requestCooldown--;
			}
		}
	};

	/**
	 * Constructor.
	 * @param width the width of the framebuffer
	 * @param height the height of the framebuffer
	 * @throws IOException on I/O errors while loading the textures
	 */
	public CubeWorldHandler(final int width, final int height) throws IOException {

		// the resources (textures)
		this.resources = MinerResources.getInstance();

		// the world
		final EngineParameters engineParameters = new EngineParameters(resources.getCubeTextures());
		workingSet = new WorldWorkingSet(engineParameters);
		workingSet.getSectionRenderer().prepareForTextures(resources.getCubeTextures());

		// the player
		player = new Player(workingSet);
		player.getPosition().setX(0);
		player.getPosition().setY(10);
		player.getPosition().setZ(0);

		// other stuff
		rayActionSupport = new RayActionSupport(width, height);
		screenWidth = width;
		screenHeight = height;
		aspectRatio = (float)width / (float)height;
		frameDurationSensor = new FrameDurationSensor();
		playerProxies = new ArrayList<PlayerProxy>();
		footstepSound = new RegularSound(resources.getFootstep(), 500);
		cooldownFinishTime = System.currentTimeMillis();
		otherPlayerVisualTemplate = new OtherPlayerVisualTemplate(resources, player);

	}

	/**
	 * Getter method for the resources.
	 * @return the resources
	 */
	public MinerResources getResources() {
		return resources;
	}

	/**
	 * Getter method for the currentCubeType.
	 * @return the currentCubeType
	 */
	public byte getCurrentCubeType() {
		return currentCubeType;
	}

	/**
	 * Getter method for the workingSet.
	 * @return the workingSet
	 */
	public WorldWorkingSet getWorkingSet() {
		return workingSet;
	}

	/**
	 * Getter method for the player.
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Getter method for the playerProxies.
	 * @return the playerProxies
	 */
	public List<PlayerProxy> getPlayerProxies() {
		return playerProxies;
	}

	/**
	 * Setter method for the playerProxies.
	 * @param playerProxies the playerProxies to set
	 */
	public void setPlayerProxies(final List<PlayerProxy> playerProxies) {
		this.playerProxies = playerProxies;
	}

	/**
	 * 
	 */
	@Override
	public void handleStep() {

		// TODO avoid filling up the render queue, should detect when the logic thread is running too fast
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
		}

		final boolean keysEnabled = !Ingame.get().isGuiOpen();
		final boolean mouseMovementEnabled = !Ingame.get().isGuiOpen();

		// first, handle the stuff that already works without the world being loaded "enough"
		frameDurationSensor.tick();
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_M)) {
			if (!infoButtonPressed) {
				player.dump();
			}
			infoButtonPressed = true;
		} else {
			infoButtonPressed = false;
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_P)) {
			player.setObserverMode(false);
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_O)) {
			player.setObserverMode(true);
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_G)) {
			grid = true;
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_H)) {
			grid = false;
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_K)) {
			MouseUtil.ungrab();
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_L)) {
			MouseUtil.grab();
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_SLASH) && !minusPressed) {
			Ingame.get().showFlashMessage("foobar! " + flashMessageCounter);
			flashMessageCounter++;
		}
		minusPressed = keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_SLASH);
		wireframe = keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_F);
		player.setWantsToJump(keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_SPACE));
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_1)) {
			currentCubeType = 1;
		} else if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_2)) {
			currentCubeType = 2;
		} else if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_3)) {
			currentCubeType = 3;
		} else if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_4)) {
			currentCubeType = 50;
		}
		if (mouseMovementEnabled) {
			player.getOrientation().setHorizontalAngle(player.getOrientation().getHorizontalAngle() - Mouse.getDX() * 0.5);
			double newUpAngle = player.getOrientation().getVerticalAngle() + Mouse.getDY() * 0.5;
			newUpAngle = (newUpAngle > 90) ? 90 : (newUpAngle < -90) ? -90 : newUpAngle;
			player.getOrientation().setVerticalAngle(newUpAngle);
		}
		sectionLoadHandler.handleStep();

		// process keyboard events -- needed for flawless GUI toggling
		// TODO properly disable all keyboard / mouse handling in the CubeWorldHandler when the GUI is active
		if (keysEnabled) {
			while (Keyboard.next()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && Keyboard.getEventKeyState()) {
					Ingame.get().openGui(new MainMenuPage());
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_I && Keyboard.getEventKeyState()) {
					Ingame.get().openGui(new InventoryPage());
				}
			}
		}

		// check if the world is loaded "enough"
		workingSet.acceptLoadedSections();
		if (!workingSet.hasAllRenderModels(player.getSectionId(), 1) || !workingSet.hasAllColliders(player.getSectionId(), 1)) {
			final Instant now = new Instant();
			if (new Duration(previousConnectionProblemInstant, now).getMillis() >= 1000) {
				logger.warn("connection problems");
				ThreadUtil.dumpThreads(Level.INFO);
				previousConnectionProblemInstant = now;
			}
			return;
		}

		// ---------------------------------------------------------------------------------------------------
		// now, handle the stuff that only works with enough information from the world
		// ---------------------------------------------------------------------------------------------------

		// normal movement: If on the ground, we move the player step-up, then front/side, then step-down.
		// This way the player can climb stairs while walking. In the air, this boils down to front/side movement.
		// We also keep track if the player is walking (front/side) for a "walking" sound effect.
		double speed = keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_TAB) ? 10.0 : keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 3.0 : 1.5;
		speed *= frameDurationSensor.getMultiplier();
		walking = false;
		double forward = 0, right = 0;
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_A)) {
			right = -speed;
			walking = true;
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_D)) {
			right = speed;
			walking = true;
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_W)) {
			forward = speed;
			walking = true;
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_S)) {
			forward = -speed;
			walking = true;
		}
		player.moveHorizontal(forward, right, player.isOnGround() ? MAX_STAIRS_HEIGHT : 0);

		// special movement
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_C)) {
			player.moveUp(-speed);
		}
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_E)) {
			player.moveUp(speed);
		}

		// cube placement
		if (!Mouse.isButtonDown(0)) {
			disableLeftMouseButtonBecauseWeJustClosedTheGui = false;
		}
		captureRayActionSupport = false;
		final long now = System.currentTimeMillis();
		if (now >= cooldownFinishTime) {
			if (mouseMovementEnabled && Mouse.isButtonDown(0) && !disableLeftMouseButtonBecauseWeJustClosedTheGui) {
				captureRayActionSupport = true;
				rayActionSupport.execute(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), new RayAction(false) {
					@Override
					public void handleImpact(final int x, final int y, final int z, final double distance) {
						if (distance < 3.0) {
							final byte effectiveCubeType;
							if (currentCubeType == 50) {
								final int angle = ((int)player.getOrientation().getHorizontalAngle() % 360 + 360) % 360;
								if (angle < 45) {
									effectiveCubeType = 52;
								} else if (angle < 45 + 90) {
									effectiveCubeType = 50;
								} else if (angle < 45 + 180) {
									effectiveCubeType = 53;
								} else if (angle < 45 + 270) {
									effectiveCubeType = 51;
								} else {
									effectiveCubeType = 52;
								}
							} else {
								effectiveCubeType = currentCubeType;
							}

							/* TODO: The call to breakFree() will remove a stairs cube if the player is standing
							 * on the lower step, because the player's bounding box intersects with the cube's
							 * bounding box. Solution 1: Remove breakFree(), don't place a cube if the player then
							 * collides. Solution 2: Make breakFree() more accurate.
							 */
							CubeModification.Builder builder = new CubeModification.Builder();
							builder.add(x, y, z, effectiveCubeType);
							breakFree(builder);
							Ingame.get().getProtocolClient().send(builder.build());

							// cooldownFinishTime = now + 1000;
							cooldownFinishTime = now + 200;
						}
					}
				});
			} else if (mouseMovementEnabled && Mouse.isButtonDown(1)) {
				captureRayActionSupport = true;
				rayActionSupport.execute(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), new RayAction(true) {
					@Override
					public void handleImpact(final int x, final int y, final int z, final double distance) {
						if (distance < 2.0) {
							Ingame.get().getProtocolClient().sendDigNotification(x, y, z);
							resources.getHitCube().playAsSoundEffect(1.0f, 1.0f, false);
							// cooldownFinishTime = now + 1000;
							cooldownFinishTime = now + 200;
						}
					}
				});
			}
		}

		// special actions
		if (keysEnabled && Keyboard.isKeyDown(Keyboard.KEY_B)) {
			final CubeModification.Builder builder = new CubeModification.Builder();
			breakFree(builder);
			Ingame.get().getProtocolClient().send(builder.build());
		}

		// handle player logic
		player.step(frameDurationSensor.getMultiplier());

		// handle sound effects
		if (player.isOnGround() && walking) {
			footstepSound.handleActiveTime();
		} else {
			footstepSound.reset();
		}
		if (player.isJustLanded()) {
			resources.getLandOnGround().playAsSoundEffect(1.0f, 1.0f, false);
		}

	}

	/**
	 * 
	 */
	private void breakFree(final CubeModification.Builder builder) {
		final RectangularRegion region = player.createCollisionRegion();
		for (int x = region.getStartX(); x < region.getEndX(); x++) {
			for (int y = region.getStartY(); y < region.getEndY(); y++) {
				for (int z = region.getStartZ(); z < region.getEndZ(); z++) {
					builder.add(x, y, z, (byte)0);
				}
			}
		}
	}

	/**
	 * @param glWorkerLoop the OpenGL worker loop
	 */
	public void draw(final GlWorkerLoop glWorkerLoop) {

		// determine player's position as integers
		final int playerX = (int)(Math.floor(player.getPosition().getX()));
		final int playerY = (int)(Math.floor(player.getPosition().getY()));
		final int playerZ = (int)(Math.floor(player.getPosition().getZ()));

		// set the GL worker loop for the section renderer
		workingSet.getSectionRenderer().setGlWorkerLoop(glWorkerLoop);

		// run preparation code in the OpenGL worker thread
		glWorkerLoop.schedule(new GlWorkUnit() {
			@Override
			public void execute() {

				// profiling
				ProfilingHelper.start();

				// set up projection matrix
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				gluPerspective(60, aspectRatio, 0.1f, 10000.0f);

				// set up modelview matrix
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity(); // model transformation (direct)
				glRotatef((float)player.getOrientation().getVerticalAngle(), -1, 0, 0); // view transformation (reversed)
				glRotatef((float)player.getOrientation().getHorizontalAngle(), 0, -1, 0); // ...
				glTranslated(-player.getPosition().getX(), -player.getPosition().getY(), -player.getPosition().getZ()); // ...

				// clear the screen
				glDepthMask(true);
				glClearColor(0.5f, 0.5f, 1.0f, 1.0f);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

				// some more preparation
				glDepthFunc(GL_LESS);
				glEnable(GL_DEPTH_TEST);
				workingSet.getSectionRenderer().setWireframe(wireframe);

				// scale by the inverse detail factor for drawing the cubes, but prepare for scaling back
				glPushMatrix();
				float inverseFactor = 1.0f / Constants.GEOMETRY_DETAIL_FACTOR;
				glScalef(inverseFactor, inverseFactor, inverseFactor);
				
			}
		});

		// actually draw the world TODO pass the GL worker
		workingSet.draw(new FrameRenderParameters(playerX, playerY, playerZ));

		// post-draw code, again in the GL worker thread
		glWorkerLoop.schedule(new GlWorkUnit() {
			@Override
			public void execute() {

				// scale back for the remaining operations
				glPopMatrix();
				
				// Measure visible distance in the center of the crosshair, with only the world visible (no HUD or similar).
				// Only call if needed, this stalls the rendering pipeline --> 2x frame rate possible!
				if (captureRayActionSupport) {
					rayActionSupport.capture();
				} else {
					rayActionSupport.release();
				}

				// draw the sky
				glDisable(GL_TEXTURE_GEN_S);
				glDisable(GL_TEXTURE_GEN_T);
				glDisable(GL_TEXTURE_GEN_Q);
				glDisable(GL_TEXTURE_GEN_R);
				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				glEnable(GL_TEXTURE_2D);
				resources.getClouds().glBindTexture();
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				final float tex = 10.0f;
				glColor3f(1.0f, 1.0f, 1.0f);
				glBegin(GL_QUADS);
				glTexCoord2f(0, 0);
				glVertex3i(-100000, 1000, -100000);
				glTexCoord2f(tex, 0);
				glVertex3i(+100000, 1000, -100000);
				glTexCoord2f(tex, tex);
				glVertex3i(+100000, 1000, +100000);
				glTexCoord2f(0, tex);
				glVertex3i(-100000, 1000, +100000);
				glEnd();
				glDisable(GL_BLEND);

				// draw the grid
				if (grid) {
					glDisable(GL_TEXTURE_2D);
					glColor3f(1.0f, 1.0f, 1.0f);
					final int sectionX = playerX >> Constants.SECTION_SIZE.getShiftBits();
					final int sectionY = playerY >> Constants.SECTION_SIZE.getShiftBits();
					final int sectionZ = playerZ >> Constants.SECTION_SIZE.getShiftBits();
					final int distance = 48;
					glLineWidth(2.0f);
					glBegin(GL_LINES);
					for (int u = -3; u <= 4; u++) {
						for (int v = -3; v <= 4; v++) {
							for (final AxisAlignedDirection direction : AxisAlignedDirection.values()) {
								if (direction.isNegative()) {
									continue;
								}
								final int x = Constants.SECTION_SIZE.getSize() * (sectionX + direction.selectByAxis(0, u, v));
								final int dx = direction.selectByAxis(distance, 0, 0);
								final int y = Constants.SECTION_SIZE.getSize() * (sectionY + direction.selectByAxis(v, 0, u));
								final int dy = direction.selectByAxis(0, distance, 0);
								final int z = Constants.SECTION_SIZE.getSize() * (sectionZ + direction.selectByAxis(u, v, 0));
								final int dz = direction.selectByAxis(0, 0, distance);
								glVertex3f(x + dx, y + dy, z + dz);
								glVertex3f(x - dx, y - dy, z - dz);
							}
						}
					}
					glEnd();
				}

				// draw player proxies (i.e. other players)
				glBindTexture(GL_TEXTURE_2D, 0);
				glEnable(GL_BLEND);
				glMatrixMode(GL_MODELVIEW);
				for (final PlayerProxy playerProxy : playerProxies) {
					otherPlayerVisualTemplate.renderEmbedded(playerProxy);
				}
				glDisable(GL_BLEND);

				// draw the crosshair
				glLineWidth(1.0f);
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
				glDisable(GL_DEPTH_TEST);
				glDisable(GL_TEXTURE_2D);
				glColor3f(1.0f, 1.0f, 1.0f);
				glBegin(GL_LINES);
				glVertex2f(-0.1f, 0.0f);
				glVertex2f(+0.1f, 0.0f);
				glVertex2f(0.0f, -0.1f);
				glVertex2f(0.0f, +0.1f);
				glEnd();

				// draw the HUD
				glBindTexture(GL_TEXTURE_2D, 0);
				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				glWindowPos2i(screenWidth, screenHeight - 30);
				GL11.glPixelTransferf(GL11.GL_RED_BIAS, 1.0f);
				GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 1.0f);
				GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 1.0f);
				resources.getFont().drawText("coins: " + Ingame.get().getProtocolClient().getCoins(), 2, Font.ALIGN_RIGHT, Font.ALIGN_TOP);
				GL11.glPixelTransferf(GL11.GL_RED_BIAS, 0.0f);
				GL11.glPixelTransferf(GL11.GL_GREEN_BIAS, 0.0f);
				GL11.glPixelTransferf(GL11.GL_BLUE_BIAS, 0.0f);

				// profiling
				ProfilingHelper.checkRelevant("draw", 50);

			}
		});

	}

}
