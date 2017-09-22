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
import dorkbox.tweenEngine.TweenEngine;
import dorkbox.tweenEngine.TweenEquations;
import dorkbox.tweenEngine.TweenPaths;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Types extends Test {
    public
    Types(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Attributes";
	}

	@Override
	public String getInfo() {
		return "It is up to you to define what attributes you want to animate, "
			+ "you just need imagination!";
	}

	@Override
	public String getImageName() {
		return "tile-types";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(5);
		enableDots(2);
		center(sprites[0], -3, +1.5F);
		center(sprites[1], +0, +1.5F);
		center(sprites[2], -3, -1.0F);
		center(sprites[3], +0, -1.0F);
		center(sprites[4], +3, -1.0F);

        tweenEngine.to(sprites[0], SpriteAccessor.SCALE_XY, 0.5F)
                   .ease(TweenEquations.Back_InOut)
                   .target(2F, 2F)
                   .repeatAutoReverse(-1, 0.6F)
                   .start();

        tweenEngine.to(sprites[1], SpriteAccessor.OPACITY, 0.7F)
                   .target(0F)
                   .repeatAutoReverse(-1, 0.5F)
                   .start();

        tweenEngine.to(sprites[2], SpriteAccessor.CPOS_XY, 1.0F)
                   .ease(TweenEquations.Back_InOut)
                   .target(3F, 1.5F)
                   .repeatAutoReverse(-1, 0.5F)
                   .start();

        tweenEngine.to(sprites[3], SpriteAccessor.ROTATION, 1.0F)
                   .target(580F)
                   .ease(TweenEquations.Cubic_InOut)
                   .repeatAutoReverse(-1, 0.7F)
                   .start();

        tweenEngine.to(sprites[4], SpriteAccessor.TINT, 5.5F)
                   .waypoint(1, 0, 0)
                   .waypoint(1, 1, 1)
                   .waypoint(0, 1, 0)
                   .waypoint(1, 1, 1)
                   .waypoint(0, 0, 1)
                   .target(1, 1, 1)
                   .path(TweenPaths.Linear)
                   .repeat(-1, 0.0F)
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
