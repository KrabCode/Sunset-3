
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform vec2 resolution;
uniform float time;
uniform float mX;
uniform float mY;

const vec3 colorA = vec3(1.0,0.,0.);
const vec3 colorB = vec3(0.005,0.005,0.09);


uniform sampler2D texture;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    //vec2 mv = vec2(mX/resolution.x, 1-(mY/resolution.y));
    //float d = distance(vec2(uv.x,uv.y), vec2(.5,.8))*3.;
    //vec3 color = mix(colorA, colorB, d);
//     vec3 red = vec3(1.,0.,0.);
//     vec3 lit = mix(colorB, (texture2D(texture, vertTexCoord.st) * vertColor).xyz, .5);
    gl_FragColor = vec4((texture2D(texture, vertTexCoord.st) * vertColor).xyz, .6);
}
