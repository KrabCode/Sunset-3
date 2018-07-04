#ifdef GL_ES
precision highp float;
precision highp int;
#endif
uniform vec2 resolution;
uniform float time;
uniform float mX;
uniform float mY;

varying vec4 vertColor;
#define PROCESSING_LINE_SHADER

vec3 colorA = vec3(1.0,0.0,0.0);
vec3 colorB = vec3(0.2,0.0,0.0);

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    //vec2 mv = vec2(mX/resolution.x, 1-(mY/resolution.y));
    float d = distance(vec2(uv.x,uv.y), vec2(.5, .3))*2.;
    vec3 color = mix(colorA, colorB, d);
    if(d<.5f){
       //color = vec3(0);
    }
    gl_FragColor = vec4(color, 1.);
}