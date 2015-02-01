package org.alchemy.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class AlcEasing {
    static final int EASING_LINEAR = 1;
    static final int EASING_SIN_INOUT = 2;
    static final int EASING_QUAD_INOUT = 3;
    static final int EASING_QUAD_IN = 4;

    AlcEasing() {
    }

    public List<Map<String,Float>> getEventSequence(float startX, float startY, float endX, float endY, int startTime, int endTime, int easingType) {
        int deltaTime = endTime - startTime;
        float deltaX = endX - startX;
        float deltaY = endY - startY;
        List<Map<String,Float>> sequence = new ArrayList<Map<String,Float>>();

        Random rand = new Random();
        int maxOffsetX = rand.nextInt((40 - -40)) + -40;
        int maxOffsetY = rand.nextInt((40 - -40)) + -40;

        for (int currentTime = startTime; currentTime <= endTime; currentTime++) {
            Map<String,Float> event = new HashMap<String,Float>();

            float currentX;
            float currentY;
            float percentComplete = (currentTime - startTime) / (float) deltaTime;
            float offsetX;
            float offsetY;

            if (easingType != EASING_LINEAR) {
                if (percentComplete < 0.1) {
                    continue;
                } else if (percentComplete > 0.9) {
                    continue;
                }
            }

            if (percentComplete < 0.5) {
                offsetX = quadIn(currentTime, 0, maxOffsetX, deltaTime / 2);
                offsetY = quadIn(currentTime, 0, maxOffsetY, deltaTime / 2);
            } else {
                offsetX = quadIn(currentTime - (deltaTime / 2), maxOffsetX, 0, deltaTime / 2);
                offsetY = quadIn(currentTime - (deltaTime / 2), maxOffsetY, 0, deltaTime / 2);
            }

            if (easingType == EASING_SIN_INOUT) {
                currentX = sinInOut(currentTime, startX, deltaX, deltaTime);
                currentY = sinInOut(currentTime, startY, deltaY, deltaTime);
            } else if (easingType == EASING_QUAD_INOUT) {
                currentX = quadInOut(currentTime, startX, deltaX, deltaTime);
                currentY = quadInOut(currentTime, startY, deltaY, deltaTime);
            } else {
                currentX = linear(currentTime, startX, deltaX, deltaTime);
                currentY = linear(currentTime, startY, deltaY, deltaTime);
            }

            event.put("time", (float) currentTime);
            event.put("x", currentX + offsetX);
            event.put("y", currentY + offsetY);
            sequence.add(event);
        }
        return sequence;
    }

    private float linear(int currentTime, float startValue, float changeInValue, int duration) {
        return changeInValue * currentTime / duration + startValue;
    }

    private float sinInOut(int currentTime, float startValue, float changeInValue, int duration) {
        return (float) (- changeInValue / 2 * (Math.cos(Math.PI*currentTime/duration) - 1) + startValue);
    }

    private float quadInOut(int currentTime, float startValue, float changeInValue, int duration) {
        float localCurrentTime = (float) currentTime / (duration / 2);
        if (localCurrentTime < 1) {
            return changeInValue / 2 * localCurrentTime * localCurrentTime + startValue;
        }
        localCurrentTime--;
        return - changeInValue / 2 * (localCurrentTime * (localCurrentTime-2) - 1) + startValue;
    }

    private float quadIn(int currentTime, float startValue, float changeInValue, int duration) {
        float localCurrentTime = (float) currentTime / duration;
        return changeInValue * localCurrentTime * localCurrentTime + startValue;
    }
}
