package mx.itesm.goback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/*
 * Created by gerry on 3/22/17.
 */
class ArcadeSophie {    //TODO ADAPT FOR LEVELS
    private Body body;
    private Sprite sprite;
    private Preferences stats = Gdx.app.getPreferences("STATS");
    protected float life = stats.getFloat("SophieLife");
    private int color = 1;
    private Animation<TextureRegion> standby;
    private Animation<TextureRegion> walking;
    private Animation<TextureRegion> waking;
    private Animation<TextureRegion> dying;
    private Animation<TextureRegion> jumping;

    private float timerchangeframestandby;
    private float timerchangeframewalk;
    private float timerchangeframedie;
    private float timerchangeframewake;
    private float timerchangeframejump;
    private ArcadeSophie.MovementState currentstate = ArcadeSophie.MovementState.STILL_RIGHT; //STILL_RIGHT

    private Preferences pref=Gdx.app.getPreferences("getLevel");
    private BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("Physicshit.json"));
    private PolygonShape shape;

    private long jumpFor;
    private long jumpFor2;
    float vi=3;
    private float ac;
    private boolean done=false;

    ArcadeSophie(World world, Texture tx){
        TextureRegion texturaCompleta = new TextureRegion(tx);

        TextureRegion[][] texturaPersonaje = texturaCompleta.split(160,160);

        standby = new Animation(0.18f, texturaPersonaje[0][0], texturaPersonaje[0][1],
                texturaPersonaje[0][2], texturaPersonaje[0][3],texturaPersonaje[0][4],
                texturaPersonaje[0][5], texturaPersonaje[0][6]);
        walking = new Animation(0.06f, texturaPersonaje[0][7], texturaPersonaje[0][8],
                texturaPersonaje[0][9], texturaPersonaje[0][10],
                texturaPersonaje[0][11], texturaPersonaje[0][12],texturaPersonaje[0][13],
                texturaPersonaje[0][14], texturaPersonaje[0][15], texturaPersonaje[0][16],
                texturaPersonaje[0][17], texturaPersonaje[0][18], texturaPersonaje[0][19],
                texturaPersonaje[0][20],texturaPersonaje[0][21], texturaPersonaje[0][22],
                texturaPersonaje[0][23], texturaPersonaje[0][24],texturaPersonaje[0][25]);
        waking= new Animation(0.15f, texturaPersonaje[0][42], texturaPersonaje[0][41],
                texturaPersonaje[0][40], texturaPersonaje[0][39],
                texturaPersonaje[0][38], texturaPersonaje[0][37],texturaPersonaje[0][36],
                texturaPersonaje[0][35], texturaPersonaje[0][34], texturaPersonaje[0][33],
                texturaPersonaje[0][32], texturaPersonaje[0][31], texturaPersonaje[0][30],
                texturaPersonaje[0][29],texturaPersonaje[0][28], texturaPersonaje[0][27],
                texturaPersonaje[0][26]);
        dying= new Animation(0.18f, texturaPersonaje[0][23],texturaPersonaje[0][24],
                texturaPersonaje[0][25], texturaPersonaje[0][26], texturaPersonaje[0][27],
                texturaPersonaje[0][28], texturaPersonaje[0][29],
                texturaPersonaje[0][30], texturaPersonaje[0][31],texturaPersonaje[0][32],
                texturaPersonaje[0][33], texturaPersonaje[0][34], texturaPersonaje[0][35],
                texturaPersonaje[0][36], texturaPersonaje[0][37], texturaPersonaje[0][38],
                texturaPersonaje[0][39],texturaPersonaje[0][40], texturaPersonaje[0][41],
                texturaPersonaje[0][42]);
        jumping=new Animation(0.4f,texturaPersonaje[0][0],texturaPersonaje[0][43],
                texturaPersonaje[0][44],texturaPersonaje[0][45],texturaPersonaje[0][46],
                texturaPersonaje[0][47],texturaPersonaje[0][48],texturaPersonaje[0][49]);


        // Animación infinita
        standby.setPlayMode(Animation.PlayMode.LOOP);
        walking.setPlayMode(Animation.PlayMode.LOOP);
        dying.setPlayMode(Animation.PlayMode.NORMAL);
        jumping.setPlayMode(Animation.PlayMode.NORMAL);

        // Inicia el timer que contará tiempo para saber qué frame se dibuja
        timerchangeframewalk = 0;
        timerchangeframestandby=0;
        timerchangeframedie=0;
        timerchangeframewake=0;
        timerchangeframejump=0;

        sprite = new Sprite(texturaPersonaje[0][0]);
        sprite.setCenter(0,0);



        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation=true;
        bodyDef.position.set(
                ArcadeValues.meterspelletOriginX-7,
                ArcadeValues.meterspelletOriginY+(ArcadeValues.pxToMeters(tx.getHeight()/4))
        );

        body = world.createBody(bodyDef);
        fixturer(100f, 0.0f);
        body.setLinearVelocity(0f, 0f);
        body.setUserData(this);
    }

    private void fixturer(float density, float restitution) {

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.shape = shape;
        fixtureDef.friction = 0;


        fixtureDef.filter.categoryBits = ArcadeValues.sophieCat; //its category
        fixtureDef.filter.maskBits = ArcadeValues.sophieMask; //or of its category with colliding categories
        loader.attachFixture(body,"sophie",fixtureDef,1.5f);
        body.createFixture(fixtureDef);
    }

    boolean getHurtDie(int type, float damage){
        if(color == type){
            life -= damage;
        }else{
            life -= damage * 2;
        }
        return life <= 0.0f;
    }

    private void blink(float speed, Batch batch) {
        ac = ac <= 1.0 ? ac + Gdx.graphics.getDeltaTime() : 0;
        batch.setColor(1f, 1f, 1f, (0.3f * MathUtils.sin(ac * (2 * MathUtils.PI / speed)) + 0.5f));
    }

    void draw(SpriteBatch batch) {
        TextureRegion region;

        Gdx.app.log("sophie life",(   (life*10) / stats.getFloat("SophieLife")    )+"" );
        switch((int) (   (life*10) / stats.getFloat("SophieLife")    ) ){
            case 10:case 9:
                batch.setColor(1f, 1f, 1f, 1f);
                break;
            case 8:case 7:  //low opacity
                batch.setColor(1f, 1f, 1f, 0.8f);
                break;
            case 6:case 5:  //lower opacity
                batch.setColor(1f, 1f, 1f, 0.6f);
                break;
            case 4:case 3:  //low freq blink
                blink(0.5f,batch);
                break;
            case 2:case 1:  //hi freq blink
                blink(0.18f,batch);
                break;
        }





        switch (currentstate) {
            case MOVE_RIGHT:
            case MOVE_LEFT:
                timerchangeframewalk += Gdx.graphics.getDeltaTime();
                region = walking.getKeyFrame(timerchangeframewalk);

                if (currentstate== ArcadeSophie.MovementState.MOVE_LEFT) {
                    if (!region.isFlipX()) {
                        region.flip(true,false);
                    }
                } else {
                    if (region.isFlipX()) {
                        region.flip(true,false);
                    }
                }

                sprite.setCenter(
                        ArcadeValues.metersToPx(body.getPosition().x),
                        ArcadeValues.metersToPx(body.getPosition().y)
                );
                batch.draw(region,sprite.getX(),sprite.getY());
                break;
            case STILL_LEFT:
            case STILL_RIGHT:
                timerchangeframestandby += Gdx.graphics.getDeltaTime();
                region = standby.getKeyFrame(timerchangeframestandby);

                if (currentstate== ArcadeSophie.MovementState.STILL_LEFT) {
                    if (!region.isFlipX()) {
                        region.flip(true,false);
                    }

                } else {
                    if (region.isFlipX()) {
                        region.flip(true,false);
                    }

                }

                sprite.setCenter(
                        ArcadeValues.metersToPx(body.getPosition().x),
                        ArcadeValues.metersToPx(body.getPosition().y)
                );
                batch.draw(region,sprite.getX(),sprite.getY());
                break;
            case JUMP:
                timerchangeframejump+= Gdx.graphics.getDeltaTime();
                region= jumping.getKeyFrame(timerchangeframejump);
                if (currentstate== ArcadeSophie.MovementState.STILL_LEFT) {
                    if (!region.isFlipX()) {
                        region.flip(true,false);
                    }

                } else {
                    if (region.isFlipX()) {
                        region.flip(true,false);
                    }

                }
                if(jumping.isAnimationFinished(timerchangeframejump)) timerchangeframejump=0;

                sprite.setCenter(
                        ArcadeValues.metersToPx(body.getPosition().x),
                        ArcadeValues.metersToPx(body.getPosition().y)
                );
                batch.draw(region,sprite.getX(),sprite.getY());
                break;
            case JUMP2:
                timerchangeframejump+= Gdx.graphics.getDeltaTime();
                region= jumping.getKeyFrame(timerchangeframejump);
                sprite.setCenter(
                        ArcadeValues.metersToPx(body.getPosition().x),
                        ArcadeValues.metersToPx(body.getPosition().y)
                );
                batch.draw(region,sprite.getX(),sprite.getY());
                if(jumping.isAnimationFinished(timerchangeframejump)) timerchangeframejump=0;
                break;
            case DYING:
                timerchangeframedie += Gdx.graphics.getDeltaTime();
                region = dying.getKeyFrame(timerchangeframedie);

                sprite.setCenter(
                        ArcadeValues.metersToPx(body.getPosition().x),
                        ArcadeValues.metersToPx(body.getPosition().y)
                );
                batch.draw(region,sprite.getX(),sprite.getY());
                break;
            case JUMPFIN:
                timerchangeframejump+= Gdx.graphics.getDeltaTime();
                region= jumping.getKeyFrame(timerchangeframejump);
                if (currentstate== ArcadeSophie.MovementState.STILL_LEFT) {
                    if (!region.isFlipX()) {
                        region.flip(true,false);
                    }

                } else {
                    if (region.isFlipX()) {
                        region.flip(true,false);
                    }

                }

                sprite.setCenter(
                        ArcadeValues.metersToPx(body.getPosition().x),
                        ArcadeValues.metersToPx(body.getPosition().y)
                );
                batch.draw(region,sprite.getX(),sprite.getY());
                break;
        }



    }

    public void update(){
        switch (currentstate){
            case MOVE_RIGHT:
                if(pref.getInteger("level")!=3) moveHorizontal();
                else{
                    if(sprite.getX()>600&&!done) {
                        currentstate=MovementState.STILL_RIGHT;
                        done=true;
                    }else if(sprite.getX()>2900) {
                        moveHorizontal();
                    }else if(sprite.getX()>605){
                        currentstate=MovementState.STILL_LEFT;
                    }else{
                        moveHorizontal();
                    }
                }
                break;
            case MOVE_LEFT:
                moveHorizontal();
                break;
            case STILL_LEFT:
                if(pref.getInteger("level")!=3) body.setLinearVelocity(0f, 0f);
                else body.setLinearVelocity(0.7f,0);
                break;
            case STILL_RIGHT:
                if(pref.getInteger("level")!=3) body.setLinearVelocity(0f, 0f);
                else body.setLinearVelocity(0f,0);
                break;
            case JUMP:
                moveJump();
                break;
            case JUMP2:
                moveJump2();
                break;
            case JUMPFIN:
                moveJump3();
                break;
            default:
                break;
        }
    }

    private void moveHorizontal() {
        // go right
        if(currentstate == MovementState.MOVE_RIGHT){
            switch (pref.getInteger("level")){
                case 2:
                    if(this.getX() <= Level2.RIGHT_LIMIT){
                        body.setLinearVelocity(1.5f, 0f);
                        //body.setLinearVelocity(5f, 0f);
                    }else{
                        body.setLinearVelocity(0f, 0f);
                    }
                    break;
                case 3:
                    if(this.getX() <= Level3.RIGHT_LIMIT){
                        body.setLinearVelocity(0.7f, 0f);
                        //body.setLinearVelocity(5f, 0f);
                    }else if(this.getX()<3000){
                        body.setLinearVelocity(0.7f,0f);
                    } else{
                        body.setLinearVelocity(0f, 0f);
                    }
                    break;
                default:
                    break;

            }

        }

        // go left
        if(currentstate == MovementState.MOVE_LEFT){
            switch (pref.getInteger("level")){
                case 2:
                    if (this.getX() >= Level2.LEFT_LIMIT) {
                        body.setLinearVelocity(-1.5f, 0f);
                        //body.setLinearVelocity(-5f, 0f);
                    }else{
                        body.setLinearVelocity(0f, 0f);
                    }
                    break;
                case 3:
                    if (this.getX() >= Level3.LEFT_LIMIT) {
                        body.setLinearVelocity(0f, 0f);
                        //body.setLinearVelocity(-5f, 0f);
                    }else{
                        body.setLinearVelocity(0f, 0f);
                    }
                    break;
                default:
                    break;
            }

        }

    }

    private void moveJump(){
        float doWhi= velocityCalc(jumpFor);
        if(doWhi>-vi) {
            body.setLinearVelocity(0.7f,doWhi);
        }else {
            body.setLinearVelocity(0.7f,0f);
            jumpFor=0;
            currentstate=MovementState.STILL_LEFT;
        }
        jumpFor++;
    }

    private void moveJump2(){
        float doWhi= velocityCalc(jumpFor2);
        if(doWhi>-vi||sprite.getY()>232) {
            body.setLinearVelocity(0.7f,doWhi);
        }else {
            body.setLinearVelocity(0.7f,0f);
            jumpFor=0;
            currentstate=MovementState.STILL_LEFT;
            jumpFor2=0;
        }
        jumpFor2++;
    }

    private void moveJump3(){
        float doWhi= velocityCalc(jumpFor);
        body.setLinearVelocity(1f,doWhi);
        jumpFor++;
    }

    void setColor(int color){
        this.color = color;
    }

    public ArcadeSophie.MovementState getMovementState() {
        return currentstate;
    }

    public void setMovementState(ArcadeSophie.MovementState ms) {
        this.currentstate = ms;
    }

    public float getX(){
        return sprite.getX();
    }

    public float getY(){
        return sprite.getY();
    }

    public Sprite getSprite(){
      return sprite;
    }

    private float velocityCalc(float time){
        return vi-0.08f*time;
    }

    public enum MovementState {
        CREATING,
        WAKING,
        JUMP,
        JUMP2,
        STILL_RIGHT,
        STILL_LEFT,
        HIT,
        MOVE_LEFT,
        MOVE_RIGHT,
        DYING,
        JUMPFIN,
        SLEEPING,
        UNCONDITIONAL_RIGHT
    }
}
