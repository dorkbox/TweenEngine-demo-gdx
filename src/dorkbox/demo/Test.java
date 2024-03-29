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
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.tweenEngine.BaseTween;
import dorkbox.tweenEngine.TweenCallback;
import dorkbox.tweenEngine.TweenEngine;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public abstract class Test {
	private final TextureAtlas atlas;
	private final Sprite background;
	private final Sprite veil;
	private final Sprite infoBack;
	private final List<Sprite> dots = new ArrayList<Sprite>(50);
    protected final TweenEngine tweenEngine;
    private boolean[] useDots;
	private Callback callback;

	protected final OrthographicCamera camera = new OrthographicCamera();
	protected final SpriteBatch batch = new SpriteBatch();
	protected final Random rand = new Random();
	protected final BitmapFont font;
	protected final float wpw = 10;
	protected final float wph = 10 * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
	protected Sprite[] sprites;

    GlyphLayout layout = new GlyphLayout();

	public Test(final TweenEngine tweenEngine) {
        this.tweenEngine = tweenEngine;

        atlas = Assets.inst().get("test/pack", TextureAtlas.class);
		background = atlas.createSprite("background");
		veil = atlas.createSprite("white");
		infoBack = atlas.createSprite("white");

		int w = Gdx.graphics.getWidth();
		if (w > 600) font = Assets.inst().get("arial-24.fnt", BitmapFont.class);
		else font = Assets.inst().get("arial-16.fnt", BitmapFont.class);
	}

	// -------------------------------------------------------------------------
	// Abstract API
	// -------------------------------------------------------------------------

	public abstract String getTitle();
	public abstract String getInfo();
	public abstract String getImageName();
	public abstract InputProcessor getInput();
	protected abstract void initializeOverride();
	protected abstract void disposeOverride();
	protected abstract void renderOverride();

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public
    interface Callback {
		void closeRequested(Test source);
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public void initialize() {
		if (isCustomDisplay()) {
			initializeOverride();
			return;
		}

		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		background.setSize(wpw, wpw * background.getHeight() / background.getWidth());
		background.setPosition(-wpw/2, -background.getHeight()/2);

		veil.setSize(wpw, wph);
		veil.setPosition(-wpw/2, -wph/2);

		infoBack.setColor(0, 0, 0, 0.3f);
		infoBack.setPosition(0, 0);

		initializeOverride();

        tweenEngine.set(veil, SpriteAccessor.OPACITY).target(1).start();
        tweenEngine.to(veil, SpriteAccessor.OPACITY, 0.5F).target(0).start();
	}

	public void dispose() {
		dots.clear();
		sprites = null;
		useDots = null;

		disposeOverride();
	}

	public void render() {
		if (isCustomDisplay()) {
			renderOverride();
			return;
		}

		// update
        for (int i = 0; i < dots.size(); i++) {
            if (dots.get(i)
                    .getScaleX() < 0.1f) {
                dots.remove(i);
            }
        }

        // render
		GL20 gl = Gdx.gl20;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL20.GL_BLEND);
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.disableBlending();
		background.draw(batch);
		batch.enableBlending();
		for (int i=0; i<dots.size(); i++) dots.get(i).draw(batch);
		for (int i=0; i<sprites.length; i++) sprites[i].draw(batch);
		batch.end();

		renderOverride();

		if (getInfo() != null) {
			int padding = 15;
            layout.setText(font, getInfo(), Color.WHITE, w - padding*2, Align.left, true);

			infoBack.setSize(w, layout.height + padding*2);
			font.setColor(Color.WHITE);

			batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);

			batch.begin();
			infoBack.draw(batch);
			font.draw(batch, getInfo(), padding, layout.height + padding, w - padding*2, 0, true);
			batch.end();
		}

		if (veil.getColor().a > 0.1f) {
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			veil.draw(batch);
			batch.end();
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	protected boolean isCustomDisplay() {
		return false;
	}

	protected void forceClose() {
		if (callback != null) callback.closeRequested(this);
	}

	protected void createSprites(int cnt) {
		sprites = new Sprite[cnt];
		useDots = new boolean[cnt];

		for (int i=0; i<cnt; i++) {
			int idx = rand.nextInt(400)/100 + 1;
			sprites[i] = atlas.createSprite("sprite" + idx);
			sprites[i].setSize(1.0F, 1.0F * sprites[i].getHeight() / sprites[i].getWidth());
			sprites[i].setOrigin(sprites[i].getWidth()/2, sprites[i].getHeight()/2);
			useDots[i] = false;
		}
	}

	protected void center(Sprite sp, float x, float y) {
		sp.setPosition(x - sp.getWidth()/2, y - sp.getHeight()/2);
	}

	protected void enableDots(int spriteId) {
		useDots[spriteId] = true;

        tweenEngine.call(dotCallback)
                   .delay(0.02F)
                   .repeat(-1, 0.02F)
                   .setUserData(spriteId)
                   .start();
	}

	protected void disableDots(int spriteId) {
		useDots[spriteId] = false;
	}

	private final Vector2 v2 = new Vector2();
	private final Vector3 v3 = new Vector3();
	protected Vector2 touch2world(int x, int y) {
		v3.set(x, y, 0);
		camera.unproject(v3);
		return v2.set(v3.x, v3.y);
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private final TweenCallback dotCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween source) {
			int spriteId = (Integer) source.getUserData();

			if (!useDots[spriteId]) source.cancel();

			Sprite sp = sprites[spriteId];

			Sprite dot = atlas.createSprite("dot");
			dot.setSize(0.2F, 0.2F);
			dot.setOrigin(0.1F, 0.1F);
			dot.setPosition(sp.getX(), sp.getY());
			dot.translate(sp.getWidth()/2, sp.getHeight()/2);
			dot.translate(-dot.getWidth()/2, -dot.getHeight()/2);
			dots.add(dot);

            tweenEngine.to(dot, SpriteAccessor.SCALE_XY, 1.0F).target(0, 0).start();
		}
	};

	// -------------------------------------------------------------------------
	// Dummy
	// -------------------------------------------------------------------------

	public static final Test dummy = new Test(null) {
		@Override public String getTitle() {return "Dummy test";}
		@Override public String getInfo() {return null;}
		@Override public String getImageName() {return null;}
		@Override public InputProcessor getInput() {return null;}
		@Override protected void initializeOverride() {}
		@Override protected void disposeOverride() {}
		@Override protected void renderOverride() {}
	};
}
