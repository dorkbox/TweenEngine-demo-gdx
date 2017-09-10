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
package dorkbox.demo.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.demo.Assets;
import dorkbox.demo.Test;
import dorkbox.tweenengine.TweenEngine;
import dorkbox.tweenengine.TweenEquations;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Functions extends Test {
    private Sprite functions1;
	private Sprite functions2;
	private Sprite functions3;
	private int state;

    public
    Functions(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Easing functions";
	}

	@Override
	public String getInfo() {
		return "The most common easing functions - used in JQuery and Flash - are available, plus "
			+ "your own (touch to switch functions).";
	}

	@Override
	public String getImageName() {
		return "tile-functions";
	}

	@Override
	public InputProcessor getInput() {
		return inputProcessor;
	}

	@Override
	protected void initializeOverride() {
		TextureAtlas atlas = Assets.inst().get("test/pack", TextureAtlas.class);
		functions1 = atlas.createSprite("functions1");
		functions2 = atlas.createSprite("functions2");
		functions3 = atlas.createSprite("functions3");

		functions1.setSize(3.5F * functions1.getWidth() / functions1.getHeight(), 3.5F);
		functions2.setSize(3.5F * functions2.getWidth() / functions2.getHeight(), 3.5F);
		functions3.setSize(2.5F * functions3.getWidth() / functions3.getHeight(), 2.5F);

		center(functions1, -3.5F, 0.5F);
		center(functions2, -3.5F, 0.5F);
		center(functions3, -3.5F, 1.0F);

		functions1.setColor(1, 1, 1, 0);
		functions2.setColor(1, 1, 1, 0);
		functions3.setColor(1, 1, 1, 0);

		createSprites(4);
		enableDots(0);
		enableDots(1);
		enableDots(2);
		enableDots(3);

		center(sprites[0], -2, +2);
		center(sprites[1], -2, +1);
		center(sprites[2], -2, +0);
		center(sprites[3], -2, -1);

		startFunctions1(0.5F);
	}

	@Override
	protected void disposeOverride() {
		tweenEngine.cancelAll();
	}

	@Override
	protected void renderOverride() {
        tweenEngine.update(Gdx.graphics.getDeltaTime());

        batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if (functions1.getColor().a > 0.1F) functions1.draw(batch);
		if (functions2.getColor().a > 0.1F) functions2.draw(batch);
		if (functions3.getColor().a > 0.1F) functions3.draw(batch);

		batch.end();
	}

	private void reset() {
        tweenEngine.cancelAll();

        center(sprites[0], -2, +2);
        center(sprites[1], -2, +1);
        center(sprites[2], -2, +0);
        center(sprites[3], -2, -1);
	}

	private void startFunctions1(float delay) {
        state = 0;
		enableDots(3);

        tweenEngine.createParallel()
                   .push(tweenEngine.to(functions1, SpriteAccessor.OPACITY, 0.4F).target(1))
                   .push(tweenEngine.to(functions2, SpriteAccessor.OPACITY, 0.4F).target(0))
                   .push(tweenEngine.to(functions3, SpriteAccessor.OPACITY, 0.4F).target(0))
                   .push(tweenEngine.to(sprites[3], SpriteAccessor.OPACITY, 0.4F).target(1))
                   .start();

        tweenEngine.createSequential()
                   .beginParallel()
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Quad_InOut))
                   .push(tweenEngine.to(sprites[1], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Cubic_InOut))
                   .push(tweenEngine.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Quart_InOut))
                   .push(tweenEngine.to(sprites[3], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Quint_InOut))
                   .end()
                   .pushPause(0.5F)
                   .repeat(-1, 1.0f)
                   .delay(delay)
                   .start();
	}

	private void startFunctions2(float delay) {
		state = 1;

        tweenEngine.createParallel()
                   .push(tweenEngine.to(functions1, SpriteAccessor.OPACITY, 0.4F).target(0))
                   .push(tweenEngine.to(functions2, SpriteAccessor.OPACITY, 0.4F).target(1))
                   .push(tweenEngine.to(functions3, SpriteAccessor.OPACITY, 0.4F).target(0))
                   .push(tweenEngine.to(sprites[3], SpriteAccessor.OPACITY, 0.4F).target(1))
                   .start();

        tweenEngine.createSequential()
                   .beginParallel()
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Circle_InOut))
                   .push(tweenEngine.to(sprites[1], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Sine_InOut))
                   .push(tweenEngine.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Expo_InOut))
                   .push(tweenEngine.to(sprites[3], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Linear))
                   .end()
                   .pushPause(0.5F)
                   .repeat(-1, 1.0F)
                   .delay(delay)
                   .start();
	}

	private void startFunctions3(float delay) {
		state = 2;
		disableDots(3);

        tweenEngine.createParallel()
                   .push(tweenEngine.to(functions1, SpriteAccessor.OPACITY, 0.4F).target(0))
                   .push(tweenEngine.to(functions2, SpriteAccessor.OPACITY, 0.4F).target(0))
                   .push(tweenEngine.to(functions3, SpriteAccessor.OPACITY, 0.4F).target(1))
                   .push(tweenEngine.to(sprites[3], SpriteAccessor.OPACITY, 0.4F).target(0))
                   .start();

        tweenEngine.createSequential()
                   .beginParallel()
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Back_Out))
                   .push(tweenEngine.to(sprites[1], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Elastic_Out))
                   .push(tweenEngine.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0F).targetRelative(6, 0).ease(TweenEquations.Bounce_Out))
                   .end()
                   .pushPause(0.5F)
                   .repeat(-1, 1.0F)
                   .delay(delay)
                   .start();
	}

	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			switch (state) {
				case 0: reset(); startFunctions2(1.0F); break;
				case 1: reset(); startFunctions3(1.0F); break;
				case 2: reset(); startFunctions1(1.0F); break;
			}

			return true;
		}
	};
}
