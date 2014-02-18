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
	samplerOut = vec4(imageLoad(output0, ivec2( gl_FragCoord.xy * vec2(8) ) )  * .0001);

	positionOut = p_eye;
	normalOut = n_eye;
}