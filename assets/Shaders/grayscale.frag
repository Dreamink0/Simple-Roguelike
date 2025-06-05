#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_grayscaleAmount; // 灰度化强度，0.0-1.0

void main()
{
    vec4 color = texture2D(u_texture, v_texCoords) * v_color;
    
    // 计算灰度值，使用标准的亮度公式
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    
    // 在原色和灰度之间插值
    vec3 finalColor = mix(color.rgb, vec3(gray), u_grayscaleAmount);
    
    gl_FragColor = vec4(finalColor, color.a);
}
