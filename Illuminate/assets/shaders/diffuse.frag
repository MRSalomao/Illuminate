#version 430 core

uniform sampler2D texture_diffuse0;
uniform sampler2D texture_diffuse1;

in vec4 pass_Color;
in vec2 pass_TextureCoord1;
in vec2 pass_TextureCoord2;

out vec4 out_Color;

void main(void) {
	out_Color = pass_Color;
	// Override out_Color with our texture pixel
	out_Color = texture2D(texture_diffuse0, pass_TextureCoord2) * texture2D(texture_diffuse1, pass_TextureCoord2);
}