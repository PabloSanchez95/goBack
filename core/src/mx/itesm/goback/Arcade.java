package mx.itesm.goback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.graphics.g2d.Animation;

/*
 * Created by gerry on 2/18/17.
 */


@SuppressWarnings("ConstantConditions")
class Arcade extends Frame{


    private Sound shootSound;

    //B2D bodies
    private Array<Body> squirts = new Array<Body>();

    private World world;
    private HashSet<Body> deadThings;
    private ArrayList<Body> wall = new ArrayList<Body>();
    private float cooldown;

    private IArcadeBoss boss;
    private boolean bossFight;
    private boolean bossActive = false;
    private float betweenSpawns = ArcadeValues.initalFrequency;

    private boolean putXp = false;
    private float acumulator = 0;
    private float elapsed = 0;
    private float arcadeMultiplier;

    private float shot = 0;
    private float hit = 0;
    private float match = 0;



    //CURRENT COLOR ORB
    private orbColor currentColor = orbColor.YELLOW;

    //MOTHER FUCKER
    private int d;

    //Textures
    private Texture background; //Background

    private Texture sophieTx;
    private Sprite sophie;

    //orbes
    private boolean died = false;
    private ArcadeOrb[] orbs = new ArcadeOrb[3];
    private Texture orbRed;
    private Texture orbBlue;
    private Texture orbYellow;

    private Texture pelletYellow;
    private Texture pelletBlue;
    private Texture pelletRed;

    private Texture lizard;
    private Texture goo;
    private Texture skull;
    private Texture spike;
    private Texture meteor;
    private Texture jaguar;
    private Texture arrowBlue;
    private Texture arrowRed;
    private Texture arrowYellow;

    private Animation<TextureRegion> lizardAnimation;
    private Animation<TextureRegion> yellowGooAnimation;
    private Animation<TextureRegion> blueGooAnimation;
    private Animation<TextureRegion> redGooAnimation;
    private Animation<TextureRegion> skullRedAnimation;
    private Animation<TextureRegion> skullBlueAnimation;
    private Animation<TextureRegion> skullYellowAnimation;
    private Animation<TextureRegion> jaguarAnimation;

    private Texture bossLizard;
    private Texture bossLizardName;
    private Texture bossLizardSymbol;
    private Sprite bossLizardNameSpr;
    private Sprite bossLizardSymbolSpr;

    private Texture bossJaguar;
    private Texture bossJaguarName;
    private Texture bossJaguarSymbol;
    private Sprite bossJaguarNameSpr;
    private Sprite bossJaguarSymbolSpr;

    private Texture bossEyesY;
    private Texture bossEyesB;
    private Texture bossEyesR;
    private Texture bossEyesYSymbol;
    private Texture bossEyesBSymbol;
    private Texture bossEyesRSymbol;
    private Texture bossEyesName;
    private Sprite bossEyesYSymbolSpr;
    private Sprite bossEyesBSymbolSpr;
    private Sprite bossEyesRSymbolSpr;
    private Sprite bossEyesNameSpr;
    private ArrayList<Sprite> info;

    private static final float WIDTH_MAP = 1280;
    private static final float HEIGHT_MAP = 720;

    private Dialogue dialogue;
    private GlyphLayout glyph = new GlyphLayout();
    private float dialoguetime = 0.0f;

    private Input input;
    private float ac;

    private Preferences stats = Gdx.app.getPreferences("STATS");
    boolean flag;

    private Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;

    private Stage lostScreen;

    private GlyphLayout scoreDisplay;
    private BitmapFont font;
    private float kills;
    


    Arcade(App app) {
        super(app, WIDTH_MAP,HEIGHT_MAP);
    }

    @Override
    public void show() {
        super.show();

        d = pref.getInteger("level");
        bossFight = pref.getBoolean("boss") && ArcadeValues.bossFightFlag;
        arcadeMultiplier = !bossFight ? ArcadeValues.arcadeMultiplier : 1;

        textureInit();
        worldInit();
        allyInit();
        wallsInit();

        debugRenderer = new Box2DDebugRenderer();
        input = new Input();
        dialogue = new Dialogue(aManager);
        Gdx.input.setInputProcessor(input);
        Gdx.input.setCatchBackKey(true);

        font = new BitmapFont(Gdx.files.internal("fira.fnt"));
        scoreDisplay = new GlyphLayout();
    }


    private void textureInit() {
//        lostScreen = new Stage(view);
//
//        //LOST SCREEN
//        Texture LOSTBordersTx = aManager.get("Interfaces/LOST/LOSTBorders.png");
//
//        Texture LOSTContinueTx = aManager.get("Interfaces/LOST/LOSTContinue.png");
//        TextureRegionDrawable LOSTContinueTrd = new TextureRegionDrawable(new TextureRegion(LOSTContinueTx));
//
//        Texture LOSTMenuTx = aManager.get("Interfaces/LOST/LOSTMenu.png");
//        TextureRegionDrawable LOSTMenuTrd = new TextureRegionDrawable(new TextureRegion(LOSTMenuTx));
//
//        Image LOSTBordersImg = new Image(LOSTBordersTx);
//        LOSTBordersImg.setPosition(0,0);
//        lostScreen.addActor(LOSTBordersImg);
//
//        ImageButton LOSTContinueBtn = new ImageButton(LOSTContinueTrd);
//        LOSTContinueBtn.setPosition(500,500);
//        lostScreen.addActor(LOSTContinueBtn);
//
//        ImageButton LOSTMenuBtn = new ImageButton(LOSTMenuTrd);
//        LOSTContinueBtn.setPosition(500,500);
//        lostScreen.addActor(LOSTMenuBtn);



        shootSound = aManager.get("MUSIC/shoot.mp3", Sound.class);
        orbYellow = aManager.get("Interfaces/GAMEPLAY/ARCADE/ARCADEYellowOrb.png");

        orbBlue = aManager.get("Interfaces/GAMEPLAY/ARCADE/ARCADEBlueOrb.png");

        orbRed = aManager.get("Interfaces/GAMEPLAY/ARCADE/ARCADERedOrb.png");


        pelletYellow = aManager.get("PELLET/ATAQUEYellowPellet.png");
        pelletBlue = aManager.get("PELLET/ATAQUEBluePellet.png");
        pelletRed = aManager.get("PELLET/ATAQUERedPellet.png");

        switch (d){
            case 1:
                background = aManager.get("HARBOR/GoBackHARBORPanoramic.png");
                break;
            case 2:
                background = aManager.get("MOUNTAINS/GoBackMOUNTAINSPanoramic.png");
                break;
            case 3:
                background = aManager.get("UNDERGROUND/UNDERGROUNDArcade.png");
                break;
            case 4:
                background = aManager.get("WOODS/WOODSEnding.png");
                break;
            case 5:
                background = aManager.get("HARBOR/GoBackHARBOR0.png");
                break;
        }


        //TODO USE ASSET MANAGER LAZY KOREAN
        lizard=new Texture("MINIONS/LIZARD/MINIONYellowLizard.png");
        goo=new Texture("MINIONS/GOO/MINIONAnimation.png");
        skull=new Texture("SKULL/MINIONSkulls.png");
        spike=new Texture("MINIONS/SPIKE/MINIONYellowSpike00.png");
        jaguar= new Texture("MINIONS/JAGUAR/MINIONJaguarAnimation.png");
        arrowBlue= new Texture("MINIONS/ARROW/MINIONBlueArrow00.png");
        arrowRed= new Texture("MINIONS/ARROW/MINIONRedArrow00.png");
        arrowYellow= new Texture("MINIONS/ARROW/MINIONYellowArrow00.png");

        //LIKE SO
        meteor = aManager.get("MINIONS/METEOR/MINIONMeteor00.png");

        TextureRegion texturaCompleta = new TextureRegion(lizard);
        TextureRegion[][] texturaPersonaje = texturaCompleta.split(227,65);
        lizardAnimation = new Animation(0.18f, texturaPersonaje[0][0], texturaPersonaje[0][1]);
        lizardAnimation.setPlayMode(Animation.PlayMode.LOOP);

        texturaCompleta=new TextureRegion(goo);
        texturaPersonaje=texturaCompleta.split(75,150);
        yellowGooAnimation = new Animation(0.18f, texturaPersonaje[0][0], texturaPersonaje[0][1]);
        yellowGooAnimation.setPlayMode(Animation.PlayMode.LOOP);
        redGooAnimation = new Animation(0.18f, texturaPersonaje[0][2], texturaPersonaje[0][3]);
        redGooAnimation.setPlayMode(Animation.PlayMode.LOOP);
        blueGooAnimation = new Animation(0.18f, texturaPersonaje[0][4], texturaPersonaje[0][5]);
        blueGooAnimation.setPlayMode(Animation.PlayMode.LOOP);

        texturaCompleta=new TextureRegion(skull);
        texturaPersonaje=texturaCompleta.split(128,242);
        skullBlueAnimation = new Animation(0.18f, texturaPersonaje[0][0], texturaPersonaje[0][1]);
        skullRedAnimation = new Animation(0.18f, texturaPersonaje[0][2], texturaPersonaje[0][3]);
        skullYellowAnimation = new Animation(0.18f, texturaPersonaje[0][4], texturaPersonaje[0][5]);
        skullBlueAnimation.setPlayMode(Animation.PlayMode.LOOP);
        skullRedAnimation.setPlayMode(Animation.PlayMode.LOOP);
        skullYellowAnimation.setPlayMode(Animation.PlayMode.LOOP);

        texturaCompleta=new TextureRegion(jaguar);
        texturaPersonaje=texturaCompleta.split(175,65);
        jaguarAnimation=new Animation(0.4f,texturaPersonaje[0][0],texturaPersonaje[0][1]);
        jaguarAnimation.setPlayMode(Animation.PlayMode.LOOP);

        sophieTx = aManager.get("Interfaces/GAMEPLAY/ARCADE/ARCADESophie.png");

        bossLizard = aManager.get("BOSS/IGUANA/BOSSIguanaBody.png");
        bossLizardName = aManager.get("BOSS/IGUANA/BOSSIguanaName.png");
        bossLizardSymbol =  aManager.get("BOSS/IGUANA/BOSSIguanaSymbol.png");
        bossLizardNameSpr = new Sprite(bossLizardName);
        bossLizardSymbolSpr = new Sprite(bossLizardSymbol);

        bossJaguar= aManager.get("BOSS/JAGUAR/BOSSJaguarBody.png");
        bossJaguarName = aManager.get("BOSS/JAGUAR/BOSSJaguarSymbol.png");
        bossJaguarSymbol = aManager.get("BOSS/JAGUAR/BOSSJaguarName.png");
        bossJaguarNameSpr = new Sprite(bossJaguarName);
        bossJaguarSymbolSpr = new Sprite(bossJaguarSymbol);

        bossEyesY = aManager.get("BOSS/BATS/BOSSBatYellow.png");
        bossEyesB = aManager.get("BOSS/BATS/BOSSBatBlue.png");
        bossEyesR = aManager.get("BOSS/BATS/BOSSBatRed.png");
        bossEyesYSymbol = aManager.get("BOSS/BATS/BOSSBatYellowSymbol.png");
        bossEyesBSymbol = aManager.get("BOSS/BATS/BOSSBatBlueSymbol.png");
        bossEyesRSymbol = aManager.get("BOSS/BATS/BOSSBatRedSymbol.png");
        bossEyesName = aManager.get("BOSS/BATS/BOSSBatName.png");
        bossEyesYSymbolSpr = new Sprite(bossEyesYSymbol);
        bossEyesBSymbolSpr = new Sprite(bossEyesBSymbol);
        bossEyesRSymbolSpr = new Sprite(bossEyesRSymbol);
        bossEyesNameSpr = new Sprite(bossEyesName);

        info = new ArrayList<Sprite>();
        if(bossFight){
            switch (d){
                case 1:
                    bossLizardNameSpr.setPosition(210,0);
                    bossLizardSymbolSpr.setPosition(0,0);
                    info.add(bossLizardNameSpr);
                    info.add(bossLizardSymbolSpr);
                    break;
                case 2:
                    bossJaguarNameSpr.setPosition(300,0);
                    bossJaguarSymbolSpr.setPosition(0,0);
                    info.add(bossJaguarNameSpr);
                    info.add(bossJaguarSymbolSpr);
                    break;
                case 3:
                    bossEyesYSymbolSpr.setPosition(0,0);
                    bossEyesBSymbolSpr.setPosition(180,0);
                    bossEyesRSymbolSpr.setPosition(360,0);
                    bossEyesNameSpr.setPosition(530,0);
                    info.add(bossEyesYSymbolSpr);
                    info.add(bossEyesBSymbolSpr);
                    info.add(bossEyesRSymbolSpr);
                    info.add(bossEyesNameSpr);
                    break;
            }
        }

    }

    private void allyInit(){

        switch(d){
            case 1:
                orbs[0] = new ArcadeYellowOrb(world, orbYellow, 0, true, stats.getFloat("YellowLife"));
                break;
            case 2:
                orbs[0] = new ArcadeYellowOrb(world, orbYellow, 0, true, stats.getFloat("YellowLife"));
                orbs[1] = new ArcadeBlueOrb(world, orbBlue, 1, false, stats.getFloat("BlueLife"));
                orbs[2] = null;
                break;
            default:
            case 3:
                orbs[0] = new ArcadeYellowOrb(world, orbYellow, 0, true, stats.getFloat("YellowLife"));
                orbs[1] = new ArcadeBlueOrb(world, orbBlue, 1, false, stats.getFloat("BlueLife"));
                orbs[2] = new ArcadeRedOrb(world, orbRed, 2, false, stats.getFloat("RedLife"));
        }
        sophie = new Sprite(sophieTx);

        if(bossFight){
            switch(d){
                case 1:
                    boss = new ArcadeBoss(world, 1, bossLizard);
                    break;
                case 2:
                    boss = new ArcadeBoss(world, 2, bossJaguar);
                    break;
                case 3:
                    boss = new ArcadeEyes(world, bossEyesY, bossEyesB, bossEyesR);
                    break;
            }
        }
    }

    private void wallsInit(){
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(ArcadeValues.pxToMeters(-120),0);

        wall.add(world.createBody(bd)); //append to body array
        makeWallFixture(wall.get(0),100,HEIGHT_MAP);

        bd.position.set(ArcadeValues.pxToMeters(WIDTH_MAP+120),0);

        wall.add(world.createBody(bd)); //append to body array
        makeWallFixture(wall.get(1),100,HEIGHT_MAP);

        bd.position.set(ArcadeValues.pxToMeters(-120),ArcadeValues.pxToMeters(-120));

        wall.add(world.createBody(bd)); //append to body array
        makeWallFixture(wall.get(2),WIDTH_MAP+200,100);

        bd.position.set(ArcadeValues.pxToMeters(-120), ArcadeValues.pxToMeters(HEIGHT_MAP+120));

        wall.add(world.createBody(bd)); //append to body array
        makeWallFixture(wall.get(3),WIDTH_MAP+200,100);
    }

    private void makeWallFixture(Body b, float x, float y){
        FixtureDef f = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ArcadeValues.pxToMeters(x),ArcadeValues.pxToMeters(y));
        f.shape = shape;

        f.filter.categoryBits = ArcadeValues.wallCat;
        f.filter.maskBits = ArcadeValues.wallMask;
        b.createFixture(f);
    }

    private void worldInit(){
        world = new World(Vector2.Zero, true);
        //fstep = 0f;
        deadThings = new HashSet<Body>();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                //check who the fuck is colliding and update deadThings for deletion
                Object ob1 = contact.getFixtureA().getBody().getUserData();
                Object ob2 = contact.getFixtureB().getBody().getUserData();

                //pellets die no matter who they collide with
                if(ob1 instanceof OrbAttack){
                    deadThings.add(contact.getFixtureA().getBody());
                    //Gdx.app.log("DESTRUC", deadThings.size+"");
                }
                if(ob2 instanceof OrbAttack){
                    deadThings.add(contact.getFixtureB().getBody());
                    //Gdx.app.log("DESTRUC", deadThings.size+"");
                }



                if(ob1 instanceof ArcadeOrb || ob2 instanceof ArcadeOrb) {//If we got hit
                    if (ob1 instanceof ArcadeOrb) {
                        if (((ArcadeOrb)ob1).getHurtDie(((Enemy)ob2).getColor(), ((Enemy)ob2).getDamage())){
                            deadThings.add(contact.getFixtureA().getBody());
                            orbs[ ((ArcadeOrb)ob1).getColor()-1 ] = null;
                            died = true;
                        }
                        deadThings.add(contact.getFixtureB().getBody());
                    }
                    if (ob2 instanceof ArcadeOrb) {
                        if (((ArcadeOrb)ob2).getHurtDie(((Enemy)ob1).getColor(), ((Enemy)ob1).getDamage())){
                            deadThings.add(contact.getFixtureB().getBody());
                            orbs[((ArcadeOrb)ob2).getColor()-1] = null;
                            died = true;
                        }
                        deadThings.add(contact.getFixtureA().getBody());
                    }

                }else{//If some bad guy got hit
                    if (ob1 instanceof Enemy) {
                        hit++;
                        if ( ((Enemy)ob1).getHurtDie( ((OrbAttack)ob2).getColor(), ((OrbAttack)ob2).getDamage()) ) {
                            if (((Enemy) ob1).getColor() == ((OrbAttack) ob2).getColor()) match++;
                            kills++;
                            deadThings.add(contact.getFixtureA().getBody());
                        }
                    }
                    if (ob2 instanceof Enemy) {
                        hit++;
                        if (((Enemy)ob2).getHurtDie(((OrbAttack)ob1).getColor(), ((OrbAttack)ob1).getDamage()) ) {
                            if (((OrbAttack) ob1).getColor() == ((Enemy) ob2).getColor()) match++;
                            kills++;
                            deadThings.add(contact.getFixtureB().getBody());
                        }
                    }

                    if (ob1 instanceof IArcadeBoss) {
                        if ( ((IArcadeBoss)ob1).getHurtDie( ((OrbAttack)ob2).getColor(), ((OrbAttack)ob2).getDamage()) ) {
                            deadThings.add(contact.getFixtureA().getBody());
                        }
                    }
                    if (ob2 instanceof IArcadeBoss) {
                        if (((IArcadeBoss)ob2).getHurtDie(((OrbAttack)ob1).getColor(), ((OrbAttack)ob1).getDamage()) ) {
                            deadThings.add(contact.getFixtureB().getBody());
                        }
                    }

                    //Gdx.app.log("enemy", "was hit");
                }

            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

    }//         <------COLLISION EVENT HERE

    @Override//TODO GET DEBUG SHIT ORGANIZED
    public void render(float delta) {
        debugMatrix = new Matrix4(super.camera.combined);
        debugMatrix.scale(100, 100, 1f);

        cls();
        batch.begin();
        drawShit();
        batch.draw(pauseButton,camera.position.x+HALFW-pauseButton.getWidth(),camera.position.y-HALFH);

        //If all orbes died
        flag = false;
        for(ArcadeOrb orb : orbs) {
            if (orb != null) flag = true;
        }
        if(!flag) state = GameState.LOST;

        if(bossFight)if(boss.getHurtDie(1, 0)) state = GameState.WON;

        if(soundPreferences.getBoolean("soundOn")){
            if(bgMusic!= null){
                bgMusic.play();
            }
        }else{
            if(bgMusic!= null){
                bgMusic.stop();
            }
        }

        if(state == GameState.WON){
            win(delta);
        }else if(state == GameState.CLUE){
            batch.end();
            clueStage.draw();
            Gdx.input.setInputProcessor(clueStage);
        }else if(state == GameState.STATS){
            batch.end();
            statsStage.sophieCoins.setText(Integer.toString(statsStage.statsPrefs.getInteger("Coins")));
            statsStage.yellowXPLbl.setText(Integer.toString(statsStage.statsPrefs.getInteger("XP")));
            statsStage.blueXPLbl.setText(Integer.toString(statsStage.statsPrefs.getInteger("XP")));
            statsStage.redXPLbl.setText(Integer.toString(statsStage.statsPrefs.getInteger("XP")));
            statsStage.draw();
            Gdx.input.setInputProcessor(inputMultiplexer);
        }else if (state == GameState.PAUSED) {//Draw pause menu
            Gdx.input.setInputProcessor(pauseStage);
            batch.end();
            pauseStage.draw();
        }else if(state != GameState.LOST){ //TODO RETURN TO SAVE OR MAIN MENU
            Gdx.input.setInputProcessor(input);
            stepper(delta);
            spawnMonsters(delta);
            if(died){
                swapOrbes();
                died = false;
            }
            batch.end();
        }else{
            loose(delta);
        }
        batch.begin();
        batch.setProjectionMatrix(super.camera.combined);
        //debugRenderer.render(world, debugMatrix);//DEBUG
        batch.end();
        cooldown += delta;
    }

    private void drawShit(){
        if(d == 1 || d == 2)batch.draw(background,-2560,0);
        else batch.draw(background,0,0);
        sophie.setColor(1.0f,1.0f,1.0f,0.8f);
        sophie.setPosition(ArcadeValues.pelletOriginX-100
                , ArcadeValues.pelletOriginY-35);
        sophie.draw(batch);
        //TODO DRAW ICON
        if(bossFight){
            for (Sprite sprite : info) {
                switch (boss.getLife()) {
                    case 10:
                    case 9:
                        sprite.setColor(1f, 1f, 1f, 1f);
                        break;
                    case 8:
                    case 7:  //low opacity
                        sprite.setColor(1f, 1f, 1f, 0.8f);
                        break;
                    case 6:
                    case 5:  //lower opacity
                        sprite.setColor(1f, 1f, 1f, 0.6f);
                        break;
                    case 4:
                    case 3:  //low freq blink
                        sprite.setColor(1f, 1f, 1f, blink(0.5f));
                        break;
                    case 2:
                    case 1:  //hi freq blink
                        sprite.setColor(1f, 1f, 1f, blink(0.18f));
                        break;
                }
                sprite.draw(batch);
            }
        }else{
            scoreDisplay.setText(font, "Spirits defeated: "+((int)kills) );
            font.draw(batch, scoreDisplay, 5, 25);
        }
        drawBodies();
    }

    private float blink(float speed) {
        ac = ac <= 1.0 ? ac + Gdx.graphics.getDeltaTime() : 0;
        return(0.3f * MathUtils.sin(ac * (2 * MathUtils.PI / speed)) + 0.5f);
    }

    private void drawBodies(){
        world.getBodies(squirts);
        Object obj;
        for(Body b: squirts){
            obj = b.getUserData();
            if(obj instanceof OrbAttack) {
                ((OrbAttack) obj).draw(batch);
            }else if(obj instanceof ArcadeOrb){
                ((ArcadeOrb)obj).draw(batch);
            }else if(obj instanceof Enemy){
                ((Enemy)obj).draw(batch);
            }else if(obj instanceof IArcadeBoss){
                ((IArcadeBoss)obj).draw(batch);
            }
        }
        squirts.clear();
    }

    private void stepper(float delta){
        world.step(1/60f, 6, 2);

        //clean dead things
        for(Body b: deadThings){
            while(b.getFixtureList().size > 0){
                b.destroyFixture(b.getFixtureList().get(0));
            }
            world.destroyBody(b);
        }
        deadThings.clear();
    }

    private void win(float delta){

        dialoguetime += delta;
        if (dialoguetime < 6f) {
            dialogue.makeText(glyph, batch, "You have proven yourself worthy of going on forward.\n You now carry new knowledge", camera.position.x);
            batch.end();
        } else {
            batch.end();
            stats.putInteger("Coins", stats.getInteger("Coins")+10);
            stats.putInteger("Quartz", stats.getInteger("Quartz")+10);
            stats.putInteger("Obsidian", stats.getInteger("Obsidian")+10);
            stats.flush();
            LoaderState next;
            switch(d){
                case 1:
                    pref.putInteger("level", 2);
                    pref.putBoolean("boss", false);
                    pref.flush();
                    next = LoaderState.LEVEL2;
                    break;
                case 2:
                    pref.putInteger("level", 3);
                    pref.putBoolean("boss", false);
                    pref.flush();
                    next = LoaderState.LEVEL3;
                    break;
                case 3:
                    pref.putInteger("level", 4);
                    pref.putBoolean("boss", false);
                    pref.flush();
                    next = LoaderState.LEVEL4;
                    break;
                default:
                    next = LoaderState.ARCADE;
            }
            ArcadeValues.bossFightFlag = false;
            app.setScreen(new Fade(app, next));
            this.dispose();
        }
    }

    private void loose(float delta){
        if(bossFight) {
            dialoguetime += delta;
            if (dialoguetime < 6f) {
                dialogue.makeText(glyph, batch, "This dream overwhelmed you\n Your inexperienced soul was not ready.\n The endless dream will give you experience", camera.position.x);
                batch.end();
            } else {
                batch.end();
                ArcadeValues.bossFightFlag = true;
                app.setScreen(new Fade(app, LoaderState.ARCADE));
            }
        }else{
            ArcadeValues.bossFightFlag = true;
            if (!putXp) {
                stats.putInteger("XP", !ArcadeValues.debug ? (stats.getInteger("XP") + ((int)(hit + 10 * (hit / shot) + 10 * (hit * (match / hit))))/10):557);
                stats.flush();
                putXp = true;
            }
            dialoguetime += delta;
            if (dialoguetime < 6f) {
                dialogue.makeText(glyph, batch, "This dream overwhelmed you\n Your experience grew to " + stats.getInteger("XP"), camera.position.x);
                batch.end();
            } else {
                batch.end();
                app.setScreen(new Fade(app, LoaderState.MAINMENU));
            }
        }

        this.dispose();
    }

    private void cls() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void spawnMonsters(float delta){
        //TODO MAKE STEPS
        acumulator += delta;
        elapsed += delta;
        //betweenSpawns = 2f;
        betweenSpawns = freq(acumulator);
        if(elapsed > betweenSpawns){
            Gdx.app.log("time: "+acumulator, "freq: "+betweenSpawns);
            spawnSomething();
            elapsed = 0;
        }
    }

    private float freq(float time){
        if(time >= 0 && time < ArcadeValues.stepTimes[0]){
            return 2;
        }
        if(time >= ArcadeValues.stepTimes[0] && time < ArcadeValues.stepTimes[1]){// goes up and ends in 1.6
            return betweenSpawns - (0.006666666667f*arcadeMultiplier)/30;
        }
        if(time >= ArcadeValues.stepTimes[1] && time < ArcadeValues.stepTimes[2]){// stays in 1.6
            if(!bossActive && bossFight) {
                (boss).move();

                bossActive = true;
            }
            return betweenSpawns;
        }
        if(time >= ArcadeValues.stepTimes[2] && time < ArcadeValues.stepTimes[3]){//goes up and ends in 1.2
            if(bossActive && bossFight){
                (boss).move();
                bossActive = false;
            }
            return betweenSpawns - (0.006666666667f*arcadeMultiplier)/30;
        }
        if(time >= ArcadeValues.stepTimes[3] && time < ArcadeValues.stepTimes[4]){//stays in 1.2
            if(!bossActive && bossFight) {
                (boss).move();
                bossActive = true;
            }
            return betweenSpawns;
        }
        if(time >= ArcadeValues.stepTimes[4] && time < ArcadeValues.stepTimes[5]){//goes up and ends in 0.8
            if(bossActive && bossFight) {
                (boss).move();
                bossActive = false;
            }
            return betweenSpawns - (0.006666666667f*arcadeMultiplier)/30;
        }
        if(time >= ArcadeValues.stepTimes[5] && time < ArcadeValues.stepTimes[6]){//starts in 0.8 and goes slowly
            if(!bossActive && bossFight) {
                (boss).move();
                bossActive = true;
            }
            return betweenSpawns - (0.006666666667f*arcadeMultiplier)/50;
        }
        return betweenSpawns;

    }

    private void spawnSomething(){
        float hitCheck = hit <= 120 ? hit : 120;
        float e1 = (float)(1 - 0.5*(0.005* hitCheck + 0.2));
        float e0 = (float)(1 - (0.005* hitCheck + 0.2));

        float p = MathUtils.random();
        float lr = MathUtils.random();

        int c = calcColor();
        if(0 <= p && p < e0/2){ //skull
            float a = lr * MathUtils.PI;
            float x = ArcadeValues.pelletOriginX + ArcadeValues.highOnPot * MathUtils.cos(a);
            float y = ArcadeValues.pelletOriginY + ArcadeValues.highOnPot * MathUtils.sin(a);
            switch(c){
                case 1:
                    new ArcadeSkull(world, 1, a, x, y, skullYellowAnimation);
                    Gdx.app.log("Spawn", "Yellow Skull");
                    break;
                case 2:
                    new ArcadeSkull(world, 2, a, x, y, skullBlueAnimation);
                    Gdx.app.log("Spawn", "Blue Skull");
                    break;
                case 3:
                    new ArcadeSkull(world, 3, a, x, y, skullRedAnimation);
                    Gdx.app.log("Spawn", "Red Skull");
                    break;
            }
        }
        if(e0/2 <= p && p <e0){ //goo
            Gdx.app.log("Goo", "to Spawn");
            float a = lr * MathUtils.PI;
            float x = ArcadeValues.pelletOriginX + ArcadeValues.highOnPot * MathUtils.cos(a);
            float y = ArcadeValues.pelletOriginY + ArcadeValues.highOnPot * MathUtils.sin(a);
            switch(c){
                case 1:
                    new ArcadeGoo(world, 1, a, x, y, yellowGooAnimation);
                    Gdx.app.log("Spawn", "Yellow Goo");
                    break;
                case 2:
                    new ArcadeGoo(world, 2, a, x, y, blueGooAnimation);
                    Gdx.app.log("Spawn", "Blue Goo");
                    break;
                case 3:
                    new ArcadeGoo(world, 3, a, x, y, redGooAnimation);
                    Gdx.app.log("Spawn", "Red Goo");
                    break;
            }
        }
        if(e0 <= p && p < e1){ //lizard
            switch(c){
                case 1: //Lizard
                    new ArcadeLizard(world, 1, lr > 0.5 ? 0 : 1, lizardAnimation);
                    break;
                case 2: //Jaguar
                    new ArcadeJaguar(world, 2, lr > 0.5 ? 0 : 1, jaguarAnimation);
                    break;
                case 3: //Bat
                    new ArcadeLizard(world, 3, lr > 0.5 ? 0 : 1, lizardAnimation);
                    break;
            }
        }
        if(e1 <= p && p < e1+(1-e1)/3){ //spike
            float a = lr * MathUtils.PI;
            float x = ArcadeValues.pelletOriginX + ArcadeValues.highOnPot * MathUtils.cos(a);
            float y = ArcadeValues.pelletOriginY + ArcadeValues.highOnPot * MathUtils.sin(a);
            new ArcadeSpike(world, 1, a, x, y, spike);
        }
        if(e1+(1-e1)/3 <= p && p < e1+(2*(1-e1))/3){//meteor
            Gdx.app.log("Meteor", "Spawn");
            new ArcadeMeteor(world, (100+1080*lr), meteor);
        }
        if(e1+((2*(1-e1))/3) <= p && p <= 1){//arrow
            new ArcadeArrow(world, 0, 0, lr > 0.5 ? 0 : 1, arrowBlue);
            new ArcadeArrow(world, 1, 0, lr > 0.5 ? 0 : 1, arrowRed);
            new ArcadeArrow(world, 2, 0, lr > 0.5 ? 0 : 1, arrowYellow);
        }


    }

    private int calcColor() {
        float r = MathUtils.random();
        if (bossFight) {
            switch (d) {
                case (1):
                    return d;
                case (2):
                    if (r >= 0.0f && r < 0.5f) return 1;
                    if (r >= 0.5f && r < 1f) return 2;
                    break;
                case (3):
                    if (r >= 0.0f && r < 0.33f) return 1;
                    if (r >= 0.33f && r < 0.66f) return 2;
                    if (r >= 0.66f && r <= 1f) return 3;
                    break;
                default:
                    return 1;
            }
        }else{
            switch (d) {
                case (1):
                    if (r >= 0.0f && r < 0.8f) return 1;
                    if (r >= 0.8f && r < 0.9f) return 2;
                    if (r >= 0.9f && r <= 1f) return 3;
                    break;
                case (2):
                    if (r >= 0.0f && r < 0.4f) return 1;
                    if (r >= 0.4f && r < 0.8f) return 2;
                    if (r >= 0.8f && r <= 1f) return 3;
                    break;
                default:
                case (3):
                    if (r >= 0.0f && r < 0.33f) return 1;
                    if (r >= 0.33f && r < 0.66f) return 2;
                    if (r >= 0.66f && r <= 1f) return 3;
                    break;

            }
        }
        return 1;
    }

    private void swapOrbes(){
        if(d > 1) {
            Integer[] places = new Integer[3];
            Float[] lives = new Float[3];
            int n = 0;
            for(int i = 0; i < orbs.length; i++){
                if(orbs[i] != null){
                    places[i] = orbs[i].getPlace();
                    lives[i] = orbs[i].getLife();
                    deadThings.add(orbs[i].getBody());
                    orbs[i] = null;
                    n++;

                }else{
                    places[i] = null;
                    lives[i] = null;
                }

            }

            for(int i = 0; i < places.length; i++){
                if(places[i] != null){
                    switch(i){
                        case 0:
                            orbs[i] = new ArcadeYellowOrb(  world, orbYellow, (places[i]+1)%n,  ((places[i]+1)%n)==0,  lives[0]);
                            break;
                        case 1:
                            orbs[i] = new ArcadeBlueOrb(    world, orbBlue, (places[i]+1)%n,  ((places[i]+1)%n)==0,    lives[1]);
                            break;
                        case 2:
                            orbs[i] = new ArcadeRedOrb(     world, orbRed, (places[i]+1)%n,  ((places[i]+1)%n)==0,    lives[2]);
                    }
                }
            }
            for(int i = 0; i < 3; i++){
                if(orbs[i] != null){
                    if(orbs[i].getPlace() == 0) {
                        switch (orbs[i].getColor() - 1) {
                            case 0:
                                currentColor = orbColor.YELLOW;
                                break;
                            case 1:
                                currentColor = orbColor.BLUE;
                                break;
                            case 2:
                                currentColor = orbColor.RED;
                        }
                    }
                }
            }
        }

    }

    void kill(Body b){
        deadThings.add(b);
    }

    @Override
    public void resize(int width, int height) {
        view.update(width, height);
    }

    //WTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTF
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        if(bgMusic != null){
            if(bgMusic.isPlaying()){
                bgMusic.pause();
            }
        }
    }
    //WTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTFWTF
    private class Input implements InputProcessor {
        private Vector3 v = new Vector3();

        @Override
        public boolean keyDown(int keycode) {

            if (keycode == com.badlogic.gdx.Input.Keys.BACK) {
                dispose();
                app.setScreen(new Fade(app, LoaderState.MAINMENU));
                return true;
            }
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
        public boolean touchDown(int screenX, int screenY, int pointer, int button)
        {
            v.set(screenX,screenY,0);
            camera.unproject(v);

            if(//if hit orb
                    (v.x > ArcadeValues.pelletOriginX-60 && v.x < ArcadeValues.pelletOriginX+60)
                            &&  (v.y > ArcadeValues.pelletOriginY-60 && v.y < ArcadeValues.pelletOriginY+60)
                            &&  state == GameState.PLAYING
                    )
            {
                swapOrbes();
            }else if(v.x > 1172 && v.y < 135){//if hit pause
                state = GameState.PAUSED;
            }else if(state == GameState.PLAYING){//if we're not switching orbs we shooting
                if(cooldown > 0.2f){
                    float angle = MathUtils.atan2(
                            v.y - ArcadeValues.pelletOriginY,
                            v.x - ArcadeValues.pelletOriginX
                    );
                    switch (currentColor) {
                        case YELLOW:
                            new OrbAttack(world, 1, angle, pelletYellow);
                            //Gdx.app.log("1", "");
                            break;
                        case BLUE:
                            new OrbAttack(world, 2, angle, pelletBlue);
                            //Gdx.app.log("2", "");
                            break;
                        case RED:
                            new OrbAttack(world, 3, angle, pelletRed);
                            //Gdx.app.log("3", "");
                            break;
                    }
                    if(soundPreferences.getBoolean("fxOn")){shootSound.play(0.3f);}
                    cooldown = 0.0f;
                }

            }
            shot ++;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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

    private enum orbColor {
        YELLOW,
        BLUE,
        RED
    }
}