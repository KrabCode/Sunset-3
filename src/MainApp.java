import ch.bildspur.postfx.builder.PostFX;
import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.opengl.PShader;

//based on
//Coding Challenge #11: Terrain Generation with Perlin Noise in Processing
// - Daniel Shiffman
// https://www.youtube.com/watch?v=IKB1hWWedMk

public class MainApp extends PApplet{
    Minim minim;
    AudioInput input;

    PShader sea;
    PShader line;
    PShader sun;
    PostFX fx;
    int rows, cols;
    int sizeMod = 2;
    int scl = 30;
    float[][] b1;
    float[][] b2;
    float[][] tmp;
    float mouseForce = 500;
    float damping = .92f;
    int sunDetail = 30;
    int orbDetail= 4;
    float mx;
    float my;
    boolean rolling;

    public static void main(String[] args) {
        PApplet.main("MainApp");
    }

    public void settings() {
        fullScreen(P3D, 1);
//        size(1920,1080, P3D);
    }

    public void setup() {
        minim = new Minim(this);
        input = minim.getLineIn( Minim.MONO);

        fx = new PostFX(this);
        sphereDetail(6);
        colorMode(HSB);
        sea = loadShader("sea.glsl", "vert.glsl");
        sun = loadShader("sun.glsl");
        line = loadShader("line.glsl");
        sea.set("resolution", width, height);
        sun.set("resolution", width, height);
        line.set("resolution", width, height);
        rows = ((height*sizeMod)/scl)+1;
        cols = ((width*sizeMod)/scl)+1;
        b1 = new float[cols][rows];
        b2 = new float[cols][rows];
        tmp = new float[cols][rows];
    }


    @Override
    public void keyPressed() {
        super.keyPressed();
        rolling = !rolling;
//        save("outrunsun-"+frameCount+".png");
    }

    public void draw() {
        mx = map(mouseX, 0, width, -TWO_PI, TWO_PI);
        my = map(mouseY, 0, height,.9f, 9999f);
//        rotateX(mx);
        println(mx);
//        noLights();
        lights();
//        directionalLight(255,255,255,0,-my,-200);

        if(rolling){
//            save("outrunsun-"+frameCount+".png");
        }
        background(0);
        input();
        droplets();
        propagate();
        drawTriangleStrip();
        drawSun();
        fx.render().bloom(0.005f,10,10).compose();
    }

    private void input() {
        //wave machine
//      int centerX = cols / 2;
//      int off = rows - rows / 8;
        //for(int y = rows-2; y > rows/1.8f; y--){
//            b1[centerX-off][y] =  sin(radians((frameCount*40+y*75)%360))*1050;
//            b1[centerX+off][y] =  cos(radians((frameCount*40+y*75)%360))*1050;
//            b1[centerX+of][y]=1000*noise((y+off)*my, radians(frameCount*47));
        //}
        /*
        b1[centerX-off+1][rows-2] =  sin(radians((frameCount*40)%360))*850;
        b1[centerX+off-1][rows-2] =  sin(radians((frameCount*40)%360))*850;
        */
    for(int i = 0; i < cols; i++){
        int fromCenter = abs(cols/2-i);
        int soundSpaceIndex = round(map(fromCenter, 0, cols/2, 0, input.mix.size()-1));
        b1[i][0] = mouseForce*(input.mix.get(soundSpaceIndex));
    }

        if(mousePressed){
            int x = mouseX*sizeMod/scl;
            int y = mouseY*sizeMod/scl;
            if(x > 0 && x < cols && y > 0 && y < rows){
                b1[x][y] -= mouseForce;
//                println(x+":"+y);
            }
        }
    }

    private void droplets() {
        if(frameCount%4==0){
            b1[round(random(cols-1))][round(random(rows-1))] -= mouseForce/40;
        }
        if(frameCount%5==0){
            b1[round(random(cols-1))][round(random(rows-1))] -= mouseForce/30;
        }
        if(frameCount%6==0){
            b1[round(random(cols-1))][round(random(rows-1))] -= mouseForce/15;
        }
    }

    private void propagate() {
        for(int x = 0; x < cols; x++){
            for(int y = 0; y < rows; y++){
                if(x==0||y==0||x==cols-1||y==rows-1){
                    b2[x][y] = 0;
                    continue;
                }
                b2[x][y] = (b1[x-1][y]
                        +b1[x+1][y]
                        +b1[x]  [y+1]
                        +b1[x]  [y-1])/2 - b2[x][y];
                b2[x][y] *= damping;
            }
        }
        tmp = b2;
        b2 = b1;
        b1 = tmp;
    }

    private void drawSun() {
        sphereDetail(sunDetail);

        //move into position
        translate(width/2,500,-5000);
        float planetRotation = radians(frameCount/12f);
        rotateX(PI/2);
        rotateY(planetRotation);

        pushMatrix();
        //draw outer wireframe
        sun.set("time", radians(frameCount));
        sun.set("mX", (float)mouseX);
        sun.set("mY", (float)mouseY);
        shader(sun);
        noFill();
        stroke(255,255,255);
        strokeWeight(5);
        sphere(2500);

        //draw inner nontransparent core
        resetShader();
        fill(0);
        noStroke();
        sphere(2490);

        //draw rays
        shader(sun);
        stroke(255);
        noFill();
        rotateY(-planetRotation);
        rotateX(-PI/2);
        sphereDetail(orbDetail);
        strokeWeight(2);
        for(float i = 0; i <= TWO_PI; i+=PI/16){
            pushMatrix();
            rotateZ(i+planetRotation);
            rotateY(i+radians(frameCount/2));

            float rayStartGirth = 240;
            float rayGirthDiff = 320;
            float rayStartSize = 7500;
            float raySizeDiff = -3000;
            translate(0,6750,0);
            for(float j = 0; j < 1; j++){
                box(rayStartGirth+j*rayGirthDiff, rayStartSize+j*raySizeDiff, rayStartGirth+j*rayGirthDiff);
            }
            popMatrix();
        }

        popMatrix();
    }

    private void drawTriangleStrip() {
        pushMatrix();
        translate(0, height/2);
        rotateX(1.450983f);
//        println(radians(mouseY/4));
        translate(-width/2, -height/2);

            pushMatrix();

            sea.set("time", radians(frameCount));
            sea.set("mX", (float)mouseX);
            sea.set("mY", (float)mouseY);
            shader(sea);
//                noFill();
            fill(255);
            noStroke();


//            shader(sea);

            for (int y = 0; y <= rows -2; y++) {
                beginShape(TRIANGLE_STRIP);
                for (int x = 0; x <= cols -1; x++) {

                    float elev = b1[x][y];
                    float elev2 = b1[x][y+1];
                    float threshold = 200;
                    if(elev >  threshold)    elev = threshold;
                    if(elev <- threshold)    elev = -threshold;
                    if(elev2 >  threshold)   elev2 = threshold;
                    if(elev2 <- threshold)   elev2 = -threshold;

                    vertex(x*scl,y*scl, elev*2);
                    vertex(x*scl,(y+1)*scl, elev2*2);

                }
                endShape(CLOSE);
            }
            popMatrix();
        popMatrix();
        resetShader();
    }
}