package GierJava.ui.dialogs;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.Group;
import arc.util.Reflect;
import mindustry.ui.dialogs.*;
import mindustry.Vars;
import mindustry.graphics.Pal;
import arc.scene.ui.layout.*;

public class GierResearchDialog extends ResearchDialog{

    public float spacingX = 30f;
    public float spacingY = 45f;

    public UpwardResearchDialog(){
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
        Reflect.set(Vars.ui, "research", new UpwardResearchDialog());
    }

    float subtreeWidth(TechTreeNode node){
        if(node.children.length == 0) return nodeSize;

        float width = 0f;

        for(TechTreeNode child : node.children){
            width += subtreeWidth(child) + spacingX;
        }

        return Math.max(nodeSize, width - spacingX);
    }

    void layoutNode(TechTreeNode node, float x, float y){
        node.x = x;
        node.y = y;

        if(node.children.length == 0) return;

        float total = subtreeWidth(node);
        float cursor = x - total / 2f;

        for(TechTreeNode child : node.children){
            float width = subtreeWidth(child);
            float childX = cursor + width / 2f;

            layoutNode(child, childX, y + spacingY);

            cursor += width + spacingX;
        }
    }

    @Override
    void treeLayout(){
        layoutNode(root, 0f, 0f);

        float minx = 0f;
        float miny = 0f;
        float maxx = 0f;
        float maxy = 0f;

        for(TechTreeNode node : nodes){
            if(!node.visible) continue;

            minx = Math.min(minx, node.x - node.width / 2f);
            maxx = Math.max(maxx, node.x + node.width / 2f);
            miny = Math.min(miny, node.y - node.height / 2f);
            maxy = Math.max(maxy, node.y + node.height / 2f);
        }

        bounds = new Rect(minx, miny, maxx - minx, maxy - miny);
        bounds.y += nodeSize * 1.5f;
    }

    public class UpwardView extends View{

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

                    boolean locked = locked(node.node) || locked(child.node);

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

            super.drawChildren();
        }
    }
}
