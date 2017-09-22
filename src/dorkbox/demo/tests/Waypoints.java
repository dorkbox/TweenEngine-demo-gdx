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
import dorkbox.tweenEngine.TweenPaths;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Waypoints extends Test {
    public
    Waypoints(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Bezier paths";
	}

	@Override
	public String getInfo() {
		return "Tweens can navigate through waypoints, which define a 'bezier' path (here "
			+ "using a Catmull-Rom spline).";
	}

	@Override
	public String getImageName() {
		return "tile-path";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		enableDots(0);
		center(sprites[0], -3, 2);

        tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 3.0F)
                   .waypoint(1F, 1F)
                   .waypoint(-1F, -1F)
                   .waypoint(2F, 0F)
                   .target(3F, -1F)
                   .ease(TweenEquations.Quad_InOut)
                   .path(TweenPaths.CatmullRom)
                   .repeatAutoReverse(Tween.INFINITY, 0.2F)
                   .delay(0.5F)
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
