#version 330

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in ivec2 UV2;

layout(std140) uniform DynamicTransforms {
	mat4 ModelViewMat;
};

layout(std140) uniform Projection {
	mat4 ProjMat;
};

out vec2 texCoord;
out vec4 vertColor;
out vec2 progressData;

void main() {
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
	texCoord    = UV0;
	vertColor   = Color;
	progressData = vec2(UV2) / 32767.0;
}