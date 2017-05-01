package com.tec.goback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by sergiohernandezjr on 01/05/17.
 */

class Level4 extends Frame {

    private static final float SOPHIE_START_X = 10800;
    private static final float SOPHIE_START_Y = 220;
    private Sophie sophie;

    public static final float WIDTH_MAP = 11520;
    public static final float HEIGHT_MAP = 720;

    public static final float LEFT_LIMIT = 980;
    public static float RIGHT_LIMIT = 11000;


    private Texture sophieTexture;
    private Texture background01;

    private Sprite yellowOrb;
    private Sprite blueOrb;
    private Sprite redOrb;

    Level4(App app) {
        super(app, WIDTH_MAP,HEIGHT_MAP);
    }

    @Override
    public void show() {
        super.show();
        textureInit();
        objectInit();


        sophie.setMovementState(Sophie.MovementState.WAKING_LEFT);
        sophie.sprite.setPosition(SOPHIE_START_X, SOPHIE_START_Y);

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(new Input());
        //musicInit();

    }

    private void textureInit() {
        sophieTexture = new Texture("Squirts/Sophie/SOPHIEWalk.png");
        background01 = new Texture("WOODS/WOODSPanoramic1of2.png");

        sophie = new Sophie(sophieTexture, 100,100);
        yellowOrb = new Sprite((Texture)aManager.get("Interfaces/GAMEPLAY/CONSTANT/GobackCONSTYellowOrb.png"));
        blueOrb = new Sprite((Texture)aManager.get("Interfaces/GAMEPLAY/CONSTANT/GobackCONSTBlueOrb.png"));
        redOrb = new Sprite((Texture)aManager.get("Interfaces/GAMEPLAY/CONSTANT/GobackCONSTRedOrb.png"));
    }

    private void objectInit() {
        batch = new SpriteBatch();
        batch.begin();
        batch.draw(background01,7680,0);
        camera.position.set(7680+background01.getWidth()-HALFW,camera.position.y, 0);
        camera.update();
        batch.end();
    }

    @Override
    public void render(float delta) {
        cls();

        batch.setProjectionMatrix(super.camera.combined);
        batch.begin();

        if(state == GameState.CLUE){
            clueStage.draw();
            Gdx.input.setInputProcessor(clueStage);
        }else if(state == GameState.STATS){
            statsStage.sophieCoins.setText(Integer.toString(statsStage.statsPrefs.getInteger("Coins")));
            statsStage.yellowXPLbl.setText(Integer.toString(statsStage.statsPrefs.getInteger("XP")));
            statsStage.blueXPLbl.setText(Integer.toString(statsStage.statsPrefs.getInteger("XP")));
            statsStage.redXPLbl.setText(Integer.toString(statsStage.statsPrefs.getInteger("XP")));
            statsStage.draw();
            Gdx.input.setInputProcessor(inputMultiplexer);
        }else if (state==GameState.PAUSED) {
            pauseStage.draw();
            Gdx.input.setInputProcessor(pauseStage);
        }else if(state==GameState.PLAYING){
            batch.draw(background01,7680,0);
            batch.draw(pauseButton,camera.position.x+HALFW-pauseButton.getWidth(),camera.position.y-HALFH);

            sophie.draw(batch);
            sophie.update();
        }


        batch.end();

    }

    private class Input implements InputProcessor {
        private Vector3 v = new Vector3();
        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            v.set(screenX,screenY,0);
            camera.unproject(v);

            Gdx.app.log(""+v.x, "");

            if(camera.position.x - v.x < -522 && v.y < 135){
                state = GameState.PAUSED;
            }

            if(sophie.getMovementState()==Sophie.MovementState.STILL_LEFT||sophie.getMovementState()==Sophie.MovementState.STILL_RIGHT) {
                if (v.x >= camera.position.x) {
                    sophie.setMovementState(Sophie.MovementState.MOVE_RIGHT);
                } else {
                    sophie.setMovementState(Sophie.MovementState.MOVE_LEFT);
                }
            }
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(sophie.getMovementState()==Sophie.MovementState.MOVE_LEFT)
                sophie.setMovementState(Sophie.MovementState.STILL_LEFT);
            else if(sophie.getMovementState() == Sophie.MovementState.MOVE_RIGHT)
                sophie.setMovementState(Sophie.MovementState.STILL_RIGHT);
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void cls() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}