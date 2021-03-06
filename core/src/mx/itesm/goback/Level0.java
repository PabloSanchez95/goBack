package mx.itesm.goback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by pablo on 18/03/17.
 */

class Level0 implements Screen {


    Preferences pref = Gdx.app.getPreferences("getLevel");
    Preferences soundPreferences = Gdx.app.getPreferences("My Preferences");


    private final App app;
    private AssetManager aManager;
    private GameState state = GameState.PLAYING;

    public static final float WIDTH = 1280;
    public static final float HEIGHT = 720;
    public static final float HALFW = WIDTH / 2;
    public static final float HALFH = HEIGHT / 2;

    private Viewport view;

    // camera
    private OrthographicCamera camera;

    // textures
    private Texture background;
    private Texture boat;
    private Texture oar;
    private Texture charon0;
    private Texture charon1;

    private SpriteBatch batch;

    private float accumulator;

    //charon
    private Sprite charonSprite0;
    private Sprite charonSprite1;

    // boat atributes
    private Sprite boatSprite;
    private float boatXPosition;
    boolean boatRotateLeft = true;

    //oar atribute
    private Sprite oarSprite;
    private float oarXPosition;
    boolean oarRotateLeft = false;

    //textboxes
    private Dialogue dialogue;
    private GlyphLayout glyph = new GlyphLayout();

    private Music bgMusic;

    public Level0(App app) {
        this.app = app;
        this.aManager = app.getAssetManager();
    }

    @Override
    public void show() {
        cameraInit();
        texturesInit();
        objectInit();
        Gdx.input.setInputProcessor(null);
        musicInit();
    }

    private void musicInit() {
        bgMusic = aManager.get("MUSIC/GoBackMusicMainMenu.mp3");
        if(soundPreferences.getBoolean("soundOn")) {

            bgMusic.setLooping(true);
            bgMusic.play();
        }
    }

    private void cameraInit() {
        camera = new OrthographicCamera(WIDTH, HEIGHT);
        camera.position.set(HALFW, HALFH, 0);
        camera.update();
        view = new StretchViewport(WIDTH, HEIGHT);

    }

    private void texturesInit() {
        background = aManager.get("INTRO/INTROBackground.png");
        boat = aManager.get("INTRO/INTROBoat.png");
        oar = aManager.get("INTRO/INTROOar.png");
        charon0 = aManager.get("INTRO/INTROAbundioDialogue.png");
        charon1 = aManager.get("INTRO/INTROAbundioDialogueBlink.png");
    }

    private void objectInit() {

        batch = new SpriteBatch();
        dialogue = new Dialogue(aManager);

        // background
        Image backImg = new Image(background);
        backImg.setPosition(WIDTH / 2 - backImg.getWidth() / 2, HEIGHT / 2 - backImg.getHeight() / 2);

        //boat
        boatSprite = new Sprite(boat);
        boatXPosition = -boatSprite.getWidth();

        // oar
        oarSprite = new Sprite(oar);
        oarSprite.setRotation(-13);
        oarXPosition = -oarSprite.getWidth();

        //charon
        charonSprite0 = new Sprite(charon0);
        charonSprite1 = new Sprite(charon1);

        Gdx.input.setCatchBackKey(true);

    }


    @Override
    public void render(float delta) {
        accumulator += delta;
        cls();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        moveObject(delta, boatSprite, boatXPosition, boatRotateLeft, "boat");
        moveObject(delta, oarSprite, oarXPosition, oarRotateLeft, "oar");
        batch.draw(background, 0, 0);

        boatSprite.draw(batch);
        boatSprite.setPosition(boatXPosition, HALFH - boatSprite.getHeight() / 2);

        oarSprite.draw(batch);
        oarSprite.setPosition(oarXPosition, HALFH - oarSprite.getHeight() / 2 - 80);

        if (boatSprite.getX() > 1250) {
            changeScreen(boatSprite);
        }
        if(boatSprite.getX() > 300 && boatSprite.getX() < 1100){
            dialogue.makeText(glyph, batch, "We only come to sleep.\nWe only come to dream.", charonSprite0, "Abundio",  camera.position.x);
        }

        batch.end();


    }

    private void cls() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void changeScreen(Sprite sprite) {
        pref.putInteger("level",1);
        pref.flush();
        app.setScreen(new Fade(app, LoaderState.LEVEL1));
        bgMusic.stop();
        this.dispose();
    }

    private void moveObject(float delta, Sprite sprite, float xPosition, boolean rotateLeft, String type) {

        if(!(accumulator > 8.5 && accumulator < 13)) xPosition += delta * 100;

        if (rotateLeft) {
            sprite.rotate((float) 0.25);
        } else {
            sprite.rotate((float) -0.25);
        }
        if (sprite.getRotation() > 5) {
            rotateLeft = false;
        } else if (sprite.getRotation() < -20) {
            rotateLeft = true;
        }
            if (type.equals("boat")) {
                boatXPosition = xPosition;
                boatRotateLeft = rotateLeft;
            } else {
                oarXPosition = xPosition;
                oarRotateLeft = rotateLeft;
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
        aManager.unload("INTRO/INTROBackground.png");
        aManager.unload("INTRO/INTROBoat.png");
        aManager.unload("INTRO/INTROOar.png");
    }

}