package GierJava.dialogs;

import arc.graphics.Color; 
import arc.graphics.g2d.Draw; 
import arc.graphics.g2d.Fill; 
import arc.graphics.g2d.Lines; 
import arc.math.Mathf; 
import arc.util.Time; 
import mindustry.graphics.Pal; 
import arc.scene.ui.layout.Scl; 
import mindustry.ui.dialogs.ResearchDialog;

public class GierResearchDialog extends ResearchDialog{

public GierResearchDialog(){
    super();
}

@Override
public class View extends ResearchDialog.View{

    public View(){
        super();
    }

    @Override
    public void drawChildren(){
        clamp();

        float ox = panX + width / 2f;
        float oy = panY + height / 2f;
        float radius = nodeSize / 2f;

        Draw.sort(true);

        for(TechTreeNode node : nodes){
            if(!node.visible) continue;

            for(TechTreeNode child : node.children){
                if(!child.visible) continue;

                float x1 = node.x + ox;
                float y1 = node.y + oy + radius;
                float x2 = child.x + ox;
                float y2 = child.y + oy - radius;
                float midY = (y1 + y2) / 2f;

                boolean locked =
                    locked(node.node) || locked(child.node);

                Color color = locked ? Pal.gray : Pal.accent;

                Draw.color(color, parentAlpha);
                Lines.stroke(Scl.scl(2f));

                // Plain connector path, no radial decoration.
                Lines.line(x1, y1, x1, midY);
                Lines.line(x1, midY, x2, midY);
                Lines.line(x2, midY, x2, y2);

                float a = Math.abs(midY - y1);
                float b = Math.abs(x2 - x1);
                float c = Math.abs(y2 - midY);
                float total = a + b + c;

                if(total <= 1f) continue;

                float progress =
                    (Time.time / 90f) % 1f;

                for(int i = 0; i < 3; i++){
                    float distance =
                        (progress + i / 3f) * total;

                    float px, py;

                    if(distance < a){
                        px = x1;
                        py = y1 + distance;
                    }else if(distance < a + b){
                        float t = distance - a;
                        px = x1 + Mathf.sign(x2 - x1) * t;
                        py = midY;
                    }else{
                        float t = distance - a - b;
                        px = x2;
                        py = midY + t;
                    }

                    Draw.color(color, parentAlpha);
                    Fill.circle(px, py, Scl.scl(3f));
                }
            }
        }

        Draw.reset();
        Draw.sort(false);

        super.drawChildren();
    }
}
}