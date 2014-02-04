#version 430 core

in vec3 p_eye;
in vec3 n_eye;
in vec2 uv2_eye;

layout (location = 0) out vec3 def_p; // "go to GL_COLOR_ATTACHMENT0"
layout (location = 1) out vec3 def_n; // "go to GL_COLOR_ATTACHMENT1"
layout (location = 2) out vec2 def_uv2; // "go to GL_COLOR_ATTACHMENT2"

out vec4 out_Color;

void main () 
{
	def_p = p_eye;
	def_n = n_eye;
  
	//def_uv2 = uv2_eye;
}