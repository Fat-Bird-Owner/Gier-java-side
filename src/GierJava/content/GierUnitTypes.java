package GierJava.content;

import ent.anno.Annotations.*;
import mindustry.gen.*;
import mindustry.type.*;
import GierJava.gen.*;
import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import arc.struct.Seq;

public class GierUnitTypes {
    // `genIO` is only used here to avoid creating a `revision` directory, which is used to track IO.
    public static @EntityDef(value = {Unitc.class}, genIO = false) UnitType restoration;

    public static void load(){
        restoration = new UnitType("restoration"){{
            speed = 3.5f;
            flying = true;
            outlineColor = Color.valueOf("2f2f3f");
            accel = 0.116667f;
            drag = 0.035f;
            flyingLayer = 123f;
            targetable = false;
            faceTarget = false;
            drawSoftShadow = false;

            isEnemy = false;
            mineTier = 2;
            mineSpeed = 10f;
            mineBeamOffset = 0f;
            buildSpeed = 1.5f;

            itemCapacity = 40;

            engineSize = 0f;
            hitSize = 14f;

            engines.addAll(Seq.with(
            new UnitEngine(-4f, 0f, 2f, 0f),
            new UnitEngine(4f, 0f, 2f, 180f),
            new UnitEngine(0f, 4f, 2f, -90f),
            new UnitEngine(0f, -4f, 2f, 90f)
            ));

        }};


        EntityRegistry.registerUnits();
    }
}
