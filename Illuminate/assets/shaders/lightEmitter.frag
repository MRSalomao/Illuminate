#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

layout (binding=3, rgba32f) uniform image2D output_buffer;

void main(void) 
{
	vec4 val = imageLoad(output_buffer, ivec2(pass_TextureCoord1*256));

	val += vec4(1.0 - distance(gl_FragCoord.xy/vec2(256.), vec2(0.5) ) ) * 0.00001;

	imageStore(output_buffer, ivec2(pass_TextureCoord1*256), val);
	
	out_Color = texture2D(texture_diffuse0, pass_TextureCoord2);//val;// * texture2D(texture_diffuse1, pass_TextureCoord2);
}