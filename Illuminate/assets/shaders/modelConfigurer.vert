#version 430 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 in_Position;
in vec4 in_Normal;
in vec2 in_TextureCoord1;
in vec2 in_TextureCoord2;

out vec4 pass_Color;
out vec2 pass_TextureCoord1;
out vec2 pass_TextureCoord2;

out vec3 p_eye;
out vec3 n_eye;
out vec2 uv;

void main() 
{
	p_eye = (modelMatrix * in_Position).xyz;
	n_eye = (modelMatrix * in_Normal).xyz;
	
	gl_Position = vec4(-1+2*in_TextureCoord2.x, 1-2*in_TextureCoord2.y , 0, 1);
}