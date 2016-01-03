/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.tests.lwjgl;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.tests.BulletTestCollection;
import com.badlogic.gdx.tests.DeltaTimeTest;
import com.badlogic.gdx.tests.FullscreenTest;
import com.badlogic.gdx.tests.LifeCycleTest;
import com.badlogic.gdx.tests.MusicTest;
import com.badlogic.gdx.tests.StageTest;
import com.badlogic.gdx.tests.TextInputDialogTest;
import com.badlogic.gdx.tests.UITest;
import com.badlogic.gdx.tests.bullet.BulletTest;
import com.badlogic.gdx.tests.g3d.Animation3DTest;
import com.badlogic.gdx.tests.g3d.BaseG3dHudTest;
import com.badlogic.gdx.tests.superkoalio.SuperKoalio;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Lwjgl3DebugStarter {
	public static void main (String[] argv) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
		GdxTest test = new GdxTest() {
			float r = 0;
			SpriteBatch batch;
			BitmapFont font;
			FPSLogger fps = new FPSLogger();
			Texture texture;
			
			@Override
			public void create () {				
				texture = new Texture("data/badlogic.jpg");
				batch = new SpriteBatch();
				font = new BitmapFont();
				Gdx.input.setInputProcessor(new InputAdapter() {

					@Override
					public boolean keyDown (int keycode) {
						System.out.println("Key down: " + Keys.toString(keycode));
						return false;
					}

					@Override
					public boolean keyUp (int keycode) {
						System.out.println("Key up: " + Keys.toString(keycode));
						return false;
					}

					@Override
					public boolean keyTyped (char character) {
						System.out.println("Key typed: '" + character + "', " + (int)character);
						
						if(character == 'f') {
							DisplayMode[] modes = Gdx.graphics.getDisplayModes();
//							Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
							for(DisplayMode mode: modes) {
								if(mode.width == 1920 && mode.height == 1080) {
									Gdx.graphics.setFullscreenMode(mode);
									break;
								}
							}
						}
						if(character == 'w') {
							Gdx.graphics.setWindowedMode(MathUtils.random(400, 800), MathUtils.random(400, 800));
						}
						if(character == 'e') {
							throw new GdxRuntimeException("derp");
						}						
						return false;
					}										
				});
			}
			
			long start = System.nanoTime();

			@Override
			public void render () {
				Gdx.gl.glClearColor(1, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
//				HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				batch.begin();
				font.draw(batch, Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight() + ", " +
									  Gdx.graphics.getBackBufferWidth() + "x" + Gdx.graphics.getBackBufferHeight() + ", " +
									  Gdx.input.getX() + ", " + Gdx.input.getY() + ", " + 
									  Gdx.input.getDeltaX() + ", " + Gdx.input.getDeltaY(), 0, 20);				
				batch.draw(texture, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
				batch.end();
				fps.log();
				if(System.nanoTime() - start > 1000000000l) {
					start = System.nanoTime();
					Gdx.app.log("Test", Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight() + ", " +
									  Gdx.graphics.getBackBufferWidth() + "x" + Gdx.graphics.getBackBufferHeight() + ", " +
									  Gdx.input.getX() + ", " + Gdx.input.getY() + ", " + 
									  Gdx.input.getDeltaX() + ", " + Gdx.input.getDeltaY());
				}
			}

			@Override
			public void resize (int width, int height) {
				Gdx.app.log("Test", "Resized " + width + "x" + height);
			}
		};
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(640, 480);
		for(DisplayMode mode: Lwjgl3ApplicationConfiguration.getDisplayModes()) {
			System.out.println(mode.width + "x" + mode.height);
		}
		new Lwjgl3Application(test, config);
	}
}
