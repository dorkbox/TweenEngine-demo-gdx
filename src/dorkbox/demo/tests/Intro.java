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

import com.badlogic.gdx.InputProcessor;

import dorkbox.demo.SplashScreen;
import dorkbox.demo.Test;
import dorkbox.tweenEngine.TweenEngine;
import dorkbox.tweenEngine.UpdateAction;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Intro extends Test {
    private SplashScreen splashScreen;

    public
    Intro(final TweenEngine tweenEngine) {
        super(tweenEngine);
    }

    @Override
	public String getTitle() {
		return "Replay intro";
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public String getImageName() {
		return "tile-intro";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	protected boolean isCustomDisplay() {
		return true;
	}

	@Override
	protected void initializeOverride() {
        splashScreen = new SplashScreen(new UpdateAction<Void>() {
            @Override
            public
            void onEvent(final Void updatedObject) {
                forceClose();
            }
        }, tweenEngine);
	}

	@Override
	protected void disposeOverride() {
		splashScreen.dispose();
		splashScreen = null;
	}

	@Override
	protected void renderOverride() {
		splashScreen.render();
	}

}
