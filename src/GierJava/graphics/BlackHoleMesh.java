package GierJava.graphics;

import arc.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.util.*;

import mindustry.*;
import mindustry.type.*;
import mindustry.graphics.g3d.*;

public class BlackHoleMesh extends PlanetMesh{

    private static BlackHoleShader shader;
    private final BlackHoleShader bhShader;

    public static void install(Planet planet){
        planet.meshLoader = () -> new BlackHoleMesh(planet);
    }

    public BlackHoleMesh(Planet planet){
        this(planet, getShader());
    }

    private BlackHoleMesh(Planet planet, BlackHoleShader shader){
        super(planet, MeshBuilder.buildIcosphere(5, planet.radius), shader);
        this.bhShader = shader;
    }

    private static BlackHoleShader getShader(){
        if(shader == null){
            shader = new BlackHoleShader();
        }

        return shader;
    }

    @Override
    public void preRender(PlanetParams params){
        bhShader.planet = planet;
    }

    public static class BlackHoleShader extends Shader{

        public Planet planet;

        private static final String vertex = """
            attribute vec4 a_position;

            uniform mat4 u_proj;
            uniform mat4 u_trans;

            varying vec3 v_worldPos;

            void main(){
                vec4 world = u_trans * a_position;
                v_worldPos = world.xyz;
                gl_Position = u_proj * world;
            }
            """;

        private static final String fragment = """
            #define HIGHP

            varying vec3 v_worldPos;

            uniform vec3 u_camera;
            uniform vec3 u_center;

            uniform float u_time;
            uniform float u_rs;
            uniform float u_step;
            uniform float u_escape;
            uniform float u_diskInner;
            uniform float u_diskOuter;
            uniform float u_diskThickness;

            float hash21(vec2 p){
                p = fract(p * vec2(123.34, 456.21));
                p += dot(p, p + 45.32);
                return fract(p.x * p.y);
            }

            vec3 rotateAxis(vec3 v, vec3 axis, float angle){
                float c = cos(angle);
                float s = sin(angle);

                return normalize(
                    v * c +
                    cross(axis, v) * s +
                    axis * dot(axis, v) * (1.0 - c)
                );
            }

            vec3 starfield(vec3 direction){
                float longitude = atan(direction.z, direction.x);
                float latitude = asin(clamp(direction.y, -1.0, 1.0));

                vec2 uv = vec2(
                    longitude * 0.15915494 + 0.5,
                    latitude * 0.31830989 + 0.5
                );

                vec2 grid = uv * vec2(900.0, 450.0);
                vec2 cell = floor(grid);
                vec2 local = fract(grid) - 0.5;

                float random = hash21(cell);

                float star = step(0.9965, random);
                star *= 1.0 - smoothstep(0.0, 0.045, length(local));

                float largeStar = step(0.9995, random);
                largeStar *= 1.0 - smoothstep(0.0, 0.09, length(local));

                float nebula = sin(uv.x * 17.0 + sin(uv.y * 12.0));
                nebula *= cos(uv.y * 19.0 + uv.x * 7.0);
                nebula = max(nebula, 0.0) * 0.008;

                vec3 color = vec3(0.002, 0.003, 0.012);

                color += vec3(0.015, 0.008, 0.04) * nebula;

                color += vec3(0.55, 0.7, 1.0) * star;
                color += vec3(1.0, 0.85, 0.65) * largeStar * 2.0;

                return color;
            }

            vec3 diskColor(vec3 pos, vec3 direction){
                float radius = length(pos.xz);

                float heat = clamp(
                    (u_diskOuter - radius) /
                    (u_diskOuter - u_diskInner),
                    0.0, 1.0
                );

                float rotation = atan(pos.z, pos.x);

                float turbulence = sin(rotation * 12.0 + u_time * 0.7);
                turbulence += sin(radius * 14.0 - u_time * 1.4);
                turbulence = turbulence * 0.5 + 0.5;

                vec3 innerColor = vec3(1.0, 0.95, 0.8);
                vec3 outerColor = vec3(1.0, 0.12, 0.015);

                vec3 color = mix(outerColor, innerColor, heat);

                vec3 tangent = normalize(vec3(-pos.z, 0.0, pos.x));

                float doppler = clamp(
                    0.75 + dot(direction, tangent) * 0.55,
                    0.2, 1.5
                );

                return color * (0.7 + turbulence * 0.7) * doppler;
            }

            void main(){

                vec3 pos = u_camera - u_center;
                vec3 dir = normalize(v_worldPos - u_camera);

                vec3 color = vec3(0.0);

                bool escaped = false;
                bool absorbed = false;
                bool diskHit = false;

                for(int i = 0; i < 192; i++){

                    float rOld = length(pos);

                    if(rOld <= u_rs){
                        absorbed = true;
                        break;
                    }

                    if(rOld > u_escape && dot(pos, dir) > 0.0){
                        escaped = true;
                        break;
                    }

                    float along = dot(-pos, dir);

                    vec3 perihelion = pos + dir * along;

                    float b = max(
                        length(perihelion),
                        u_rs * 0.02
                    );

                    float ds = min(
                        u_step,
                        max(u_rs * 0.025, rOld * 0.035)
                    );

                    vec3 futurePos = pos + dir * ds;

                    float r = length(futurePos);
                    float dr = r - rOld;

                    float radial =
                        1.0 / (b * b) -
                        (1.0 - u_rs / rOld) /
                        (rOld * rOld);

                    float theta = dr / (
                        rOld * rOld *
                        sqrt(max(radial, 0.000001))
                    );

                    vec3 normal = cross(dir, -futurePos);

                    float normalLength = length(normal);

                    if(normalLength > 0.000001){
                        dir = rotateAxis(
                            dir,
                            normal / normalLength,
                            theta
                        );
                    }

                    if(
                        abs(pos.y) < u_diskThickness ||
                        pos.y * futurePos.y <= 0.0
                    ){
                        float t = 0.0;

                        if(abs(pos.y - futurePos.y) > 0.000001){
                            t = clamp(
                                pos.y / (pos.y - futurePos.y),
                                0.0, 1.0
                            );
                        }

                        vec3 hit = mix(pos, futurePos, t);

                        float diskRadius = length(hit.xz);

                        if(
                            diskRadius > u_diskInner &&
                            diskRadius < u_diskOuter
                        ){
                            color = diskColor(hit, dir);
                            diskHit = true;
                            break;
                        }
                    }

                    pos += dir * ds;
                }

                if(escaped){
                    color = starfield(dir);
                }

                if(absorbed){
                    color = vec3(0.0);
                }

                gl_FragColor = vec4(color, 1.0);
            }
            """;

        public BlackHoleShader(){
            super(vertex, fragment);
        }

        @Override
        public void apply(){

            float rs = planet.radius * 0.18f;

            setUniformf(
                "u_camera",
                Vars.renderer.planets.cam.position
            );

            setUniformf(
                "u_center",
                planet.position
            );

            setUniformf("u_time", Time.time / 60f);

            setUniformf("u_rs", rs);

            setUniformf(
                "u_step",
                planet.radius * 0.1f
            );

            setUniformf(
                "u_escape",
                rs * 12f
            );

            setUniformf(
                "u_diskInner",
                rs * 1.6f
            );

            setUniformf(
                "u_diskOuter",
                rs * 5.5f
            );

            setUniformf(
                "u_diskThickness",
                rs * 0.045f
            );
        }
    }
}
