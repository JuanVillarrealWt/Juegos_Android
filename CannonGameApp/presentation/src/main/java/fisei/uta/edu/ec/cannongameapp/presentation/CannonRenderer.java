package fisei.uta.edu.ec.cannongameapp.presentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.List;

import androidx.core.content.ContextCompat;

import fisei.uta.edu.ec.cannongameapp.presentation.R;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Blocker;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannon;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Cannonball;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameConfig;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameElement;
import fisei.uta.edu.ec.cannongameapp.domain.entities.GameState;
import fisei.uta.edu.ec.cannongameapp.domain.entities.Target;

public class CannonRenderer {
    private final Context context;
    private final Paint textPaint = new Paint();
    private final Paint backgroundPaint = new Paint();
    private final Paint elementPaint = new Paint();
    private final Paint cannonPaint = new Paint();

    public CannonRenderer(Context context) {
        this.context = context;
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setAntiAlias(true);
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        elementPaint.setAntiAlias(true);
        cannonPaint.setColor(Color.BLACK);
        cannonPaint.setAntiAlias(true);
    }

    public void updateDimensions(int screenHeight) {
        if (screenHeight > 0) {
            textPaint.setTextSize((int) (GameConfig.TEXT_SIZE_PERCENT * screenHeight));
        }
    }

    public void render(Canvas canvas, Cannon cannon, Blocker blocker,
                       List<Target> targets, GameState gameState,
                       int screenWidth, int screenHeight) {
        if (canvas == null) return;

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
        if (gameState != null && screenHeight > 0) {
            String timeString = context.getString(R.string.time_remaining_format, gameState.getTimeLeft());
            canvas.drawText(timeString, 40, (int) (screenHeight * 0.08f), textPaint);
        }

        if (cannon != null && screenHeight > 0) {
            cannonPaint.setStrokeWidth(cannon.getBarrelWidth());
            canvas.drawLine(0, screenHeight / 2f, cannon.getBarrelEndX(),
                    cannon.getBarrelEndY(screenHeight), cannonPaint);
            canvas.drawCircle(0, screenHeight / 2f, cannon.getBaseRadius(), cannonPaint);
            Cannonball cannonball = cannon.getCannonball();
            if (cannonball != null && cannonball.isOnScreen()) {
                elementPaint.setColor(Color.BLACK);
                canvas.drawCircle(cannonball.getX() + cannonball.getRadius(),
                        cannonball.getY() + cannonball.getRadius(), cannonball.getRadius(), elementPaint);
            }
        }

        if (blocker != null) drawElement(canvas, blocker, Color.BLACK);
        if (targets != null) {
            int darkColor = ContextCompat.getColor(context, R.color.dark);
            int lightColor = ContextCompat.getColor(context, R.color.light);
            double targetSpacing = screenWidth *
                    (GameConfig.TARGET_WIDTH_PERCENT + GameConfig.TARGET_SPACING_PERCENT);
            for (Target target : targets) {
                int originalIndex = targetSpacing > 0
                        ? (int) Math.round((target.getX() - screenWidth * GameConfig.TARGET_FIRST_X_PERCENT)
                        / targetSpacing)
                        : 0;
                drawElement(canvas, target, originalIndex % 2 == 0 ? darkColor : lightColor);
            }
        }
    }

    private void drawElement(Canvas canvas, GameElement element, int color) {
        elementPaint.setColor(color);
        canvas.drawRect(element.getX(), element.getY(),
                element.getX() + element.getWidth(), element.getY() + element.getHeight(), elementPaint);
    }
}
