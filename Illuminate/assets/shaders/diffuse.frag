#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

layout (binding=4) uniform sampler2DMS texture_diffuse4;

layout (binding=3, size1x32) uniform iimage2D output0;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

void main(void) 
{
	float intensity = imageLoad(output0, ivec2(pass_TextureCoord2*256) )  * .000005;

	out_Color =  vec4(intensity);//pass_TextureCoord2*64));
	out_Color = texelFetch(texture_diffuse4, ivec2(pass_TextureCoord1*1024), 1);
}