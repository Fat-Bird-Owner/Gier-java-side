package GierJava.content;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.content.*;
import mindustry.entities.effect.*;

import static mindustry.Vars.*;

public class GierStatusEffects {
    public static StatusEffect constructionOverdrive;

    public static void load(){

        constructionOverdrive = new StatusEffect("construction-overdrive"){{

          buildSpeedMultiplier = 1.5f;
          outline = false;
  
          color = Color.valueOf("FFD37F");
          effectChance = 0.05f;
          
          effect = new WrapEffect(
            Fx.overdriven,
            Color.valueOf("FFD37F")
          );
          
        }};

    }
}
