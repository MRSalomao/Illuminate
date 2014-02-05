#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;
uniform sampler2D texture_diffuse2;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

layout (binding=3, r32i) uniform iimage2D output_buffer;

void main(void) 
{
	vec2 coord = vec2(texture2D(texture_diffuse2, pass_TextureCoord1));
	ivec2 coord256 = ivec2(coord * 256);
	

	int luminance = imageLoad(output_buffer, coord256);

	//luminance += vec4(1) * 0.00001;

	//imageStore(output_buffer, coord256, luminance);
	
	
	//int luminance = imageAtomicAdd(output_buffer, coord256, 1);
	
	out_Color = vec4(vec3(luminance*0.5), 1.0);
	//out_Color = texture2D(texture_diffuse2, pass_TextureCoord1);
}