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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Align;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.tweenengine.TweenCallback;
import dorkbox.tweenengine.TweenEngine;
import dorkbox.tweenengine.TweenEquations;
import dorkbox.tweenengine.primitives.MutableFloat;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Tile {
	private final float x, y;
	private final Test test;
	private final Sprite sprite;
	private final Sprite interactiveIcon;
	private final Sprite veil;
	private final OrthographicCamera camera;
	private final BitmapFont font;
    private final TweenEngine tweenEngine;
    private final MutableFloat textOpacity = new MutableFloat(1);

	public Tile(float x,
                float y,
                float w,
                float h,
                Test test,
                TextureAtlas atlas,
                OrthographicCamera camera,
                BitmapFont font,
                final TweenEngine tweenEngine) {

		this.x = x;
		this.y = y;
		this.test = test;
		this.camera = camera;
		this.font = font;
        this.tweenEngine = tweenEngine;

        this.sprite = test.getImageName() != null ? atlas.createSprite(test.getImageName()) : atlas.createSprite("tile");
		this.interactiveIcon = atlas.createSprite("interactive");
		this.veil = atlas.createSprite("white");

		sprite.setSize(w, h);
		sprite.setOrigin(w/2, h/2);
		sprite.setPosition(x + camera.viewportWidth, y);

		interactiveIcon.setSize(w/10, w/10 * interactiveIcon.getHeight() / interactiveIcon.getWidth());
		interactiveIcon.setPosition(x+w - interactiveIcon.getWidth() - w/50, y+h - interactiveIcon.getHeight() - w/50);
		interactiveIcon.setColor(1, 1, 1, 0);

		veil.setSize(w, h);
		veil.setOrigin(w/2, h/2);
		veil.setPosition(x, y);
		veil.setColor(1, 1, 1, 0);
	}

	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
		if (test.getInput() != null) interactiveIcon.draw(batch);

		float wrapW = (sprite.getWidth() - sprite.getWidth()/10) * Gdx.graphics.getWidth() / camera.viewportWidth;

		font.setColor(1, 1, 1, textOpacity.floatValue());
		font.draw(batch, test.getTitle(),
			sprite.getX() + sprite.getWidth()/20,
			sprite.getY() + sprite.getHeight()*19/20,
                  wrapW, Align.left, true);

		if (veil.getColor().a > 0.1f) veil.draw(batch);
	}

	public void enter(final float delay) {
        tweenEngine.createSequential()
                   .push(tweenEngine.to(sprite, SpriteAccessor.POS_XY, 0.7F).target(x, y).ease(TweenEquations.Cubic_InOut))
                   .pushPause(0.1F)
                   .push(tweenEngine.to(interactiveIcon, SpriteAccessor.OPACITY, 0.4F).target(1))
                   .delay(delay)
                   .start();
	}

	public void maximize(TweenCallback callback) {
		tweenEngine.cancelTarget(interactiveIcon);
        tweenEngine.cancelTarget(textOpacity);
        tweenEngine.cancelTarget(sprite);

		float tx = camera.position.x - sprite.getWidth()/2;
		float ty = camera.position.y - sprite.getHeight()/2;
		float sx = camera.viewportWidth / sprite.getWidth();
		float sy = camera.viewportHeight / sprite.getHeight();

        tweenEngine.createSequential()
                   .push(tweenEngine.set(veil, SpriteAccessor.POS_XY).target(tx, ty))
                   .push(tweenEngine.set(veil, SpriteAccessor.SCALE_XY).target(sx, sy))
                   .beginParallel()
                   .push(tweenEngine.to(textOpacity, 0, 0.2F).target(0))
                   .push(tweenEngine.to(interactiveIcon, SpriteAccessor.OPACITY, 0.2F).target(0))
                   .end()
                   .push(tweenEngine.to(sprite, SpriteAccessor.SCALE_XY, 0.3F).target(0.9F, 0.9F).ease(TweenEquations.Quad_Out))
                   .beginParallel()
                   .push(tweenEngine.to(sprite, SpriteAccessor.SCALE_XY, 0.5F).target(sx, sy).ease(TweenEquations.Cubic_In))
                   .push(tweenEngine.to(sprite, SpriteAccessor.POS_XY, 0.5F).target(tx, ty).ease(TweenEquations.Quad_In))
                   .pushPause(0.3F)
                   .push(tweenEngine.to(veil, SpriteAccessor.OPACITY, 0.7F).target(1))
                   .end()
                   .setUserData(this)
                   .addCallback(callback)
                   .start();
	}

	public void minimize(TweenCallback callback) {
        tweenEngine.cancelTarget(sprite);
        tweenEngine.cancelTarget(textOpacity);

        tweenEngine.createSequential()
                   .push(tweenEngine.set(veil, SpriteAccessor.OPACITY).target(0))
                   .beginParallel()
                   .push(tweenEngine.to(sprite, SpriteAccessor.SCALE_XY, 0.3F).target(1, 1).ease(TweenEquations.Quad_Out))
                   .push(tweenEngine.to(sprite, SpriteAccessor.POS_XY, 0.5F).target(x, y).ease(TweenEquations.Quad_Out))
                   .end()
                   .beginParallel()
                   .push(tweenEngine.to(textOpacity, 0, 0.3F).target(1))
                   .push(tweenEngine.to(interactiveIcon, SpriteAccessor.OPACITY, 0.3F).target(1))
                   .end()
                   .setUserData(this)
                   .addCallback(callback)
                   .start();
	}

	public boolean isOver(float x, float y) {
		return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
			&& sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
	}

	public Test getTest() {
		return test;
	}
}
