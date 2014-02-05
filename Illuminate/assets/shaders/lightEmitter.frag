#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;
uniform sampler2D texture_diffuse2;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

layout (binding=3, size1x32) uniform iimage2D output_buffer;

void main(void) 
{
	vec2 coord = vec2(texture2D(texture_diffuse2, pass_TextureCoord1));
	ivec2 coord256 = ivec2(coord * 1024);
	

	//int luminance = imageLoad(output_buffer, coord256);

	//luminance += vec4(1) * 0.00001;

	//imageStore(output_buffer, coord256, ivec4(luminance+1));
	
	if ( coord256 != ivec2(0,0) )
	{
		int luminance = imageAtomicAdd(output_buffer, coord256, 1);
		/*imageAtomicAdd(output_buffer, coord256+ivec2(0,1), 1);
		imageAtomicAdd(output_buffer, coord256+ivec2(1,0), 1);
		imageAtomicAdd(output_buffer, coord256+ivec2(1,1), 1);
		imageAtomicAdd(output_buffer, coord256+ivec2(0,-1), 1);
		imageAtomicAdd(output_buffer, coord256+ivec2(-1,0), 1);
		imageAtomicAdd(output_buffer, coord256+ivec2(-1,-1), 1);
		imageAtomicAdd(output_buffer, coord256+ivec2(-1,1), 1);
		imageAtomicAdd(output_buffer, coord256+ivec2(1,-1), 1);*/
	
		out_Color = vec4(float(luminance) * .000001);
		
	}
	else
	{
		out_Color = vec4(0.2, 0.3, 0.7, 1.0);
	}
	out_Color = vec4(imageLoad(output_buffer, ivec2(gl_FragCoord.xy) ).x * .000001);
	//out_Color = texture2D(texture_diffuse2, pass_TextureCoord1);
}