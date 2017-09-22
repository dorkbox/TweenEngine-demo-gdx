/*
 * Copyright 2012 Aurelien Ribon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dorkbox.demo;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.tweenEngine.BaseTween;
import dorkbox.tweenEngine.TweenCallback;
import dorkbox.tweenEngine.TweenEngine;
import dorkbox.tweenEngine.TweenEquations;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Launcher {
	private static final int TILES_PER_LINE = 3;
	private static final float TILES_PADDING = 0.04f;

	private final List<Tile> tiles = new ArrayList<Tile>();
	private final OrthographicCamera camera = new OrthographicCamera();
	private final SpriteBatch batch = new SpriteBatch();
	private final BitmapFont font;
	private final Sprite background;
	private final Sprite title;
	private final Sprite titleLeft;
	private final Sprite titleRight;
	private final Sprite veil;
	private final float tileW, tileH;
    private final TweenEngine tweenEngine;
    private Tile selectedTile;

	public Launcher(Test[] tests, final TweenEngine tweenEngine) {
        this.tweenEngine = tweenEngine;

        int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		float wpw = 2;
		float wph = wpw * h / w;

		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		font = Assets.inst().get("arial-18.fnt", BitmapFont.class);
        font.getData().setScale(0.0025f);
		font.setUseIntegerPositions(false);

		TextureAtlas atlas = Assets.inst().get("launcher/pack", TextureAtlas.class);
		background = atlas.createSprite("background");
		title = atlas.createSprite("title");
		titleLeft = atlas.createSprite("title-left");
		titleRight = atlas.createSprite("title-right");
		veil = atlas.createSprite("white");

		background.setSize(w, w * background.getHeight() / background.getWidth());
		background.setPosition(0, (h - background.getHeight())/2);

		float titleHmts = wph/8;
		float titleHpxs = titleHmts * h / wph;
		titleLeft.setSize(titleHpxs* titleLeft.getWidth() / titleLeft.getHeight(), titleHpxs);
		titleLeft.setPosition(0, h);
		titleRight.setSize(titleHpxs * titleRight.getWidth() / titleRight.getHeight(), titleHpxs);
		titleRight.setPosition(w-titleRight.getWidth(), h);
		title.setSize(w, titleHpxs);
		title.setPosition(0, h);

		veil.setSize(w, h);
        tweenEngine.to(veil, SpriteAccessor.OPACITY, 1f).target(0).delay(0.5f).addCallback(veilEndCallback).start();

		Gdx.input.setInputProcessor(launcherInputProcessor);

		tileW = (wpw-TILES_PADDING)/TILES_PER_LINE - TILES_PADDING;
		tileH = tileW * 150 / 250;
		float tileX = -wpw/2 + TILES_PADDING;
		float tileY = wph/2 - tileH - TILES_PADDING - titleHmts;

		for (int i=0; i<tests.length; i++) {
			tiles.add(new Tile(tileX, tileY, tileW, tileH, tests[i], atlas, camera, font, tweenEngine));
			tests[i].setCallback(testCallback);

			tileX += tileW + TILES_PADDING;
			if (i > 0 && i%TILES_PER_LINE == TILES_PER_LINE-1) {
				tileX = -camera.viewportWidth/2 + TILES_PADDING;
				tileY += -tileH - TILES_PADDING;
			}
		}
	}

	public void dispose() {
		tweenEngine.cancelAll();
		batch.dispose();
		font.dispose();
	}

	public void render() {
		GL20 gl = Gdx.gl20;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL20.GL_BLEND);
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		if (selectedTile == null) {
            tweenEngine.update(Gdx.graphics.getDeltaTime());

			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
			batch.begin();
			batch.disableBlending();
			background.draw(batch);
			batch.end();

			batch.setProjectionMatrix(camera.combined);
			batch.begin();
                batch.enableBlending();
                for (int i=0; i<tiles.size(); i++) {
                    tiles.get(i).draw(batch);
                }
			batch.end();

			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);

			batch.begin();
                batch.disableBlending();
                title.draw(batch);
                titleLeft.draw(batch);
                titleRight.draw(batch);
                batch.enableBlending();

                if (veil.getColor().a > 0.1f) {
                    veil.draw(batch);
                }
			batch.end();

		} else {
			selectedTile.getTest().render();
		}
	}

	private void showTitle(float delay) {
		float dy = -title.getHeight();
		tweenEngine.to(title, SpriteAccessor.POS_XY, 0.5F).targetRelative(0, dy).delay(delay).ease(TweenEquations.Quart_Out).start();
		tweenEngine.to(titleLeft, SpriteAccessor.POS_XY, 0.5F).targetRelative(0, dy).delay(delay).ease(TweenEquations.Quart_Out).start();
		tweenEngine.to(titleRight, SpriteAccessor.POS_XY, 0.5F).targetRelative(0, dy).delay(delay).ease(TweenEquations.Quart_Out).start();
	}

	private void hideTitle(float delay) {
		float dy = title.getHeight();
		tweenEngine.to(title, SpriteAccessor.POS_XY, 0.3F).targetRelative(0, dy).delay(delay).ease(TweenEquations.Cubic_In).start();
		tweenEngine.to(titleLeft, SpriteAccessor.POS_XY, 0.3F).targetRelative(0, dy).delay(delay).ease(TweenEquations.Cubic_In).start();
		tweenEngine.to(titleRight, SpriteAccessor.POS_XY, 0.3F).targetRelative(0, dy).delay(delay).ease(TweenEquations.Cubic_In).start();
	}

	private void closeSelectedTile() {
		selectedTile.minimize(minimizeCallback);
		selectedTile = null;
		Gdx.input.setInputProcessor(null);
		showTitle(0.2F);
	}

	private final Test.Callback testCallback = new Test.Callback() {
		@Override
		public void closeRequested(Test source) {
			closeSelectedTile();
		}
	};

	// -------------------------------------------------------------------------
	// Callbacks
	// -------------------------------------------------------------------------

	private final TweenCallback veilEndCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			showTitle(0);
			for (int i=0; i<tiles.size(); i++) {
				int row = i / TILES_PER_LINE;
				int col = i % TILES_PER_LINE;
				float delay = row * 0.07F + col * 0.15F;
				tiles.get(i).enter(delay);
			}
		}
	};

	private final TweenCallback maximizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			selectedTile = (Tile) source.getUserData();
			selectedTile.getTest().initialize();
			Gdx.input.setInputProcessor(testInputMultiplexer);
			Gdx.input.setCatchBackKey(true);

			testInputMultiplexer.clear();
			testInputMultiplexer.addProcessor(testInputProcessor);
			if (selectedTile.getTest().getInput() != null) {
				testInputMultiplexer.addProcessor(selectedTile.getTest().getInput());
			}
		}
	};

	private final TweenCallback minimizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			Tile tile = (Tile) source.getUserData();
			tile.getTest().dispose();
			Gdx.input.setInputProcessor(launcherInputProcessor);
			Gdx.input.setCatchBackKey(false);
		}
	};

	// -------------------------------------------------------------------------
	// Inputs
	// -------------------------------------------------------------------------

	private final InputProcessor launcherInputProcessor = new InputAdapter() {
		private boolean isDragged;
		private float firstY;
		private float lastY;

		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			firstY = lastY = y;
			isDragged = false;
			return true;
		}

		@Override
		public boolean touchDragged(int x, int y, int pointer) {
			float threshold = 0.5f * Gdx.graphics.getPpcY();
			if (Math.abs(y - firstY) > threshold && !isDragged) {
				isDragged = true;
				lastY = y;
			}

			if (isDragged) {
				float dy = (y - lastY) * camera.viewportHeight / Gdx.graphics.getHeight();
				camera.translate(0, dy, 0);
				trimCamera();
				camera.update();
				lastY = y;
			}

			return true;
		}

		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			if (!isDragged) {
				Vector3 v = new Vector3(x, y, 0);
				camera.unproject(v);

				Tile tile = getOverTile(v.x, v.y);

				if (tile != null) {
					tiles.remove(tile);
					tiles.add(tile);
					tile.maximize(maximizeCallback);
					Gdx.input.setInputProcessor(null);
					hideTitle(0.4F);
				}
			}

			return true;
		}

		@Override
		public boolean scrolled(int amount) {
			camera.position.y += amount > 0 ? -0.1f : 0.1f;
			trimCamera();
			camera.update();
			return true;
		}

		private Tile getOverTile(float x, float y) {
			for (int i=0; i<tiles.size(); i++)
				if (tiles.get(i).isOver(x, y)) return tiles.get(i);
			return null;
		}

		private void trimCamera() {
			int linesCntMinusOne = Math.max(tiles.size()-1, 0) / TILES_PER_LINE;
			float min = -linesCntMinusOne * (tileH + TILES_PADDING) + camera.viewportHeight/2;
			float max = 0;

			camera.position.y = Math.max(camera.position.y, min);
			camera.position.y = Math.min(camera.position.y, max);
		}
	};

	private final InputMultiplexer testInputMultiplexer = new InputMultiplexer();
	private final InputProcessor testInputProcessor = new InputAdapter() {
		@Override
		public boolean keyDown(int keycode) {
			if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && selectedTile != null) {
				closeSelectedTile();
			}

			return false;
		}
	};
}
