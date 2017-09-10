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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.demo.Test;
import dorkbox.tweenengine.BaseTween;
import dorkbox.tweenengine.Timeline;
import dorkbox.tweenengine.TweenCallback;
import dorkbox.tweenengine.TweenEngine;
import dorkbox.tweenengine.TweenEquations;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class TimeManipulation extends Test {
	private boolean canBeRestarted = false;
	private boolean canControlSpeed = false;
	private String text = "";
	private int iterationCnt;
	private float speed;

    public
    TimeManipulation(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Time manipulation";
	}

	@Override
	public String getInfo() {
		return "Time scale can be easily modified in real-time. "
			+ "(Drag the screen to change the animation speed)";
	}

	@Override
	public String getImageName() {
		return "tile-hourglass";
	}

	@Override
	public InputProcessor getInput() {
		return inputProcessor;
	}

	@Override
	protected void initializeOverride() {
		createSprites(4);
		sprites[0].setColor(1, 1, 1, 0);
		sprites[1].setColor(1, 1, 1, 0);
		sprites[2].setColor(1, 1, 1, 0);
		sprites[3].setColor(1, 1, 1, 0);
		launchAnimation();
	}

	@Override
	protected void disposeOverride() {
        tweenEngine.cancelAll();
		canBeRestarted = canControlSpeed = false;
	}

	@Override
	protected void renderOverride() {
        tweenEngine.update(Gdx.graphics.getDeltaTime() * speed);

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		batch.begin();
		font.setColor(Color.WHITE);
		font.draw(batch, "Current speed: " + speed, 15, h - 15);
        font.draw(batch, text, 15, h - 45);
		batch.end();
	}

	// -------------------------------------------------------------------------
	// ANIMATION
	// -------------------------------------------------------------------------

	private void launchAnimation() {
		canControlSpeed = true;
		canBeRestarted = false;
		iterationCnt = 0;
		speed = 1;
        tweenEngine.cancelAll();

		// The callback (to change the text at the right moments)

		TweenCallback callback = new TweenCallback(TweenCallback.Events.START | TweenCallback.Events.BACK_START | TweenCallback.Events.COMPLETE | TweenCallback.Events.BACK_COMPLETE) {
			@Override public void onEvent(int type, BaseTween source) {
				switch (type) {
					case Events.START: text = "Iteration: " + (++iterationCnt) + " / " + 3; break;
					case Events.BACK_START: text = "Iteration: " + (--iterationCnt) + " / " + 3; break;
					case Events.COMPLETE: text = "Forwards play complete (touch to restart)"; canBeRestarted = true; break;
					case Events.BACK_COMPLETE: text = "Backwards play complete (touch to restart)"; canBeRestarted = true; break;
				}
			}
		};

		// The animation itself
        tweenEngine.createParallel()
                   .push(buildSequence(sprites[0], 1, 0.0F, 1.4F))
                   .push(buildSequence(sprites[1], 2, 0.2F, 1.0F))
                   .push(buildSequence(sprites[2], 3, 0.4F, 0.6F))
                   .push(buildSequence(sprites[3], 4, 0.6F, 0.2F))
                   .addCallback(callback)
                   .repeat(2, 0.0F)
                   .start();
	}

	private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
		return tweenEngine.createSequential()
                          .push(tweenEngine.set(target, SpriteAccessor.POS_XY).target(-0.5F, -0.5F))
                          .push(tweenEngine.set(target, SpriteAccessor.SCALE_XY).target(10, 10))
                          .push(tweenEngine.set(target, SpriteAccessor.ROTATION).target(0))
                          .push(tweenEngine.set(target, SpriteAccessor.OPACITY).target(0))
                          .pushPause(delay1)
                          .beginParallel()
                          .push(tweenEngine.to(target, SpriteAccessor.OPACITY, 1.0F).target(1).ease(TweenEquations.Quart_InOut))
                          .push(tweenEngine.to(target, SpriteAccessor.SCALE_XY, 1.0F).target(1, 1).ease(TweenEquations.Quart_InOut))
                          .pushPause(0.5F)
                          .push(tweenEngine.to(target, SpriteAccessor.POS_XY, 1.0F).target((6.0F / 5.0F) * id - 3 - 0.5F, -0.5F).ease(TweenEquations.Back_Out))
                          .end()
                          .push(tweenEngine.to(target, SpriteAccessor.ROTATION, 0.8F).target(360).ease(TweenEquations.Cubic_InOut))
                          .pushPause(delay2)
                          .beginParallel()
                          .push(tweenEngine.to(target, SpriteAccessor.SCALE_XY, 0.3F).target(3, 3).ease(TweenEquations.Quad_In))
                          .push(tweenEngine.to(target, SpriteAccessor.OPACITY, 0.3F).target(0).ease(TweenEquations.Quad_In))
                          .end();
	}

	// -------------------------------------------------------------------------
	// Input
	// -------------------------------------------------------------------------

	private final InputProcessor inputProcessor = new InputAdapter() {
		private int lastX;

		@Override public boolean touchDown(int x, int y, int pointer, int button) {
			if (canBeRestarted) launchAnimation();
			lastX = x;
			return true;
		}

		@Override public boolean touchDragged(int x, int y, int pointer) {
			if (canControlSpeed) {
				float dx = (x - lastX) * camera.viewportWidth / Gdx.graphics.getWidth();
				speed += dx / 4;
			}

			lastX = x;
			return true;
		}
	};
}
