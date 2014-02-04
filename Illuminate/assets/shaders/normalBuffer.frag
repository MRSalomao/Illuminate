#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

layout (binding=0, rgba32f) uniform image2D output_buffer;

void main(void) {
	imageStore(output_buffer, ivec2(gl_FragCoord.xy), pass_Color);

	out_Color = pass_Color;
}