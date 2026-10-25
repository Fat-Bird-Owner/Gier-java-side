package GierJava.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.units.UnitAssembler.*;

import static arc.graphics.g2d.Draw.rect;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;

public class GierEffects {

    public static final Effect 
    
    ventSmoke = new ParticlEffect(){{
        colorFrom = Color.valueOf("60606000");
        colorTo = Color.valueOf("363636c2");
        cone = 10f;
        length = 180f;
        lifetime = 260f;
        sizeFrom = 2f;
        sizeTo = 8f;
        baseRotation = 90f;
    }};

}