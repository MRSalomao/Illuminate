#version 430 core

in vec3 p_eye;
in vec3 n_eye;
in vec2 uv;

layout (location = 0) out vec4 samplerOut;
layout (location = 1) out vec3 positionOut;
layout (location = 2) out vec3 normalOut;

layout (binding=3, size1x32) uniform iimage2D output0;

void main () 
{
	ivec2 coord = ivec2(gl_FragCoord.x * 4, (128 - gl_FragCoord.y) * 4);

	float intensity = imageLoad(output0, coord)  * .0002;

	samplerOut = vec4(intensity);

	positionOut = p_eye;
	normalOut = n_eye;
}