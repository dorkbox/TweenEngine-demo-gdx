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
import com.badlogic.gdx.utils.Align;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.demo.Test;
import dorkbox.tweenengine.TweenEngine;
import dorkbox.tweenengine.TweenEquations;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com
 */
public class Info extends Test {
	private final String msg;

    public Info(final TweenEngine tweenEngine) {
        super(tweenEngine);

        msg = "The Universal Tween Engine enables the interpolation of every "
			+ "attributes from any object in any Java project (being an opengl "
			+ "or java2d game, an android application, a swing/swt interface "
			+ "or even a console project).\n"
			+ "\n"
			+ "This project was considered dead, and thus was forked in order "
            + "to continue development, features and fixes by dorkbox.\n"
			+ "\n"
			+ ":: Project page\n"
            + "https://github.com/dorkbox/TweenEngine\n"
			+ "\n"
			+ ":: Original Project page (no longer updated)\n"
			+ "http://code.google.com/p/java-universal-tween-engine/";
	}

	@Override
	public String getTitle() {
		return "Information";
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public String getImageName() {
		return "tile-info";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected void initializeOverride() {
		createSprites(1);
		center(sprites[0], wpw/2-0.8F, -wph/2+0.8F);
        tweenEngine.to(sprites[0], SpriteAccessor.ROTATION, 3.0F).target(360).ease(TweenEquations.Linear).repeat(-1, 0).start();
	}

	@Override
	protected void disposeOverride() {
        tweenEngine.cancelAll();
	}

	@Override
	protected void renderOverride() {
        tweenEngine.update(Gdx.graphics.getDeltaTime());

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h);
		batch.begin();
		font.draw(batch, msg, 20, h-20, w-40, Align.left, true);
		batch.end();
	}
}
