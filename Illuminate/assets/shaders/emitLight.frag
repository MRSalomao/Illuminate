#version 430 core

//uniform sampler2D texture_diffuse0;
//uniform sampler2D texture_diffuse1;
uniform sampler2D texture_diffuse2;

//in vec4 pass_Color;
//in vec2 pass_TextureCoord1;
//in vec2 pass_TextureCoord2;

out vec4 out_Color;

layout (binding=3, size1x32) uniform iimage2D output_buffer;

void main() 
{
	vec2 coord = vec2(texture(texture_diffuse2, gl_FragCoord.xy/512));
	ivec2 coordInt = ivec2(coord * imageSize(output_buffer));
	

	//int luminance = imageLoad(output_buffer, coordInt);

	//luminance += vec4(1) * 0.00001;

	//imageStore(output_buffer, coordInt, ivec4(luminance+1));
	
	if ( coordInt != ivec2(0,0) )
	{
		int luminance = imageAtomicAdd(output_buffer, coordInt, 1);
		/*imageAtomicAdd(output_buffer, coordInt+ivec2(0,1), 1);
		imageAtomicAdd(output_buffer, coordInt+ivec2(1,0), 1);
		imageAtomicAdd(output_buffer, coordInt+ivec2(1,1), 1);
		imageAtomicAdd(output_buffer, coordInt+ivec2(0,-1), 1);
		imageAtomicAdd(output_buffer, coordInt+ivec2(-1,0), 1);
		imageAtomicAdd(output_buffer, coordInt+ivec2(-1,-1), 1);
		imageAtomicAdd(output_buffer, coordInt+ivec2(-1,1), 1);
		imageAtomicAdd(output_buffer, coordInt+ivec2(1,-1), 1);*/
	
		out_Color = vec4(float(luminance) * .0000005);
	}
	else
	{
		out_Color = vec4(0.2, 0.3, 0.7, 1.0);
	}
	
	//out_Color = vec4((imageLoad(output_buffer, ivec2(gl_FragCoord.xy) ).x * .5).x);
	//out_Color = texture(texture_diffuse2, pass_TextureCoord1);
}