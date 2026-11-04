package GierJava.ui.dialogs;

import arc.scene.Element;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.Group;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.content.TechTree.TechNode;
import mindustry.graphics.Pal;
import arc.scene.ui.layout.Scl;
import mindustry.ui.dialogs.*;

public class GierResearchDialog extends ResearchDialog{

    private static final float horizontalSpacing = Scl.scl(75f);
    private static final float verticalSpacing = Scl.scl(390f);

    private float nextX;

    public GierResearchDialog(){
        super();

        View oldView = view;
        Group parent = oldView.parent;
        int index = parent.getChildren().indexOf(oldView, true);

        UpwardView newView = new UpwardView();

        parent.removeChild(oldView);
        parent.addChildAt(index, newView);

        view = newView;
    }

    public static void load(){
        Reflect.set(Vars.ui, "research", new GierResearchDialog());
    }

    private float layoutNode(TechTreeNode node, int depth){
    float left = Float.MAX_VALUE;
    float right = -Float.MAX_VALUE;

    boolean hasChildren = false;

    for(TechTreeNode child : node.children){
        if(!child.visible) continue;

        float childX = layoutNode(child, depth + 1);

        left = Math.min(left, childX);
        right = Math.max(right, childX);
        hasChildren = true;
    }

    if(!hasChildren){
        node.x = nextX;
        nextX += horizontalSpacing + nodeSize;
    }else{
        node.x = (left + right) / 2f;
    }

    node.y = depth * (verticalSpacing + nodeSize);

    return node.x;
    }

private float layoutNode(TechTreeNode node, int depth){
    float left = Float.MAX_VALUE;
    float right = -Float.MAX_VALUE;

    boolean hasChildren = false;

    for(TechTreeNode child : node.children){
        if(!child.visible) continue;

        float childX = layoutNode(child, depth + 1);

        left = Math.min(left, childX);
        right = Math.max(right, childX);
        hasChildren = true;
    }

    if(!hasChildren){
        node.x = nextX;
        nextX += horizontalSpacing;
    }else{
        node.x = (left + right) / 2f;
    }

    node.y = depth * verticalSpacing;

    return node.x;
}

    public class UpwardView extends View{

        private void clamp(){
            float pad = nodeSize;

            float ox = width/2f, oy = height/2f;
            float rx = bounds.x + panX + ox, ry = panY + oy + bounds.y;
            float rw = bounds.width, rh = bounds.height;
            rx = Mathf.clamp(rx, -rw + pad, Core.graphics.getWidth() - pad);
            ry = Mathf.clamp(ry, -rh + pad, Core.graphics.getHeight() - pad);
            panX = rx - bounds.x - ox;
            panY = ry - bounds.y - oy;
        }
        
        @Override
        public void drawChildren(){
            clamp();

            float offsetX = panX + width / 2f;
            float offsetY = panY + height / 2f;

            Draw.sort(true);

            for(TechTreeNode node : nodes){
                if(!node.visible) continue;

                for(TechTreeNode child : node.children){
                    if(!child.visible) continue;

                    boolean locked = !node.node.content.unlockedHost() || !child.node.content.unlockedHost();

                    Draw.z(locked ? 1f : 2f);

                    Lines.stroke(
                        Scl.scl(4f),
                        locked ? Pal.gray : Pal.accent
                    );

                    Draw.alpha(parentAlpha);

                    Lines.line(
                        node.x + offsetX,
                        node.y + offsetY,
                        child.x + offsetX,
                        child.y + offsetY
                    );
                }
            }

            Draw.sort(false);
            Draw.reset();

            for(Element child : getChildren()){
                if(!child.visible) continue;
                child.draw();
            }
            
        }
        
    }
}
