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

void main(void) 
{
	gl_Position = vec4( (2*in_TextureCoord2.x-1), (2*in_TextureCoord2.y-1) , 0, 1);
}