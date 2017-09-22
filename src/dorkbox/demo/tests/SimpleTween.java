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
import com.badlogic.gdx.math.Vector2;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.demo.Test;
import dorkbox.tweenEngine.TweenEngine;
import dorkbox.tweenEngine.TweenEquations;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class SimpleTween extends Test {
    public
    SimpleTween(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Simple Tween";
	}

	@Override
	public String getInfo() {
		return "A 'tween' is an interpolation from a value to an other. "
			+ "(Click anywhere to start a 'position tween')";
	}

	@Override
	public String getImageName() {
		return "tile-tween";
	}

	@Override
	public InputProcessor getInput() {
		return inputProcessor;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		enableDots(0);
		center(sprites[0], 0.0F, 0.0F);
	}

	@Override
	protected void disposeOverride() {
        tweenEngine.cancelAll();
	}

	@Override
	protected void renderOverride() {
        tweenEngine.update(Gdx.graphics.getDeltaTime());
	}

	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchDown(int x, int y, int pointer, int button) {
			Vector2 v = touch2world(x, y);

            tweenEngine.cancelAll();

            tweenEngine.to(sprites[0], SpriteAccessor.CPOS_XY, 0.6F)
                       .target(v.x, v.y)
                       .ease(TweenEquations.Cubic_InOut)
                       .start();

			return true;
		}
	};
}
