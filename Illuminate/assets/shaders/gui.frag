#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

layout (binding=3, rgba32f) uniform image2D output_buffer;

void main(void) {
	out_Color = vec4( imageLoad(output_buffer, ivec2(gl_FragCoord.xy)) );
	// Override out_Color with our texture pixel
	//out_Color = texture2D(texture_diffuse0, pass_TextureCoord1) * texture2D(texture_diffuse1, pass_TextureCoord2);
}