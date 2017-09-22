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
import com.badlogic.gdx.InputProcessor;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.demo.Test;
import dorkbox.tweenEngine.Tween;
import dorkbox.tweenEngine.TweenEngine;
import dorkbox.tweenEngine.TweenEquations;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class SimpleTimeline extends Test {
    public
    SimpleTimeline(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Simple Timeline";
	}

	@Override
	public String getInfo() {
		return "A timeline sequences multiple tweens (or other timelines) either one after the other, or all at once!";
	}

	@Override
	public String getImageName() {
		return "tile-timeline";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		center(sprites[0], -3, 2);

        tweenEngine.createSequential()
                   .push(tweenEngine.set(sprites[0], SpriteAccessor.OPACITY).target(0))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.OPACITY, 0.7F).target(1))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7F).target(3, 0).ease(TweenEquations.Cubic_InOut))
                   .beginParallel()
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.ROTATION, 1.0F).target(360))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.OPACITY, 0.5F).target(0).repeatAutoReverse(1, 0))
                   .end()
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7F).target(0, 0).ease(TweenEquations.Quint_InOut))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.SCALE_XY, 0.8F).target(2.5F, 2.5F).ease(TweenEquations.Back_InOut))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.TINT, 0.4F).target(1, 0, 0))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.TINT, 0.4F).target(0, 1, 0))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.TINT, 0.4F).target(0, 0, 1))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.TINT, 0.4F).target(0, 0, 0))
                   .push(tweenEngine.to(sprites[0], SpriteAccessor.OPACITY, 0.7F).target(0))
                   .repeat(Tween.INFINITY, 1.0F)
                   .start();
	}

	@Override
	protected void disposeOverride() {
        tweenEngine.cancelAll();
	}

	@Override
	protected void renderOverride() {
        tweenEngine.update(Gdx.graphics.getDeltaTime());
	}
}
