#version 330

in vec2 texCoord;
in vec4 vertColor;
in vec2 progressData;

out vec4 fragColor;

#define PI 3.14159265359

void main() {
    float progress  = progressData.x;
    float thickness = progressData.y;

    vec2 uv = texCoord - vec2(0.5);
    float dist = length(uv);

    float angle = atan(uv.x, -uv.y);
    if (angle < 0.0) angle += 2.0 * PI;

    float innerRadius = 0.5 - thickness;
    float ringMask = step(innerRadius, dist) * step(dist, 0.5);
    float progressMask = step(angle / (2.0 * PI), progress);

    if (ringMask * progressMask < 0.1) discard;

    fragColor = vertColor;
}