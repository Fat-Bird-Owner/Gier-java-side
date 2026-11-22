package GierJava.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.graphics.Pal;

public class AsteroidHexGrid{
    public float x, y;
    public float radius;
    public float hexSize = 4f;
    public float rotation;

    public Color gridColor = Pal.gray;

    public AsteroidHexGrid(float x, float y, float radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void draw(){
        float stepX = hexSize * 1.5f;
        float stepY = hexSize * Mathf.sqrt3;

        int range = Mathf.ceil(radius / stepX) + 2;
        float innerRadius = Math.max(0f, radius - hexSize);

        Draw.color(gridColor);
        Lines.stroke(0.6f);

        for(int a = -range; a <= range; a++){
            for(int c = -range; c <= range; c++){
                int b = -a - c;

                float hx = stepX * a;
                float hy = stepY * (c + a * 0.5f);

                if(Mathf.dst2(hx, hy, 0f, 0f) > innerRadius * innerRadius) continue;

                Lines.poly(
                    x + hx,
                    y + hy,
                    6,
                    hexSize,
                    rotation
                );
            }
        }

        Lines.circle(x, y, radius);

        Draw.reset();
    }
}
