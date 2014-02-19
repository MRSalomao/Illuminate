#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

//layout (binding=4) uniform sampler2DMS texture_diffuse4;
layout (binding=4) uniform sampler2D texture_diffuse4;

layout (binding=3, size1x32) uniform iimage2D output0;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

void main() 
{
	int nSamples = 32;

	float intensity = imageLoad(output0, ivec2(pass_TextureCoord2*512) )  * .00000001;

	//vec4 edgeCorrection = texelFetch(texture_diffuse4, ivec2(pass_TextureCoord2 * textureSize(texture_diffuse4)), 0) / nSamples;
	vec4 edgeCorrection = texture(texture_diffuse4, pass_TextureCoord2);
	//for (int i=1; i<nSamples; i++)
	{
		//edgeCorrection += texelFetch(texture_diffuse4, ivec2(pass_TextureCoord2 * textureSize(texture_diffuse4)), i) / nSamples;
	}
	
	if (edgeCorrection.x == 0)
	{
		out_Color =  vec4(0);
	}
	else
	{
		//out_Color =  vec4(intensity) / (1 - edgeCorrection.x + 1/32.);
		out_Color =  vec4(intensity) / edgeCorrection.x;
		//out_Color =  vec4(edgeCorrection.x);
		//out_Color =  vec4(intensity) / (.5);
	}
	//out_Color = texture(texture_diffuse4, gl_FragCoord.xy/1024f);
}