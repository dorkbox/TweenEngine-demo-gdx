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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.demo.tests.Functions;
import dorkbox.demo.tests.Info;
import dorkbox.demo.tests.Intro;
import dorkbox.demo.tests.Repetitions;
import dorkbox.demo.tests.SimpleTimeline;
import dorkbox.demo.tests.SimpleTween;
import dorkbox.demo.tests.TimeManipulation;
import dorkbox.demo.tests.Types;
import dorkbox.demo.tests.Waypoints;
import dorkbox.tweenengine.TweenEngine;
import dorkbox.tweenengine.UpdateAction;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class App implements ApplicationListener {
	private SplashScreen splashScreen;
	private Launcher launcherScreen;
	private boolean isLoaded = false;

    private final TweenEngine tweenEngine = TweenEngine.create()
                                                       .setWaypointsLimit(10)
                                                       .setCombinedAttributesLimit(3)
                                                       .registerAccessor(Sprite.class, new SpriteAccessor())
                                                       .build();

	@Override
	public void create() {
        Assets inst = Assets.inst();

        inst.load("splash/pack", TextureAtlas.class);
        inst.load("launcher/pack", TextureAtlas.class);
        inst.load("test/pack", TextureAtlas.class);
        inst.load("arial-16.fnt", BitmapFont.class);
        inst.load("arial-18.fnt", BitmapFont.class);
        inst.load("arial-20.fnt", BitmapFont.class);
        inst.load("arial-24.fnt", BitmapFont.class);
    }

	@Override
	public void dispose() {
		Assets.inst().dispose();
		if (splashScreen != null) splashScreen.dispose();
		if (launcherScreen != null) launcherScreen.dispose();
	}

	@Override
	public void render() {
		if (isLoaded) {
			if (splashScreen != null) splashScreen.render();
			if (launcherScreen != null) launcherScreen.render();
		} else {
			if (Assets.inst().getProgress() < 1) {
				Assets.inst().update();
			} else {
				launch();
				isLoaded = true;
			}
		}
	}

	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}

	private void launch() {
        // will be called once render is done, and timeline is complete
		splashScreen = new SplashScreen(new UpdateAction<Void>() {
            @Override
            public
            void onEvent(final Void updatedObject) {
                Test[] tests = new Test[] {
                                new Intro(tweenEngine),
                                new Info(tweenEngine),
                                new SimpleTween(tweenEngine),
                                new SimpleTimeline(tweenEngine),
                                new Repetitions(tweenEngine),
                                new TimeManipulation(tweenEngine),
                                new Waypoints(tweenEngine),
                                new Functions(tweenEngine),
                                new Types(tweenEngine)
                };

                splashScreen.dispose();
                splashScreen = null;
				launcherScreen = new Launcher(tests, tweenEngine);
            }
		}, tweenEngine);
	}
}
