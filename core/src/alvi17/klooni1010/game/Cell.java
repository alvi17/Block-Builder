package alvi17.klooni1010.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import alvi17.klooni1010.Klooni;
import alvi17.klooni1010.serializer.BinSerializable;

// Represents a single cell, with a position, size and color.
// Instances will use the cell texture provided by the currently used skin.
public class Cell implements BinSerializable {

    //region Members

    // Negative index indicates that the cell is empty
    private int colorIndex;

    private Vector2 pos;
    private float size;

    // No need to store the vanish color as a color index since
    // this is something quick and shouldn't really affect the UX
    private Color vanishColor;
    private float vanishSize;
    private float vanishElapsed;
    private float vanishLifetime;

    //endregion

    //region Constructor

    Cell(float x, float y, float cellSize) {
        pos = new Vector2(x, y);
        size = cellSize;

        colorIndex = -1;
        vanishElapsed = Float.POSITIVE_INFINITY;
    }

    //endregion

    //region Package local methods

    // Sets the cell to be non-empty and of the specified color index
    void set(int ci) {
        colorIndex = ci;
    }

    void draw(SpriteBatch batch) {
        // Always query the color to the theme, because it might have changed
        draw(Klooni.theme.getCellColor(colorIndex), batch, pos.x, pos.y, size);

        // Draw the previous vanishing cell
        if (vanishElapsed <= vanishLifetime) {
            vanishElapsed += Gdx.graphics.getDeltaTime();

            // vanishElapsed might be < 0 (delay), so clamp to 0
            float progress = Math.min(1f,
                    Math.max(vanishElapsed, 0f) / vanishLifetime);

            vanishSize = Interpolation.elasticIn.apply(size, 0, progress);

            float centerOffset = size * 0.5f - vanishSize * 0.5f;
            draw(vanishColor, batch, pos.x + centerOffset, pos.y + centerOffset, vanishSize);
        }
    }

    // Vanish from indicates the point which caused the vanishing to happen,
    // in this case, a piece was put. The closer it was put, the faster
    // this piece will vanish. This immediately marks the piece as empty.
    void vanish(Vector2 vanishFrom) {
        if (isEmpty()) // We cannot vanish twice
            return;

        vanishSize = size;
        vanishColor = Klooni.theme.getCellColor(colorIndex).cpy();
        vanishLifetime = 1f;
        colorIndex = -1;

        // The vanish distance is this measure (distance² + size³ * 20% size)
        // because it seems good enough. The more the distance, the more the
        // delay, but we decrease the delay depending on the cell size too or
        // it would be way too high
        Vector2 center = new Vector2(pos.x + size * 0.5f, pos.y + 0.5f);
        float vanishDist = Vector2.dst2(
                vanishFrom.x, vanishFrom.y, center.x, center.y) / (size * size * size * size * 0.2f);

        // Negative time = delay, + 0.4*lifetime because elastic interpolation has that delay
        vanishElapsed = vanishLifetime * 0.4f - vanishDist;
    }

    boolean isEmpty() {
        return colorIndex < 0;
    }

    //endregion

    //region Static methods

    // TODO Use skin atlas
    public static void draw(Color color, Batch batch,
                            float x, float y, float size) {
        batch.setColor(color);
        Klooni.theme.cellPatch.draw(batch, x, y, size, size);
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        // Only the color index is saved
        out.writeInt(colorIndex);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        colorIndex = in.readInt();
    }

    //endregion
}
