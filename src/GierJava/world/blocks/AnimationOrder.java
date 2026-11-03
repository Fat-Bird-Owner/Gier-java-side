package GierJava.world.blocks;

import arc.graphics.Color;

public class AnimationOrder{

    public enum AnimationType{
        weapon,
        construct,
        liquid,
        paint
    }

    public AnimationType type;
    public float prob;
    public float fin;

    public Color color = Color.white.cpy();
    public float intensity = 1f;

    public AnimationOrder(AnimationType type, float prob, float fin){
        this.type = type;
        this.prob = prob;
        this.fin = fin;
    }

    public AnimationOrder color(Color color){
        this.color = color.cpy();
        return this;
    }

    public AnimationOrder intensity(float intensity){
        this.intensity = intensity;
        return this;
    }

    public float progress(float buildProgress){
        if(fin <= prob){
            return buildProgress >= fin ? 1f : 0f;
        }

        return arc.math.Mathf.clamp(
            (buildProgress - prob) / (fin - prob)
        );
    }
}
