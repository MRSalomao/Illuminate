#version 150 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

void main(void) {
	out_Color = vec4(1,1,1,1);
}