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
	gl_Position = in_Position;
	// Override gl_Position with our new calculated position
	//gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_Position;
	
	//pass_Color = in_Normal;
	pass_TextureCoord1 = in_TextureCoord1;
	pass_TextureCoord1.y = 1-pass_TextureCoord1.y;
	//pass_TextureCoord2 = in_TextureCoord2;
}