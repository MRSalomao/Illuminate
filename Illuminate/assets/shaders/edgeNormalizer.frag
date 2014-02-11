#version 430 core

uniform sampler2D texture_diffuse0;
//uniform sampler2D texture_diffuse1;
layout (binding=3, size1x32) uniform iimage2D output0;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

void main(void) 
{
	//float intensity = imageLoad(output0, ivec2(pass_TextureCoord2*256) )  * .000005;

	out_Color = vec4(1,0,0,1);//pass_TextureCoord2*64));
}