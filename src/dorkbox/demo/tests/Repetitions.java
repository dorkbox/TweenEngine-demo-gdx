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

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Repetitions extends Test {
    public
    Repetitions(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Repetitions";
	}

	@Override
	public String getInfo() {
		return "Difference between a 'repeat' behavior and a 'repeat w/ AutoReverse' behavior.";
	}

	@Override
	public String getImageName() {
		return "tile-repeat";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(2);
		enableDots(0);
		enableDots(1);
		center(sprites[0], -3, +1);
		center(sprites[1], -3, -1);

        tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 0.7F)
                   .ease(TweenEquations.Cubic_InOut)
                   .target(3F, 1F)
                   .repeat(-1, 0.3F)
                   .delay(0.5F)
                   .start();

        tweenEngine.to(sprites[1], SpriteAccessor.CPOS_XY, 0.7F)
                   .ease(TweenEquations.Cubic_InOut)
                   .target(3F, -1F)
                   .repeatAutoReverse(-1, 0.3F)
                   .delay(0.5F)
                   .start();
	}

	@Override
	protected void disposeOverride() {
        tweenEngine.cancelAll();
	}

    @Override
    protected
    void renderOverride() {
        tweenEngine.update(Gdx.graphics.getDeltaTime());
    }
}
