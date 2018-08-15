#ifdef GL_ES
precision highp float;
precision highp int;
#endif

uniform vec2 resolution;
uniform float time;
uniform float mX;
uniform float mY;

#define PROCESSING_LINE_SHADER

vec3 colorA = vec3(1.8,0.0,0.);
vec3 colorB = vec3(1.,0.7,.1);

void main(){
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    float pct = abs(1.-uv.y*1.8);
    vec3 color = mix(colorA, colorB, pct);

    gl_FragColor = vec4(color,1.);
}