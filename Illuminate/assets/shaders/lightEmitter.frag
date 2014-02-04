#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;
uniform sampler2D texture_diffuse2;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

layout (binding=3, rgba32f) uniform image2D output_buffer;

void main(void) 
{
	vec2 coord = vec2(texture2D(texture_diffuse2, pass_TextureCoord1));
	ivec2 coord256 = ivec2(coord * 256);
	

	vec4 luminance = imageLoad(output_buffer, coord256);

	luminance += vec4(1) * 0.00001;

	imageStore(output_buffer, coord256, luminance);
	
	
	out_Color = luminance;
	//out_Color = texture2D(texture_diffuse2, pass_TextureCoord1);
}