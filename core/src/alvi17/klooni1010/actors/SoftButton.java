package alvi17.klooni1010.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import alvi17.klooni1010.Klooni;
import alvi17.klooni1010.Theme;

// Small wrapper to use themed image buttons more easily
public class SoftButton extends ImageButton {

    //region Members

    private int styleIndex;
    public Drawable image;


    public SoftButton(int styleIndex, String imageName) {
        super(Klooni.theme.getStyle(styleIndex));

        this.styleIndex = styleIndex;
        image = Theme.skin.getDrawable(imageName);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Always update the style to make sure we're using the right image.
        // This might not always be the case since two buttons can be using
        // the "same" style (except for the image up, i.e. after coming from
        // the customize menu), so make sure to update it always.
        ImageButtonStyle style = getStyle();
        Klooni.theme.updateStyle(style, styleIndex);
        style.imageUp = image;

        super.draw(batch, parentAlpha);
    }

    //endregion
}
