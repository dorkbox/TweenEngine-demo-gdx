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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dorkbox.accessors.SpriteAccessor;
import dorkbox.tweenengine.BaseTween;
import dorkbox.tweenengine.TweenCallback;
import dorkbox.tweenengine.TweenEngine;
import dorkbox.tweenengine.TweenEquations;
import dorkbox.tweenengine.UpdateAction;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public
class SplashScreen  {
    private static final int PX_PER_METER = 400;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final SpriteBatch batch = new SpriteBatch();

    private final UpdateAction<Void> callback;
    private final TweenEngine tweenEngine;
    private final TweenCallback tweenCallback;
    private boolean finishedAnimation = false;

    private final Sprite universal;
    private final Sprite tween;
    private final Sprite engine;
    private final Sprite logo;
    private final Sprite strip;
    private final Sprite powered;
    private final Sprite gdx;
    private final Sprite veil;
    private final TextureRegion gdxTex;

    public
    SplashScreen(UpdateAction<Void> callback, final TweenEngine tweenEngine) {
        this.callback = callback;
        this.tweenEngine = tweenEngine;

        tweenCallback = new TweenCallback() {
            @Override
            public
            void onEvent(final int type, final BaseTween<?> source) {
                finishedAnimation = true;
            }
        };


        TextureAtlas atlas = Assets.inst()
                                   .get("splash/pack", TextureAtlas.class);
        universal = atlas.createSprite("universal");
        tween = atlas.createSprite("tween");
        engine = atlas.createSprite("engine");
        logo = atlas.createSprite("logo");
        strip = atlas.createSprite("white");
        powered = atlas.createSprite("powered");
        gdx = atlas.createSprite("gdxblur");
        veil = atlas.createSprite("white");
        gdxTex = atlas.findRegion("gdx");

        float wpw = 1.0f;
        float wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        camera.viewportWidth = wpw;
        camera.viewportHeight = wph;
        camera.update();

        final InputProcessor inputProcessor = new InputAdapter() {
            @Override
            public
            boolean touchUp(int x, int y, int pointer, int button) {
                tweenEngine.to(veil, SpriteAccessor.OPACITY, 0.7f)
                        .target(1)
                        .addCallback(tweenCallback)
                        .start();
                return true;
            }
        };
        Gdx.input.setInputProcessor(inputProcessor);

        Sprite[] sprites = new Sprite[] {universal, tween, engine, logo, powered, gdx};
        for (Sprite sp : sprites) {
            sp.setSize(sp.getWidth() / PX_PER_METER, sp.getHeight() / PX_PER_METER);
            sp.setOrigin(sp.getWidth() / 2, sp.getHeight() / 2);
        }

        universal.setPosition(-0.325F, 0.028F);
        tween.setPosition(-0.320F, -0.066F);
        engine.setPosition(0.020F, -0.087F);
        logo.setPosition(0.238F, 0.022F);

        strip.setSize(wpw, wph);
        strip.setOrigin(wpw / 2, wph / 2);
        strip.setPosition(-wpw / 2, -wph / 2);

        powered.setPosition(-0.278F, -0.025F);
        gdx.setPosition(0.068F, -0.077F);

        veil.setSize(wpw, wph);
        veil.setPosition(-wpw / 2, -wph / 2);
        veil.setColor(1, 1, 1, 0);

        tweenEngine.createSequential()
                // init
                .push(tweenEngine.set(tween, SpriteAccessor.POS_XY)
                              .targetRelative(-1, 0))
                .push(tweenEngine.set(engine, SpriteAccessor.POS_XY)
                              .targetRelative(1, 0))
                .push(tweenEngine.set(universal, SpriteAccessor.POS_XY)
                              .targetRelative(0, 0.5F))
                .push(tweenEngine.set(logo, SpriteAccessor.SCALE_XY)
                              .target(7, 7))
                .push(tweenEngine.set(logo, SpriteAccessor.OPACITY)
                              .target(0))
                .push(tweenEngine.set(strip, SpriteAccessor.SCALE_XY)
                              .target(1, 0))
                .push(tweenEngine.set(powered, SpriteAccessor.OPACITY)
                              .target(0))
                .push(tweenEngine.set(gdx, SpriteAccessor.OPACITY)
                              .target(0))

                .push(tweenEngine.to(strip, SpriteAccessor.SCALE_XY, 0.8F)
                              .target(1, 0.6F)
                              .ease(TweenEquations.Back_Out))
                .push(tweenEngine.to(tween, SpriteAccessor.POS_XY, 0.5F)
                              .targetRelative(1, 0)
                              .ease(TweenEquations.Quart_Out))
                .push(tweenEngine.to(engine, SpriteAccessor.POS_XY, 0.5F)
                              .targetRelative(-1, 0)
                              .ease(TweenEquations.Quart_Out))
                .push(tweenEngine.to(universal, SpriteAccessor.POS_XY, 0.6F)
                              .targetRelative(0, -0.5F)
                              .ease(TweenEquations.Bounce_Out))

                .pushPause(0.5F)

                .beginParallel()
                    .push(tweenEngine.set(logo, SpriteAccessor.OPACITY)
                                 .target(1))
                    .push(tweenEngine.to(logo, SpriteAccessor.SCALE_XY, 0.8F)
                                  .target(1, 1)
                                  .ease(TweenEquations.Back_Out))
                .end()

                .push(tweenEngine.to(strip, SpriteAccessor.SCALE_XY, 0.5F)
                              .target(1, 1)
                              .ease(TweenEquations.Back_In))

                .pushPause(0.3F)

                .beginParallel()
                    .push(tweenEngine.to(tween, SpriteAccessor.POS_XY, 0.5F)
                                  .targetRelative(1, 0)
                                  .ease(TweenEquations.Back_In))
                    .push(tweenEngine.to(engine, SpriteAccessor.POS_XY, 0.5F)
                                  .targetRelative(1, 0)
                                  .ease(TweenEquations.Back_In))
                    .push(tweenEngine.to(universal, SpriteAccessor.POS_XY, 0.5F)
                                  .targetRelative(1, 0)
                                  .ease(TweenEquations.Back_In))
                    .push(tweenEngine.to(logo, SpriteAccessor.POS_XY, 0.5F)
                                  .targetRelative(1, 0)
                                  .ease(TweenEquations.Back_In))
                .end()

                .pushPause(0.3F)

                .push(tweenEngine.to(powered, SpriteAccessor.OPACITY, 0.3F)
                              .target(1))
                .beginParallel()
                    .push(tweenEngine.to(gdx, SpriteAccessor.OPACITY, 1.5F)
                                  .target(1)
                                  .ease(TweenEquations.Cubic_In))
                    .push(tweenEngine.to(gdx, SpriteAccessor.ROTATION, 2.0F)
                                  .target(360 * 15)
                                  .ease(TweenEquations.Quad_Out))
                .end()
                .pushPause(0.3f)

                .push(tweenEngine.to(gdx, SpriteAccessor.SCALE_XY, 0.6F)
                              .waypoint(1.6F, 0.4F)
                              .target(1.2F, 1.2F)
                              .ease(TweenEquations.Cubic_Out))
                .pushPause(0.3f)
                .beginParallel()
                    .push(tweenEngine.to(powered, SpriteAccessor.POS_XY, 0.5F)
                                  .targetRelative(1, 0)
                                  .ease(TweenEquations.Back_In))
                    .push(tweenEngine.to(gdx, SpriteAccessor.POS_XY, 0.5F)
                                  .targetRelative(1, 0)
                                  .ease(TweenEquations.Back_In))
                .end()
                .pushPause(0.3F)

                .addCallback(tweenCallback)
                .start();
    }

    public
    void dispose() {
        tweenEngine.cancelAll();
        batch.dispose();
    }

    public
    void render() {
        tweenEngine.update(Gdx.graphics.getDeltaTime());

        if (gdx.getRotation() > 360 * 15 - 20) {
            gdx.setRegion(gdxTex);
        }

        GL20 gl = Gdx.gl20;
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gl.glEnable(GL20.GL_BLEND);
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
            strip.draw(batch);
            universal.draw(batch);
            tween.draw(batch);
            engine.draw(batch);
            logo.draw(batch);
            powered.draw(batch);
            gdx.draw(batch);

            if (veil.getColor().a > 0.1f) {
                veil.draw(batch);
            }
        batch.end();

        if (finishedAnimation) {
            callback.onEvent(null);
        }
    }
}
