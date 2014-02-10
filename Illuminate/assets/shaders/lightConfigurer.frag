#version 430 core

in vec3 p_eye;
in vec3 n_eye;
in vec2 uv;

layout (location = 0) out vec4 samplerOut;
layout (location = 1) out vec3 positionOut;
layout (location = 2) out vec3 normalOut;

void main () 
{
	samplerOut = vec4(1,1,1,1);

	positionOut = p_eye;
	normalOut = n_eye;
}