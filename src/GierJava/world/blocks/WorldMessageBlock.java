
package GierJava.world.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.ui.Label;
import arc.scene.ui.TextArea;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Align;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;

public class WorldMessageBlock extends Block{

    public int maxTextLength = 400;

    public float panelWidth = 360f;
    public float panelHeight = 140f;

    public float scrollSpeed = 25f;
    public float stripeSpacing = 36f;
    public float stripeWidth = 10f;

    public Color backgroundColor = Color.valueOf("101923");
    public Color stripeColor = Color.valueOf("24394a");

    public WorldMessageBlock(String name){
        super(name);

        configurable = true;
        solid = true;
        destructible = true;

        buildType = MessageBuild::new;

        config(String.class, (MessageBuild build, String text) -> {
            if(!build.canEdit() || text.length() > maxTextLength) return;

            build.message = text;
        });
    }

    public boolean canEdit(){
        return Vars.state != null &&
            (Vars.state.rules.editor || Vars.state.rules.infiniteResources);
    }

    public class MessageBuild extends Building{

        public String message = "";

        public boolean canEdit(){
            return WorldMessageBlock.this.canEdit();
        }

        @Override
        public boolean shouldShowConfigure(mindustry.gen.Player player){
            return true;
        }

        @Override
        public void buildConfiguration(Table table){
            Stack stack = new Stack();
            stack.add(new ScrollingBackground());
            Table foreground = new Table();

            Label label = new Label(
                message.isEmpty() ? "[lightgray]Empty message" : message
            );

            label.setWrap(true);
            label.setAlignment(Align.center);

            foreground.add(label).grow().pad(12f);

            stack.add(foreground);

            table.add(stack)
                .size(panelWidth, panelHeight)
                .pad(5f);
          
            if(canEdit()){
                table.button(Icon.pencil, Styles.cleari, () -> {
                    showEditor(label);
                }).size(40f);
            }
        }

        private void showEditor(Label label){
            BaseDialog dialog = new BaseDialog("@editmessage");

            TextArea area = dialog.cont
                .add(new TextArea(message))
                .size(380f, 160f)
                .get();

            area.setMaxLength(maxTextLength);

            dialog.buttons.button("@ok", () -> {
                String text = area.getText();

                if(canEdit() && text.length() <= maxTextLength){
                    configure(text);
                    label.setText(text.isEmpty() ? "[lightgray]Empty message" : text);
                }

                dialog.hide();
            }).size(130f, 60f);

            dialog.buttons.button("@cancel", dialog::hide)
                .size(130f, 60f);

            dialog.closeOnBack();
            dialog.show();
        }

        @Override
        public void draw(){
            super.draw();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.str(message);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            message = read.str();
        }

        @Override
        public String config(){
            return message;
        }

        public class ScrollingBackground extends Element{

            @Override
            public void draw(){
                super.draw();

                Draw.color(backgroundColor);
                Fill.rect(
                    x + width / 2f,
                    y + height / 2f,
                    width,
                    height
                );

                Draw.color(stripeColor);

                float offset = Mathf.mod(
                    Time.time * scrollSpeed / 60f,
                    stripeSpacing
                );

                for(float sx = -stripeSpacing;
                    sx < width + stripeSpacing;
                    sx += stripeSpacing){

                    float center = x + sx + offset;

                    float left = Math.max(x, center - stripeWidth / 2f);
                    float right = Math.min(x + width, center + stripeWidth / 2f);

                    if(right <= left) continue;

                    Fill.rect(
                        (left + right) / 2f,
                        y + height / 2f,
                        right - left,
                        height
                    );
                }

                Draw.color();
            }
        }
    }
}
